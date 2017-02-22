package de.voidnode.trading4j.domain;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;

import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoUnit.DAYS;

import de.voidnode.trading4j.domain.marketdata.CandleStick;

/**
 * The aggregation time frame of a {@link CandleStick}.
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

    /**
     * A 1 minute time frame.
     */
    class M1 implements TimeFrame {

        @Override
        public Instant instantOfNextFrame(final Instant current) {
            return current.atOffset(UTC).withSecond(0).withNano(0).plusMinutes(1).toInstant();
        }

        @Override
        public boolean areInSameTimeFrame(final Instant instant1, final Instant instant2) {
            final OffsetDateTime localStart = instant1.atOffset(UTC).withSecond(0).withNano(0);
            final Instant start = localStart.toInstant();
            final Instant startOfNext = localStart.plusMinutes(1).toInstant();
            return instant2.equals(start) || (instant2.isAfter(start) && instant2.isBefore(startOfNext));
        }
    }

    /**
     * A 5 minute time frame.
     */
    class M5 implements TimeFrame {

        @Override
        public Instant instantOfNextFrame(final Instant current) {
            final OffsetDateTime time = current.atOffset(UTC);
            final int minutes = time.get(MINUTE_OF_HOUR);
            return time.withSecond(0).withNano(0).plusMinutes(5 - (minutes % 5)).toInstant();
        }

        @Override
        public boolean areInSameTimeFrame(final Instant instant1, final Instant instant2) {
            final OffsetDateTime localBase = instant1.atOffset(UTC).withSecond(0).withNano(0);
            final OffsetDateTime localStart = localBase.withMinute(localBase.getMinute() - localBase.getMinute() % 5);

            final Instant start = localStart.toInstant();
            final Instant startOfNext = localStart.plusMinutes(5).toInstant();

            return instant2.equals(start) || (instant2.isAfter(start) && instant2.isBefore(startOfNext));
        }
    }

    /**
     * A 15 minutes time frame.
     */
    class M15 implements TimeFrame {

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

    /**
     * A 30 minutes time frame.
     */
    class M30 implements TimeFrame {

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

    /**
     * A 1 hour time frame.
     *
     */
    class H1 implements TimeFrame {

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

    /**
     * A 1 day time frame.
     * 
     * <p>
     * A day starts at 00:00 o'clock London time except for Monday which starts at 22:00 o'clock at the previous Sunday.
     * </p>
     */
    class D1 implements TimeFrame {

        private static final ZoneId BRITISH_TIME_ZONE = ZoneId.of("Europe/London");

        @Override
        public Instant instantOfNextFrame(final Instant instant) {
            final ZonedDateTime britishTime = instant.atZone(BRITISH_TIME_ZONE);
            final DayOfWeek britishDay = britishTime.getDayOfWeek();
            if (britishDay != SUNDAY) {
                return removeMinutesAndLess(britishTime).withHour(0).plus(1, DAYS).toInstant();
            }

            final ZonedDateTime britishTimeCorrectTimeValues = removeMinutesAndLess(britishTime).withHour(22);
            if (britishTime.getDayOfWeek() == SUNDAY) {
                if (britishTime.isBefore(britishTimeCorrectTimeValues)) {
                    return britishTimeCorrectTimeValues.toInstant();
                }
                return removeMinutesAndLess(britishTime).withHour(0).plus(2, DAYS).toInstant();
            }
            return britishTimeCorrectTimeValues.with(TemporalAdjusters.next(SUNDAY)).toInstant();
        }

        private ZonedDateTime removeMinutesAndLess(final ZonedDateTime time) {
            return time.withSecond(0).withNano(0).withMinute(0);
        }

        @Override
        public boolean areInSameTimeFrame(final Instant instant1, final Instant instant2) {

            final ZonedDateTime britishTime = instant1.atZone(BRITISH_TIME_ZONE);
            final DayOfWeek britishDay = britishTime.getDayOfWeek();

            final ZonedDateTime localBase = britishTime.withHour(0).withMinute(0).withSecond(0).withNano(0);

            final ZonedDateTime start;
            final ZonedDateTime end;

            if (britishDay == SUNDAY) {
                if (britishTime.isBefore(localBase.withHour(22))) {
                    start = localBase;
                    end = localBase.withHour(22);
                } else {
                    start = localBase.withHour(22);
                    end = localBase.plusDays(2);
                }
            } else if (britishDay == MONDAY) {
                start = localBase.minusDays(1).withHour(22);
                end = localBase.plusDays(1);
            } else {
                start = localBase;
                end = localBase.plusDays(1);
            }

            return instant2.equals(start.toInstant())
                    || (instant2.isAfter(start.toInstant()) && instant2.isBefore(end.toInstant()));
        }
    }
}
