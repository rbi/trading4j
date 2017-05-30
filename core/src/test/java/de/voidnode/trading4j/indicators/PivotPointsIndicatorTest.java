package de.voidnode.trading4j.indicators;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.marketdata.CandleStick;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.monetary.PriceLevels;
import de.voidnode.trading4j.domain.timeframe.D1;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if the {@link PivotPointsIndicator} works as expected.
 * 
 * @author Raik Bieniek
 */
public class PivotPointsIndicatorTest {

    private final Indicator<PriceLevels, CandleStick<D1>> cut = new PivotPointsIndicator<>();

    /**
     * The indicator should calculate pivot points as specified in its JavaDoc for bullish {@link CandleStick}s.
     */
    @Test
    public void shouldCalculatePivotPointsCorrectlyForBullishCandles() {
        final CandleStick<D1> input = new CandleStick<>(new Price(129183), new Price(130273), new Price(129007),
                new Price(129735));
        
        final PriceLevels pivotPoints = cut.indicate(input).get();
        
        assertThat(pivotPoints.count()).isEqualTo(5);
        assertThat(pivotPoints.get(0)).isEqualTo(new Price(128405));
        assertThat(pivotPoints.get(1)).isEqualTo(new Price(129069));
        assertThat(pivotPoints.get(2)).isEqualTo(new Price(129671));
        assertThat(pivotPoints.get(3)).isEqualTo(new Price(130335));
        assertThat(pivotPoints.get(4)).isEqualTo(new Price(130937));
    }

    /**
     * The indicator should calculate pivot points as specified in its JavaDoc for bearish {@link CandleStick}s.
     */
    @Test
    public void shouldCalculatePivotPointsCorrectlyForBearishCandles() {
        final CandleStick<D1> input = new CandleStick<>(new Price(25182), new Price(25683), new Price(22381),
                new Price(23869));
        
        final PriceLevels pivotPoints = cut.indicate(input).get();
        
        assertThat(pivotPoints.count()).isEqualTo(5);
        assertThat(pivotPoints.get(0)).isEqualTo(new Price(20675));
        assertThat(pivotPoints.get(1)).isEqualTo(new Price(22271));
        assertThat(pivotPoints.get(2)).isEqualTo(new Price(23977));
        assertThat(pivotPoints.get(3)).isEqualTo(new Price(25573));
        assertThat(pivotPoints.get(4)).isEqualTo(new Price(27279));
    }
}
