package de.voidnode.trading4j.server.reporting;

/**
 * Provides functionality to notify the developer on certain events.
 * 
 * @author Raik Bieniek
 */
public interface DeveloperNotifier {

    /**
     * An unexpected exception was thrown that was not handled in a way that would have allowed the failing program
     * parts to continue working.
     * 
     * <p>
     * In other words, some part of the program has crashed.
     * </p>
     * 
     * @param message
     *            A human readable message that describes the problem.
     * @param error
     *            The unexpected exception. If the program part that cached this exception can provide more information
     *            where the problem occurred it is recommended to wrap the causing exception in a more descriptive one.
     */
    void unrecoverableProgrammingError(String message, Throwable error);
}
