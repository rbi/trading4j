package de.voidnode.trading4j.domain.environment;

import java.util.Currency;

/**
 * Information about a trading account.
 * 
 * @author Raik Bieniek
 */
public class AccountInformation {

    private final String brokerName;
    private final long accountNumber;
    private final Currency accountCurrency;

    /**
     * Initializes an instance with all required data.
     * 
     * @param brokerName
     *            see {@link #getBrokerName()}
     * @param accountNumber
     *            see {@link #getAccountNumber()}
     * @param accountCurrency
     *            see {@link #getAccountCurrency()}
     */
    public AccountInformation(final String brokerName, final long accountNumber, final Currency accountCurrency) {
        this.brokerName = brokerName;
        this.accountNumber = accountNumber;
        this.accountCurrency = accountCurrency;
    }

    /**
     * The name of the broker where this account is registered.
     * 
     * @return the name of the broker
     */
    public String getBrokerName() {
        return brokerName;
    }

    /**
     * The number of the account at the broker.
     * 
     * @return the number of the account
     */
    public long getAccountNumber() {
        return accountNumber;
    }

    /**
     * The currency that the balance of this account is kept.
     * 
     * @return the currency of the account
     */
    public Currency getAccountCurrency() {
        return accountCurrency;
    }

    @Override
    public String toString() {
        return "AccountInformation [brokerName=" + brokerName + ", accountNumber=" + accountNumber
                + ", accountCurrency=" + accountCurrency + "]";
    }
}
