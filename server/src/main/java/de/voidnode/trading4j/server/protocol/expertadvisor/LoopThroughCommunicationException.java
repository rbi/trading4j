package de.voidnode.trading4j.server.protocol.expertadvisor;

import de.voidnode.trading4j.server.protocol.exceptions.CommunicationException;

/**
 * A wrapper for {@link CommunicationException}s that are thrown when sending data to loop it through to the message
 * read loop.
 * 
 * @author Raik Bieniek
 */
class LoopThroughCommunicationException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final CommunicationException communicationException;

    /**
     * Initializes the exception with its wrapped exception.
     * 
     * @param c
     *            The wrapped exception.
     */
    LoopThroughCommunicationException(final CommunicationException c) {
        super(c);
        this.communicationException = c;
    }

    /**
     * The exception that was wrapped.
     * 
     * @return The exception
     */
    public CommunicationException getWrappedException() {
        return communicationException;
    }
}