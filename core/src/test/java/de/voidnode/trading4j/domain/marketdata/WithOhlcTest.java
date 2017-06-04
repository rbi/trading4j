package de.voidnode.trading4j.domain.marketdata;

import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.monetary.Price;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link WithOhlc} works as expected.
 * 
 * @author Raik Bieniek
 */
public class WithOhlcTest {

    /**
     * {@link WithOhlc} should be able to calculate its own volatility.
     */
    @Test
    public void volatilityIsCalculatedCorretly() {
        assertThat(new OhlcImpl(new Price(10), new Price(25), new Price(5), new Price(10)).getVolatility())
                .isEqualTo(new Price(20));
        assertThat(new OhlcImpl(new Price(100), new Price(100), new Price(100), new Price(100)).getVolatility())
                .isEqualTo(new Price(0));
    }

    /**
     * {@link WithOhlc} can return the strongest {@link Price} considering a given {@link MarketDirection}
     * correctly.
     */
    @Test
    public void strongestPriceIsCalculatedCorrectly() {
        final WithOhlc stick1 = new OhlcImpl(new Price(15), new Price(28), new Price(12), new Price(16));
        assertThat(stick1.getStrongest(MarketDirection.UP)).isEqualTo(new Price(28));
        assertThat(stick1.getStrongest(MarketDirection.DOWN)).isEqualTo(new Price(12));

        final WithOhlc stick2 = new OhlcImpl(new Price(30), new Price(15), new Price(15), new Price(29));
        assertThat(stick2.getStrongest(MarketDirection.UP)).isEqualTo(new Price(15));
        assertThat(stick2.getStrongest(MarketDirection.DOWN)).isEqualTo(new Price(15));
    }

    /**
     * {@link WithOhlc} can return the weakest {@link Price} considering a given {@link MarketDirection}
     * correctly.
     */
    @Test
    public void weakestPriceIsCalculatedCorrectly() {
        final WithOhlc stick1 = new OhlcImpl(new Price(951), new Price(9816), new Price(8915), new Price(951));
        assertThat(stick1.getWeakest(MarketDirection.UP)).isEqualTo(new Price(8915));
        assertThat(stick1.getWeakest(MarketDirection.DOWN)).isEqualTo(new Price(9816));

        final WithOhlc stick2 = new OhlcImpl(new Price(9215), new Price(992), new Price(992), new Price(9210));
        assertThat(stick2.getWeakest(MarketDirection.UP)).isEqualTo(new Price(992));
        assertThat(stick2.getWeakest(MarketDirection.DOWN)).isEqualTo(new Price(992));
    }

    /**
     * An {@link WithOhlc} implementation for being able to test the methods of the {@link WithOhlc} interface. 
     */
    private static class OhlcImpl implements WithOhlc {

        private final Price close;
        private final Price open;
        private final Price high;
        private final Price low;

        OhlcImpl(final Price open, final Price high, final Price low, final Price close) {
            this.close = close;
            this.open = open;
            this.high = high;
            this.low = low;
        }

        @Override
        public Price getClose() {
            return close;
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
    }
}
