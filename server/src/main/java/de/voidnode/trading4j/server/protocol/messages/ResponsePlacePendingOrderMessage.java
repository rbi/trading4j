package de.voidnode.trading4j.server.protocol.messages;

import java.util.Optional;

import de.voidnode.trading4j.domain.orders.PendingOrder;

/**
 * Tells if the last placed {@link PendingOrder} was placed successfully or not.
 * 
 * @author Raik Bieniek
 */
public class ResponsePlacePendingOrderMessage implements Message {

    private final int idOrError;
    private final boolean success;

    /**
     * Initializes the message.
     * 
     * <p>
     * Although this constructor is <code>public</code> this message is read-only.
     * </p>
     * 
     * @param success
     *            <code>true</code> when placing the pending order succeed and <code>false</code> if not.
     * @param idOrError
     *            The id for the acknowledged {@link PendingOrder} or the error code for the reason the order failed.
     */
    public ResponsePlacePendingOrderMessage(final boolean success, final int idOrError) {
        this.success = success;
        this.idOrError = idOrError;
    }

    /**
     * If placing the pending order succeed or not.
     * 
     * @return <code>true</code> if placing succeed and <code>false</code> if not.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * The id for the last {@link PendingOrder} if it was placed successfully.
     * 
     * @return The id of the {@link PendingOrder} or an empty {@link Optional} if the order has failed to be placed.
     */
    public Optional<Integer> getId() {
        if (success) {
            return Optional.of(idOrError);
        }
        return Optional.empty();
    }

    /**
     * The error code for the reason the {@link PendingOrder} failed to be placed if it has failed to be placed.
     * 
     * @return The error code or an empty {@link Optional} if placing the pending order has not failed.
     */
    public Optional<Integer> getErrorCode() {
        if (!success) {
            return Optional.of(idOrError);
        }
        return Optional.empty();
    }
}
