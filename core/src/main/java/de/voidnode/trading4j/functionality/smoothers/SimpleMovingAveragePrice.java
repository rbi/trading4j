package de.voidnode.trading4j.functionality.smoothers;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

import de.voidnode.trading4j.domain.monetary.Price;

/**
 * The simple average {@link Price} of the most recent {@link Price}s passed as input.
 *
 * @author Raik Bieniek
 */
public class SimpleMovingAveragePrice implements Smoother<Price> {

    private final int aggregationCount;
    private final Queue<Long> prices;
    private long sum;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param aggregationCount
     *            The amount of {@link Price}s that the average should be build from.
     */
    public SimpleMovingAveragePrice(final int aggregationCount) {
        this.aggregationCount = aggregationCount;
        this.prices = new LinkedList<>();
        sum = 0;
    }

    @Override
    public Optional<Price> smooth(final Price dataPoint) {
        prices.add(dataPoint.asPipette());
        sum += dataPoint.asPipette();
        if (prices.size() < aggregationCount) {
            return Optional.empty();
        }
        final long returnVal = sum / aggregationCount;
        sum -= prices.poll();
        return Optional.of(new Price(returnVal));
    }
}
