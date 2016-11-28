package de.voidnode.trading4j.api;

import de.voidnode.trading4j.domain.ForexSymbol;
import de.voidnode.trading4j.domain.monetary.Money;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * Tracks the account balance and it's worth in other currencies.
 * 
 * <p>
 * This is usually implemented together with {@link VolumeLender} to provide a {@link MoneyManagement}.
 * </p>
 * 
 * @author Raik Bieniek
 */
public interface AccountBalanceManager {

    /**
     * Updates an exchange rate of the account currency to an other currency.
     * 
     * @param currencyExchange
     *            The forex symbol that describes for which currency this exchange rate applies.
     * @param exchangeRate
     *            The exchange rate.
     */
    void updateExchangeRate(ForexSymbol currencyExchange, Price exchangeRate);

    /**
     * Updates the currently available balance that is available for trading.
     * 
     * @param balance
     *            The balance that is currently available.
     */
    void updateBalance(Money balance);
}
