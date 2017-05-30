package de.voidnode.trading4j.indicators.adx;

import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.marketdata.CandleStick;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.timeframe.M1;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link DirectionalMovement} works as expected.
 * 
 * @author Raik Bieniek
 */
public class DirectionalMovementTest {

    private final DirectionalMovement<CandleStick<M1>> dmPlus = new DirectionalMovement<>(MarketDirection.UP);
    private final DirectionalMovement<CandleStick<M1>> dmMinus = new DirectionalMovement<>(MarketDirection.DOWN);

    /**
     * The directional movement is empty for the first market data.
     */
    @Test
    public void emptyOnFirstMarketData() {
        assertThat(dmPlus.indicate(new CandleStick<>(1.0, 1.0, 1.0, 1.0))).isEmpty();
        assertThat(dmMinus.indicate(new CandleStick<>(1.0, 1.0, 1.0, 1.0))).isEmpty();
    }

    ///////////
    /// +DM ///
    ///////////

    /**
     * The +DM is zero if <code>high(t-1) &gt;= high(t)</code>.
     */
    @Test
    public void plusDMIszeroWhenYesterdayHighHigherThanTodayHigh() {
        dmPlus.indicate(new CandleStick<>(1.0, 2.0, 1.0, 1.0));
        assertThat(dmPlus.indicate(new CandleStick<>(1.0, 1.5, 1.0, 1.0))).contains(new Price(0));
    }

    /**
     * The +DM is <code>high(1) - high(t-1)</code> when <code>high(t) &gt; high(t-1)</code> and (
     * <code>low(t-1) &gt;= low(t)</code> or <code>low(t-1) - low(t) &lt; high(t) - high(t-1)</code>).
     */
    @Test
    public void plusDMIsHighDifferenceWhenYesterdayHighLowerThanTodayHighAndLowDifferenceToSmall() {
        dmPlus.indicate(new CandleStick<>(new Price(10), new Price(15), new Price(8), new Price(10)));

        // low today isn't lower
        assertThat(dmPlus.indicate(new CandleStick<>(new Price(10), new Price(20), new Price(12), new Price(10))))
                .contains(new Price(5));

        // low today is lower but low difference is less than high difference
        assertThat(dmPlus.indicate(new CandleStick<>(new Price(10), new Price(26), new Price(11), new Price(10))))
                .contains(new Price(6));
    }

    /**
     * The +DM is zero when <code>high(t) &gt; high(t-1)</code> but ( <code>low(t) &lt; low(t-1)</code> and
     * <code>low(t-1) - low(t) &gt; high(t) - high(t-1)</code>).
     */
    @Test
    public void plusDMIsZeroWhenYesterdayHighLowerThanTodayHighButLowDifferenceToHigh() {
        dmPlus.indicate(new CandleStick<>(10, 15, 12, 10));
        assertThat(dmPlus.indicate(new CandleStick<>(10, 20, 5, 10))).contains(new Price(0));
    }

    ///////////
    /// -DM ///
    ///////////
    /**
     * The -DM is zero if <code>low(t-1) &lt;= low(t)</code>.
     */
    @Test
    public void minusDMIszeroWhenYesterdayLowIsLowerThanTodayLow() {
        dmMinus.indicate(new CandleStick<>(1.0, 1.0, 0.8, 1.0));
        assertThat(dmMinus.indicate(new CandleStick<>(1.0, 1.0, 1.2, 1.0))).contains(new Price(0));
    }

    /**
     * The -DM is <code>low(t-1) - low(t)</code> when <code>low(t) &lt; low(t-1)</code> and (
     * <code>high(t-1) &gt;= high(t)</code> or <code>high(t) - high(t-1) &lt; low(t-1) - low(t)</code>).
     */
    @Test
    public void minusDMIsLowDifferenceWhenYesterdayLowHigherThanTodayLowAndHighDifferenceToSmall() {
        dmMinus.indicate(new CandleStick<>(new Price(10), new Price(20), new Price(15), new Price(10)));

        // high today isn't higher
        assertThat(dmMinus.indicate(new CandleStick<>(new Price(10), new Price(15), new Price(10), new Price(10))))
                .contains(new Price(5));

        // high today is higher but high difference is less than low difference
        assertThat(dmMinus.indicate(new CandleStick<>(new Price(10), new Price(16), new Price(8), new Price(10))))
                .contains(new Price(2));
    }

    /**
     * The -DM is zero when <code>low(t) &lt; low(t-1)</code> but ( <code>high(t) &lt; high(t-1)</code> and
     * <code>high(t) - high(t-1) &lt; low(t-1) - low(t)</code>).
     */
    @Test
    public void minusDMIsZeroWhenYesterdayLowHigherThanTodayLowButHighDifferenceToHigh() {
        dmMinus.indicate(new CandleStick<>(10, 15, 12, 10));
        assertThat(dmMinus.indicate(new CandleStick<>(10, 25, 5, 10))).contains(new Price(0));
    }
}
