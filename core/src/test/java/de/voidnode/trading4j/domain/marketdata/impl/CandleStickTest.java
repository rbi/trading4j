package de.voidnode.trading4j.domain.marketdata.impl;

import de.voidnode.trading4j.domain.monetary.Price;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link CandleStick} works as expected.
 * 
 * @author Raik Bieniek
 */
public class CandleStickTest {

    /**
     * A {@link CandleStick} is only equal to other {@link CandleStick}s that {@link Price} values each are equal.
     */
    @Test
    public void shouldEqualCandleSticksWithSameValue() {
        assertThat(new CandleStick(1.0, 2.0, 3.0, 4.0))
                .isEqualTo(new CandleStick(new Price(1.0), new Price(2.0), new Price(3.0), new Price(4.0)));
        assertThat(new CandleStick(1.0, 2.0, 3.0, 4.0))
                .isEqualTo(new ExemplaryCandleStickSubClass(1.0, 2.0, 3.0, 4.0, 42));
        assertThat(new CandleStick(1.0, 2.592, 1.12345, 5.12345))
                .isEqualTo(new CandleStick(1.0, 2.592, 1.123449, 5.12345018));

        assertThat(new CandleStick(1.0, 2.592, 1.12345, 5.12345))
                .isNotEqualTo(new CandleStick(1.0, 2.592, 1.12346, 5.12343));
        assertThat(new CandleStick(1.0, 2.592, 1.12345, 5.12345)).isNotEqualTo("not a candle stick");
        assertThat(new CandleStick(1.0, 2.592, 1.12345, 5.12345)).isNotEqualTo(null);
    }

    /**
     * An exemplary sub-class of a {@link CandleStick}.
     */
    private static class ExemplaryCandleStickSubClass extends CandleStick {
        private final int dummyValue;

        /**
         * Initializes an instance.
         * 
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
        ExemplaryCandleStickSubClass(final double open, final double high, final double low, final double close,
                final int dummyValue) {
            super(open, high, low, close);
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
            final ExemplaryCandleStickSubClass other = (ExemplaryCandleStickSubClass) obj;
            if (dummyValue != other.dummyValue) {
                return false;
            }
            return true;
        }
    }
}
