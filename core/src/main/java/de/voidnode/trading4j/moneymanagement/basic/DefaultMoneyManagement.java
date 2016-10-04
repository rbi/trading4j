package de.voidnode.trading4j.moneymanagement.basic;

import java.util.Optional;

import de.voidnode.trading4j.api.MoneyManagement;
import de.voidnode.trading4j.api.UsedVolumeManagement;
import de.voidnode.trading4j.domain.ForexSymbol;
import de.voidnode.trading4j.domain.Ratio;
import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.monetary.Money;
import de.voidnode.trading4j.domain.monetary.Price;

import static de.voidnode.trading4j.domain.RatioUnit.PERCENT;

/**
 * An implementation that takes the trading account balance and currencies traded into account.
 * 
 * @author Raik Bieniek
 */
public class DefaultMoneyManagement implements MoneyManagement {

    private final TradeBlocker currencyBlocker;
    private final RiskMoneyProvider moneyProvider;
    private final PipetteValueCalculator pipetteValueCalculator;
    private final VolumeCalculator volumeCalculator;
    private Money balance;
    private VolumeStepSizeRounder volumeRounder;

    /**
     * Creates an instance using the default implementations for its dependencies.
     */
    public DefaultMoneyManagement() {
        this(new OneTradePerCurrency(), new RiskMoneyProvider(new Ratio(1, PERCENT)), new PipetteValueCalculator(),
                new VolumeCalculator(), new VolumeStepSizeRounder());
    }

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param currencyBlocker
     *            Used to prevents risking too much money in the same currency.
     * @param moneyProvider
     *            Used to get the amount of money that can be risk in a single trade.
     * @param pipetteValueCalculator
     *            Used to calculate the worth of a single Pipette.
     * @param volumeCalculator
     *            Used to calculate the {@link Volume} that can be spend for a trade.
     * @param volumeRounder
     *            Used to round volume to multiples of the allowed step size.
     */
    DefaultMoneyManagement(final TradeBlocker currencyBlocker, final RiskMoneyProvider moneyProvider,
            final PipetteValueCalculator pipetteValueCalculator, final VolumeCalculator volumeCalculator,
            final VolumeStepSizeRounder volumeRounder) {
        this.currencyBlocker = currencyBlocker;
        this.moneyProvider = moneyProvider;
        this.pipetteValueCalculator = pipetteValueCalculator;
        this.volumeCalculator = volumeCalculator;
        this.volumeRounder = volumeRounder;

        // Does not matter if it is initially wrong as there is no balance initially available anyway.
        this.balance = new Money(0, "EUR");
    }

    @Override
    public Optional<UsedVolumeManagement> requestVolume(final ForexSymbol symbol, final Price currentPrice,
            final ForexSymbol accountCurrencyExchangeSymbol, final Price accountCurrencyExchangeRate,
            final Price pipLostOnStopLoose, final Volume allowedStepSize) {
        if (!currencyBlocker.isTradingAllowed(symbol)) {
            return Optional.empty();
        }
        currencyBlocker.blockCurrencies(symbol);

        final Volume volume = volumeRounder.round(volumeCalculator.calculateVolumeForTrade(
                pipetteValueCalculator.calculatePipetteValue(balance.getCurrency(), symbol, currentPrice,
                        accountCurrencyExchangeSymbol, accountCurrencyExchangeRate),
                pipLostOnStopLoose, moneyProvider.calculateMoneyToRisk(balance)), allowedStepSize);

        return Optional.of(new UsedVolumeManagement() {
            @Override
            public void releaseVolume() {
                currencyBlocker.unblockCurrencies(symbol);
            }

            @Override
            public Volume getVolume() {
                return volume;
            }
        });
    }

    @Override
    public void updateBalance(final Money money) {
        this.balance = money;
    }
}
