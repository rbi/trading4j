package de.voidnode.trading4j.server.protocol.messages;

import java.time.Instant;

import de.voidnode.trading4j.api.ExpertAdvisor;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.PendingOrder;

/**
 * Notifies that a {@link PendingOrder} that was previously opened by the {@link ExpertAdvisor} has been executed
 * because its execution conditions have been met.
 * 
 * @author Raik Bieniek
 */
public class PendingOrderConditionalyExecutedMessage implements Message {

    private final int orderId;
    private final Price price;
    private final Instant time;

    /**
     * Initializes this message.
     * 
     * @param orderId
     *            The id of the order that was executed.
     * @param time
     *            The time at which the order was executed.
     * @param price
     *            The price at which the assets where bought or sold.
     */
    public PendingOrderConditionalyExecutedMessage(final int orderId, final Instant time, final Price price) {
        this.orderId = orderId;
        this.price = price;
        this.time = time;
    }

    /**
     * The id of the order that was executed.
     * 
     * @return The id
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * The time at which the order was executed.
     * 
     * @return The time
     */
    public Instant getTime() {
        return time;
    }

    /**
     * The price at which the assets where bought or sold.
     * 
     * @return The price
     */
    public Price getPrice() {
        return price;
    }
}
