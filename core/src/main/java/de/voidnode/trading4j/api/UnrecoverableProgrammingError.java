package de.voidnode.trading4j.api;

/**
 * A fatal programming error that can not be worked around automatically.
 * 
 * @author Raik Bieniek
 */
public class UnrecoverableProgrammingError extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs the exception with a message.
     * 
     * @param message
     *            A human readable message describing the occured problem.
     */
    public UnrecoverableProgrammingError(final String message) {
        super(message);
    }

    /**
     * Constructs the exception with a message and the causing exception.
     * 
     * @param message
     *            A human readable message describing the occured problem.
     * @param cause
     *            The causing exception.
     */
    public UnrecoverableProgrammingError(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs the exception with a causing exception.
     * 
     * @param cause
     *            The causing exception.
     */
    public UnrecoverableProgrammingError(final Throwable cause) {
        super(cause);
    }
}
