package de.voidnode.trading4j.moneymanagement;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import de.voidnode.trading4j.api.MoneyManagement;
import de.voidnode.trading4j.api.TraderNotifier;
import de.voidnode.trading4j.api.UsedVolumeManagement;
import de.voidnode.trading4j.domain.ForexSymbol;
import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.monetary.Money;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * Creates {@link MoneyManagement}s that can ensure that {@link Volume} lent from a shared {@link MoneyManagement} can
 * be returned.
 * 
 * <p>
 * This is useful to return lent {@link Volume} in case the expert advisor or the broker are terminated or stopped
 * working unexpectedly.
 * </p>
 * 
 * @author Raik Bieniek
 */
public class SharedMoneyManagement {

    private final MoneyManagement moneyManagement;
    private final TraderNotifier notifier;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param moneyManagement
     *            The {@link MoneyManagement} that is shared amongst multiple expert advisors.
     * @param notifier
     *            A notifier to inform the trader of enforced volume returning.
     */
    public SharedMoneyManagement(final MoneyManagement moneyManagement, final TraderNotifier notifier) {
        this.moneyManagement = moneyManagement;
        this.notifier = notifier;
    }

    /**
     * Creates a new client for the shared {@link MoneyManagement} thats lent {@link Volume} can be returned
     * individually.
     * 
     * @return The new client.
     */
    public ReleasableMoneyManagement newConnection() {
        return new ReleasableMoneyManagement();
    }

    /**
     * Keeps track of all lent {@link Volume} and allows to return all of it to the {@link MoneyManagement} in case of
     * unexpected termination of the expert advisor.
     */
    public class ReleasableMoneyManagement implements MoneyManagement {

        private Set<ReleaseableUsedVolumeManagement> lentVolumes = new HashSet<>();

        /**
         * Enforces releasing all {@link Volume} that was lent by the expert advisor in case of unexpected termination.
         * 
         * <p>
         * <b>WARNING:</b> The expert advisor that requested the {@link Volume} will not know that it was released by
         * this method.
         * </p>
         */
        public void realeaseAllAquieredVolume() {
            for (final ReleaseableUsedVolumeManagement lentVolume : lentVolumes) {
                lentVolume.enforceReleaseVolume();
            }
        }

        @Override
        public Optional<UsedVolumeManagement> requestVolume(final ForexSymbol symbol, final Price currentPrice,
                final ForexSymbol accountCurrencyExchangeSymbol, final Price accountCurrencyExchangeRate,
                final Price pipLostOnStopLoose, final Volume allowedStepSize) {
            final Optional<UsedVolumeManagement> requestedVolume = moneyManagement.requestVolume(symbol, currentPrice,
                    accountCurrencyExchangeSymbol, accountCurrencyExchangeRate, pipLostOnStopLoose, allowedStepSize);
            if (!requestedVolume.isPresent()) {
                return requestedVolume;
            }
            final ReleaseableUsedVolumeManagement volumeManagement = new ReleaseableUsedVolumeManagement(
                    requestedVolume.get());
            lentVolumes.add(volumeManagement);
            return Optional.of(volumeManagement);
        }

        @Override
        public void updateBalance(final Money balance) {
            moneyManagement.updateBalance(balance);
        }

        /**
         * Lent volume that can be returned.
         */
        private class ReleaseableUsedVolumeManagement implements UsedVolumeManagement {

            private final UsedVolumeManagement usedVolumeManagement;

            ReleaseableUsedVolumeManagement(final UsedVolumeManagement usedVolumeManagement) {
                this.usedVolumeManagement = usedVolumeManagement;
            }

            @Override
            public Volume getVolume() {
                return usedVolumeManagement.getVolume();
            }

            @Override
            public void releaseVolume() {
                lentVolumes.remove(this);
                usedVolumeManagement.releaseVolume();
            }

            public void enforceReleaseVolume() {
                usedVolumeManagement.releaseVolume();
                notifier.unexpectedEvent("A volume of " + getVolume()
                + " lent by an expert advisor was forcefully returned to the money management "
                + "because the expert advisor was shut down.");
            }
        }
    }
}
