package de.voidnode.trading4j.server.protocol.expertadvisor;

import de.voidnode.trading4j.api.UnrecoverableProgrammingError;

/**
 * A wrapper for {@link UnrecoverableProgrammingError}s that are thrown when sending data to loop it through to the
 * message read loop.
 * 
 * @author Raik Bieniek
 */
class LoopThroughProgrammingErrorException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final UnrecoverableProgrammingError unrecoverableProgrammingError;

    /**
     * Initializes the exception with its wrapped exception.
     * 
     * @param c
     *            The wrapped exception.
     */
    LoopThroughProgrammingErrorException(final UnrecoverableProgrammingError c) {
        super(c);
        this.unrecoverableProgrammingError = c;
    }

    /**
     * The exception that was wrapped.
     * 
     * @return The exception
     */
    public UnrecoverableProgrammingError getWrappedException() {
        return unrecoverableProgrammingError;
    }
}