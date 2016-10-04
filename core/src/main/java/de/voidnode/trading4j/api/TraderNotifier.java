package de.voidnode.trading4j.api;

/**
 * A notifier that informs a trader of different events on trades.
 * 
 * @author Raik Bieniek
 */
public interface TraderNotifier {

    /**
     * An unexpected event concerning actual trading occurred which may require actions of a trader.
     * 
     * @param message
     *            A human readable message describing the event.
     */
    void unexpectedEvent(final String message);
}
