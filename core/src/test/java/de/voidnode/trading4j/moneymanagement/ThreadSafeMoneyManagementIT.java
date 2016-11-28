package de.voidnode.trading4j.moneymanagement;

import java.util.Currency;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.stream.IntStream.range;

import de.voidnode.trading4j.api.MoneyManagement;
import de.voidnode.trading4j.api.UsedVolumeManagement;
import de.voidnode.trading4j.domain.ForexSymbol;
import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.VolumeUnit;
import de.voidnode.trading4j.domain.monetary.Money;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.testutils.ThreadSafetyAsserter;

import org.junit.Test;

/**
 * Checks that {@link ThreadSafeMoneyManagement} works as expected.
 * 
 * @author Raik Bieniek
 */
public class ThreadSafeMoneyManagementIT {

    private static final long SIMULATED_MONEY_MANAGEMENT_ACTIONS_TIME = 10;
    private static final int SIMULATED_CONCURRENT_THREADS = 10;
    private static final int SIMULATED_TRIES_PER_THREAD = 10;

    private static final long TIMEOUT = 1000 + SIMULATED_TRIES_PER_THREAD * SIMULATED_MONEY_MANAGEMENT_ACTIONS_TIME * 2;

    private static final ForexSymbol SOME_SYMBOL = new ForexSymbol("GBPUSD");
    private static final Price SOME_PRICE = new Price(1.0);
    private static final Currency SOME_CURRENCY = Currency.getInstance("EUR");
    private static final Money SOME_BALANCE = new Money(1000, 00, SOME_CURRENCY);
    private static final Volume SOME_STEP_SIZE = new Volume(10, VolumeUnit.MICRO_LOT);

    private final ThreaySaftyRequiereingMoneyManagement testMoneyManagenent = new ThreaySaftyRequiereingMoneyManagement();
    private final MoneyManagement cut = new ThreadSafeMoneyManagement(testMoneyManagenent);
    private final AtomicReference<AssertionError> exception = new AtomicReference<>();

    /**
     * The cut should ensure thread-safe access to an original {@link MoneyManagement} instance.
     * 
     * @throws InterruptedException
     *             Not expected
     */
    @Test
    public void threadSafeAccessToOriginalMoneyManagementShouldBeEnsured() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(SIMULATED_CONCURRENT_THREADS);

        range(0, SIMULATED_CONCURRENT_THREADS).forEach(i -> {
            new Thread(() -> {
                for (int i2 = 0; i2 < SIMULATED_TRIES_PER_THREAD; i2++) {
                    try {
                        cut.updateBalance(SOME_BALANCE);
                        cut.updateExchangeRate(SOME_SYMBOL, SOME_PRICE);
                        final UsedVolumeManagement requestVolume = cut
                                .requestVolume(SOME_SYMBOL, SOME_PRICE, SOME_PRICE, SOME_STEP_SIZE).get();
                        requestVolume.getVolume();
                        requestVolume.releaseVolume();
                    } catch (final Exception e) {
                        exception.set(new AssertionError("The cut threw an exception.", e));
                        break;
                    }
                }
                latch.countDown();
            }).start();
        });

        latch.await(TIMEOUT, TimeUnit.MILLISECONDS);

        if (exception.get() != null) {
            throw exception.get();
        }
        testMoneyManagenent.getConcurrentAccessError().ifPresent(error -> {
            throw error;
        });
    }

    /**
     * A dummy money management instance that requires thread-safety or else will fail.
     */
    private final class ThreaySaftyRequiereingMoneyManagement extends ThreadSafetyAsserter
            implements MoneyManagement, UsedVolumeManagement {

        ThreaySaftyRequiereingMoneyManagement() {
            super(SIMULATED_MONEY_MANAGEMENT_ACTIONS_TIME);
        }

        @Override
        public Volume getVolume() {
            simulateThreadSafetyRequiereingAction();
            return new Volume(42, VolumeUnit.MINI_LOT);
        }

        @Override
        public void releaseVolume() {
            simulateThreadSafetyRequiereingAction();
        }

        @Override
        public void updateBalance(final Money balance) {
            simulateThreadSafetyRequiereingAction();
        }

        @Override
        public void updateExchangeRate(final ForexSymbol currencyExchange, final Price exchangeRate) {
            simulateThreadSafetyRequiereingAction();
        }

        @Override
        public Optional<UsedVolumeManagement> requestVolume(final ForexSymbol symbol, final Price currentPrice,
                final Price pipLostOnStopLoose, final Volume allowedStepSize) {
            simulateThreadSafetyRequiereingAction();
            return Optional.of(this);
        }
    }
}
