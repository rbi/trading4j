package de.voidnode.trading4j.strategyexpertadvisor;

import java.time.Instant;
import java.util.Optional;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.Failed;
import de.voidnode.trading4j.domain.TimeFrame.M1;
import de.voidnode.trading4j.domain.marketdata.FullMarketData;
import de.voidnode.trading4j.domain.marketdata.MutableFullMarketData;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;

import static de.voidnode.trading4j.domain.VolumeUnit.LOT;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Checks if {@link StrategyExpertAdvisor} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class StrategyExpertAdvisorTest {

    private static final FullMarketData<M1> EXEMPLARY_CANDLE_STICK = new MutableFullMarketData<M1>()
            .setTime(Instant.ofEpochSecond(1)).setOpen(1.0).setHigh(1.0).setLow(1.0).setClose(1.0)
            .setSpread(new Price(20)).setVolume(1, LOT).setTickCount(100).toImmutableFullMarketData();

    private static final Price EXEMPLARY_PRICE = new Price(42.0);
    private static final Instant EXEMPLARY_TIME = Instant.ofEpochMilli(5486);

    @Mock
    private TradingStrategy<FullMarketData<M1>> calculator;

    @Mock
    private PendingOrderCreator creator;

    @Mock
    private PendingOrderManager orderManager;

    @Mock
    private TradeManager tradeManager;

    @InjectMocks
    private StrategyExpertAdvisor<FullMarketData<M1>> cut;

    @Mock
    private Order exemplaryOrder1;

    @Mock
    private Order exemplaryOrder2;

    /**
     * Sets up the default behavior of the mocks.
     */
    @Before
    public void setUpDefaultBehaviorOfMocks() {
        // simulate the creation of a pending order
        when(creator.checkMarketEntry(any())).thenReturn(Optional.of(exemplaryOrder1));
        // simulate a non-changed pending order
        when(orderManager.manageOrder(any(), any())).thenAnswer((a) -> Optional.of(a.getArguments()[0]));
        // simulate a non-changed trade
        when(tradeManager.manageTrade(any())).thenAnswer((a) -> Optional.of(a.getArguments()[0]));
    }

    /**
     * The cut triggers the update for potential order values when new market data arrives.
     */
    @Test
    public void triggersUpdateOfValuesForPotentialPendingOrders() {
        cut.newData(EXEMPLARY_CANDLE_STICK);

        verify(calculator).update(EXEMPLARY_CANDLE_STICK);
    }

    /**
     * The cut tries to create new pending orders using the {@link PendingOrderCreator}.
     */
    @Test
    public void triesToCreateNewPendingOrdersWhenNoOrderIsActive() {
        cut.newData(EXEMPLARY_CANDLE_STICK);

        verify(creator).checkMarketEntry(cut);
    }

    /**
     * When creating a {@link BasicPendingOrder} succeed the resulting {@link BasicPendingOrder} instance should be
     * managed.
     */
    @Test
    public void resultingPendingOrderShouldBeManagedWhenPendingOrderWasCreated() {
        cut.newData(EXEMPLARY_CANDLE_STICK);
        cut.newData(EXEMPLARY_CANDLE_STICK);
        cut.newData(EXEMPLARY_CANDLE_STICK);

        final InOrder inOrder = inOrder(creator, orderManager);
        inOrder.verify(creator).checkMarketEntry(cut);
        inOrder.verify(orderManager, times(2)).manageOrder(exemplaryOrder1, cut);

        verifyNoMoreInteractions(creator, orderManager, tradeManager);
    }

    /**
     * When a created pending order was rejected by the broker, the cut tries to create a new pending order.
     */
    @Test
    public void triesToCreateNewPendingOrderWhenPendingOrderWasRejected() {
        cut.newData(EXEMPLARY_CANDLE_STICK);
        cut.orderRejected(new Failed("Simulated rejected order."));
        cut.newData(EXEMPLARY_CANDLE_STICK);

        verify(creator, times(2)).checkMarketEntry(cut);

        verifyNoMoreInteractions(creator, orderManager, tradeManager);
    }

    /**
     * When an active {@link BasicPendingOrder} was canceled the cut should try to create a new one.
     */
    @Test
    public void triesToCreateNewPendingOrderWhenPendingOrderWasCanceled() {
        when(orderManager.manageOrder(exemplaryOrder1, cut)).thenReturn(Optional.empty());

        cut.newData(EXEMPLARY_CANDLE_STICK);
        cut.newData(EXEMPLARY_CANDLE_STICK);
        cut.newData(EXEMPLARY_CANDLE_STICK);

        final InOrder inOrder = inOrder(creator, orderManager);
        inOrder.verify(creator).checkMarketEntry(cut);
        inOrder.verify(orderManager).manageOrder(exemplaryOrder1, cut);
        inOrder.verify(creator).checkMarketEntry(cut);

        verifyNoMoreInteractions(creator, orderManager, tradeManager);
    }

    /**
     * When the {@link PendingOrderManager} changed the {@link BasicPendingOrder}, the new {@link BasicPendingOrder}
     * should be managed.
     */
    @Test
    public void managesNewPendingOrderWhenPendingOrderChanged() {
        when(orderManager.manageOrder(exemplaryOrder1, cut)).thenReturn(Optional.of(exemplaryOrder2));

        cut.newData(EXEMPLARY_CANDLE_STICK);
        cut.newData(EXEMPLARY_CANDLE_STICK);
        cut.newData(EXEMPLARY_CANDLE_STICK);

        final InOrder inOrder = inOrder(creator, orderManager);
        inOrder.verify(creator).checkMarketEntry(cut);
        inOrder.verify(orderManager).manageOrder(exemplaryOrder1, cut);
        inOrder.verify(orderManager).manageOrder(exemplaryOrder2, cut);

        verifyNoMoreInteractions(creator, orderManager, tradeManager);
    }

    /**
     * When the {@link Broker} opened the pending order, the trade should be managed.
     */
    @Test
    public void managesTradeWhenPendingOrderWasOpened() {
        cut.newData(EXEMPLARY_CANDLE_STICK);
        cut.newData(EXEMPLARY_CANDLE_STICK);
        cut.orderOpened(EXEMPLARY_TIME, EXEMPLARY_PRICE);
        cut.newData(EXEMPLARY_CANDLE_STICK);

        final InOrder inOrder = inOrder(creator, orderManager, tradeManager);
        inOrder.verify(creator).checkMarketEntry(cut);
        inOrder.verify(orderManager).manageOrder(exemplaryOrder1, cut);
        inOrder.verify(tradeManager).manageTrade(exemplaryOrder1);

        verifyNoMoreInteractions(creator, orderManager, tradeManager);
    }

    /**
     * When the {@link TradeManager} closed a trade, new {@link BasicPendingOrder}s should be created.
     */
    @Test
    public void triesToCreateNewPendingOrderWhenTradeWasCanceled() {
        when(tradeManager.manageTrade(exemplaryOrder1)).thenReturn(Optional.empty());

        cut.newData(EXEMPLARY_CANDLE_STICK);
        cut.orderOpened(EXEMPLARY_TIME, EXEMPLARY_PRICE);
        cut.newData(EXEMPLARY_CANDLE_STICK);
        cut.newData(EXEMPLARY_CANDLE_STICK);

        final InOrder inOrder = inOrder(creator, orderManager, tradeManager);
        inOrder.verify(creator).checkMarketEntry(cut);
        inOrder.verify(tradeManager).manageTrade(exemplaryOrder1);
        inOrder.verify(creator).checkMarketEntry(cut);

        verifyNoMoreInteractions(creator, orderManager, tradeManager);
    }

    /**
     * When the {@link TradeManager} changed the prices of a trade, the new trade should be managed.
     */
    @Test
    public void managesNewTradeWhenTradeWasChanged() {
        when(tradeManager.manageTrade(exemplaryOrder1)).thenReturn(Optional.of(exemplaryOrder2));

        cut.newData(EXEMPLARY_CANDLE_STICK);
        cut.orderOpened(EXEMPLARY_TIME, EXEMPLARY_PRICE);
        cut.newData(EXEMPLARY_CANDLE_STICK);
        cut.newData(EXEMPLARY_CANDLE_STICK);

        final InOrder inOrder = inOrder(creator, orderManager, tradeManager);
        inOrder.verify(creator).checkMarketEntry(cut);
        inOrder.verify(tradeManager).manageTrade(exemplaryOrder1);
        inOrder.verify(tradeManager).manageTrade(exemplaryOrder2);

        verifyNoMoreInteractions(creator, orderManager, tradeManager);
    }

    /**
     * When a the {@link Broker} closed a trade, a new {@link BasicPendingOrder} should be created.
     */
    @Test
    public void triesToCreateNewPendingOrderWhenTradeWasChanged() {
        cut.newData(EXEMPLARY_CANDLE_STICK);
        cut.orderOpened(EXEMPLARY_TIME, EXEMPLARY_PRICE);
        cut.newData(EXEMPLARY_CANDLE_STICK);
        cut.orderClosed(EXEMPLARY_TIME, EXEMPLARY_PRICE);
        cut.newData(EXEMPLARY_CANDLE_STICK);

        final InOrder inOrder = inOrder(creator, orderManager, tradeManager);
        inOrder.verify(creator).checkMarketEntry(cut);
        inOrder.verify(tradeManager).manageTrade(exemplaryOrder1);
        inOrder.verify(creator).checkMarketEntry(cut);

        verifyNoMoreInteractions(creator, orderManager, tradeManager);
    }
}