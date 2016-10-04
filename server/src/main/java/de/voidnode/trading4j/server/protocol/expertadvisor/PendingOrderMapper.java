package de.voidnode.trading4j.server.protocol.expertadvisor;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import de.voidnode.trading4j.api.OrderEventListener;
import de.voidnode.trading4j.api.UnrecoverableProgrammingError;

/**
 * Stores {@link OrderEventListener} and makes them accessible by the id of the pending order they listen for.
 * 
 * @author Raik Bieniek
 */
public class PendingOrderMapper {

    private final Map<Integer, OrderEventListener> idToOrder = new HashMap<>();

    /**
     * Stores an {@link OrderEventListener} for a given order id.
     * 
     * @param id
     *            The id of the order the listener receives events for.
     * @param listener
     *            The listener for events of the given order.
     */
    public void put(final int id, final OrderEventListener listener) {
        idToOrder.put(id, listener);
    }

    /**
     * Removes a {@link OrderEventListener} for a given order id from this store.
     * 
     * @param id
     *            The order id thats event listener should be removed.
     * @throws UnrecoverableProgrammingError
     *             When no event listener for the order with the given order is registered.
     */
    public void remove(final int id) throws UnrecoverableProgrammingError {
        final OrderEventListener removed = idToOrder.remove(id);
        if (removed == null) {
            throw new UnrecoverableProgrammingError("An OrderEventListener with the id " + id
                    + " should be removed from the " + PendingOrderMapper.class.getSimpleName()
                    + " but no OrderEventListener with this id is known.", new NoSuchElementException());
        }
    }

    /**
     * Checks if an order with the given id is known.
     * 
     * @param id
     *            The id of the order that should be checked.
     * @return <code>true</code> if an order with the given id is known and <code>false</code> if not.
     */
    public boolean has(final int id) {
        return idToOrder.containsKey(id);
    }

    /**
     * Queries for the event listener of a given order.
     * 
     * @param orderId
     *            The id of the order thats registered event listener should be returned.
     * @return The event listener if one is known.
     * @throws UnrecoverableProgrammingError
     *             When no event listener for the order with the given order is known.
     */
    public OrderEventListener get(final int orderId) throws UnrecoverableProgrammingError {
        final OrderEventListener order = idToOrder.get(orderId);
        if (order == null) {
            throw new UnrecoverableProgrammingError("An OrderEventListener for the order id " + orderId
                    + " was requested from the " + PendingOrderMapper.class.getSimpleName()
                    + " but no OrderEventListener with this id is known.", new NoSuchElementException());
        }
        return order;
    }
}
