package de.voidnode.trading4j.server.reporting.implementations;

import de.voidnode.trading4j.domain.trades.CompletedTrade;
import de.voidnode.trading4j.server.reporting.AdmininstratorNotifier;
import de.voidnode.trading4j.server.reporting.CombinedNotifier;
import de.voidnode.trading4j.server.reporting.DeveloperNotifier;
import de.voidnode.trading4j.server.reporting.TraderNotifier;

/**
 * Distributes events to multiple other notifiers.
 * 
 * @author Raik Bieniek
 */
class CombiningNotifier implements CombinedNotifier {

    private final Iterable<TraderNotifier> traders;
    private final Iterable<AdmininstratorNotifier> administators;
    private final Iterable<DeveloperNotifier> developers;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param traders
     *            All traders that should be informed on events for traders.
     * @param administators
     *            All administrators that should be informed on events for administrators.
     * @param developers
     *            All developers that should be informed on events for developers.
     */
    CombiningNotifier(final Iterable<TraderNotifier> traders,
            final Iterable<AdmininstratorNotifier> administators, final Iterable<DeveloperNotifier> developers) {
        this.traders = traders;
        this.administators = administators;
        this.developers = developers;
    }

    ////////////
    // Trader //
    ////////////

    @Override
    public void tradeCompleted(final CompletedTrade trade) {
        for (final TraderNotifier trader : traders) {
            trader.tradeCompleted(trade);
        }
    }

    ///////////////////
    // Administrator //
    ///////////////////

    @Override
    public void unrecoverableError(final String message, final Throwable cause) {
        for (final AdmininstratorNotifier admin : administators) {
            admin.unrecoverableError(message, cause);
        }
    }

    @Override
    public void unexpectedEvent(final String message, final Throwable cause) {
        for (final AdmininstratorNotifier admin : administators) {
            admin.unexpectedEvent(message, cause);
        }
    }

    @Override
    public void unexpectedEvent(final String message) {
        for (final AdmininstratorNotifier admin : administators) {
            admin.unexpectedEvent(message);
        }
    }

    @Override
    public void informalEvent(final String message) {
        for (final AdmininstratorNotifier admin : administators) {
            admin.informalEvent(message);
        }
    }

    ///////////////
    // Developer //
    ///////////////

    @Override
    public void unrecoverableProgrammingError(final String message, final Throwable error) {
        for (final DeveloperNotifier dev : developers) {
            dev.unrecoverableProgrammingError(message, error);
        }
    }
}
