package de.voidnode.trading4j.server.protocol.messages;

import de.voidnode.trading4j.domain.orders.PendingOrder;

/**
 * Instructs the broker to place a {@link PendingOrder}.
 * 
 * @author Raik Bieniek
 */
public class PlacePendingOrderMessage implements Message {

    private final PendingOrder pendingOrder;

    /**
     * Initializes the data of the message.
     * 
     * @param pendingOrder
     *            The pending order that should be placed.
     */
    public PlacePendingOrderMessage(final PendingOrder pendingOrder) {
        this.pendingOrder = pendingOrder;
    }

    /**
     * The pending order that should be placed.
     * 
     * @return The pending order
     */
    public PendingOrder getPendingOrder() {
        return pendingOrder;
    }
}
