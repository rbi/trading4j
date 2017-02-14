package de.voidnode.trading4j.functionality.broker;

import java.time.Instant;
import java.util.Optional;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.Failed;
import de.voidnode.trading4j.api.OrderEventListener;
import de.voidnode.trading4j.api.OrderManagement;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;
import de.voidnode.trading4j.domain.orders.CloseConditions;

/**
 * Allows only a single open {@link BasicPendingOrder} or trade at a time.
 * 
 * <p>
 * {@link BasicPendingOrder}s that are placed while others are open will return {@link Failed}.
 * </p>
 * 
 * @author Raik Bieniek
 */
public class SingleTradeBroker implements Broker<BasicPendingOrder> {

    private static final OrderManagement NO_OP_ORDER_MANAGEMENT = new NoOpOrderManagement();
    private static final Failed FAILED =
            new Failed("There is another active pending order or trade and only one is allowed at a time.");

    private final Broker<BasicPendingOrder> broker;
    private TradeUnblocker tradeUnblocker;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param broker
     *            The broker used to execute non-blocked {@link BasicPendingOrder}s.
     */
    public SingleTradeBroker(final Broker<BasicPendingOrder> broker) {
        this.broker = broker;
    }

    @Override
    public OrderManagement sendOrder(final BasicPendingOrder order,
            final OrderEventListener eventListener) {
        if (tradeUnblocker != null) {
            eventListener.orderRejected(FAILED);
            return NO_OP_ORDER_MANAGEMENT;
        }

        this.tradeUnblocker = new TradeUnblocker(eventListener);
        final OrderManagement originalOrderManagement = broker.sendOrder(order, tradeUnblocker);

        if (tradeUnblocker == null) {
            // The original broker has already rejected the trade.
            return NO_OP_ORDER_MANAGEMENT;
        } else {
            tradeUnblocker.setOrderManagement(originalOrderManagement);
            return tradeUnblocker;
        }
    }

    /**
     * Unblocks trading when the order or trade is closed or canceled.
     */
    private class TradeUnblocker implements OrderEventListener, OrderManagement {

        private final OrderEventListener listener;
        private OrderManagement orderManagement;

        TradeUnblocker(final OrderEventListener listener) {
            this.listener = listener;

        }

        public void setOrderManagement(final OrderManagement orderManagement) {
            this.orderManagement = orderManagement;
        }

        @Override
        public void orderRejected(final Failed failure) {
            tradeUnblocker = null;
            listener.orderRejected(failure);
        }

        @Override
        public void orderOpened(final Instant time, final Price price) {
            listener.orderOpened(time, price);
        }

        @Override
        public void orderClosed(final Instant time, final Price price) {
            tradeUnblocker = null;
            listener.orderClosed(time, price);
        }

        @Override
        public void closeOrCancelOrder() {
            tradeUnblocker = null;
            orderManagement.closeOrCancelOrder();
        }

        @Override
        public Optional<Failed> changeCloseConditionsOfOrder(final CloseConditions conditions) {
            return orderManagement.changeCloseConditionsOfOrder(conditions);
        }
    }

    /**
     * An {@link OrderManagement} that does nothing on requests.
     */
    private static final class DummyOrderManagement implements OrderManagement {

        private static final Optional<Failed> CHANGE_CLOSE_CONDITIONS_NOT_SUPPORTED = Optional.of(new Failed("Changeing close"
                + " conditions failed because the order was never passed to a real broker."));

        @Override
        public void closeOrCancelOrder() {

        }

        @Override
        public Optional<Failed> changeCloseConditionsOfOrder(final CloseConditions conditions) {
            return CHANGE_CLOSE_CONDITIONS_NOT_SUPPORTED;
        }
    }
}
