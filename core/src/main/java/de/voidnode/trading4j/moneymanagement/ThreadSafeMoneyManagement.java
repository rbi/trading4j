package de.voidnode.trading4j.moneymanagement;

import java.util.Optional;

import de.voidnode.trading4j.api.MoneyManagement;
import de.voidnode.trading4j.api.UsedVolumeManagement;
import de.voidnode.trading4j.domain.ForexSymbol;
import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.monetary.Money;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * Guarantees thread-safety for an original {@link MoneyManagement} instance and its produced
 * {@link UsedVolumeManagement} instances.
 * 
 * @author Raik Bieniek
 */
public class ThreadSafeMoneyManagement implements MoneyManagement {

    private final Object lock = new Object();
    private final MoneyManagement orig;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param orig
     *            The orignal instance that should be made thread safe.
     */
    public ThreadSafeMoneyManagement(final MoneyManagement orig) {
        this.orig = orig;
    }

    @Override
    public void updateBalance(final Money balance) {
        synchronized (lock) {
            orig.updateBalance(balance);
        }
    }

    @Override
    public void updateExchangeRate(final ForexSymbol currencyExchange, final Price exchangeRate) {
        synchronized (lock) {
            orig.updateExchangeRate(currencyExchange, exchangeRate);
        }
    }

    @Override
    public Optional<UsedVolumeManagement> requestVolume(final ForexSymbol symbol, final Price currentPrice,
            final Price pipLostOnStopLoose, final Volume allowedStepSize) {
        synchronized (lock) {
            return orig.requestVolume(symbol, currentPrice, pipLostOnStopLoose, allowedStepSize)
                    .map(orig -> new ThreadSafeUsedVolumeManagement(lock, orig));
        }
    }

    /**
     * Ensures serialized access to the {@link UsedVolumeManagement} of the original {@link MoneyManagement}.
     */
    private static final class ThreadSafeUsedVolumeManagement implements UsedVolumeManagement {

        private final UsedVolumeManagement volumeManagement;
        private final Volume volume;
        private final Object lock;

        ThreadSafeUsedVolumeManagement(final Object lock, final UsedVolumeManagement volumeManagement) {
            this.volume = volumeManagement.getVolume();
            this.volumeManagement = volumeManagement;
            this.lock = lock;
        }

        @Override
        public Volume getVolume() {
            return volume;
        }

        @Override
        public void releaseVolume() {
            synchronized (lock) {
                volumeManagement.releaseVolume();
            }
        }
    }
}
