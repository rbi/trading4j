package de.voidnode.trading4j.domain.marketdata.impl;

import de.voidnode.trading4j.domain.marketdata.MarketData;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * An implementation for the most basic time-depended information of an security - the closing price.
 *
 * @author Raik Bieniek
 */
public class BasicMarketData implements MarketData {

    private final Price close;

    /**
     * Constructs the {@link BasicMarketData}.
     * 
     * @param close
     *            see {@link #getClose()}
     */
    public BasicMarketData(final Price close) {
        this.close = close;
    }

    /**
     * A convenience constructor that takes raw {@link Price}es as input.
     * 
     * <p>
     * See {@link Price#Price(double)} for the meaning of the expected double inputs.
     * </p>
     * 
     * @param close
     *            see {@link #getClose()}
     */
    public BasicMarketData(final double close) {
        this(new Price(close));
    }

    @Override
    public Price getClose() {
        return close;
    }

    /**
     * A {@link BasicMarketData} is only equal to other {@link BasicMarketData}s thats close {@link Price}es are equal.
     * 
     * <p>
     * The object to compare with can be an instance of a sub-class of a {@link BasicMarketData} and still be equal.
     * </p>
     * 
     * @param obj
     *            The object to compare with.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof BasicMarketData)) {
            return false;
        }
        final BasicMarketData other = (BasicMarketData) obj;
        if (close == null) {
            if (other.close != null) {
                return false;
            }
        } else if (!close.equals(other.close)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((close == null) ? 0 : close.hashCode());
        return result;
    }
    
    @Override
    public String toString() {
        return "BasicMarketPrice [close=" + close + "]";
    }
}
