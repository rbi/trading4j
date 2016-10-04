package de.voidnode.trading4j.domain.marketdata;

import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.TimeFrame;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * Contains the data for a single candle stick chart.
 *
 * @author Raik Bieniek
 * @param <T>
 *            The time frame that candle stick this candle stick aggregates.
 */
public class CandleStick<T extends TimeFrame> extends MarketData<T> {

    private final Price open;
    private final Price high;
    private final Price low;

    /**
     * A convenience constructor that takes raw {@link Price}es as input.
     * 
     * <p>
     * See {@link Price#Price(double)} for the meaning of the expected double inputs.
     * </p>
     * 
     * @param open
     *            see {@link #getOpen()}
     * @param high
     *            see {@link #getHigh()}
     * @param low
     *            see {@link #getLow()}
     * @param close
     *            see {@link #getClose()}
     */
    public CandleStick(final double open, final double high, final double low, final double close) {
        this(new Price(open), new Price(high), new Price(low), new Price(close));
    }

    /**
     * Constructs the candle stick.
     * 
     * @param open
     *            see {@link #getOpen()}
     * @param high
     *            see {@link #getHigh()}
     * @param low
     *            see {@link #getLow()}
     * @param close
     *            see {@link #getClose()}
     */
    public CandleStick(final Price open, final Price high, final Price low, final Price close) {
        super(close);
        this.open = open;
        this.high = high;
        this.low = low;
    }

    /**
     * The value at the beginning of the time period this candle stick aggregates.
     * 
     * @return the opening value
     */
    public Price getOpen() {
        return open;
    }

    /**
     * The highest value in the time period this candle stick aggregates.
     * 
     * @return the highest value
     */
    public Price getHigh() {
        return high;
    }

    /**
     * The lowest value in the time period this candle stick aggregates.
     * 
     * @return the lowest value
     */
    public Price getLow() {
        return low;
    }

    /**
     * The absolute difference between the {@link #getHigh()} and the {@link #getLow()} price.
     * 
     * @return the volatility of this {@link CandleStick}
     */
    public Price getVolatility() {
        return high.minus(low);
    }

    /**
     * The strongest market {@link Price} in the {@link TimeFrame} of this candle considering a given market direction.
     * 
     * <p>
     * If the {@link MarketDirection} is up this is the {@link #getHigh()} {@link Price}. If it is down this is the
     * {@link #getLow()} {@link Price}.
     * </p>
     * 
     * @param trend
     *            The {@link MarketDirection} to consider.
     * @return The strongest {@link Price}.
     */
    public Price getStrongest(final MarketDirection trend) {
        return trend == MarketDirection.UP ? high : low;
    }

    /**
     * The strongest market {@link Price} in the {@link TimeFrame} of this candle considering a given market direction.
     * 
     * <p>
     * If the {@link MarketDirection} is up this is the {@link #getLow()} {@link Price}. If it is down this is the
     * {@link #getHigh()} {@link Price}.
     * </p>
     * 
     * @param trend
     *            The {@link MarketDirection} to consider.
     * @return The weakest {@link Price}.
     */
    public Price getWeakest(final MarketDirection trend) {
        return trend == MarketDirection.UP ? low : high;
    }

    /**
     * A {@link CandleStick} is only equal to other {@link CandleStick}s thats {@link Price} values (open, high, low and
     * close) are each equal.
     * 
     * <p>
     * The object to compare with can be an instance of a sub-class of a {@link CandleStick} and still be equal.
     * </p>
     * 
     * @param obj
     *            The object to compare with.
     */
    // CHECKSTYLE:OFF mostly eclipse generated
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof CandleStick)) {
            return false;
        }
        final CandleStick<?> other = (CandleStick<?>) obj;
        if (high == null) {
            if (other.high != null) {
                return false;
            }
        } else if (!high.equals(other.high)) {
            return false;
        }
        if (low == null) {
            if (other.low != null) {
                return false;
            }
        } else if (!low.equals(other.low)) {
            return false;
        }
        if (open == null) {
            if (other.open != null) {
                return false;
            }
        } else if (!open.equals(other.open)) {
            return false;
        }
        return true;
    }

    // CHECKSTYLE:ON

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((high == null) ? 0 : high.hashCode());
        result = prime * result + ((low == null) ? 0 : low.hashCode());
        result = prime * result + ((open == null) ? 0 : open.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "CandleStick [open=" + open.asDouble() + ", high=" + high.asDouble() + ", low=" + low.asDouble()
                + ", close=" + getClose().asDouble() + "]";
    }
}
