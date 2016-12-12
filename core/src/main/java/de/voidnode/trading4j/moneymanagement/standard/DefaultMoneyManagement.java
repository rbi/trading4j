package de.voidnode.trading4j.moneymanagement.standard;

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
 * <p>
 * This implementation will risk a defined fraction of the current account balance in each trade. The default is 1%. It
 * is ensured, that for each currency only one trade is active at a time. For example when both pairs EURUSD and EURGPB
 * are traded, only one active trade is allowed for both of them.
 * </p>
 * 
 * @author Raik Bieniek
 */
public class DefaultMoneyManagement implements MoneyManagement {

    private final TradeBlocker currencyBlocker;
    private final RiskMoneyProvider moneyProvider;
    private final PipetteValueCalculator pipetteValueCalculator;
    private final VolumeCalculator volumeCalculator;
    private final VolumeStepSizeRounder volumeRounder;
    private final ExchangeRateStore exchangeRateStore;
    private Money balance;

    /**
     * Creates an instance using that risks around 1% of the current account balance per trade.
     */
    public DefaultMoneyManagement() {
        this(new Ratio(1, PERCENT));
    }

    /**
     * Creates a new instance.
     * 
     * @param moneyPerTrade
     *            The percentage of the current account balance that should be risked per trade.
     */
    public DefaultMoneyManagement(final Ratio moneyPerTrade) {
        this.currencyBlocker = new OneTradePerCurrency();
        this.moneyProvider = new RiskMoneyProvider(moneyPerTrade);
        this.exchangeRateStore = new ExchangeRateStore();
        this.pipetteValueCalculator = new PipetteValueCalculator(exchangeRateStore);
        this.volumeCalculator = new VolumeCalculator();
        this.volumeRounder = new VolumeStepSizeRounder();
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
     * @param exchangeRateStore
     *            Used to manage currency exchange rates.
     */
    DefaultMoneyManagement(final TradeBlocker currencyBlocker, final RiskMoneyProvider moneyProvider,
            final PipetteValueCalculator pipetteValueCalculator, final VolumeCalculator volumeCalculator,
            final VolumeStepSizeRounder volumeRounder, final ExchangeRateStore exchangeRateStore) {
        this.currencyBlocker = currencyBlocker;
        this.moneyProvider = moneyProvider;
        this.pipetteValueCalculator = pipetteValueCalculator;
        this.volumeCalculator = volumeCalculator;
        this.volumeRounder = volumeRounder;
        this.exchangeRateStore = exchangeRateStore;

        // Does not matter if it is initially wrong as there is no balance initially available anyway.
        this.balance = new Money(0, "EUR");
    }

    @Override
    public Optional<UsedVolumeManagement> requestVolume(final ForexSymbol symbol, final Price currentPrice,
            final Price pipLostOnStopLoose, final Volume allowedStepSize) {
        if (!currencyBlocker.isTradingAllowed(symbol)) {
            return Optional.empty();
        }
        currencyBlocker.blockCurrencies(symbol);

        final Volume volume = volumeRounder.round(volumeCalculator.calculateVolumeForTrade(
                pipetteValueCalculator.calculatePipetteValue(balance.getCurrency(), symbol, currentPrice),
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

    @Override
    public void updateExchangeRate(final ForexSymbol currencyExchange, final Price exchangeRate) {
        exchangeRateStore.updateExchangeRate(currencyExchange, exchangeRate);
    }
}
