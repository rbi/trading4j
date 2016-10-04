package de.voidnode.trading4j.server.reporting.implementations;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Thread.sleep;
import static java.util.stream.IntStream.range;

import de.voidnode.trading4j.domain.trades.CompletedTrade;
import de.voidnode.trading4j.server.reporting.CombinedNotifier;
import de.voidnode.trading4j.testutils.ThreadSafetyAsserter;

import org.junit.After;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Checks if {@link BackgroundSerializingNotifier} works as expected.
 * 
 * @author Raik Bieniek
 */
public class BackgroundSerializingNotifierIT {

    private static final long SIMULATED_ACTION_TIME = 5;
    private static final int SIMULATED_CONCURRENT_THREADS = 5;
    private static final int SIMULATED_TRIES_PER_THREAD = 5;

    private final ThreaySaftyRequiereingNotifier testNotifier = new ThreaySaftyRequiereingNotifier();
    private final BackgroundSerializingNotifier cut = new BackgroundSerializingNotifier(Optional.of(testNotifier),
            Optional.of(testNotifier), Optional.of(testNotifier));
    private final AtomicReference<AssertionError> exception = new AtomicReference<>();

    private CompletedTrade someTrade = mock(CompletedTrade.class);
    private final Throwable someException = new RuntimeException("some exception");
    private final String someMessage = "some message";

    /**
     * Cleans up the cut after the tests.
     */
    @After
    public void cleanUpCut() {
        cut.shutdown();
    }

    /**
     * The notifiers should be accessed thread safe.
     * 
     * @throws InterruptedException
     *             Not expected in the test
     */
    @Test
    public void threadSafeAccessToNotifiersShouldBeGuaranteed() throws InterruptedException {

        range(0, SIMULATED_CONCURRENT_THREADS).forEach(i -> {
            new Thread(() -> {
                for (int i2 = 0; i2 < SIMULATED_TRIES_PER_THREAD; i2++) {
                    try {
                        cut.tradeCompleted(someTrade);
                        cut.unrecoverableError(someMessage, someException);
                        cut.unexpectedEvent(someMessage, someException);
                        cut.unexpectedEvent(someMessage);
                        cut.informalEvent(someMessage);
                        cut.unrecoverableProgrammingError(someMessage, someException);
                    } catch (final Exception e) {
                        exception.set(new AssertionError("The cut threw an exception.", e));
                        break;
                    }

                }
            }).start();
        });

        sleep(SIMULATED_CONCURRENT_THREADS * SIMULATED_TRIES_PER_THREAD * SIMULATED_ACTION_TIME * 10);

        if (exception.get() != null) {
            throw exception.get();
        }
        testNotifier.getConcurrentAccessError().ifPresent(error -> {
            throw error;
        });
    }

    /**
     * Not all notifiers need to be passed in the constructor.
     */
    @Test
    public void notifiersAreOptional() {
        final Optional<CombinedNotifier> testNotifier = Optional.of(mock(CombinedNotifier.class));
        final CombinedNotifier cut = new BackgroundSerializingNotifier(Optional.empty(), Optional.empty(),
                testNotifier);

        cut.tradeCompleted(someTrade);
        cut.unrecoverableError(someMessage, someException);
        verifyNoMoreInteractions(testNotifier.get());
    }

    /**
     * Simulates a notifier that requieres thread-safety.
     */
    private class ThreaySaftyRequiereingNotifier extends ThreadSafetyAsserter implements CombinedNotifier {

        ThreaySaftyRequiereingNotifier() {
            super(SIMULATED_ACTION_TIME);
        }

        @Override
        public void tradeCompleted(final CompletedTrade trade) {
            simulateThreadSafetyRequiereingAction();
        }

        @Override
        public void unrecoverableError(final String message, final Throwable cause) {
            simulateThreadSafetyRequiereingAction();
        }

        @Override
        public void unexpectedEvent(final String message, final Throwable cause) {
            simulateThreadSafetyRequiereingAction();
        }

        @Override
        public void unexpectedEvent(final String message) {
            simulateThreadSafetyRequiereingAction();
        }

        @Override
        public void informalEvent(final String message) {
            simulateThreadSafetyRequiereingAction();
        }

        @Override
        public void unrecoverableProgrammingError(final String message, final Throwable error) {
            simulateThreadSafetyRequiereingAction();
        }
    }
}
