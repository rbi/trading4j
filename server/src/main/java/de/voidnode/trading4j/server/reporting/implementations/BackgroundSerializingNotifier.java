package de.voidnode.trading4j.server.reporting.implementations;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.voidnode.trading4j.domain.trades.CompletedTrade;
import de.voidnode.trading4j.server.reporting.AdmininstratorNotifier;
import de.voidnode.trading4j.server.reporting.CombinedNotifier;
import de.voidnode.trading4j.server.reporting.DeveloperNotifier;
import de.voidnode.trading4j.server.reporting.TraderNotifier;

/**
 * Executes event notification in the background and ensures that only one event notification is active at a time.
 * 
 * <p>
 * WARNING: To prevent lingering threads when an object is no longer needed, you must call {@link #shutdown()}.
 * </p>
 * <p>
 * This class is thread-safe.
 * </p>
 * 
 * @author Raik Bieniek
 */
class BackgroundSerializingNotifier implements CombinedNotifier {

    private final Optional<? extends TraderNotifier> trader;
    private final Optional<? extends AdmininstratorNotifier> admin;
    private final Optional<? extends DeveloperNotifier> developer;

    private final ExecutorService backgroundExecutor;

    /**
     * Initializes an instance with its dependencies.
     * 
     * @param trader
     *            An optional trader that should be notified in the background.
     * @param admin
     *            An optional administrator that should be notified in the background.
     * @param developer
     *            An optional developer that should be notified in the background.
     */
    BackgroundSerializingNotifier(final Optional<? extends TraderNotifier> trader,
            final Optional<? extends AdmininstratorNotifier> admin,
            final Optional<? extends DeveloperNotifier> developer) {
        this.trader = trader;
        this.admin = admin;
        this.developer = developer;
        this.backgroundExecutor = Executors.newFixedThreadPool(1, runnable -> {
            final Thread thread = new Thread(runnable);
            thread.setName("background serializing notifier-" + thread.getId());
            thread.setDaemon(true);
            return thread;
        });
    }

    /**
     * Stops all threads started by this object.
     * 
     * <p>
     * If this objects exists until the end of the lifetime of the program, this method does not need to be called but
     * if the object is stopped being used before it should.
     * </p>
     */
    public void shutdown() {
        backgroundExecutor.shutdown();
    }

    ////////////
    // Trader //
    ////////////

    @Override
    public void tradeCompleted(final CompletedTrade trade) {
        trader.ifPresent(trader -> backgroundExecutor.execute(() -> trader.tradeCompleted(trade)));
    }

    ///////////////////
    // Administrator //
    ///////////////////

    @Override
    public void unrecoverableError(final String message, final Throwable cause) {
        admin.ifPresent(admin -> backgroundExecutor.execute(() -> admin.unrecoverableError(message, cause)));
    }

    @Override
    public void unexpectedEvent(final String message, final Throwable cause) {
        admin.ifPresent(admin -> backgroundExecutor.execute(() -> admin.unexpectedEvent(message, cause)));
    }

    @Override
    public void unexpectedEvent(final String message) {
        admin.ifPresent(admin -> backgroundExecutor.execute(() -> admin.unexpectedEvent(message)));
    }

    @Override
    public void informalEvent(final String message) {
        admin.ifPresent(admin -> backgroundExecutor.execute(() -> admin.informalEvent(message)));
    }

    ///////////////
    // Developer //
    ///////////////

    @Override
    public void unrecoverableProgrammingError(final String message, final Throwable error) {
        developer.ifPresent(
                developer -> backgroundExecutor.execute(() -> developer.unrecoverableProgrammingError(message, error)));
    }
}
