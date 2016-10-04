package de.voidnode.trading4j.server.protocol.messages;

import java.util.Optional;

import de.voidnode.trading4j.domain.orders.CloseConditions;
import de.voidnode.trading4j.domain.orders.PendingOrder;

/**
 * The response to an {@link ChangeCloseConditionsMessage} message which indicates if the {@link CloseConditions} where
 * successfully changed or not.
 * 
 * @author Raik Bieniek
 */
public class ResponseChangeCloseConditionsMessage implements Message {

    private final Optional<Integer> errorCode;

    /**
     * Initializes an instance that indicates a successful change of the {@link CloseConditions}.
     */
    public ResponseChangeCloseConditionsMessage() {
        this.errorCode = Optional.empty();
    }

    /**
     * Initializes an instance that indicates that changing the {@link CloseConditions} of a {@link PendingOrder} has
     * failed.
     * 
     * @param errorCode
     *            see {@link #getErrorCode()}
     */
    public ResponseChangeCloseConditionsMessage(final int errorCode) {
        this.errorCode = Optional.of(errorCode);
    }

    /**
     * The code for the reason that changing the {@link CloseConditions} has failed if it has failed.
     * 
     * @return The error code or an empty {@link Optional} if the {@link CloseConditions} where changed successful.
     */
    public Optional<Integer> getErrorCode() {
        return errorCode;
    }

}
