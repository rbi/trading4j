package de.voidnode.trading4j.domain.monetary;

import java.util.Currency;

import de.voidnode.trading4j.api.UnrecoverableProgrammingError;

/**
 * An amount of a currency that can be used for trading.
 * 
 * @author Raik Bieniek
 */
public final class Money {

    private final long value;
    private final Currency currency;

    /**
     * Initializes an instance.
     * 
     * @param rawValue
     *            see {@link #asRawValue()}
     * @param currency
     *            see {@link #getCurrency()}
     */
    public Money(final long rawValue, final Currency currency) {
        this.value = rawValue;
        this.currency = currency;
    }

    /**
     * Initializes an instance.
     * 
     * @param rawValue
     *            see {@link #asRawValue()}
     * @param currencyCode
     *            The {@link Currency} as {@link String}, see {@link Currency#getInstance(String)}
     * @throws IllegalArgumentException
     *             When the currency code is not supported, see {@link Currency#getInstance(String)}
     */
    public Money(final long rawValue, final String currencyCode) throws IllegalArgumentException {
        this(rawValue, Currency.getInstance(currencyCode));
    }

    /**
     * Initializes an instance.
     * 
     * @param major
     *            see #getMajor().
     * @param minor
     *            see #getMinor(). This value must be &gt;= 0 and &lt;100 or an exception will be thrown.
     * @param currency
     *            see {@link #getCurrency()}
     * @throws UnrecoverableProgrammingError
     *             When <code>minor</code> is &lt;0 or &gt;= 100.
     */
    public Money(final long major, final int minor, final Currency currency) throws UnrecoverableProgrammingError {
        if (minor < 0 || minor > 99) {
            throw new UnrecoverableProgrammingError(
                    new IllegalArgumentException("A money instance with a minor value of \"" + minor
                            + "\" should be created but the minor value must be >= 0 and <=99."));
        }

        final long absolute = Math.abs(major) * 100 + minor;
        this.value = major < 0 ? absolute * -1 : absolute;
        this.currency = currency;
    }

    /**
     * Initializes an instance.
     * 
     * @param major
     *            see #getMajor().
     * @param minor
     *            see #getMinor(). This value must be &gt;= 0 and &lt;100 or an exception will be thrown.
     * @param currencyCode
     *            The {@link Currency} as {@link String}, see {@link Currency#getInstance(String)}
     * @throws UnrecoverableProgrammingError
     *             When <code>minor</code> is &lt;0 or &gt;= 100.
     * @throws IllegalArgumentException
     *             When the currency code is not supported, see {@link Currency#getInstance(String)}
     */
    public Money(final long major, final int minor, final String currencyCode)
            throws UnrecoverableProgrammingError, IllegalArgumentException {
        this(major, minor, Currency.getInstance(currencyCode));
    }

    /**
     * The raw version of this money instance which has the unit "minor".
     * 
     * @return The raw version
     */
    public long asRawValue() {
        return value;
    }

    /**
     * The {@link Currency} in which the amount of this instance is measured in.
     * 
     * @return The currency
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * A {@link Money} instance is only equal to other {@link Money} instances with the same {@link #asRawValue()}.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Money other = (Money) obj;
        if (currency == null) {
            if (other.currency != null) {
                return false;
            }
        } else if (!currency.equals(other.currency)) {
            return false;
        }
        if (value != other.value) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((currency == null) ? 0 : currency.hashCode());
        result = prime * result + (int) (value ^ (value >>> 32));
        return result;
    }

    /**
     * A {@link String} representation of this {@link Money} object that always has a sign prepended, event when the
     * value is positive.
     * 
     * @return The {@link String} representation.
     */
    public String toStringWithSign() {
        final long absolute = absolute();
        return (value < 0 ? "-" : "+") + absolute / 100 + "." + String.format("%02d", absolute % 100) + " "
                + currency.getCurrencyCode();
    }

    @Override
    public String toString() {
        final long absolute = absolute();
        return (value < 0 ? "-" : "") + absolute / 100 + "." + String.format("%02d", absolute % 100) + " "
                + currency.getCurrencyCode();
    }

    private long absolute() {
        return value < 0 ? value * -1 : value;
    }
}
