package de.voidnode.trading4j.indicators.adx;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.Ratio;
import de.voidnode.trading4j.domain.marketdata.MarketData;
import de.voidnode.trading4j.domain.marketdata.WithOhlc;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.functionality.smoothers.ExponentialMovingAverageRatio;
import de.voidnode.trading4j.functionality.smoothers.IndicatorSmoother;

/**
 * Creates the average directional movement index (ADX) that indicates the strength of a trend.
 * 
 * @author Raik Bieniek
 */
public class AverageDirectionalMovementIndexFactory {

    /**
     * Creates a new average directional movement index indicator instance with default smoothing periods.
     * 
     * <p>
     * The smoothing period for the average directional movement index is 14. The smoothing period for the average true
     * range and the average directional indices is 14 too.
     * </p>
     * 
     * @param <C>
     *            The type of candle sticks that are used as input.
     * @return The created instance
     */
    public <C extends MarketData & WithOhlc> Indicator<Ratio, C> createDefaultAdxIndicator() {
        return createDefaultAdxIndicator(14, 14);
    }

    /**
     * Creates a new average directional movement index indicator instance.
     * 
     * @param adxSmoothingPeriod
     *            The smoothing period that should be used to create the average directional index.
     * @param atrAndAdiSmoothingPeriod
     *            The smoothing period that is used for the internal average true range and the average directional
     *            indices.
     * @param <C>
     *            The type of candle sticks that are used as input.
     * @return The created instance
     */
    public <C extends MarketData & WithOhlc> Indicator<Ratio, C> createDefaultAdxIndicator(final int adxSmoothingPeriod,
            final int atrAndAdiSmoothingPeriod) {
        final Indicator<Price, C> plusDm = new DirectionalMovement<>(MarketDirection.UP);
        final Indicator<Ratio, C> plusDi = new DirectionalIndex<>(plusDm, new TrueRange<>());
        final Indicator<Ratio, C> averagePlusDi = new IndicatorSmoother<>(plusDi,
                new ExponentialMovingAverageRatio(atrAndAdiSmoothingPeriod));

        final Indicator<Price, C> minusDm = new DirectionalMovement<>(MarketDirection.DOWN);
        final Indicator<Ratio, C> minusDi = new DirectionalIndex<>(minusDm, new TrueRange<>());
        final Indicator<Ratio, C> averageMinusDi = new IndicatorSmoother<>(minusDi,
                new ExponentialMovingAverageRatio(atrAndAdiSmoothingPeriod));

        final Indicator<Ratio, C> dx = new DirectionalMovementIndex<>(averagePlusDi, averageMinusDi);
        return new IndicatorSmoother<>(dx, new ExponentialMovingAverageRatio(adxSmoothingPeriod));
    }
}
