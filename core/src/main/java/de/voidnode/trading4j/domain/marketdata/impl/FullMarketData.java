package de.voidnode.trading4j.domain.marketdata.impl;

import java.time.Instant;

import static java.time.ZoneId.systemDefault;

import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.marketdata.MarketData;
import de.voidnode.trading4j.domain.marketdata.WithSpread;
import de.voidnode.trading4j.domain.marketdata.WithTickCount;
import de.voidnode.trading4j.domain.marketdata.WithVolume;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.timeframe.TimeFrame;

/**
 * Contains all data that can be directly associated with a single {@link MarketData}.
 *
 * @author Raik Bieniek
 * @param <T>
 *            The time frame that candle stick this candle stick aggregates.
 */
public class FullMarketData<T extends TimeFrame> extends DatedCandleStick<T> implements WithVolume, WithSpread, WithTickCount {

    private final Price spread;
    private final Volume volume;
    private final long tickCount;

    /**
     * {@link FullMarketData}s should be build with the {@link MutableFullMarketData}. Therefore the constructor is
     * package private.
     * 
     * @param time
     *            see {@link #getTime()}
     * @param open
     *            see {@link #getOpen()}
     * @param high
     *            see {@link #getHigh()}
     * @param low
     *            see {@link #getLow()}
     * @param close
     *            see {@link #getClose()}
     * @param spread
     *            see {@link #getSpread()}
     * @param volume
     *            see {@link #getVolume()}
     * @param tickCount
     *            see {@link #getTickCount()}
     */
    FullMarketData(final Instant time, final Price open, final Price high, final Price low, final Price close,
            final Price spread, final Volume volume, final long tickCount) {
        super(time, open, high, low, close);
        this.spread = spread;
        this.volume = volume;
        this.tickCount = tickCount;
    }

    @Override
    public Price getSpread() {
        return spread;
    }

    @Override
    public Volume getVolume() {
        return volume;
    }

    @Override
    public long getTickCount() {
        return tickCount;
    }

    /**
     * {@link FullMarketData}s are only equal to other {@link FullMarketData} thats fields are equal.
     * 
     * <p>
     * A {@link FullMarketData} can also be equal to instances of sub-class of {@link FullMarketData}.
     * </p>
     */
    // CHECKSTYLE:OFF mostly eclipse generated code
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof FullMarketData)) {
            return false;
        }
        @SuppressWarnings("rawtypes")
        FullMarketData other = (FullMarketData) obj;
        if (spread == null) {
            if (other.spread != null) {
                return false;
            }
        } else if (!spread.equals(other.spread)) {
            return false;
        }
        if (tickCount != other.tickCount) {
            return false;
        }
        if (volume == null) {
            if (other.volume != null) {
                return false;
            }
        } else if (!volume.equals(other.volume)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((spread == null) ? 0 : spread.hashCode());
        result = prime * result + (int) (tickCount ^ (tickCount >>> 32));
        result = prime * result + ((volume == null) ? 0 : volume.hashCode());
        return result;
    }
    // CHECKSTYLE:ON

    @Override
    public String toString() {
        return "FatCandleStick [time=" + getTime().atZone(systemDefault()) + ", open=" + getOpen() + ", high="
                + getHigh() + ", low=" + getLow() + ", close=" + getClose() + ", spread=" + spread + ", volume="
                + volume + ", tickCount=" + tickCount + "]";
    }
}
