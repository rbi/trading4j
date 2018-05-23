package de.voidnode.trading4j.server.protocol.messages;

import de.voidnode.trading4j.domain.monetary.Price;

/**
 * Indicates that the exchange rate for the account currency to the quote currency of the traded symbol has changed.
 * 
 * @author Raik Bieniek;
 */
public class AccountCurrencyExchangeRateChangedMessage implements Message {

    private final Price newRate;

    /**
     * Initializes an instance with all its data.
     * 
     * <p>
     * Only reading this type of messages is supported. Therefore this constructor is package private.
     * </p>
     * 
     * @param newRate
     *            see {@link #getNewRate()}
     */
    public AccountCurrencyExchangeRateChangedMessage(final Price newRate) {
        this.newRate = newRate;
    }

    /**
     * The new exchange rate for the account currency to the quote currency of the traded symbol.
     * 
     * @return the new rate
     */
    public Price getNewRate() {
        return newRate;
    }
}
