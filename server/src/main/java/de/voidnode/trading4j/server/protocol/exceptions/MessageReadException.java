package de.voidnode.trading4j.server.protocol.exceptions;

/**
 * Signalizes that a message could not be read from a {@link de.voidnode.trading4j.server.protocol.ClientConnection} as
 * expected.
 * 
 * <p>
 * This could mean that the client did not send enough data or did send it in the wrong order.
 * </p>
 * 
 * @author Raik Bieniek
 */
public class MessageReadException extends CommunicationException {

    private static final long serialVersionUID = 1L;

    /**
     * Initializes this exception with a human readable message describing the problem.
     * 
     * @param message
     *            A human readable message describing the problem.
     */
    public MessageReadException(final String message) {
        super(message);
    }

}
