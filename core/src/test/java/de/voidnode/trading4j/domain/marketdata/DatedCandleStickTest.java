package de.voidnode.trading4j.domain.marketdata;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.MINUTES;

import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.timeframe.M1;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link DatedCandleStick} works as expected.
 * 
 * @author Raik Bieniek
 */
public class DatedCandleStickTest {

    /**
     * A {@link DatedCandleStick} {@link Object#equals(Object)} only other {@link DatedCandleStick} that have the same
     * value.
     * 
     * <p>
     * A {@link DatedCandleStick} can also be equal to instances of sub-class of {@link DatedCandleStick}.
     * </p>
     */
    @Test
    public void shouldBeEqualToOtherDatedCandleSticksWithSameValue() {
        final Instant someTime = Instant.EPOCH;
        final Instant otherTime = Instant.EPOCH.plus(1, MINUTES);

        assertThat(new DatedCandleStick<>(someTime, 1.0, 2.0, 3.0, 4.0)).isEqualTo(
                new DatedCandleStick<>(someTime, new Price(1.0), new Price(2.0), new Price(3.0), new Price(4.0)));
        assertThat(new DatedCandleStick<>(someTime, 1.0, 2.0, 3.0, 4.0)).isEqualTo(
                new ExemplaryDatedCandleStickSubClass(someTime, 1.0, 2.0, 3.0, 4.0, 42));
        assertThat(new DatedCandleStick<>(someTime, 1.0, 2.592, 1.12345, 5.12345)).isEqualTo(
                new DatedCandleStick<>(someTime, 1.0, 2.592, 1.123449, 5.12345018));

        assertThat(new DatedCandleStick<>(someTime, 1.0, 2.0, 3.0, 4.0)).isNotEqualTo(
                new DatedCandleStick<>(otherTime, 1.0, 2.0, 3.0, 4.0));
        assertThat(new DatedCandleStick<>(someTime, 1.0, 2.592, 1.12345, 5.12345)).isNotEqualTo(
                new DatedCandleStick<>(someTime, 1.0, 2.592, 1.12346, 5.12343));
        assertThat(new DatedCandleStick<>(someTime, 1.0, 2.0, 3.0, 4.0)).isNotEqualTo("not a candle stick");
        assertThat(new DatedCandleStick<>(someTime, 1.0, 2.0, 3.0, 4.02)).isNotEqualTo(null);
    }

    /**
     * An exemplary sub-class of a {@link DatedCandleStick}.
     */
    private static class ExemplaryDatedCandleStickSubClass extends DatedCandleStick<M1> {
        private final int dummyValue;

        /**
         * Initializes an instance.
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
         * @param dummyValue
         *            a value without special meaning other than having an extra field in this class.
         */
        ExemplaryDatedCandleStickSubClass(final Instant time, final double open, final double high,
                final double low, final double close, final int dummyValue) {
            super(time, open, high, low, close);
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
            final ExemplaryDatedCandleStickSubClass other = (ExemplaryDatedCandleStickSubClass) obj;
            if (dummyValue != other.dummyValue) {
                return false;
            }
            return true;
        }
    }
}
