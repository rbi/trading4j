package de.voidnode.trading4j.domain.marketdata;

import de.voidnode.trading4j.domain.TimeFrame;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * The {@link Price} of an asset in a given time frame.
 *
 * @author Raik Bieniek
 * @param <T>
 *            The time frame that this {@link MarketData} has data for.
 */
public class MarketData<T extends TimeFrame> {

    private final Price close;

    /**
     * Constructs the {@link MarketData}.
     * 
     * @param close
     *            see {@link #getClose()}
     */
    public MarketData(final Price close) {
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
    public MarketData(final double close) {
        this(new Price(close));
    }

    /**
     * The value at the end of the time period this market price aggregates.
     * 
     * @return the closing price
     */
    public Price getClose() {
        return close;
    }

    /**
     * A {@link MarketData} is only equal to other {@link MarketData}s thats close {@link Price}es are equal.
     * 
     * <p>
     * The object to compare with can be an instance of a sub-class of a {@link MarketData} and still be equal.
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
        if (!(obj instanceof MarketData)) {
            return false;
        }
        final MarketData<?> other = (MarketData<?>) obj;
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
        return "MarketPrice [close=" + close + "]";
    }
}
