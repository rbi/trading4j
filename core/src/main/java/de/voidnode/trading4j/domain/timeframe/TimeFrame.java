package de.voidnode.trading4j.domain.timeframe;

import java.time.Instant;

/**
 * A concept for the division of linear time into consecutive chunks.
 * 
 * <p>
 * Often each chunk has the same length. Each point in time must be assignable to exactly one of the chunks.
 * </p>
 * 
 * @author Raik Bieniek
 */
public interface TimeFrame {

    /**
     * Checks if two {@link Instant}s belong to the same time frame or if they belong to different ones.
     * 
     * @param instant1
     *            The first {@link Instant} for the comparison.
     * @param instant2
     *            The second {@link Instant} for the comparison.
     * @return <code>true</code> if they both belong the the same time frame and <code>false</code> if not.
     */
    boolean areInSameTimeFrame(Instant instant1, Instant instant2);

    /**
     * The earliest instant {@link Instant} that is later in time then the {@link Instant} passed as argument and that
     * is the first instant of the next time frame.
     * 
     * @param current
     *            An arbitrary {@link Instant}.
     * @return The {@link Instant} for the succeeding frame.
     */
    Instant instantOfNextFrame(Instant current);

}
