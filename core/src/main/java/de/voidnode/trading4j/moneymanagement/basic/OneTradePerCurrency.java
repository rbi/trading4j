package de.voidnode.trading4j.moneymanagement.basic;

import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

import de.voidnode.trading4j.api.UnrecoverableProgrammingError;
import de.voidnode.trading4j.domain.ForexSymbol;

/**
 * Ensures that only a single trade per currency is allowed at a time.
 * 
 * @author Raik Bieniek
 */
class OneTradePerCurrency implements TradeBlocker {

    private final Set<Currency> currenciesInvestedIn = new HashSet<>();

    @Override
    public boolean isTradingAllowed(final ForexSymbol symbol) {
        return !(currenciesInvestedIn.contains(symbol.getBaseCurrency())
                || currenciesInvestedIn.contains(symbol.getQuoteCurrency()));
    }

    @Override
    public void blockCurrencies(final ForexSymbol symbol) throws UnrecoverableProgrammingError {
        if (!isTradingAllowed(symbol)) {
            throw new UnrecoverableProgrammingError("Should block the currencies of symbol " + symbol
                    + " from trading but one or both currencies are already blocked.");
        }

        currenciesInvestedIn.add(symbol.getBaseCurrency());
        currenciesInvestedIn.add(symbol.getQuoteCurrency());
    }

    @Override
    public void unblockCurrencies(final ForexSymbol symbol) throws UnrecoverableProgrammingError {
        if (isTradingAllowed(symbol)) {
            throw new UnrecoverableProgrammingError("Should unblock te currencies of symbol " + symbol
                    + " for trading but one or both currencies are not blocked.");
        }

        currenciesInvestedIn.remove(symbol.getBaseCurrency());
        currenciesInvestedIn.remove(symbol.getQuoteCurrency());
    }
}
