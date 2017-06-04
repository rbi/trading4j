package de.voidnode.trading4j.indicators;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.marketdata.MarketData;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.functionality.smoothers.ExponentialMovingAveragePrice;
import de.voidnode.trading4j.functionality.smoothers.SimpleMovingAveragePrice;
import de.voidnode.trading4j.functionality.smoothers.SmoothedMovingAveragePrice;
import de.voidnode.trading4j.functionality.smoothers.SmoothedPriceIndicator;

/**
 * Creates moving average {@link Indicator}s.
 * 
 * @author Raik Bieniek
 */
public class MovingAverageIndicatorFactory {

    /**
     * Creates a new simple moving average {@link Indicator}.
     * 
     * @param aggregationCount
     *            The amount of {@link MarketData}s that the average should be build from.
     * @param <MP>
     *            The concrete {@link MarketData} type that is used as data input.
     * @return The created indicator.
     */
    public <MP extends MarketData> Indicator<Price, MP> createSimpleMovingAverage(final int aggregationCount) {
        return new SmoothedPriceIndicator<>(new SimpleMovingAveragePrice(aggregationCount));
    }

    /**
     * Creates a new exponential moving average {@link Indicator}.
     * 
     * @param aggregationCount
     *            The amount of {@link MarketData}s that the average should be build from.
     * @param <MP>
     *            The concrete {@link MarketData} type that is used as data input.
     * @return The created indicator.
     */
    public <MP extends MarketData> Indicator<Price, MP> createExponentialMovingAverage(final int aggregationCount) {
        return new SmoothedPriceIndicator<>(new ExponentialMovingAveragePrice(aggregationCount));
    }

    /**
     * Creates a new smoothed moving average {@link Indicator}.
     * 
     * @param smoothingPeriod
     *            The period over which this moving average should smooth.
     * @param <MP>
     *            The concrete {@link MarketData} type that is used as data input.
     * @return The created indicator.
     */
    public <MP extends MarketData> Indicator<Price, MP> createSmoothedMovingAverage(final int smoothingPeriod) {
        return new SmoothedPriceIndicator<>(new SmoothedMovingAveragePrice(smoothingPeriod));
    }
}
