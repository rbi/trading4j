package de.voidnode.trading4j.functionality.broker;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.Failed;
import de.voidnode.trading4j.api.OrderEventListener;
import de.voidnode.trading4j.api.OrderManagement;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;

/**
 * A wrapper around another {@link Broker} that allows to block new trades to this broker.
 *
 * <p>
 * Trading is deactivated when an instance is created.
 * </p>
 *
 * @author Raik Bieniek
 */
public class DeactivateableBroker implements Broker<BasicPendingOrder> {

    private static final Failed TRADING_BLOCKED = new Failed("Trading is programatically deactivated at the moment.");
    private static final OrderManagement NO_OP_ORDER_MANAGEMENT = new NoOpOrderManagement();

    private final Broker<BasicPendingOrder> broker;

    private boolean activated;

    /**
     * Initializes an instance with all its dependencies.
     *
     * @param broker The broker that orders should be send to when trading is activated.
     */
    public DeactivateableBroker(final Broker<BasicPendingOrder> broker) {
        this.broker = broker;
    }

    @Override
    public OrderManagement sendOrder(final BasicPendingOrder order,
                                     final OrderEventListener eventListener) {
        if (activated) {
            return broker.sendOrder(order, eventListener);
        } else {
            eventListener.orderRejected(TRADING_BLOCKED);
            return NO_OP_ORDER_MANAGEMENT;
        }
    }

    /**
     * Activates trading with the wrapped broker.
     */
    public void activate() {
        activated = true;
    }

    /**
     * Deactivates trading with the wrapped broker.
     *
     * <p>
     * As long as trading is deactivated all orders placed with
     * {@link #sendOrder(BasicPendingOrder, OrderEventListener)} will fail (return {@link Failed}).
     * </p>
     */
    public void deactivate() {
        activated = false;
    }
}
