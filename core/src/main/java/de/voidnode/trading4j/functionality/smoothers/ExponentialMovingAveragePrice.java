package de.voidnode.trading4j.functionality.smoothers;

import java.util.Optional;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.TimeFrame;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * The average {@link Price} of the most recent {@link Price}s with a higher weight for more recent {@link Price}s.
 * 
 * <p>The {@link Smoother} calculates a weighting factor (<code>WF</code>) based on an amount of
 * {@link Price}s (<code>n</code>) the following way.
 * </p>
 * <code>WF = 2 / ( n + 1)</code>
 * <p>
 * The {@link ExponentialMovingAveragePrice} (<code>EMA</code>) for each period of the {@link TimeFrame} (<code>t</code>
 * ) is calculated the following way.
 * </p>
 * <code>EMA(t) = ((Close(t) – EMA(t-1)) * WF) + EMA(t-1)</code>
 * 
 * <p>
 * As long as less than <code>n</code> {@link Price}s where passed to {@link #smooth(Price)} this {@link Indicator}
 * returns an empty {@link Optional}.
 * </p>
 *
 * @author Raik Bieniek
 */
public class ExponentialMovingAveragePrice implements Smoother<Price> {

    private final double factor;
    private final int aggregatedShould;
    private int aggregatedIs = 0;
    private double ema;

    /**
     * Initializes an instance with its fixed data.
     * 
     * @param aggregationCount
     *            The amount of {@link Price}s that the average should be build from.
     */
    public ExponentialMovingAveragePrice(final int aggregationCount) {
        this.aggregatedShould = aggregationCount;
        this.factor = 2 / (double) (aggregationCount + 1);
    }


    @Override
    public Optional<Price> smooth(final Price dataPoint) {
        if (aggregatedIs == 0) {
            ema = dataPoint.asPipette();
        } else {
            ema += (dataPoint.asPipette() - ema) * factor;
        }
        if (aggregatedIs < aggregatedShould - 1) {
            aggregatedIs++;
            return Optional.empty();
        }
        return Optional.of(new Price((long) ema));
    }
}
