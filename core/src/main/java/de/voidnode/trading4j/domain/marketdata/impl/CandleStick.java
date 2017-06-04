package de.voidnode.trading4j.domain.marketdata.impl;

import de.voidnode.trading4j.domain.marketdata.WithOhlc;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * Contains the data for a single candle stick chart.
 *
 * @author Raik Bieniek
 */
public class CandleStick extends BasicMarketData implements WithOhlc {

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

    @Override
    public Price getOpen() {
        return open;
    }

    @Override
    public Price getHigh() {
        return high;
    }

    @Override
    public Price getLow() {
        return low;
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
        final CandleStick other = (CandleStick) obj;
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
