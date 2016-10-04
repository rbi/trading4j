package de.voidnode.trading4j.testutils;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Makes sure that calls to to it are thread-safe.
 * 
 * <p>
 * You must check for the presence of {@link #getConcurrentAccessError()} to know whether there was an error or not.
 * </p>
 * 
 * @author Raik Bieniek
 */
public abstract class ThreadSafetyAsserter {

    private final long simulatedActionTime;
    private final AtomicReference<Thread> thread = new AtomicReference<>();
    private final AtomicReference<AssertionError> concurrentAccess = new AtomicReference<>();

    /**
     * Initializes an instance with all its data.
     * 
     * @param simulatedActionTime
     *            The time the simulated action should take in milliseconds.
     */
    public ThreadSafetyAsserter(final long simulatedActionTime) {
        this.simulatedActionTime = simulatedActionTime;
    }

    /**
     * Check if thread-safety was guaranteed.
     * 
     * @return An empty {@link Optional} if it was guaranteed and an error if it wasn't.
     */
    public Optional<AssertionError> getConcurrentAccessError() {
        return Optional.ofNullable(concurrentAccess.get());
    }

    /**
     * Simulates an action that requires thread-safety.
     * 
     * <p>
     * If other threads access this object while this method has not returned yet, the assertions failed an
     * {@link #getConcurrentAccessError()} is set.
     * </p>
     */
    protected void simulateThreadSafetyRequiereingAction() {
        thread.set(Thread.currentThread());
        try {
            // When no concurrent access happens, no other thread will have overwritten the thread variable when
            // waking up.
            Thread.sleep(simulatedActionTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!thread.get().equals(Thread.currentThread())) {
            concurrentAccess.set(new AssertionError(
                    "Another thread accessed this object before it has finished its previouse work."));
        }
    }
}
