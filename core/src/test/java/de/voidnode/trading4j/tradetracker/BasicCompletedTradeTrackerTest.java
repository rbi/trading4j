package de.voidnode.trading4j.tradetracker;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Arrays.asList;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.Either;
import de.voidnode.trading4j.api.Failed;
import de.voidnode.trading4j.api.OrderEventListener;
import de.voidnode.trading4j.api.OrderManagement;
import de.voidnode.trading4j.domain.TimeFrame.M1;
import de.voidnode.trading4j.domain.marketdata.CandleStick;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;
import de.voidnode.trading4j.domain.orders.CloseConditions;
import de.voidnode.trading4j.domain.orders.MutableCloseConditions;
import de.voidnode.trading4j.domain.orders.MutablePendingOrder;
import de.voidnode.trading4j.domain.trades.BasicCompletedTrade;
import de.voidnode.trading4j.domain.trades.TradeEvent;

import static de.voidnode.trading4j.domain.VolumeUnit.LOT;
import static de.voidnode.trading4j.domain.orders.ExecutionCondition.LIMIT;
import static de.voidnode.trading4j.domain.orders.OrderType.BUY;
import static de.voidnode.trading4j.domain.orders.OrderType.SELL;
import static de.voidnode.trading4j.domain.trades.TradeEventType.CLOSE_CONDITIONS_CHANGED;
import static de.voidnode.trading4j.domain.trades.TradeEventType.PENDING_ORDER_CANCELD;
import static de.voidnode.trading4j.domain.trades.TradeEventType.PENDING_ORDER_OPENED;
import static de.voidnode.trading4j.domain.trades.TradeEventType.PENDING_ORDER_PLACED;
import static de.voidnode.trading4j.domain.trades.TradeEventType.TRADE_CLOSED;
import static de.voidnode.trading4j.testutils.assertions.Assertions.assertThat;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests if {@link BasicCompletedTradeTracker} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class BasicCompletedTradeTrackerTest {

    /**
     * The reason of basic completed trades is not compared.
     */
    private static final String REASON_NOT_COMPARED = "";

    private static final Instant BROKER_TIME = Instant.EPOCH;
    private static final Instant LOCAL_TIME = Instant.EPOCH.plusMillis(5960);

    // the cut and its dependencies

    @Mock
    private Broker<BasicPendingOrder> broker;

    @Mock
    private TradeEventListener<BasicCompletedTrade> backtestEventLlistener;

    @Mock
    private Supplier<Instant> currentTime;

    private BasicCompletedTradeTracker<CandleStick<M1>> cut;

    // misc

    @Mock
    private OrderEventListener someOrderEventListener;

    @Mock
    private OrderManagement someOrderManagement;

    @Captor
    private ArgumentCaptor<OrderEventListener> orderEventListenerCaptor;

    // test data

    @Mock
    private Failed someFailure;

    private MutablePendingOrder someOrder = new MutablePendingOrder().setType(BUY).setExecutionCondition(LIMIT)
            .setVolume(10, LOT).setEntryPrice(new Price(10))
            .setCloseConditions(new MutableCloseConditions().setTakeProfit(new Price(20)).setStopLoose(new Price(30)));

    private Price somePrice = new Price(5381);

    /**
     * Sets up the class under test and the mocks.
     */
    @Before
    public void setUpCutAndMocks() {
        cut = new BasicCompletedTradeTracker<>(broker, currentTime);
        cut.setEventListener(backtestEventLlistener);

        when(currentTime.get()).thenReturn(LOCAL_TIME);
        when(broker.sendOrder(any(), any())).thenReturn(Either.withRight(someOrderManagement));
    }

    /**
     * All pending orders passed to the cut are passed through to the broker. All events received from the broker are
     * passed through too.
     */
    @Test
    public void passesThroughOrdersAndEventsToAndFromExecutingBroker() {
        final BasicPendingOrder order = someOrder.toImmutableBasicPendingOrder();
        cut.sendOrder(order, someOrderEventListener);

        // The cut may wrap the OrderEventListener to intercept the events.
        verify(broker).sendOrder(eq(order), orderEventListenerCaptor.capture());

        // events

        orderEventListenerCaptor.getValue().orderOpened(BROKER_TIME, somePrice);
        verify(someOrderEventListener).orderOpened(BROKER_TIME, somePrice);

        orderEventListenerCaptor.getValue().orderClosed(BROKER_TIME, new Price(42));
        verify(someOrderEventListener).orderClosed(BROKER_TIME, new Price(42));
    }

    /**
     * Failed orders are passed through.
     */
    @Test
    public void failedOrdersArePassedThrough() {
        when(broker.sendOrder(any(), any())).thenReturn(Either.withLeft(someFailure));

        final Either<Failed, OrderManagement> passedThrough = cut.sendOrder(someOrder.toImmutableBasicPendingOrder(),
                someOrderEventListener);

        assertThat(passedThrough).hasLeftEqualTo(someFailure);
    }

    /**
     * Trades closed by the broker produce completed trades events that are passed to the {@link TradeEventListener}.
     */
    @Test
    public void completedTradesAreSendToListenerWhenBrokerClosedATrade() {
        final BasicPendingOrder order = someOrder.toImmutableBasicPendingOrder();

        cut.sendOrder(order, someOrderEventListener);
        verify(broker).sendOrder(eq(order), orderEventListenerCaptor.capture());

        orderEventListenerCaptor.getValue().orderOpened(BROKER_TIME, new Price(40));
        orderEventListenerCaptor.getValue().orderClosed(BROKER_TIME, new Price(50));

        verify(backtestEventLlistener).tradeCompleted(basicCompleteTradeMatcher(new BasicCompletedTrade(BUY, LIMIT,
                asList(new TradeEvent(PENDING_ORDER_PLACED, LOCAL_TIME, REASON_NOT_COMPARED, new Price(10),
                        new Price(20), new Price(30)), //
                        new TradeEvent(PENDING_ORDER_OPENED, BROKER_TIME, REASON_NOT_COMPARED, new Price(40)), //
                        new TradeEvent(TRADE_CLOSED, BROKER_TIME, REASON_NOT_COMPARED, new Price(50))))));
    }

    /**
     * Trades closed by the expert adviser produce completed trades events that are passed to the
     * {@link TradeEventListener}.
     */
    @Test
    public void completedTradesAreSendToListenerWhenExpertAdvisorClosedATrade() {
        someOrder.setType(SELL);
        final BasicPendingOrder order = someOrder.toImmutableBasicPendingOrder();

        final OrderManagement orderManagement = cut.sendOrder(order, someOrderEventListener).getRight();
        verify(broker).sendOrder(eq(order), orderEventListenerCaptor.capture());

        orderEventListenerCaptor.getValue().orderOpened(BROKER_TIME, new Price(60));
        // on manually closed trades, the cut takes the last close price.
        cut.newData(new CandleStick<>(somePrice, somePrice, somePrice, new Price(70)));
        orderManagement.closeOrCancelOrder();

        verify(backtestEventLlistener).tradeCompleted(basicCompleteTradeMatcher(new BasicCompletedTrade(SELL, LIMIT,
                asList(new TradeEvent(PENDING_ORDER_PLACED, LOCAL_TIME, REASON_NOT_COMPARED, new Price(10),
                        new Price(20), new Price(30)), //
                        new TradeEvent(PENDING_ORDER_OPENED, BROKER_TIME, REASON_NOT_COMPARED, new Price(60)), //
                        new TradeEvent(TRADE_CLOSED, LOCAL_TIME, REASON_NOT_COMPARED, new Price(70))))));
    }

    /**
     * Pending orders canceled by the expert adviser produce completed trades events that are passed to the
     * {@link TradeEventListener}.
     */
    @Test
    public void completedTradesAreSendToListenerWhenPendingOrdersAreCanceled() {
        someOrder.setType(SELL);
        final BasicPendingOrder order = someOrder.toImmutableBasicPendingOrder();

        cut.newData(new CandleStick<>(new Price(200), new Price(300), new Price(400), new Price(500)));
        final OrderManagement orderManagement = cut.sendOrder(order, someOrderEventListener).getRight();
        orderManagement.closeOrCancelOrder();

        verify(backtestEventLlistener).tradeCompleted(basicCompleteTradeMatcher(new BasicCompletedTrade(SELL, LIMIT,
                asList(new TradeEvent(PENDING_ORDER_PLACED, LOCAL_TIME, REASON_NOT_COMPARED, new Price(10),
                        new Price(20), new Price(30)), //
                        new TradeEvent(PENDING_ORDER_CANCELD, LOCAL_TIME, REASON_NOT_COMPARED, new Price(500))))));
    }

    /**
     * Pending orders that failed to being placed are send to {@link TradeEventListener}.
     */
    @Test
    public void completedTradesAreSendToListenerWhenPendingOrdersCouldNotBePlaced() {
        when(broker.sendOrder(any(), any())).thenReturn(Either.withLeft(someFailure));

        cut.sendOrder(someOrder.toImmutableBasicPendingOrder(), someOrderEventListener);

        verify(backtestEventLlistener).tradeCompleted(basicCompleteTradeMatcher(new BasicCompletedTrade(BUY, LIMIT,
                asList(new TradeEvent(PENDING_ORDER_PLACED, LOCAL_TIME, REASON_NOT_COMPARED, new Price(10),
                        new Price(20), new Price(30)), //
                        new TradeEvent(PENDING_ORDER_CANCELD, LOCAL_TIME, REASON_NOT_COMPARED)))));
    }

    /**
     * When close conditions of a pending order or a trade are changed this is logged.
     */
    @Test
    public void changedCloseConditionsAreLogged() {
        final BasicPendingOrder order = someOrder.toImmutableBasicPendingOrder();
        when(someOrderManagement.changeCloseConditionsOfOrder(any()))
                .thenReturn(Optional.of(new Failed("some reason")));

        final OrderManagement orderManagement = cut.sendOrder(order, someOrderEventListener).getRight();
        verify(broker).sendOrder(eq(order), orderEventListenerCaptor.capture());

        orderEventListenerCaptor.getValue().orderOpened(BROKER_TIME, new Price(40));
        orderManagement.changeCloseConditionsOfOrder(new CloseConditions(new Price(80), new Price(90)));
        orderEventListenerCaptor.getValue().orderClosed(BROKER_TIME, new Price(50));

        verify(backtestEventLlistener).tradeCompleted(basicCompleteTradeMatcher(new BasicCompletedTrade(BUY, LIMIT,
                asList(new TradeEvent(PENDING_ORDER_PLACED, LOCAL_TIME, REASON_NOT_COMPARED, new Price(10),
                        new Price(20), new Price(30)),
                        new TradeEvent(PENDING_ORDER_OPENED, BROKER_TIME, REASON_NOT_COMPARED, new Price(40)),
                        // expert advisor changed the close conditions
                        new TradeEvent(CLOSE_CONDITIONS_CHANGED, LOCAL_TIME, REASON_NOT_COMPARED, new Price(80),
                                new Price(90)),
                        // broker failed to execute this change, therefore it is assumed that the old close conditions
                        // are still active.
                        new TradeEvent(CLOSE_CONDITIONS_CHANGED, LOCAL_TIME, REASON_NOT_COMPARED, new Price(20),
                                new Price(30)),
                        new TradeEvent(TRADE_CLOSED, BROKER_TIME, REASON_NOT_COMPARED, new Price(50))))));
    }

    /**
     * The cut can handle multiple paralel trades.
     */
    @Test
    public void canHandleMultipleParallelTrades() {
        final BasicPendingOrder order = someOrder.toImmutableBasicPendingOrder();

        cut.sendOrder(order, someOrderEventListener);
        cut.sendOrder(order, someOrderEventListener);
        verify(broker, times(2)).sendOrder(eq(order), orderEventListenerCaptor.capture());

        final OrderEventListener firstOrder = orderEventListenerCaptor.getAllValues().get(0);
        final OrderEventListener secondOrder = orderEventListenerCaptor.getAllValues().get(1);

        firstOrder.orderOpened(BROKER_TIME, new Price(5000));
        secondOrder.orderOpened(BROKER_TIME, new Price(600));
        firstOrder.orderClosed(BROKER_TIME, new Price(6000));
        secondOrder.orderClosed(BROKER_TIME, new Price(500));

        verify(backtestEventLlistener).tradeCompleted(basicCompleteTradeMatcher(new BasicCompletedTrade(BUY, LIMIT,
                asList(new TradeEvent(PENDING_ORDER_PLACED, LOCAL_TIME, REASON_NOT_COMPARED, new Price(10),
                        new Price(20), new Price(30)), //
                        new TradeEvent(PENDING_ORDER_OPENED, BROKER_TIME, REASON_NOT_COMPARED, new Price(5000)), //
                        new TradeEvent(TRADE_CLOSED, BROKER_TIME, REASON_NOT_COMPARED, new Price(6000))))));
        verify(backtestEventLlistener).tradeCompleted(basicCompleteTradeMatcher(new BasicCompletedTrade(BUY, LIMIT,
                asList(new TradeEvent(PENDING_ORDER_PLACED, LOCAL_TIME, REASON_NOT_COMPARED, new Price(10),
                        new Price(20), new Price(30)), //
                        new TradeEvent(PENDING_ORDER_OPENED, BROKER_TIME, REASON_NOT_COMPARED, new Price(600)), //
                        new TradeEvent(TRADE_CLOSED, BROKER_TIME, REASON_NOT_COMPARED, new Price(500))))));
    }

    private BasicCompletedTrade basicCompleteTradeMatcher(final BasicCompletedTrade trade) {
        return argThat(new BasicCompletedTradeMatcher(trade));
    }

    /**
     * Checks that two {@link BasicCompletedTrade} are equal expect for the reason {@link String} of their
     * {@link TradeEvent}s.
     */
    private static class BasicCompletedTradeMatcher extends TypeSafeMatcher<BasicCompletedTrade> {

        private final BasicCompletedTrade trade;

        BasicCompletedTradeMatcher(final BasicCompletedTrade trade) {
            this.trade = trade;
        }

        @Override
        protected boolean matchesSafely(final BasicCompletedTrade item) {
            if (trade.getType() != item.getType() || trade.getExecutionCondition() != item.getExecutionCondition()
                    || trade.getEvents().size() != item.getEvents().size()) {
                return false;
            }
            for (int i = 0; i < trade.getEvents().size(); i++) {
                if (!matches(trade.getEvents().get(i), item.getEvents().get(i))) {
                    return false;
                }
            }
            return true;
        }

        private boolean matches(final TradeEvent should, final TradeEvent is) {
            if (!(should.getType().equals(is.getType()) && should.getTime().equals(is.getTime()))) {
                return false;
            }
            return should.getPrice().equals(is.getPrice())
                    && should.getCloseConditions().equals(is.getCloseConditions());
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText(
                    "The basic completed trade should be mostly equal to " + trade + " but the reason may differ.");
        }
    }
}
