package de.voidnode.trading4j.functionality.smoothers;

import java.util.Optional;

import de.voidnode.trading4j.domain.Ratio;

/**
 * The average {@link Ratio} of the most recent {@link Ratio}s with a higher weight for more recent {@link Ratio}s.
 * 
 * <p>
 * The calculation of the {@link ExponentialMovingAverageRatio} is similar to the calculation of the
 * {@link ExponentialMovingAveragePrice}.
 * </p>
 * 
 * @author Raik Bieniek
 */
public class ExponentialMovingAverageRatio implements Smoother<Ratio> {

    private final int smoothingPeriod;
    private final double factor;
    private int aggregatedIs = 0;
    private double ema;

    /**
     * Initializes an instance with all required configuration.
     * 
     * @param smoothingPeriod
     *            The amount of candle sticks that should be considered.
     */
    public ExponentialMovingAverageRatio(final int smoothingPeriod) {
        this.smoothingPeriod = smoothingPeriod;
        this.factor = 2 / (double) (smoothingPeriod + 1);
    }

    @Override
    public Optional<Ratio> smooth(final Ratio dataPoint) {
        if (aggregatedIs == 0) {
            ema = dataPoint.asBasic();
        } else {
            ema += (dataPoint.asBasic() - ema) * factor;
        }
        if (aggregatedIs < smoothingPeriod - 1) {
            aggregatedIs++;
            return Optional.empty();
        }
        return Optional.of(new Ratio(ema));
    }
}
