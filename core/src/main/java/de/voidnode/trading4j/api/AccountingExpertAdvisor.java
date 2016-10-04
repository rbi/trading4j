package de.voidnode.trading4j.api;

import de.voidnode.trading4j.domain.TimeFrame.M1;
import de.voidnode.trading4j.domain.marketdata.CandleStick;
import de.voidnode.trading4j.domain.monetary.Money;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * An {@link ExpertAdvisor} that is also interested in change of the trading account balance.
 * 
 * @author Raik Bieniek
 * @param <C>
 *            The type of {@link M1} {@link CandleStick}s that is required as input.
 */
public interface AccountingExpertAdvisor<C extends CandleStick<M1>> extends ExpertAdvisor<C> {

    /**
     * Informs that the current trading balance has changed.
     * 
     * @param money
     *            The new balance available for trading.
     */
    void balanceChanged(Money money);

    /**
     * Informs that the exchange rate for the account currency to the quote currency of the traded symbol has changed.
     * 
     * @param newPrice
     *            The new exchange rate.
     */
    void accountCurrencyPriceChanged(Price newPrice);
}
