package de.voidnode.trading4j.domain.timeframe;

import java.time.Instant;
import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;

/**
 * A 1 hour time frame.
 * 
 * @author Raik Bieniek
 *
 */
public class H1 implements TimeFrame {

    @Override
    public Instant instantOfNextFrame(final Instant current) {
        final OffsetDateTime time = current.atOffset(UTC);
        return time.withSecond(0).withNano(0).withMinute(0).plusHours(1).toInstant();
    }

    @Override
    public boolean areInSameTimeFrame(final Instant instant1, final Instant instant2) {
        final OffsetDateTime localStart = instant1.atOffset(UTC).withMinute(0).withSecond(0).withNano(0);

        final Instant start = localStart.toInstant();
        final Instant startOfNext = localStart.plusHours(1).toInstant();

        return instant2.equals(start) || (instant2.isAfter(start) && instant2.isBefore(startOfNext));
    }

}
