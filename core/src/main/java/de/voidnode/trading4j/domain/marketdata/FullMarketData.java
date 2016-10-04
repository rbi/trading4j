package de.voidnode.trading4j.domain.marketdata;

import java.time.Instant;

import static java.time.ZoneId.systemDefault;

import de.voidnode.trading4j.domain.TimeFrame;
import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * Contains all data that can be directly associated with a single {@link CandleStick}.
 *
 * @author Raik Bieniek
 * @param <T>
 *            The time frame that candle stick this candle stick aggregates.
 */
public class FullMarketData<T extends TimeFrame> extends DatedCandleStick<T> {

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

    /**
     * The difference between the ask {@link Price}, the {@link Price} for that the asset can be bought, and the bid
     * {@link Price}, the {@link Price} for that the asset can be sold.
     * 
     * <p>
     * Prices of {@link #getOpen()}, {@link #getHigh()}, {@link #getLow()} and {@link #getClose()} are the bid
     * {@link Price}. The spread has to be added to them to get the ask {@link Price}. If there is any markup fee it is
     * contained in this spread.
     * </p>
     * 
     * @return The spread.
     */
    public Price getSpread() {
        return spread;
    }

    /**
     * The volume of the asset that was traded in the {@link TimeFrame} of this {@link FullMarketData}.
     * 
     * @return The volume
     */
    public Volume getVolume() {
        return volume;
    }

    /**
     * The amount of individual trades that where executed in the {@link TimeFrame} of this {@link FullMarketData}.
     * 
     * @return The trade amount.
     */
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
