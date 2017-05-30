package de.voidnode.trading4j.domain.timeframe;

import java.time.Instant;
import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;

/**
 * A 15 minutes time frame.
 * 
 * @author Raik Bieniek
 */
public class M15 implements TimeFrame {

    @Override
    public Instant instantOfNextFrame(final Instant current) {
        final OffsetDateTime time = current.atOffset(UTC);
        final int minutes = time.get(MINUTE_OF_HOUR);
        return time.withSecond(0).withNano(0).plusMinutes(15 - (minutes % 15)).toInstant();
    }

    @Override
    public boolean areInSameTimeFrame(final Instant instant1, final Instant instant2) {
        final OffsetDateTime localBase = instant1.atOffset(UTC).withSecond(0).withNano(0);
        final OffsetDateTime localStart = localBase.withMinute(localBase.getMinute() - localBase.getMinute() % 15);

        final Instant start = localStart.toInstant();
        final Instant startOfNext = localStart.plusMinutes(15).toInstant();

        return instant2.equals(start) || (instant2.isAfter(start) && instant2.isBefore(startOfNext));
    }
}
