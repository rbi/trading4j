package de.voidnode.trading4j.moneymanagement;

import java.util.Optional;

import de.voidnode.trading4j.api.MoneyManagement;
import de.voidnode.trading4j.api.UsedVolumeManagement;
import de.voidnode.trading4j.domain.ForexSymbol;
import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.monetary.Money;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * An implementation that will provide the same volume for every request.
 * 
 * <p>
 * This implementation will never deny a request for money.
 * </p>
 * 
 * @author Raik Bieniek
 */
public class NonMoneyManagement implements MoneyManagement {

    private final Optional<UsedVolumeManagement> volumeManagement;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param volume
     *            The volume that should be returned for every request.
     */
    public NonMoneyManagement(final Volume volume) {
        this.volumeManagement = Optional.of(new UsedVolumeManagement() {
            @Override
            public void releaseVolume() {
                // nothing to do here
            }

            @Override
            public Volume getVolume() {
                return volume;
            }
        });
    }

    @Override
    public Optional<UsedVolumeManagement> requestVolume(final ForexSymbol symbol, final Price currentPrice,
            final ForexSymbol accountCurrencyExchangeSymbol, final Price accountCurrencyExchangeRate,
            final Price pipLostOnStopLoose, final Volume allowedStepSize) {
        return volumeManagement;
    }

    @Override
    public void updateBalance(final Money balance) {
        // not needed
    }
}
