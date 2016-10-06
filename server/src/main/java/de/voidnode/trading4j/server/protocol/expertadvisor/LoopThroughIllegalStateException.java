package de.voidnode.trading4j.server.protocol.expertadvisor;

/**
 * A wrapper for {@link IllegalStateException}s that are thrown when sending data to loop it through to the
 * message read loop.
 * 
 * @author Raik Bieniek
 */
class LoopThroughIllegalStateException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private final IllegalStateException illegalStateException;

    /**
     * Initializes the exception with its wrapped exception.
     * 
     * @param c
     *            The wrapped exception.
     */
    LoopThroughIllegalStateException(final IllegalStateException c) {
        super(c);
        this.illegalStateException = c;
    }

    /**
     * The exception that was wrapped.
     * 
     * @return The exception
     */
    public IllegalStateException getWrappedException() {
        return illegalStateException;
    }
}