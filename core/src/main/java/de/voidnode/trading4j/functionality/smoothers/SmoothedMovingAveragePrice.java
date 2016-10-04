package de.voidnode.trading4j.functionality.smoothers;

import java.util.Optional;

import de.voidnode.trading4j.domain.monetary.Price;

/**
 * The smoothed moving average for {@link Price}.
 * 
 * @author Raik Bieniek
 */
public class SmoothedMovingAveragePrice implements Smoother<Price> {

    private final int smoothingPeriod;
    private final int aggregationCount;
    private int count;
    private long smma;

    /**
     * Initializes the moving average with its dependencies.
     * 
     * @param smoothingPeriod
     *            The period over which this moving average should smooth.
     */
    public SmoothedMovingAveragePrice(final int smoothingPeriod) {
        this.smoothingPeriod = smoothingPeriod;
        this.aggregationCount = smoothingPeriod - 1;
    }

    @Override
    public Optional<Price> smooth(final Price dataPoint) {
        if (count < aggregationCount) {
            smma += dataPoint.asPipette();
            count++;
            return Optional.empty();
        }
        if (count == aggregationCount) {
            smma = Math.round((smma + dataPoint.asPipette()) / (double) 3);
            count++;
            return Optional.of(new Price(smma));
        }
        smma = Math.round((smma * aggregationCount + dataPoint.asPipette()) / (double) smoothingPeriod);
        return Optional.of(new Price(smma));
    }
}
