package de.voidnode.trading4j.moneymanagement.standard;

import java.util.Currency;
import java.util.Optional;

import de.voidnode.trading4j.domain.ForexSymbol;
import de.voidnode.trading4j.domain.monetary.AccuratePrice;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.monetary.PriceUnit;

/**
 * Calculates the worth of a single {@link PriceUnit#PIPETTE Pipette} in a given currency.
 * 
 * @author Raik Bieniek
 */
class PipetteValueCalculator {

    private static final AccuratePrice PIPETTE = new AccuratePrice(0.00001);

    private final ExchangeRateStore exchangeRateStore;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param exchangeRateStore
     *            used to retrieve the exchange rate when the {@link PriceUnit#PIPETTE Pipette} worth of a
     *            {@link ForexSymbol} that does not contain the account currency should be calculated.
     */
    PipetteValueCalculator(final ExchangeRateStore exchangeRateStore) {
        this.exchangeRateStore = exchangeRateStore;
    }

    /**
     * Calculates the worth of a single Pipette in a given currency.
     * 
     * @param accountCurrency
     *            The currency in which the worth of a single pipette should be calculated.
     * @param tradedSymbol
     *            The symbol thats Pipette worth should be calculated.
     * @param tradeSymbolPrice
     *            The current market price of the symbol.
     * @return The worth of a single Pipette.
     */
    public AccuratePrice calculatePipetteValue(final Currency accountCurrency, final ForexSymbol tradedSymbol,
            final Price tradeSymbolPrice) {
        if (accountCurrency.equals(tradedSymbol.getBaseCurrency())) {
            return new AccuratePrice(0.00001 / tradeSymbolPrice.asDouble());
        } else if (accountCurrency.equals(tradedSymbol.getQuoteCurrency())) {
            return PIPETTE;
        }

        final Optional<AccuratePrice> exchangeRate = exchangeRateStore.getExchangeRate(tradedSymbol.getQuoteCurrency(),
                accountCurrency);
        return exchangeRate.map(rate -> new AccuratePrice(0.00001 * exchangeRate.get().asRawValue()))
                .orElseThrow(() -> createIlegalArgumentsException(accountCurrency, tradedSymbol));
    }

    private IllegalArgumentException createIlegalArgumentsException(final Currency accountCurrency,
            final ForexSymbol tradedSymbol) {
        return new IllegalArgumentException("Should calculate the value of a single pipette of the symbol \""
                + tradedSymbol + "\" in \"" + accountCurrency
                + "\" and requiering therefor the the exchange rate from \"" + tradedSymbol.getQuoteCurrency()
                + "\" to \"" + accountCurrency + "\" but that exchange rate was not available.");
    }
}
