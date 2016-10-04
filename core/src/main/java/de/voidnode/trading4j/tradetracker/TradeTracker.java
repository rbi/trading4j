package de.voidnode.trading4j.tradetracker;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.Either;
import de.voidnode.trading4j.api.ExpertAdvisor;
import de.voidnode.trading4j.api.Failed;
import de.voidnode.trading4j.api.MarketDataListener;
import de.voidnode.trading4j.api.OrderEventListener;
import de.voidnode.trading4j.api.OrderManagement;
import de.voidnode.trading4j.domain.TimeFrame.M1;
import de.voidnode.trading4j.domain.marketdata.CandleStick;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;
import de.voidnode.trading4j.domain.orders.CloseConditions;
import de.voidnode.trading4j.domain.trades.BasicCompletedTrade;
import de.voidnode.trading4j.domain.trades.TradeEvent;

import static de.voidnode.trading4j.domain.trades.TradeEventType.CLOSE_CONDITIONS_CHANGED;
import static de.voidnode.trading4j.domain.trades.TradeEventType.PENDING_ORDER_CANCELD;
import static de.voidnode.trading4j.domain.trades.TradeEventType.PENDING_ORDER_OPENED;
import static de.voidnode.trading4j.domain.trades.TradeEventType.PENDING_ORDER_PLACED;
import static de.voidnode.trading4j.domain.trades.TradeEventType.TRADE_CLOSED;

/**
 * Keeps track and notifies a listener of all complete trades that are executed at a broker.
 *
 * @author Raik Bieniek
 * @param <C>
 *            The concrete type of data passed as input.
 * @param <PO>
 *            The kind of {@link BasicPendingOrder} that is passed to this instance.
 * @param <CT>
 *            The kind of {@link BasicCompletedTrade} that should be produced.
 */
public abstract class TradeTracker<C extends CandleStick<M1>, PO extends BasicPendingOrder, CT extends BasicCompletedTrade>
        implements Broker<PO>, MarketDataListener<C> {

    private final Broker<PO> broker;
    private final Supplier<Instant> currentTime;

    private TradeEventListener<CT> listener;
    private C lastCandleStick;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param broker
     *            A broker that can execute the orders.
     * @param currentTime
     *            A supplier that returns the current time.
     */
    public TradeTracker(final Broker<PO> broker, final Supplier<Instant> currentTime) {
        this.broker = broker;
        this.currentTime = currentTime;
    }

    @Override
    public void newData(final C candleStick) {
        this.lastCandleStick = candleStick;
    }

    /**
     * Sets an listener for backtest events.
     * 
     * <p>
     * Only a single listener can be registered at a time. Calling this method a second time will overwritte the
     * listener registered at the first time.
     * </p>
     * 
     * @param listener
     *            The listener to inform on backtest events.
     */
    public void setEventListener(final TradeEventListener<CT> listener) {
        this.listener = listener;
    }

    @Override
    public Either<Failed, OrderManagement> sendOrder(final PO order, final OrderEventListener eventListener) {
        final ObservingOrderEventListener observer = new ObservingOrderEventListener(eventListener, order);
        return broker.sendOrder(order, observer).mapLeft(orig -> {
            observer.placingOrderFailed(orig);
            return orig;
        }).mapRight(orig -> {
            observer.setOrderManagement(orig);
            return observer;
        });
    }

    /**
     * Creates a concrete completed trade instance.
     * 
     * @param originalOrder
     *            The original pending order that was placed.
     * @param lastCandleStick
     *            The last candle stick that was received.
     * @param events
     *            All events that where captured for this trade.
     * @return The concrete completed trade.
     */
    protected abstract CT createCompletedTrade(final PO originalOrder, final C lastCandleStick,
            final List<TradeEvent> events);

    /**
     * Observes the conversation between an {@link ExpertAdvisor} and the broker and notifies when a trade was
     * completed.
     */
    private class ObservingOrderEventListener implements OrderEventListener, OrderManagement {

        private final OrderEventListener orderEventListener;
        private OrderManagement orderManagement;

        private final PO order;
        private final List<TradeEvent> events;
        private CloseConditions currentCloseConditions;

        ObservingOrderEventListener(final OrderEventListener orderEventListener, final PO order) {
            this.orderEventListener = orderEventListener;
            this.order = order;
            this.currentCloseConditions = order.getCloseConditions();
            this.events = new LinkedList<>();
            events.add(new TradeEvent(PENDING_ORDER_PLACED, currentTime.get(),
                    "expert advisor placed the pending order", order.getEntryPrice(),
                    order.getCloseConditions().getTakeProfit(), order.getCloseConditions().getStopLoose()));
        }

        public void setOrderManagement(final OrderManagement orderManagement) {
            this.orderManagement = orderManagement;
        }

        public void placingOrderFailed(final Failed failure) {
            events.add(new TradeEvent(PENDING_ORDER_CANCELD, currentTime.get(),
                    "broker failed placing the pending order: " + failure));
            completeTrade();
        }

        @Override
        public void orderOpened(final Instant time, final Price price) {
            events.add(new TradeEvent(PENDING_ORDER_OPENED, time, "broker opened the pending order", price));
            orderEventListener.orderOpened(time, price);
        }

        @Override
        public void orderClosed(final Instant time, final Price price) {
            events.add(new TradeEvent(TRADE_CLOSED, time, "broker closed the order", price));
            orderEventListener.orderClosed(time, price);
            completeTrade();
        }

        @Override
        public void closeOrCancelOrder() {
            orderManagement.closeOrCancelOrder();
            if (wasOpened()) {
                events.add(new TradeEvent(TRADE_CLOSED, currentTime.get(), "expert advisor closed active trade",
                        lastCandleStick.getClose()));
            } else {
                events.add(new TradeEvent(PENDING_ORDER_CANCELD, currentTime.get(),
                        "expert advisor canceled pending order", lastCandleStick.getClose()));
            }
            completeTrade();
        }

        @Override
        public Optional<Failed> changeCloseConditionsOfOrder(final CloseConditions conditions) {
            final Optional<Failed> failed = orderManagement.changeCloseConditionsOfOrder(conditions);

            final Instant time = currentTime.get();
            events.add(new TradeEvent(CLOSE_CONDITIONS_CHANGED, time, "expert advisor changed close conditions",
                    conditions));
            if (failed.isPresent()) {
                events.add(new TradeEvent(CLOSE_CONDITIONS_CHANGED, time,
                        "broker faild to changed close conditions of expert advisor: " + failed.get(),
                        currentCloseConditions));
            } else {
                currentCloseConditions = conditions;
            }

            return failed;
        }

        private boolean wasOpened() {
            return events.stream().filter((ev) -> ev.getType().equals(PENDING_ORDER_OPENED)).findFirst().isPresent();
        }

        private void completeTrade() {
            listener.tradeCompleted(createCompletedTrade(order, lastCandleStick, events));
        }
    }
}
