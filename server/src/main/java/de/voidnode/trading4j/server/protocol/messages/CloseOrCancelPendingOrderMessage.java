package de.voidnode.trading4j.server.protocol.messages;

import de.voidnode.trading4j.domain.orders.PendingOrder;

/**
 * Closes or cancels a {@link PendingOrder} that was already placed.
 * 
 * <p>
 * If the {@link PendingOrder} is closed or canceled depends on whether it was already executed or is still pending.
 * </p>
 * 
 * @author Raik Bieniek
 */
public class CloseOrCancelPendingOrderMessage implements Message {

    private final int id;

    /**
     * Initializes this message.
     * 
     * @param id
     *            The id of the {@link PendingOrder} to close or cancel.
     */
    public CloseOrCancelPendingOrderMessage(final int id) {
        this.id = id;
    }

    /**
     * The id of the {@link PendingOrder} to close or cancel.
     * 
     * @return The id
     */
    public int getId() {
        return id;
    }
}
