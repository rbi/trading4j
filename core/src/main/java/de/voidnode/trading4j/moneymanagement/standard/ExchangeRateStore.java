package de.voidnode.trading4j.moneymanagement.standard;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import de.voidnode.trading4j.domain.ForexSymbol;
import de.voidnode.trading4j.domain.monetary.AccuratePrice;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * Stores and allows to retrieve currency exchange rates.
 * 
 * @author Raik Bieniek
 */
class ExchangeRateStore {

    private Map<String, AccuratePrice> store = new HashMap<>();

    /**
     * The current price of the variable currency when it is exchanged for 1 unit of the fixed currency.
     * 
     * @param fixed
     *            The currency that is measured in 1 unit.
     * @param variable
     *            The currency thats worth equal to 1 unit of the <code>fixed</code> should be calculated.
     * @return The current exchange rate or an empty {@link Optional} if no exchange rate is known.
     */
    public Optional<AccuratePrice> getExchangeRate(final Currency fixed, final Currency variable) {
        final String forexSymbol = fixed.getSymbol() + variable.getSymbol();
        return Optional.ofNullable(store.get(forexSymbol));
    }

    /**
     * Changes the current exchange rate of a given forex pair.
     * 
     * @param pair
     *            The forex pair thats exchange rate should be updated.
     * @param exchangeRate
     *            The current exchange rate of the symbol.
     */
    public void updateExchangeRate(final ForexSymbol pair, final Price exchangeRate) {
        final String pairAsString = pair.getBaseCurrency().getSymbol() + pair.getQuoteCurrency().getSymbol();
        final String oppositePair = pair.getQuoteCurrency().getSymbol() + pair.getBaseCurrency().getSymbol();

        store.put(pairAsString, new AccuratePrice(exchangeRate.asDouble()));
        store.put(oppositePair, new AccuratePrice(1.0 / exchangeRate.asDouble()));
    }
}
