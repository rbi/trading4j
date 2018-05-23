package de.voidnode.trading4j.server.protocol.exceptions;

/**
 * Indicates that the communication protocol between the client and server has been violated and the connection had
 * therefore to be closed.
 * 
 * @author Raik Bieniek
 */
public class ProtocolException extends CommunicationException {

    private static final long serialVersionUID = 1L;

    /**
     * Initializes this exception with a human readable message describing the problem.
     * 
     * @param message
     *            A human readable message describing the problem.
     */
    public ProtocolException(final String message) {
        super(message);
    }

    /**
     * Initializes this exception with a human readable message and a causing exception describing the problem.
     * 
     * @param message
     *            A human readable message describing the problem.
     * @param cause
     *            The cause for this exception.
     */
    public ProtocolException(final String message, final Exception cause) {
        super(message, cause);
    }
}
