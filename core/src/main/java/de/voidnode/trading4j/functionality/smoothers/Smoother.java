package de.voidnode.trading4j.functionality.smoothers;

import java.util.Optional;

/**
 * Smoothes a fluctuating values by considering past data.
 * 
 * @author Raik Bieniek
 * @param <T>
 *            The type of the value that is smoothed.
 */
public interface Smoother<T> {

    /**
     * A smoothed value based on the current <code>dataPoint</code> and <code>dataPoints</code> of the past.
     * 
     * @param dataPoint
     *            The current data point.
     * @return The smoothed value or an empty {@link Optional} if not enough data was passed yet.
     */
    Optional<T> smooth(final T dataPoint);
}
