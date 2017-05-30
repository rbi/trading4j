package de.voidnode.trading4j.domain.timeframe;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneOffset;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if the {@link TimeFrame} {@link D1} works as expected.
 * 
 * @author Raik Bieniek
 */
public class D1Test {

    /**
     * The next day on Monday to Saturday starts at the next day at 00:00 o'clock London time.
     */
    @Test
    public void nextDayOfMondayToSaturdayStartsAt00OClockLondonTime() {
        final D1 cut = new D1();
        // British Summer Time
        // from Monday to Saturday
        for (int day = 10; day <= 15; day++) {
            assertThat(cut.instantOfNextFrame(
                    LocalDate.of(2010, Month.MAY, day).atTime(20, 4, 52, 5721).toInstant(ZoneOffset.ofHours(2))))
                    .isEqualTo(LocalDate.of(2010, Month.MAY, day + 1).atTime(0, 0, 0, 0)
                            .toInstant(ZoneOffset.ofHours(1)));
        }

        // Greenwich Mean Time
        // from Monday to Saturday
        for (int day = 16; day <= 21; day++) {
            assertThat(cut.instantOfNextFrame(
                    LocalDate.of(1996, Month.DECEMBER, day).atTime(20, 4, 52, 5721).toInstant(ZoneOffset.ofHours(1))))
                    .isEqualTo(LocalDate.of(1996, Month.DECEMBER, day + 1).atTime(0, 0, 0, 0)
                            .toInstant(ZoneOffset.ofHours(0)));
        }
    }

    /**
     * The next day of Saturday before 21:59 o'clock London time is Sunday 22:00 o'clock London time and after that it
     * is Monday 00:00 o'clock London time.
     */
    @Test
    public void nextDayOfSundayBefore22OClockIsSunday22OClockLondonTime() {
        final D1 cut = new D1();
        // British Summer Time
        // Sunday
        assertThat(cut.instantOfNextFrame(
                LocalDate.of(2013, Month.SEPTEMBER, 22).atTime(20, 4, 52, 5721).toInstant(ZoneOffset.ofHours(1))))
                .isEqualTo(LocalDate.of(2013, Month.SEPTEMBER, 22).atTime(22, 0, 0, 0)
                        .toInstant(ZoneOffset.ofHours(1)));

        // Greenwich Mean Time
        // Sunday
        assertThat(cut.instantOfNextFrame(
                LocalDate.of(2004, Month.JANUARY, 11).atTime(20, 4, 52, 5721).toInstant(ZoneOffset.ofHours(0))))
                .isEqualTo(LocalDate.of(2004, Month.JANUARY, 11).atTime(22, 0, 0, 0)
                        .toInstant(ZoneOffset.ofHours(0)));

        // clock change
        assertThat(cut.instantOfNextFrame(
                LocalDate.of(2014, Month.OCTOBER, 26).atTime(0, 15, 0, 0).toInstant(ZoneOffset.ofHours(1)))).isEqualTo(
                LocalDate.of(2014, Month.OCTOBER, 26).atTime(22, 0, 0, 0).toInstant(ZoneOffset.ofHours(0)));
        assertThat(cut.instantOfNextFrame(
                LocalDate.of(2014, Month.MARCH, 30).atTime(0, 26, 7, 1).toInstant(ZoneOffset.ofHours(0)))).isEqualTo(
                LocalDate.of(2014, Month.MARCH, 30).atTime(22, 0, 0, 0).toInstant(ZoneOffset.ofHours(1)));

        // same day but later
        assertThat(cut.instantOfNextFrame(
                LocalDate.of(2013, Month.SEPTEMBER, 22).atTime(22, 5, 52, 5721).toInstant(ZoneOffset.ofHours(1))))
                .isEqualTo(LocalDate.of(2013, Month.SEPTEMBER, 24).atTime(0, 0, 0, 0)
                        .toInstant(ZoneOffset.ofHours(1)));
    }

    /**
     * From Tuesday to Saturday two instances are in the same time frame if they are between 00:00 o'clock London time
     * inclusive and 00:00 o'clock of the next day exclusive.
     */
    @Test
    public void tuesdayToSaturdayTwoTimeFramesAreInTheSameDay00oClockLondonAnd00oClockOfTheNextDay() {
        final D1 cut = new D1();

        // British Summer Time
        // from Tuesday to Saturday
        for (int day = 17; day <= 20; day++) {
            assertThat(cut.areInSameTimeFrame(
                    LocalDate.of(2014, Month.JUNE, day).atTime(0, 0, 0, 0).toInstant(ZoneOffset.ofHours(1)),
                    LocalDate.of(2014, Month.JUNE, day).atTime(23, 59, 59, 99999).toInstant(ZoneOffset.ofHours(1))))
                    .isTrue();
            assertThat(cut.areInSameTimeFrame(
                    LocalDate.of(2014, Month.JUNE, day).atTime(15, 21, 35, 0).toInstant(ZoneOffset.ofHours(1)),
                    LocalDate.of(2014, Month.JUNE, day).atTime(07, 10, 58, 5912).toInstant(ZoneOffset.ofHours(1))))
                    .isTrue();
        }

        // Greenwich Mean Time
        // from Tuesday to Saturday
        for (int day = 13; day <= 16; day++) {
            assertThat(cut.areInSameTimeFrame(
                    LocalDate.of(2005, Month.DECEMBER, day).atTime(0, 0, 0, 0).toInstant(ZoneOffset.ofHours(0)),
                    LocalDate.of(2005, Month.DECEMBER, day).atTime(23, 59, 59, 99999).toInstant(ZoneOffset.ofHours(0))))
                    .isTrue();
        }

        // wrong day
        assertThat(cut.areInSameTimeFrame(
                LocalDate.of(2014, Month.JUNE, 18).atTime(0, 5, 0, 0).toInstant(ZoneOffset.ofHours(1)),
                LocalDate.of(2014, Month.JUNE, 19).atTime(0, 5, 0, 0).toInstant(ZoneOffset.ofHours(1)))).isFalse();
        // wrong year
        assertThat(cut.areInSameTimeFrame(
                LocalDate.of(2013, Month.JUNE, 18).atTime(0, 0, 34, 0).toInstant(ZoneOffset.ofHours(1)),
                LocalDate.of(2014, Month.JUNE, 18).atTime(0, 0, 34, 0).toInstant(ZoneOffset.ofHours(1)))).isFalse();
    }

    /**
     * On Sunday before 22:00 o'clock London time two instances are in the same time frame when they are between 00:00
     * o'clock inclusive and 22:00 o'clock exclusive.
     */
    @Test
    public void sundayEndsBefore22oClockLondonTime() {
        final D1 cut = new D1();

        // British Summer Time
        // Sunday
        assertThat(cut.areInSameTimeFrame(
                LocalDate.of(2014, Month.JUNE, 22).atTime(0, 0, 0, 0).toInstant(ZoneOffset.ofHours(1)),
                LocalDate.of(2014, Month.JUNE, 22).atTime(21, 59, 59, 99999).toInstant(ZoneOffset.ofHours(1))))
                .isTrue();
        assertThat(cut.areInSameTimeFrame(
                LocalDate.of(2014, Month.JUNE, 8).atTime(12, 45, 23, 321).toInstant(ZoneOffset.ofHours(1)),
                LocalDate.of(2014, Month.JUNE, 8).atTime(7, 32, 12, 543).toInstant(ZoneOffset.ofHours(1)))).isTrue();

        // Greenwich Mean Time
        // Sunday
        assertThat(cut.areInSameTimeFrame(
                LocalDate.of(2005, Month.DECEMBER, 18).atTime(0, 0, 0, 0).toInstant(ZoneOffset.ofHours(0)),
                LocalDate.of(2005, Month.DECEMBER, 18).atTime(21, 59, 59, 99999).toInstant(ZoneOffset.ofHours(0))))
                .isTrue();

        assertThat(cut.areInSameTimeFrame(
                LocalDate.of(2014, Month.JUNE, 22).atTime(0, 0, 0, 0).toInstant(ZoneOffset.ofHours(1)),
                LocalDate.of(2014, Month.JUNE, 22).atTime(22, 0, 0, 0).toInstant(ZoneOffset.ofHours(1)))).isFalse();
        assertThat(cut.areInSameTimeFrame(
                LocalDate.of(2014, Month.MAY, 11).atTime(15, 47, 34, 785).toInstant(ZoneOffset.ofHours(1)),
                LocalDate.of(2014, Month.MAY, 11).atTime(23, 4, 32, 78).toInstant(ZoneOffset.ofHours(1)))).isFalse();
        assertThat(cut.areInSameTimeFrame(
                LocalDate.of(2014, Month.JUNE, 22).atTime(0, 0, 0, 0).toInstant(ZoneOffset.ofHours(1)),
                LocalDate.of(2014, Month.JUNE, 23).atTime(0, 0, 0, 0).toInstant(ZoneOffset.ofHours(1)))).isFalse();
        assertThat(cut.areInSameTimeFrame(
                LocalDate.of(2014, Month.JUNE, 22).atTime(0, 0, 0, 0).toInstant(ZoneOffset.ofHours(1)),
                LocalDate.of(2015, Month.JUNE, 22).atTime(0, 0, 0, 0).toInstant(ZoneOffset.ofHours(1)))).isFalse();
    }

    /**
     * Instants between Sunday 22:00 o'clock London time inclusive and Tuesday 00:00 o'clock exclusive are in the same
     * time frame.
     */
    @Test
    public void mondayStartsAt22oClockLondonTimeOfLastSunday() {
        final D1 cut = new D1();
        // British Summer Time
        // Sunday to Monday
        assertThat(cut.areInSameTimeFrame(
                LocalDate.of(1996, Month.AUGUST, 18).atTime(22, 0, 0, 0).toInstant(ZoneOffset.ofHours(1)),
                LocalDate.of(1996, Month.AUGUST, 19).atTime(23, 59, 59, 99999).toInstant(ZoneOffset.ofHours(1))))
                .isTrue();

        // Monday
        assertThat(cut.areInSameTimeFrame(
                LocalDate.of(1996, Month.AUGUST, 19).atTime(12, 43, 52, 43).toInstant(ZoneOffset.ofHours(1)),
                LocalDate.of(1996, Month.AUGUST, 19).atTime(4, 52, 21, 432).toInstant(ZoneOffset.ofHours(1)))).isTrue();

        // Greenwich Mean Time
        // Sunday to Monday
        assertThat(cut.areInSameTimeFrame(
                LocalDate.of(2007, Month.FEBRUARY, 4).atTime(23, 32, 26, 5481).toInstant(ZoneOffset.ofHours(0)),
                LocalDate.of(2007, Month.FEBRUARY, 5).atTime(16, 59, 51, 481).toInstant(ZoneOffset.ofHours(0))))
                .isTrue();

        assertThat(cut.areInSameTimeFrame(
                LocalDate.of(1996, Month.AUGUST, 18).atTime(22, 0, 0, 0).toInstant(ZoneOffset.ofHours(1)),
                LocalDate.of(1996, Month.AUGUST, 20).atTime(0, 0, 0, 0).toInstant(ZoneOffset.ofHours(1)))).isFalse();
        assertThat(cut.areInSameTimeFrame(
                LocalDate.of(2007, Month.FEBRUARY, 5).atTime(23, 32, 26, 5481).toInstant(ZoneOffset.ofHours(0)),
                LocalDate.of(2007, Month.FEBRUARY, 6).atTime(16, 59, 51, 481).toInstant(ZoneOffset.ofHours(0))))
                .isFalse();
    }
}
