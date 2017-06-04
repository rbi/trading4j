package de.voidnode.trading4j.domain.marketdata.impl;

import java.time.Instant;

import static java.time.ZoneId.systemDefault;

import de.voidnode.trading4j.domain.marketdata.WithOhlc;
import de.voidnode.trading4j.domain.marketdata.WithTime;
import de.voidnode.trading4j.domain.marketdata.WithTimeFrame;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.timeframe.TimeFrame;

/**
 * A {@link CandleStick} thats date is known.
 *
 * @author Raik Bieniek
 * @param <T>
 *            The time frame that candle stick this candle stick aggregates.
 */
public class DatedCandleStick<T extends TimeFrame> extends CandleStick implements WithTime, WithTimeFrame<T> {

    private final Instant time;

    /**
     * Constructs the candle sticks
     * 
     * <p>
     * See {@link Price#Price(double)} for the meaning of the expected double inputs.
     * </p>
     * 
     * @param time
     *            see {@link #getTime()}
     * @param open
     *            see {@link WithOhlc#getOpen()}
     * @param high
     *            see {@link WithOhlc#getHigh()}
     * @param low
     *            see {@link WithOhlc#getLow()}
     * @param close
     *            see {@link WithOhlc#getClose()}
     */
    public DatedCandleStick(final Instant time, final Price open, final Price high, final Price low, final Price close) {
        super(open, high, low, close);
        this.time = time;
    }

    /**
     * A convenience constructor that takes raw {@link Price}es as input.
     * 
     * <p>
     * See {@link Price#Price(double)} for the meaning of the expected double inputs.
     * </p>
     * 
     * @param time
     *            see {@link #getTime()}
     * @param open
     *            see {@link WithOhlc#getOpen()}
     * @param high
     *            see {@link WithOhlc#getHigh()}
     * @param low
     *            see {@link WithOhlc#getLow()}
     * @param close
     *            see {@link WithOhlc#getClose()}
     */
    public DatedCandleStick(final Instant time, final double open, final double high, final double low,
            final double close) {
        super(open, high, low, close);
        this.time = time;
    }

    @Override
    public Instant getTime() {
        return time;
    }

    /**
     * {@link DatedCandleStick} are only equal to other {@link DatedCandleStick} thats price values are equal and that
     * have the same time.
     * 
     * <p>
     * A {@link DatedCandleStick} can also be equal to instances of sub-class of {@link DatedCandleStick}.
     * </p>
     */
    // CHECKSTYLE:OFF mostly eclipse generated code
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (!(obj instanceof DatedCandleStick))
            return false;
        @SuppressWarnings("rawtypes")
        DatedCandleStick other = (DatedCandleStick) obj;
        if (time == null) {
            if (other.time != null)
                return false;
        } else if (!time.equals(other.time))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((time == null) ? 0 : time.hashCode());
        return result;
    }
    // CHECKSTYLE:ON

    @Override
    public String toString() {
        return "DatedCandleStick [time=" + time.atZone(systemDefault()) + ", open=" + getOpen() + ", high=" + getHigh()
                + ", low=" + getLow() + ", close=" + getClose() + "]";
    }
}
