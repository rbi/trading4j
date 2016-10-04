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
     *            An instance that will be informed of all events on the order. If placing the order failed, nothing
     *            will be called on this interface.
     * @return An instance containing the {@link OrderManagement} to manage the placed order when the order was placed
     *         as expected. A {@link Failed} when the broker was not able or willing to place the order.
     */
    Either<Failed, OrderManagement> sendOrder(PO order, OrderEventListener eventListener);
}
