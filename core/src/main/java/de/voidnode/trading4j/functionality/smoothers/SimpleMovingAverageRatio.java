package de.voidnode.trading4j.functionality.smoothers;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

import de.voidnode.trading4j.domain.Ratio;

/**
 * The simple average {@link Ratio} of the most recent {@link Ratio}s passed as input.
 *
 * @author Raik Bieniek
 */
public class SimpleMovingAverageRatio implements Smoother<Ratio> {

    private final int aggregationCount;
    private final Queue<Double> ratios;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param aggregationCount
     *            The amount of {@link Ratio}s that the average should be build from.
     */
    public SimpleMovingAverageRatio(final int aggregationCount) {
        this.aggregationCount = aggregationCount;
        this.ratios = new LinkedList<>();
    }

    @Override
    public Optional<Ratio> smooth(final Ratio dataPoint) {
        ratios.add(dataPoint.asBasic());
        if (ratios.size() < aggregationCount) {
            return Optional.empty();
        }
        double sum = 0;
        for (final Double ratio : ratios) {
            sum += ratio;
        }
        final double returnVal = sum / aggregationCount;
        ratios.poll();
        return Optional.of(new Ratio(returnVal));
    }
}
