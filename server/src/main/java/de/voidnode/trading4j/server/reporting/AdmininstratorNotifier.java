package de.voidnode.trading4j.server.reporting;

/**
 * Provides functionality to notify the administrator on certain events.
 * 
 * @author Raik Bieniek
 */
public interface AdmininstratorNotifier {

    /**
     * Inform the administrator of a unrecoverable error.
     * 
     * @param message
     *            A description for the error.
     * @param cause
     *            The throwable causing the error
     */
    void unrecoverableError(final String message, final Throwable cause);

    /**
     * Informs the administrator of an unexpected event which could be corrected automatically.
     * 
     * @param message
     *            A description of the event.
     * @param cause
     *            The throwable causing the event.
     */
    void unexpectedEvent(final String message, final Throwable cause);

    /**
     * Informs the administrator of an unexpected event which could be corrected automatically.
     * 
     * @param message
     *            A description of the event.
     */
    void unexpectedEvent(final String message);

    /**
     * Informs the administrator of a normal event that was expected during normal operation of the server.
     * 
     * @param message
     *            A description of the event.
     */
    void informalEvent(String message);
}
