package de.voidnode.trading4j.indicators.adx;

import java.util.Optional;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.TimeFrame.M1;
import de.voidnode.trading4j.domain.marketdata.CandleStick;
import de.voidnode.trading4j.domain.monetary.Price;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link TrueRange} works as expected.
 * 
 * @author Raik Bieniek
 */
public class TrueRangeTest {

    private final Indicator<Price, CandleStick<M1>> cut = new TrueRange<>();

    /**
     * The true range for the first input data can not be calculated an is therefore empty.
     */
    @Test
    public void emptyForFirstData() {
        assertThat(cut.indicate(new CandleStick<>(1.0, 1.0, 1.0, 1.0))).isEmpty();
    }

    /**
     * When <code>volatility(n)</code> is higher than <code>high(n) - close(n-1)</code> and higher than
     * <code>close(n-1) - low(n)</code> then <code>true range(n) = volatility(n)</code>.
     */
    @Test
    public void volatilityIsUsedWhenItsHigherThanHighToCloseAndCloseToLow() {
        cut.indicate(new CandleStick<>(new Price(10), new Price(10), new Price(10), new Price(10)));
        final Optional<Price> tr = cut
                .indicate(new CandleStick<>(new Price(10), new Price(50), new Price(5), new Price(10)));

        assertThat(tr).contains(new Price(45));
    }

    /**
     * When <code>high(n) - close(n-1)</code> is higher than <code>close(n-1) - low(n)</code> and higher than
     * <code>volatility(n)</code> then <code>true range(n) = high(n) - close(n-1)</code>.
     */
    @Test
    public void highToCloseIsUsedWhenItsHigherThatCloseToLowAndVolatility() {
        cut.indicate(new CandleStick<>(new Price(10), new Price(10), new Price(10), new Price(10)));
        final Optional<Price> tr = cut
                .indicate(new CandleStick<>(new Price(15), new Price(20), new Price(15), new Price(10)));

        assertThat(tr).contains(new Price(10));
    }

    /**
     * When <code>close(n-1) - low(n)</code> is higher than <code>high(n) - close(n-1)</code> and higher than
     * <code>volatility(n)</code> then <code>true range(n) = close(n-1) - low(n)</code>.
     */
    @Test
    public void closeToLowIsUsedWhenItsHigherThenHighToCloseAndVolatility() {
        cut.indicate(new CandleStick<>(new Price(20), new Price(20), new Price(20), new Price(20)));
        final Optional<Price> tr = cut
                .indicate(new CandleStick<>(new Price(10), new Price(15), new Price(10), new Price(10)));

        assertThat(tr).contains(new Price(10));
    }

    /**
     * The true range is 0 when the volatility is 0 and there is no gap.
     */
    @Test
    public void zeroWhenVolatilityIsZeroAndThereIsNoGap() {
        cut.indicate(new CandleStick<>(new Price(50), new Price(80), new Price(10), new Price(20)));
        final Optional<Price> tr = cut
                .indicate(new CandleStick<>(new Price(20), new Price(20), new Price(20), new Price(20)));
        assertThat(tr).contains(new Price(0));
    }
}
