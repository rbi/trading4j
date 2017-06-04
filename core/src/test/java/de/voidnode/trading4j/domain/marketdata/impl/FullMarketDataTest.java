package de.voidnode.trading4j.domain.marketdata.impl;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.MINUTES;

import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.VolumeUnit;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.timeframe.M1;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link FullMarketData} works as expected.
 * 
 * @author Raik Bieniek
 */
public class FullMarketDataTest {

    /**
     * A {@link FullMarketData} {@link Object#equals(Object)} only other {@link FullMarketData} that have the same
     * value.
     * 
     * <p>
     * A {@link FullMarketData} can also be equal to instances of sub-class of {@link FullMarketData}.
     * </p>
     */
    @Test
    public void shouldBeEqualToOtherFatCandleSticksWithSameValue() {
        final Instant someTime = Instant.EPOCH;
        final Instant otherTime = Instant.EPOCH.plus(1, MINUTES);

        assertThat(
                new FullMarketData<>(someTime, new Price(1.0), new Price(2.0), new Price(3.0), new Price(4.0),
                        new Price(5.0), new Volume(5, VolumeUnit.LOT), 50)).isEqualTo(
                new FullMarketData<>(someTime, new Price(1.0), new Price(2.0), new Price(3.0), new Price(4.0),
                        new Price(5.0), new Volume(50, VolumeUnit.MINI_LOT), 50));
        assertThat(
                new FullMarketData<>(someTime, new Price(1.0), new Price(2.0), new Price(3.0), new Price(4.0),
                        new Price(5.0), new Volume(5, VolumeUnit.LOT), 50)).isEqualTo(
                new ExemplaryFatCandleStickSubClass(someTime, new Price(1.0), new Price(2.0), new Price(3.0),
                        new Price(4.0), new Price(5.0), new Volume(5, VolumeUnit.LOT), 50, 42));

        assertThat(
                new FullMarketData<>(someTime, new Price(1.0), new Price(2.0), new Price(3.0), new Price(4.0),
                        new Price(5.0), new Volume(5, VolumeUnit.LOT), 50)).isNotEqualTo(
                new FullMarketData<>(otherTime, new Price(1.0), new Price(2.0), new Price(3.0), new Price(4.0),
                        new Price(5.0), new Volume(5, VolumeUnit.LOT), 32));
        assertThat(
                new FullMarketData<>(someTime, new Price(1.0), new Price(2.0), new Price(3.0), new Price(4.0),
                        new Price(5.0), new Volume(5, VolumeUnit.LOT), 5432)).isNotEqualTo(
                new FullMarketData<>(someTime, new Price(1.0), new Price(2.0), new Price(3.0), new Price(4.0),
                        new Price(5.0), new Volume(5, VolumeUnit.LOT), 50));
        assertThat(
                new FullMarketData<>(someTime, new Price(1.0), new Price(2.0), new Price(3.0), new Price(4.0),
                        new Price(5.0), new Volume(5, VolumeUnit.LOT), 50)).isNotEqualTo("not a candle stick");
        assertThat(
                new FullMarketData<>(someTime, new Price(1.0), new Price(2.0), new Price(3.0), new Price(4.0),
                        new Price(5.0), new Volume(5, VolumeUnit.LOT), 50)).isNotEqualTo(null);
    }

    /**
     * An exemplary sub-class of a {@link FullMarketData}.
     */
    private static class ExemplaryFatCandleStickSubClass extends FullMarketData<M1> {
        private final int dummyValue;

        /**
         * Constructs a new instance.
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
         * @param dummyValue
         *            a dummy value to distinguish this sub-class from the super class.
         */
        ExemplaryFatCandleStickSubClass(final Instant time, final Price open, final Price high, final Price low,
                final Price close, final Price spread, final Volume volume, final long tickCount, final int dummyValue) {
            super(time, open, high, low, close, spread, volume, tickCount);
            this.dummyValue = dummyValue;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + dummyValue;
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ExemplaryFatCandleStickSubClass other = (ExemplaryFatCandleStickSubClass) obj;
            if (dummyValue != other.dummyValue) {
                return false;
            }
            return true;
        }
    }
}
