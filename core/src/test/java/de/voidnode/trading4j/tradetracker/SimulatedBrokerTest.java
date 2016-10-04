package de.voidnode.trading4j.tradetracker;

import java.time.Instant;

import de.voidnode.trading4j.api.Either;
import de.voidnode.trading4j.api.Failed;
import de.voidnode.trading4j.api.OrderEventListener;
import de.voidnode.trading4j.api.OrderManagement;
import de.voidnode.trading4j.domain.TimeFrame.M1;
import de.voidnode.trading4j.domain.marketdata.CandleStick;
import de.voidnode.trading4j.domain.marketdata.FullMarketData;
import de.voidnode.trading4j.domain.marketdata.MutableFullMarketData;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;
import de.voidnode.trading4j.domain.orders.MutableCloseConditions;
import de.voidnode.trading4j.domain.orders.MutablePendingOrder;

import static de.voidnode.trading4j.domain.VolumeUnit.LOT;
import static de.voidnode.trading4j.domain.orders.ExecutionCondition.STOP;
import static de.voidnode.trading4j.domain.orders.OrderType.BUY;
import static de.voidnode.trading4j.domain.orders.OrderType.SELL;
import static de.voidnode.trading4j.testutils.assertions.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Checks if {@link SimulatedBroker} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class SimulatedBrokerTest {

    @InjectMocks
    private SimulatedBroker<FullMarketData<M1>> cut;

    @Mock
    private OrderEventListener orderEventListener;

    @Mock
    private OrderEventListener otherOrderEventListener;

    private final Instant testTime = Instant.EPOCH.plusMillis(12852);

    private final MutableFullMarketData<M1> testCandleStick = new MutableFullMarketData<M1>().setOpen(new Price(10))
            .setHigh(new Price(10)).setLow(new Price(10)).setClose(new Price(10)).setTime(testTime)
            .setSpread(new Price(0)).setVolume(1, LOT).setTickCount(1000);

    private final MutableCloseConditions testCloseConditions = new MutableCloseConditions().setTakeProfit(
            new Price(20000)).setStopLoose(new Price(5000));

    private final MutablePendingOrder testOrder = new MutablePendingOrder().setEntryPrice(new Price(10)).setType(BUY)
            .setExecutionCondition(STOP).setVolume(1, LOT).setCloseConditions(testCloseConditions);

    // ///////////////////
    // / Order opening ///
    // ///////////////////

    /**
     * A submitted {@link BasicPendingOrder} is opened when its entry {@link Price} is between the current candle open and
     * close price.
     */
    @Test
    public void openPendingOrdersWhenEntryPriceIsBetweenOpenAndClosePrice() {
        testOrder.setEntryPrice(new Price(10));
        testCandleStick.setOpen(new Price(9)).setClose(new Price(11));

        cut.sendOrder(testOrder.toImmutablePendingOrder(), orderEventListener);
        cut.newData(testCandleStick.toImmutableFullMarketData());

        verify(orderEventListener).orderOpened(testTime, new Price(10));

        testOrder.setEntryPrice(new Price(100));
        // cut handles borders as inclusive
        testCandleStick.setOpen(new Price(90)).setClose(new Price(100));

        cut.sendOrder(testOrder.toImmutablePendingOrder(), orderEventListener);
        cut.newData(testCandleStick.toImmutableFullMarketData());

        verify(orderEventListener).orderOpened(testTime, new Price(100));

        testOrder.setEntryPrice(new Price(1000));
        // works for bearish candle sticks too
        testCandleStick.setOpen(new Price(1100)).setClose(new Price(900));

        cut.sendOrder(testOrder.toImmutablePendingOrder(), orderEventListener);
        cut.newData(testCandleStick.toImmutableFullMarketData());

        verify(orderEventListener).orderOpened(testTime, new Price(1000));
    }

    /**
     * The spread is added to the current candles open and close {@link Price} when considering opening pending buy
     * orders.
     */
    @Test
    public void spreadIsConsideredWhenOpeneningPendingBuyOrders() {
        testOrder.setEntryPrice(new Price(10));
        cut.sendOrder(testOrder.toImmutablePendingOrder(), orderEventListener);

        // 9+2 = 11 -> too high
        testCandleStick.setSpread(new Price(2)).setOpen(new Price(9)).setClose(new Price(11));
        cut.newData(testCandleStick.toImmutableFullMarketData());
        verifyNoMoreInteractions(orderEventListener);

        // 8+2 = 10 -> enough
        testCandleStick.setSpread(new Price(2)).setOpen(new Price(11)).setClose(new Price(8));
        cut.newData(testCandleStick.toImmutableFullMarketData());

        verify(orderEventListener).orderOpened(testTime, new Price(10));
    }

    /**
     * The spread is not added to the current candles open and close {@link Price} when considering opening pending sell
     * orders.
     */
    @Test
    public void spreadIsNotConsideredWhenOpeningPendingSellOrders() {
        testOrder.setType(SELL).setEntryPrice(new Price(10));
        cut.sendOrder(testOrder.toImmutablePendingOrder(), orderEventListener);

        // 9+2 = 10 -> would be to high but the spread is not considered.
        testCandleStick.setSpread(2).setOpen(new Price(9)).setClose(new Price(11));
        cut.newData(testCandleStick.toImmutableFullMarketData());

        verify(orderEventListener).orderOpened(testTime, new Price(10));
    }

    /**
     * The cut does not generate events for canceled pending orders.
     */
    @Test
    public void canceledPendingOrdersAreNotOpened() {
        testOrder.setEntryPrice(new Price(10));
        testCandleStick.setOpen(new Price(9)).setClose(new Price(11));

        final Either<Failed, OrderManagement> orderResult = cut.sendOrder(testOrder.toImmutablePendingOrder(), orderEventListener);
        assertThat(orderResult).hasRight();
        orderResult.getRight().closeOrCancelOrder();
        cut.newData(testCandleStick.toImmutableFullMarketData());

        verifyNoMoreInteractions(orderEventListener);
    }

    // ///////////////////
    // / Order closing ///
    // ///////////////////

    /**
     * When the take profit of an order is between the open and close {@link Price} of the current {@link CandleStick},
     * the order is closed.
     */
    @Test
    public void closesTradesWhenTakeProfitIsBetweenOpenAndClosePrice() {
        testOrder.setCloseConditions(testCloseConditions.setTakeProfit(new Price(100)));

        openPendingOrder();

        testCandleStick.setOpen(new Price(90)).setClose(new Price(110));
        cut.newData(testCandleStick.toImmutableFullMarketData());

        verify(orderEventListener).orderClosed(testTime, new Price(100));
    }

    /**
     * When the stop loose of an order is between the open and close {@link Price} of the current {@link CandleStick},
     * the order is closed.
     */
    @Test
    public void closesTradesWhenStopLooseIsBetweenOpenAndClosePrice() {
        testOrder.setCloseConditions(testCloseConditions.setStopLoose(new Price(100)));

        openPendingOrder();

        testCandleStick.setOpen(new Price(90)).setClose(new Price(100));
        cut.newData(testCandleStick.toImmutableFullMarketData());

        verify(orderEventListener).orderClosed(testTime, new Price(100));
    }

    /**
     * The spread is not added to the current candles open and close {@link Price} when considering closing buy orders.
     */
    @Test
    public void spreadIsNotConsideredWhenClosingBuyOrders() {
        testOrder.setType(BUY).setCloseConditions(testCloseConditions.setStopLoose(new Price(100)));

        openPendingOrder();

        // 96 + 5 == 101 but spread is ignored
        testCandleStick.setSpread(new Price(5)).setOpen(new Price(96)).setClose(new Price(110));
        cut.newData(testCandleStick.toImmutableFullMarketData());

        verify(orderEventListener).orderClosed(testTime, new Price(100));
    }

    /**
     * The spread is added to the current candles open and close {@link Price} when considering closing sell orders.
     */
    @Test
    public void spreadIsConsideredWhenClosingSellOrders() {
        testOrder.setType(SELL).setCloseConditions(testCloseConditions.setStopLoose(new Price(100)));

        openPendingOrder();

        // 96 + 5 == 101 -> stop loose not meat
        testCandleStick.setSpread(new Price(5)).setOpen(new Price(96)).setClose(new Price(110));
        cut.newData(testCandleStick.toImmutableFullMarketData());

        verifyNoMoreInteractions(orderEventListener);
    }

    /**
     * It is possible to change the close conditions of orders.
     */
    @Test
    public void closeConditionsCanBeChanged() {
        testOrder.setCloseConditions(testCloseConditions.setTakeProfit(new Price(100)));

        final OrderManagement orderManagement = openPendingOrder();
        orderManagement.changeCloseConditionsOfOrder(testCloseConditions.setTakeProfit(new Price(200)).toImmutable());

        // old close conditions do not apply
        testCandleStick.setOpen(new Price(90)).setClose(new Price(110));
        cut.newData(testCandleStick.toImmutableFullMarketData());
        verifyNoMoreInteractions(orderEventListener);

        // new close conditions apply
        testCandleStick.setOpen(new Price(210)).setClose(new Price(190));
        cut.newData(testCandleStick.toImmutableFullMarketData());
        verify(orderEventListener).orderClosed(testTime, new Price(200));
    }

    /**
     * When a trade is manually closed by the expert advisor, no events are generated when the close conditions are met.
     */
    @Test
    public void manuallyClosedTradesDoNotGenerateCloseEvents() {
        testOrder.setCloseConditions(testCloseConditions.setStopLoose(new Price(100)));

        final OrderManagement orderManagement = openPendingOrder();

        testCandleStick.setOpen(new Price(90)).setClose(new Price(110));
        orderManagement.closeOrCancelOrder();
        cut.newData(testCandleStick.toImmutableFullMarketData());

        verifyNoMoreInteractions(orderEventListener);
    }

    // //////////
    // / Misc ///
    // //////////

    /**
     * Multiple orders can be handled at the same time.
     */
    @Test
    public void multipleOrdersCanBeManagedInParallel() {
        testOrder.setEntryPrice(new Price(10)).setCloseConditions(testCloseConditions.setTakeProfit(new Price(20)));
        cut.sendOrder(testOrder.toImmutablePendingOrder(), orderEventListener);

        testOrder.setEntryPrice(new Price(30)).setCloseConditions(testCloseConditions.setTakeProfit(new Price(40)));
        cut.sendOrder(testOrder.toImmutablePendingOrder(), otherOrderEventListener);

        testCandleStick.setOpen(new Price(9)).setClose(new Price(11));
        cut.newData(testCandleStick.toImmutableFullMarketData());
        verify(orderEventListener).orderOpened(testTime, new Price(10));
        verifyNoMoreInteractions(otherOrderEventListener);

        testCandleStick.setOpen(new Price(19)).setClose(new Price(30));
        cut.newData(testCandleStick.toImmutableFullMarketData());
        verify(orderEventListener).orderClosed(testTime, new Price(20));
        verify(otherOrderEventListener).orderOpened(testTime, new Price(30));

        testCandleStick.setOpen(new Price(35)).setClose(new Price(45));
        cut.newData(testCandleStick.toImmutableFullMarketData());
        verifyNoMoreInteractions(orderEventListener);
        verify(otherOrderEventListener).orderClosed(testTime, new Price(40));
    }

    private OrderManagement openPendingOrder() {
        testOrder.setEntryPrice(new Price(10));
        testCandleStick.setOpen(new Price(9)).setClose(new Price(11));

        final Either<Failed, OrderManagement> orderResult = cut.sendOrder(testOrder.toImmutablePendingOrder(), orderEventListener);
        cut.newData(testCandleStick.toImmutableFullMarketData());
        // just to make verifyNoMoreInteractions work
        verify(orderEventListener).orderOpened(testTime, new Price(10));

        return orderResult.getRight();
    }
}
