package de.voidnode.trading4j.api;

import de.voidnode.trading4j.domain.orders.BasicPendingOrder;

/**
 * An instance that executes orders in financial markets.
 *
 * @author Raik Bieniek
 * @param <PO>
 *            the concrete type of {@link BasicPendingOrder} the broker expects.
 */
public interface Broker<PO extends BasicPendingOrder> {

    /**
     * Issues a new pending order to be executed by the broker.
     * 
     * @param order
     *            The order to execute.
     * @param eventListener
     *            An instance that will be informed of all events on the order. Methods on this listener may be called
     *            before this method returns or at a later time.
     * @return An instance containing the {@link OrderManagement} to manage the placed order.
     */
    OrderManagement sendOrder(PO order, OrderEventListener eventListener);
}
