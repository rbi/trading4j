package de.voidnode.trading4j.api;

import java.util.Optional;

import de.voidnode.trading4j.domain.ForexSymbol;
import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.monetary.Money;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * Manages the money of a trading account by providing the {@link Volume} an {@link ExpertAdvisor} is allowed to invest
 * for a single trade.
 * 
 * @author Raik Bieniek
 */
public interface MoneyManagement {

    /**
     * Request a {@link Volume} for a single trade.
     * 
     * @param symbol
     *            The symbol that should be traded.
     * @param currentPrice
     *            The last known price of the <code>symbol</code> that should be traded.
     * @param accountCurrencyExchangeSymbol
     *            The symbol that converts the account currency to the quote currency of the traded symbol. This is the
     *            traded symbol itself in the case that the traded symbol contains the account currency.
     * @param accountCurrencyExchangeRate
     *            The current exchange rate for the <code>accountCurrencyExchangeSymbol</code> This is the traded symbol
     *            price in the case that the traded symbol contains the account currency.
     * @param pipLostOnStopLoose
     *            The {@link Price} that the <code>symbol</code> will have fallen in case the trade is closed by the
     *            stop loose limit.
     * @param allowedStepSize
     *            The allowed step size for {@link Volume}s. All {@link Volume}s used for trading should be multiples of
     *            this step size.
     * @return An instance to manage the {@link Volume} that was granted for the trade or an empty {@link Optional} if
     *         no {@link Volume} was granted.
     */
    Optional<UsedVolumeManagement> requestVolume(ForexSymbol symbol, Price currentPrice,
            ForexSymbol accountCurrencyExchangeSymbol, Price accountCurrencyExchangeRate, Price pipLostOnStopLoose,
            Volume allowedStepSize);

    /**
     * Updates the currently available balance that is available for trading.
     * 
     * @param balance
     *            The balance that is currently available.
     */
    void updateBalance(Money balance);
}
