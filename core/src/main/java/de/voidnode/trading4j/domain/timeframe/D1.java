package de.voidnode.trading4j.domain.timeframe;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;

import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.temporal.ChronoUnit.DAYS;

/**
 * A 1 day time frame.
 *
 * <p>
 * A day starts at 00:00 o'clock London time except for Monday which starts at 22:00 o'clock at the previous Sunday.
 * </p>
 * 
 * @author Raik Bieniek
 */
public class D1 implements TimeFrame {

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
