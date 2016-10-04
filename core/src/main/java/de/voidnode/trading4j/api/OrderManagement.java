package de.voidnode.trading4j.api;

import java.util.Optional;

import de.voidnode.trading4j.domain.orders.CloseConditions;

/**
 * An instance that allows to manage a single opened or pending order.
 * 
 * @author Raik Bieniek
 */
public interface OrderManagement {

    /**
     * Closes or cancels the order.
     * 
     * <ul>
     * <li>The order is canceled if it was issued but its execution condition was not met yet.</li>
     * <li>The order is closed if it was already executed.</li>
     * </ul>
     */
    void closeOrCancelOrder();

    /**
     * Changes the close conditions of the order.
     * 
     * @param conditions
     *            The new {@link CloseConditions} for the order.
     * @return An empty {@link Optional} when the order was changed as expected and {@link Failed} when the broker was
     *         not able or willing to change the order. In that case the previous {@link CloseConditions} are still in
     *         tact.
     */
    Optional<Failed> changeCloseConditionsOfOrder(CloseConditions conditions);

}
