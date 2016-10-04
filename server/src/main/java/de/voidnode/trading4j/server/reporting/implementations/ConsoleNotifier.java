package de.voidnode.trading4j.server.reporting.implementations;

import de.voidnode.trading4j.domain.trades.CompletedTrade;
import de.voidnode.trading4j.server.reporting.CombinedNotifier;

/**
 * A notifier implementation that notifies by printing the messages on the console.
 * 
 * @author Raik Bieniek
 */
class ConsoleNotifier implements CombinedNotifier {

    // ///////////
    // / Admin ///
    // ///////////

    @Override
    public void unrecoverableError(final String message, final Throwable cause) {
        System.err.print("ERROR: ");
        System.err.println(message);
        cause.printStackTrace();
    }

    @Override
    public void unexpectedEvent(final String message, final Throwable cause) {
        unexpectedEvent(message);
        cause.printStackTrace(System.err);
    }

    @Override
    public void unexpectedEvent(final String message) {
        System.err.print("WARN : ");
        System.err.println(message);
    }

    @Override
    public void informalEvent(final String message) {
        System.out.print("INFO : ");
        System.out.println(message);
    }

    // ////////////
    // / Trader ///
    // ////////////

    @Override
    public void tradeCompleted(final CompletedTrade trade) {
        informalEvent("A trade was completed: " + trade.toString());
    }

    // ///////////////
    // / Developer ///
    // ///////////////

    @Override
    public void unrecoverableProgrammingError(final String message, final Throwable error) {
        System.err.println("ERROR: An unhandeled exception occured with message: " + message);
        error.printStackTrace(System.err);
    }

}