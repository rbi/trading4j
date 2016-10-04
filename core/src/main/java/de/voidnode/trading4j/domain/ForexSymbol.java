package de.voidnode.trading4j.domain;

import java.util.Currency;

import de.voidnode.trading4j.api.UnrecoverableProgrammingError;

/**
 * An identifier for a tradeable forex pair.
 * 
 * @author Raik Bieniek
 */
public final class ForexSymbol {

    private final Currency baseCurrency;
    private final Currency quoteCurrency;

    /**
     * Creates an instance with its raw value.
     * 
     * @param name
     *            The name of the symbol.
     * @throws UnrecoverableProgrammingError
     *             When the <code>name</code> passed in the constructor has not exactly 6 letters.
     */
    public ForexSymbol(final String name) {
        if (name.length() != 6) {
            throw new UnrecoverableProgrammingError(
                    new IllegalArgumentException("A forex symbol must have exactly 6 letters but the passed symbol \""
                            + name + "\" has " + name.length() + " letters."));
        }
        baseCurrency = Currency.getInstance(name.substring(0, 3));
        quoteCurrency = Currency.getInstance(name.substring(3));
    }

    /**
     * The base currency (the left side) of this symbol.
     * 
     * @return The base currency.
     */
    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    /**
     * The quote currency (the right side) of this symbol.
     * 
     * @return The quote currency.
     */
    public Currency getQuoteCurrency() {
        return quoteCurrency;
    }

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
        final ForexSymbol other = (ForexSymbol) obj;
        if (baseCurrency == null) {
            if (other.baseCurrency != null) {
                return false;
            }
        } else if (!baseCurrency.getSymbol().equals(other.baseCurrency.getSymbol())) {
            return false;
        }
        if (quoteCurrency == null) {
            if (other.quoteCurrency != null) {
                return false;
            }
        } else if (!quoteCurrency.getSymbol().equals(other.quoteCurrency.getSymbol())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((baseCurrency == null) ? 0 : baseCurrency.getSymbol().hashCode());
        result = prime * result + ((quoteCurrency == null) ? 0 : quoteCurrency.getSymbol().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "" + baseCurrency + quoteCurrency;
    }
}
