package de.voidnode.trading4j.domain.timeframe;

import java.time.Instant;
import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;

/**
 * A 30 minutes time frame.
 * 
 * @author Raik Bieniek
 */
public class M30 implements TimeFrame {

    @Override
    public Instant instantOfNextFrame(final Instant current) {
        final OffsetDateTime time = current.atOffset(UTC);
        final int minutes = time.get(MINUTE_OF_HOUR);
        return time.withSecond(0).withNano(0).plusMinutes(30 - (minutes % 30)).toInstant();
    }

    @Override
    public boolean areInSameTimeFrame(final Instant instant1, final Instant instant2) {
        final OffsetDateTime localBase = instant1.atOffset(UTC).withSecond(0).withNano(0);
        final OffsetDateTime localStart = localBase.withMinute(localBase.getMinute() - localBase.getMinute() % 30);

        final Instant start = localStart.toInstant();
        final Instant startOfNext = localStart.plusMinutes(30).toInstant();

        return instant2.equals(start) || (instant2.isAfter(start) && instant2.isBefore(startOfNext));
    }
}
