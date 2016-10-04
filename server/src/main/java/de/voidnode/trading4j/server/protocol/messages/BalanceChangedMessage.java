package de.voidnode.trading4j.server.protocol.messages;

/**
 * Indicates that the balance of the trading account has changed.
 * 
 * @author Raik Bieniek
 */
public class BalanceChangedMessage implements Message {

    private final long newBalance;

    /**
     * Initializes the message.
     * 
     * <p>
     * The constructor is <code>package private</code> as this message is read-only for now.
     * </p>
     * 
     * @param newBalance
     *            see {@link #getNewBalance()}
     */
    BalanceChangedMessage(final long newBalance) {
        this.newBalance = newBalance;
    }

    /**
     * The new balance of the trading account.
     * 
     * @return The new balance with the "minor" unit of the currency (e.g. Cent).
     */
    public long getNewBalance() {
        return newBalance;
    }
}
