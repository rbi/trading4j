package de.voidnode.trading4j.moneymanagement.basic;

import java.util.Currency;

import de.voidnode.trading4j.domain.ForexSymbol;
import de.voidnode.trading4j.domain.monetary.AccuratePrice;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * Calculates the worth of a single Pipette in a given currency.
 * 
 * @author Raik Bieniek
 */
class PipetteValueCalculator {

    private static final AccuratePrice PIPETTE = new AccuratePrice(0.00001);

    /**
     * Calculates the worth of a single Pipette in a given currency.
     * 
     * @param accountCurrency
     *            The currency in which the worth of a single pipette should be calculated.
     * @param tradedSymbol
     *            The symbol thats Pipette worth should be calculated.
     * @param tradeSymbolPrice
     *            The current market price of the symbol.
     * @param accountCurrencyExchangeSymbol
     *            The symbol that converts the account currency into the quote currency of the traded symbol.
     * @param accountCurrencyExchangeSymbolPrice
     *            The current {@link Price} of <code>accountCurrencyExchangeSymbol</code>.
     * @return The worth of a single Pipette.
     */
    public AccuratePrice calculatePipetteValue(final Currency accountCurrency, final ForexSymbol tradedSymbol,
            final Price tradeSymbolPrice, final ForexSymbol accountCurrencyExchangeSymbol,
            final Price accountCurrencyExchangeSymbolPrice) {
        if (accountCurrency.equals(tradedSymbol.getBaseCurrency())) {
            return new AccuratePrice(0.00001 / tradeSymbolPrice.asDouble());
        } else if (accountCurrency.equals(tradedSymbol.getQuoteCurrency())) {
            return PIPETTE;
        } else if (accountCurrency.equals(accountCurrencyExchangeSymbol.getQuoteCurrency())) {
            if (!tradedSymbol.getQuoteCurrency().equals(accountCurrencyExchangeSymbol.getBaseCurrency())) {
                throw createIlegalArgumentsException(accountCurrency, tradedSymbol, accountCurrencyExchangeSymbol);
            }
            return new AccuratePrice(0.00001 * accountCurrencyExchangeSymbolPrice.asDouble());
        } else if (accountCurrency.equals(accountCurrencyExchangeSymbol.getBaseCurrency())) {
            if (!tradedSymbol.getQuoteCurrency().equals(accountCurrencyExchangeSymbol.getQuoteCurrency())) {
                throw createIlegalArgumentsException(accountCurrency, tradedSymbol, accountCurrencyExchangeSymbol);
            }
            return new AccuratePrice(0.00001 / accountCurrencyExchangeSymbolPrice.asDouble());
        } else {
            throw createIlegalArgumentsException(accountCurrency, tradedSymbol, accountCurrencyExchangeSymbol);
        }
    }

    private IllegalArgumentException createIlegalArgumentsException(final Currency accountCurrency,
            final ForexSymbol tradedSymbol, final ForexSymbol accountCurrencyExchangeSymbol) {
        return new IllegalArgumentException("Should calculate the value of a single pipette of the symbol \""
                + tradedSymbol + "\" in \"" + accountCurrency + "\" and requiering therefor the symbol containing \""
                + accountCurrency + "\" and \"" + tradedSymbol.getQuoteCurrency() + "\" but got the symbol \""
                + accountCurrencyExchangeSymbol + "\".");
    }
}
