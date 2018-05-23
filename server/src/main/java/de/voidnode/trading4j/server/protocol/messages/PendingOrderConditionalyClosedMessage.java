package de.voidnode.trading4j.server.protocol.messages;

import java.time.Instant;

import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.PendingOrder;

/**
 * Notifies that a {@link PendingOrder} that was previously placed by the
 * {@link de.voidnode.trading4j.api.ExpertAdvisor expert advisor} and executed because its execution conditions have
 * been met is now closed because its closing conditions have been met.
 * 
 * @author Raik Bieniek
 */
public class PendingOrderConditionalyClosedMessage implements Message {

    private final int orderId;
    private final Price price;
    private final Instant time;

    /**
     * Initializes the message.
     * 
     * @param orderId
     *            The id of the order that was closed.
     * @param time
     *            The time at which the order was closed.
     * @param price
     *            The closing price at which the assets where bought or sold back.
     */
    public PendingOrderConditionalyClosedMessage(final int orderId, final Instant time, final Price price) {
        this.orderId = orderId;
        this.price = price;
        this.time = time;
    }

    /**
     * The id of the order that was closed.
     * 
     * @return The id
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * The time at which the order was closed.
     * 
     * @return The time
     */
    public Instant getTime() {
        return time;
    }

    /**
     * The closing price at which the assets where bought or sold back.
     * 
     * @return The price
     */
    public Price getPrice() {
        return price;
    }
}
