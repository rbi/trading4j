package de.voidnode.trading4j.server.protocol;

import java.io.IOException;

/**
 * When the connection to a client was closed in an expected way.
 * 
 * <p>
 * This exception would be thrown e.g. when the client terminated the connection.
 * </p>
 * 
 * @author Raik Bieniek
 */
public class NormalCloseException extends CommunicationException {

    private static final long serialVersionUID = 1L;

    /**
     * Initializes this exception.
     * 
     * @param cause
     *            The causing exception
     */
    public NormalCloseException(final IOException cause) {
        super(cause);
    }

    /**
     * Initializes this exception.
     */
    public NormalCloseException() {
    }
}
