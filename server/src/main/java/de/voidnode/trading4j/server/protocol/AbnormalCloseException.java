package de.voidnode.trading4j.server.protocol;

import java.io.IOException;

/**
 * When the connection to a client was closed in an unexpected way.
 * 
 * <p>
 * This exception would be thrown e.g. when the network cable is removed from the client.
 * </p>
 * 
 * @author Raik Bieniek
 */
public class AbnormalCloseException extends CommunicationException {

    private static final long serialVersionUID = 1L;

    /**
     * Initializes this exception.
     */
    public AbnormalCloseException() {
    }
    
    /**
     * Initializes this exception.
     * 
     * @param cause
     *            The causing exception
     */
    public AbnormalCloseException(final IOException cause) {
        super(cause);
    }
}
