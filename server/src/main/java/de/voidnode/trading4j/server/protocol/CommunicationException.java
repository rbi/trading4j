package de.voidnode.trading4j.server.protocol;

/**
 * Indicates an error in the communication to the client.
 * 
 * @author Raik Bieniek
 */
public abstract class CommunicationException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Initializes this exception.
     */
    public CommunicationException() {

    }

    /**
     * Initializes this exception with a human readable message describing the problem.
     * 
     * @param message
     *            A human readable message describing the problem.
     */
    public CommunicationException(final String message) {
        super(message);
    }

    /**
     * Initializes this exception.
     * 
     * @param cause
     *            The causing exception
     */
    public CommunicationException(final Exception cause) {
        super(cause);
    }

    /**
     * Initializes this exception with a human readable message and a causing exception describing the problem.
     * 
     * @param message
     *            A human readable message describing the problem.
     * @param cause
     *            The cause for this exception.
     */
    public CommunicationException(final String message, final Exception cause) {
        super(message, cause);
    }
}
