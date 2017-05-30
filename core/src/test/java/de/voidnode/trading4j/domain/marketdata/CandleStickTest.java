package de.voidnode.trading4j.domain.marketdata;

import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.timeframe.M1;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link CandleStick} works as expected.
 * 
 * @author Raik Bieniek
 */
public class CandleStickTest {

    /**
     * The {@link CandleStick} should be able to calculate its own volatility.
     */
    @Test
    public void volatilityIsCalculatedCorretly() {
        assertThat(new CandleStick<>(new Price(10), new Price(25), new Price(5), new Price(10)).getVolatility())
                .isEqualTo(new Price(20));
        assertThat(new CandleStick<>(new Price(100), new Price(100), new Price(100), new Price(100)).getVolatility())
                .isEqualTo(new Price(0));
    }

    /**
     * The {@link CandleStick} can return the strongest {@link Price} considering a given {@link MarketDirection} correctly.
     */
    @Test
    public void strongestPriceIsCalculatedCorrectly() {
        final CandleStick<?> stick1 = new CandleStick<>(new Price(15), new Price(28), new Price(12), new Price(16));
        assertThat(stick1.getStrongest(MarketDirection.UP)).isEqualTo(new Price(28));
        assertThat(stick1.getStrongest(MarketDirection.DOWN)).isEqualTo(new Price(12));

        final CandleStick<?> stick2 = new CandleStick<>(new Price(30), new Price(15), new Price(15), new Price(29));
        assertThat(stick2.getStrongest(MarketDirection.UP)).isEqualTo(new Price(15));
        assertThat(stick2.getStrongest(MarketDirection.DOWN)).isEqualTo(new Price(15));
    }

    /**
     * The {@link CandleStick} can return the weakest {@link Price} considering a given {@link MarketDirection} correctly.
     */
    @Test
    public void weakestPriceIsCalculatedCorrectly() {
        final CandleStick<?> stick1 = new CandleStick<>(new Price(951), new Price(9816), new Price(8915),
                new Price(951));
        assertThat(stick1.getWeakest(MarketDirection.UP)).isEqualTo(new Price(8915));
        assertThat(stick1.getWeakest(MarketDirection.DOWN)).isEqualTo(new Price(9816));

        final CandleStick<?> stick2 = new CandleStick<>(new Price(9215), new Price(992), new Price(992),
                new Price(9210));
        assertThat(stick2.getWeakest(MarketDirection.UP)).isEqualTo(new Price(992));
        assertThat(stick2.getWeakest(MarketDirection.DOWN)).isEqualTo(new Price(992));
    }

    /**
     * A {@link CandleStick} is only equal to other {@link CandleStick}s that {@link Price} values each are equal.
     */
    @Test
    public void shouldEqualCandleSticksWithSameValue() {
        assertThat(new CandleStick<>(1.0, 2.0, 3.0, 4.0))
                .isEqualTo(new CandleStick<>(new Price(1.0), new Price(2.0), new Price(3.0), new Price(4.0)));
        assertThat(new CandleStick<>(1.0, 2.0, 3.0, 4.0))
                .isEqualTo(new ExemplaryCandleStickSubClass(1.0, 2.0, 3.0, 4.0, 42));
        assertThat(new CandleStick<>(1.0, 2.592, 1.12345, 5.12345))
                .isEqualTo(new CandleStick<>(1.0, 2.592, 1.123449, 5.12345018));

        assertThat(new CandleStick<>(1.0, 2.592, 1.12345, 5.12345))
                .isNotEqualTo(new CandleStick<>(1.0, 2.592, 1.12346, 5.12343));
        assertThat(new CandleStick<>(1.0, 2.592, 1.12345, 5.12345)).isNotEqualTo("not a candle stick");
        assertThat(new CandleStick<>(1.0, 2.592, 1.12345, 5.12345)).isNotEqualTo(null);
    }

    /**
     * An exemplary sub-class of a {@link CandleStick}.
     */
    private static class ExemplaryCandleStickSubClass extends CandleStick<M1> {
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
