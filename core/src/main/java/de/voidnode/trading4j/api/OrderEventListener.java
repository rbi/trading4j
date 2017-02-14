package de.voidnode.trading4j.api;

import java.time.Instant;

import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.CloseConditions;

/**
 * A listener that is informed of all events concerning a single order placed at a broker.
 * 
 * @author Raik Bieniek
 */
public interface OrderEventListener {

    /**
     * Called when the broker refused to accept the order.
     *
     * <p>This method will not be called when the user canceled the Order with {@link OrderManagement#closeOrCancelOrder()}.
     * </p>
     *
     * @param failure The reason why the broker refused to take the order.
     */
    void orderRejected(Failed failure);

    /**
     * Informs that a pending order was executed because its execution conditions have been met.
     * 
     * @param time
     *            The time at which the order was opened.
     * @param price
     *            The price at which the asset has be acquired.
     */
    void orderOpened(Instant time, Price price);

    /**
     * Informs that a previously opened order has been closed because its closing criteria have been met.
     *
     * <p>
     * An order can be closed either because one of the {@link CloseConditions} was reached or because it was closed
     * manually.
     * </p>
     * 
     * <p>
     * This method is not called for orders closed via {@link OrderManagement#closeOrCancelOrder()}.
     * </p>
     * 
     * @param time
     *            The time at which the order was closed.
     * @param price
     *            The price at which the order was closed.
     */
    void orderClosed(Instant time, Price price);
}
