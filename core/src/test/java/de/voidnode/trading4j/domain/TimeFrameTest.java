package de.voidnode.trading4j.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneOffset;

import static java.time.ZoneOffset.UTC;

import de.voidnode.trading4j.domain.TimeFrame.D1;
import de.voidnode.trading4j.domain.TimeFrame.H1;
import de.voidnode.trading4j.domain.TimeFrame.M1;
import de.voidnode.trading4j.domain.TimeFrame.M15;
import de.voidnode.trading4j.domain.TimeFrame.M30;
import de.voidnode.trading4j.domain.TimeFrame.M5;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if the standard {@link TimeFrame}s in the class {@link TimeFrame} work as expected.
 *
 * @author Raik Bieniek
 **/
public class TimeFrameTest {

    // ////////
    // / M1 ///
    // ////////

    /**
     * Two {@link Instant}s are in the same time frame when they have they are in exactly the same minute.
     */
    @Test
    public void m1IinstantsAreInSameTimeFrameWhenTheyAreInTheSameMinute() {
        final M1 cut = new M1();

        assertThat(cut.areInSameTimeFrame(LocalDate.of(2014, Month.JULY, 31).atTime(20, 15, 59, 99999).toInstant(UTC),
                LocalDate.of(2014, Month.JULY, 31).atTime(20, 15, 0, 0).toInstant(UTC))).isTrue();
        assertThat(cut.areInSameTimeFrame(LocalDate.of(2048, Month.FEBRUARY, 8).atTime(9, 16, 42, 123).toInstant(UTC),
                LocalDate.of(2048, Month.FEBRUARY, 8).atTime(9, 16, 32, 1254).toInstant(UTC))).isTrue();

        assertThat(cut.areInSameTimeFrame(LocalDate.of(2014, Month.JULY, 31).atTime(20, 15, 0, 0).toInstant(UTC),
                LocalDate.of(2014, Month.JULY, 31).atTime(20, 16, 0, 0).toInstant(UTC))).isFalse();
        assertThat(cut.areInSameTimeFrame(LocalDate.of(1983, Month.MAY, 12).atTime(12, 56, 54, 0).toInstant(UTC),
                LocalDate.of(1983, Month.MAY, 12).atTime(12, 57, 54, 0).toInstant(UTC))).isFalse();
        assertThat(
                cut.areInSameTimeFrame(LocalDate.of(2132, Month.SEPTEMBER, 27).atTime(15, 48, 14, 43432).toInstant(UTC),
                        LocalDate.of(2132, Month.SEPTEMBER, 28).atTime(15, 48, 14, 43432).toInstant(UTC))).isFalse();
        assertThat(cut.areInSameTimeFrame(LocalDate.of(2004, Month.DECEMBER, 5).atTime(4, 7, 0, 3234).toInstant(UTC),
                LocalDate.of(2006, Month.DECEMBER, 5).atTime(4, 7, 0, 3234).toInstant(UTC))).isFalse();
    }

    /**
     * {@link M1#instantOfNextFrame(Instant)} should return the next minute.
     */
    @Test
    public void m1InstantOfNextFrameShouldReturnNextMinute() {
        final M1 cut = new M1();

        assertThat(cut.instantOfNextFrame(LocalDate.of(2004, Month.AUGUST, 12).atTime(13, 53, 59, 0).toInstant(UTC)))
                .isEqualTo(LocalDate.of(2004, Month.AUGUST, 12).atTime(13, 54, 0, 0).toInstant(UTC));

        assertThat(cut.instantOfNextFrame(LocalDate.of(1993, Month.MARCH, 8).atTime(12, 54, 59, 0).toInstant(UTC)))
                .isEqualTo(LocalDate.of(1993, Month.MARCH, 8).atTime(12, 55, 00, 0).toInstant(UTC));

        assertThat(cut.instantOfNextFrame(LocalDate.of(1875, Month.OCTOBER, 24).atTime(23, 32, 58, 0).toInstant(UTC)))
                .isEqualTo(LocalDate.of(1875, Month.OCTOBER, 24).atTime(23, 33, 00, 0).toInstant(UTC));

        assertThat(
                cut.instantOfNextFrame(LocalDate.of(2654, Month.JANUARY, 10).atTime(05, 12, 59, 31842).toInstant(UTC)))
                        .isEqualTo(LocalDate.of(2654, Month.JANUARY, 10).atTime(05, 13, 0, 0).toInstant(UTC));
    }

    // ////////
    // / M5 ///
    // ////////

    /**
     * Two instants are in the same {@link M5} time when all fields higher than minutes are the same and both instants
     * are between the last minute that has <code>m % 5 = 0</code> inclusive and <code>m + 5</code> exclusive.
     * 
     * <p>
     * Time frames start at minutes 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50 and 55.
     * </p>
     */
    @Test
    public void m5InstantsAreInSameTimeFrameWhenTheyAreInTheSame5MinuteFrame() {
        final M5 cut = new M5();

        assertThat(cut.areInSameTimeFrame(LocalDate.of(2014, Month.JULY, 7).atTime(20, 4, 59, 99999).toInstant(UTC),
                LocalDate.of(2014, Month.JULY, 7).atTime(20, 0, 0, 0).toInstant(UTC))).isTrue();

        assertThat(cut.areInSameTimeFrame(LocalDate.of(1951, Month.NOVEMBER, 15).atTime(15, 5, 32, 0).toInstant(UTC),
                LocalDate.of(1951, Month.NOVEMBER, 15).atTime(15, 7, 12, 653).toInstant(UTC))).isTrue();

        assertThat(cut.areInSameTimeFrame(LocalDate.of(2014, Month.JULY, 7).atTime(20, 0, 0, 0).toInstant(UTC),
                LocalDate.of(2014, Month.JULY, 7).atTime(20, 5, 0, 0).toInstant(UTC))).isFalse();
        assertThat(cut.areInSameTimeFrame(LocalDate.of(2341, Month.OCTOBER, 24).atTime(14, 1, 0, 0).toInstant(UTC),
                LocalDate.of(2341, Month.OCTOBER, 25).atTime(14, 1, 0, 0).toInstant(UTC))).isFalse();
        assertThat(cut.areInSameTimeFrame(LocalDate.of(2003, Month.DECEMBER, 24).atTime(17, 14, 0, 0).toInstant(UTC),
                LocalDate.of(2006, Month.DECEMBER, 24).atTime(17, 14, 0, 0).toInstant(UTC))).isFalse();
        assertThat(cut.areInSameTimeFrame(LocalDate.of(1298, Month.FEBRUARY, 10).atTime(12, 15, 21, 0).toInstant(UTC),
                LocalDate.of(1298, Month.JANUARY, 10).atTime(12, 15, 21, 0).toInstant(UTC))).isFalse();
    }

    /**
     * {@link M5#instantOfNextFrame(Instant)} should return the instant with the minutes set to the earliest possible
     * {@link Instant} that is later than the {@link Instant} past and thats minutes are a multiple of 5.
     */
    @Test
    public void m5InstantOfNextFrameShouldReturnTheNextInstantThatsMinutesAreAMultipleOfFive() {
        final M5 cut = new M5();

        assertThat(cut.instantOfNextFrame(LocalDate.of(2074, Month.MARCH, 30).atTime(20, 4, 0, 0).toInstant(UTC)))
                .isEqualTo(LocalDate.of(2074, Month.MARCH, 30).atTime(20, 5, 0, 0).toInstant(UTC));
        assertThat(cut.instantOfNextFrame(LocalDate.of(1999, Month.APRIL, 23).atTime(15, 59, 0, 0).toInstant(UTC)))
                .isEqualTo(LocalDate.of(1999, Month.APRIL, 23).atTime(16, 0, 0, 0).toInstant(UTC));
        assertThat(cut.instantOfNextFrame(LocalDate.of(2007, Month.SEPTEMBER, 6).atTime(17, 29, 0, 0).toInstant(UTC)))
                .isEqualTo(LocalDate.of(2007, Month.SEPTEMBER, 6).atTime(17, 30, 0, 0).toInstant(UTC));
        assertThat(cut.instantOfNextFrame(LocalDate.of(2021, Month.JUNE, 8).atTime(8, 34, 0, 0).toInstant(UTC)))
                .isEqualTo(LocalDate.of(2021, Month.JUNE, 8).atTime(8, 35, 0, 0).toInstant(UTC));

        assertThat(cut.instantOfNextFrame(LocalDate.of(1570, Month.NOVEMBER, 24).atTime(23, 7, 0, 0).toInstant(UTC)))
                .isEqualTo(LocalDate.of(1570, Month.NOVEMBER, 24).atTime(23, 10, 0, 0).toInstant(UTC));
        assertThat(cut.instantOfNextFrame(LocalDate.of(2047, Month.JANUARY, 24).atTime(16, 15, 0, 0).toInstant(UTC)))
                .isEqualTo(LocalDate.of(2047, Month.JANUARY, 24).atTime(16, 20, 0, 0).toInstant(UTC));
        assertThat(cut.instantOfNextFrame(LocalDate.of(1989, Month.JUNE, 10).atTime(15, 14, 37, 0).toInstant(UTC)))
                .isEqualTo(LocalDate.of(1989, Month.JUNE, 10).atTime(15, 15, 0, 0).toInstant(UTC));
        assertThat(cut.instantOfNextFrame(LocalDate.of(2006, Month.AUGUST, 10).atTime(3, 29, 0, 7521).toInstant(UTC)))
                .isEqualTo(LocalDate.of(2006, Month.AUGUST, 10).atTime(3, 30, 0, 0).toInstant(UTC));
        assertThat(
                cut.instantOfNextFrame(LocalDate.of(3057, Month.NOVEMBER, 10).atTime(8, 54, 12, 45720).toInstant(UTC)))
                        .isEqualTo(LocalDate.of(3057, Month.NOVEMBER, 10).atTime(8, 55, 0, 0).toInstant(UTC));
    }

    ///////////
    // / M15 ///
    ///////////

    /**
     * Two instants are in the same {@link M15} time frame when all fields higher than minutes are the same and both
     * instants are between the last minute that has <code>m % 15 = 0</code> inclusive and <code>m + 15</code>
     * exclusive.
     * 
     * <p>
     * Time frames start at minutes 0, 15 and 30.
     * </p>
     */
    @Test
    public void m15InstantsAreInSameTimeFrameWhenTheyAreInTheSame15MinuteFrame() {
        final M15 cut = new M15();

        final LocalDate someDay = LocalDate.of(2015, Month.SEPTEMBER, 11);

        assertThat(cut.areInSameTimeFrame(someDay.atTime(20, 14, 59, 99999).toInstant(UTC),
                someDay.atTime(20, 0, 0, 0).toInstant(UTC))).isTrue();
        assertThat(cut.areInSameTimeFrame(someDay.atTime(15, 2, 32, 0).toInstant(UTC),
                someDay.atTime(15, 13, 12, 653).toInstant(UTC))).isTrue();

        assertThat(cut.areInSameTimeFrame(someDay.atTime(20, 0, 0, 0).toInstant(UTC),
                someDay.atTime(20, 15, 0, 0).toInstant(UTC))).isFalse();
        assertThat(cut.areInSameTimeFrame(someDay.atTime(14, 1, 0, 0).toInstant(UTC),
                someDay.plusDays(1).atTime(14, 1, 0, 0).toInstant(UTC))).isFalse();
    }

    /**
     * {@link M15#instantOfNextFrame(Instant)} should return the instant with the minutes set to the earliest possible
     * {@link Instant} that is later than the {@link Instant} past and thats minutes are a multiple of 15.
     */
    @Test
    public void m15InstantOfNextFrameShouldReturnTheNextInstantWhereTheMinutesAreAMultipleOf15() {
        final M15 cut = new M15();

        final LocalDate someDay = LocalDate.of(2015, Month.SEPTEMBER, 11);

        assertThat(cut.instantOfNextFrame(someDay.atTime(20, 4, 0, 0).toInstant(UTC)))
                .isEqualTo(someDay.atTime(20, 15, 0, 0).toInstant(UTC));
        assertThat(cut.instantOfNextFrame(someDay.atTime(15, 59, 0, 0).toInstant(UTC)))
                .isEqualTo(someDay.atTime(16, 0, 0, 0).toInstant(UTC));
        assertThat(cut.instantOfNextFrame(someDay.atTime(17, 29, 0, 0).toInstant(UTC)))
                .isEqualTo(someDay.atTime(17, 30, 0, 0).toInstant(UTC));

        assertThat(cut.instantOfNextFrame(someDay.atTime(23, 14, 59, 99999).toInstant(UTC)))
                .isEqualTo(someDay.atTime(23, 15, 0, 0).toInstant(UTC));
        assertThat(cut.instantOfNextFrame(someDay.atTime(23, 53, 25, 82).toInstant(UTC)))
                .isEqualTo(someDay.plusDays(1).atTime(0, 0, 0, 0).toInstant(UTC));
    }

    // /////////
    // / M30 ///
    // /////////

    /**
     * Two instants are in the same {@link M30} time frame when all fields higher than minutes are the same and both
     * instants are between the last minute that has <code>m % 30 = 0</code> inclusive and <code>m + 30</code>
     * exclusive.
     * 
     * <p>
     * Time frames start at minutes 0, and 30.
     * </p>
     */
    @Test
    public void m30InstantsAreInSameTimeFrameWhenTheyAreInTheSame30MinuteFrame() {
        final M30 cut = new M30();

        assertThat(cut.areInSameTimeFrame(LocalDate.of(2041, Month.FEBRUARY, 7).atTime(0, 29, 59, 99999).toInstant(UTC),
                LocalDate.of(2041, Month.FEBRUARY, 7).atTime(0, 0, 0, 0).toInstant(UTC))).isTrue();
        assertThat(
                cut.areInSameTimeFrame(LocalDate.of(1752, Month.NOVEMBER, 15).atTime(21, 53, 59, 9231).toInstant(UTC),
                        LocalDate.of(1752, Month.NOVEMBER, 15).atTime(21, 35, 34, 1234).toInstant(UTC))).isTrue();

        assertThat(cut.areInSameTimeFrame(LocalDate.of(2041, Month.FEBRUARY, 7).atTime(0, 0, 0, 0).toInstant(UTC),
                LocalDate.of(2041, Month.FEBRUARY, 7).atTime(0, 30, 0, 0).toInstant(UTC))).isFalse();

        assertThat(cut.areInSameTimeFrame(LocalDate.of(2570, Month.NOVEMBER, 24).atTime(15, 47, 0, 0).toInstant(UTC),
                LocalDate.of(2570, Month.NOVEMBER, 25).atTime(15, 47, 0, 0).toInstant(UTC))).isFalse();
        assertThat(cut.areInSameTimeFrame(LocalDate.of(2501, Month.JANUARY, 24).atTime(20, 29, 0, 0).toInstant(UTC),
                LocalDate.of(1892, Month.JANUARY, 24).atTime(20, 29, 0, 0).toInstant(UTC))).isFalse();
        assertThat(cut.areInSameTimeFrame(LocalDate.of(2013, Month.JANUARY, 10).atTime(9, 0, 57, 0).toInstant(UTC),
                LocalDate.of(2013, Month.OCTOBER, 10).atTime(9, 0, 57, 0).toInstant(UTC))).isFalse();
    }

    /**
     * {@link M30#instantOfNextFrame(Instant)} should return the instant with the minutes set to the earliest possible
     * {@link Instant} that is later than the {@link Instant} past and thats minutes are a multiple of 30.
     */
    @Test
    public void m30InstantOfNextFrameShouldReturnTheNextInstantWhereTheMinutesAreAMultipleOfThirty() {
        final M30 cut = new M30();

        assertThat(cut.instantOfNextFrame(LocalDate.of(7821, Month.NOVEMBER, 24).atTime(4, 59, 0, 0).toInstant(UTC)))
                .isEqualTo(LocalDate.of(7821, Month.NOVEMBER, 24).atTime(5, 0, 0, 0).toInstant(UTC));
        assertThat(cut.instantOfNextFrame(LocalDate.of(1975, Month.FEBRUARY, 10).atTime(14, 59, 0, 0).toInstant(UTC)))
                .isEqualTo(LocalDate.of(1975, Month.FEBRUARY, 10).atTime(15, 00, 0, 0).toInstant(UTC));
        assertThat(cut.instantOfNextFrame(LocalDate.of(2047, Month.MARCH, 7).atTime(16, 29, 0, 0).toInstant(UTC)))
                .isEqualTo(LocalDate.of(2047, Month.MARCH, 7).atTime(16, 30, 0, 0).toInstant(UTC));
        assertThat(cut.instantOfNextFrame(LocalDate.of(2014, Month.SEPTEMBER, 16).atTime(21, 29, 0, 0).toInstant(UTC)))
                .isEqualTo(LocalDate.of(2014, Month.SEPTEMBER, 16).atTime(21, 30, 0, 0).toInstant(UTC));

        assertThat(cut.instantOfNextFrame(LocalDate.of(1257, Month.MAY, 24).atTime(10, 37, 0, 0).toInstant(UTC)))
                .isEqualTo(LocalDate.of(1257, Month.MAY, 24).atTime(11, 0, 0, 0).toInstant(UTC));
        assertThat(cut.instantOfNextFrame(LocalDate.of(2047, Month.NOVEMBER, 24).atTime(4, 30, 0, 0).toInstant(UTC)))
                .isEqualTo(LocalDate.of(2047, Month.NOVEMBER, 24).atTime(5, 0, 0, 0).toInstant(UTC));
        assertThat(cut.instantOfNextFrame(LocalDate.of(2104, Month.OCTOBER, 10).atTime(3, 29, 47, 0).toInstant(UTC)))
                .isEqualTo(LocalDate.of(2104, Month.OCTOBER, 10).atTime(3, 30, 0, 0).toInstant(UTC));
        assertThat(
                cut.instantOfNextFrame(LocalDate.of(2004, Month.FEBRUARY, 10).atTime(17, 29, 0, 67832).toInstant(UTC)))
                        .isEqualTo(LocalDate.of(2004, Month.FEBRUARY, 10).atTime(17, 30, 0, 0).toInstant(UTC));
        assertThat(cut.instantOfNextFrame(LocalDate.of(1947, Month.AUGUST, 10).atTime(13, 59, 25, 4578).toInstant(UTC)))
                .isEqualTo(LocalDate.of(1947, Month.AUGUST, 10).atTime(14, 0, 0, 0).toInstant(UTC));
    }

    // ////////
    // / H1 ///
    // ////////
    

    /**
     * Two instants are in the same {@link H1} time frame when all fields higher than minutes are the same.
     */
    @Test
    public void h1InstantsAreInSameTimeFrameWhenTheyAreInTheSame1HourFrame() {
        final H1 cut = new H1();

        assertThat(cut.areInSameTimeFrame(LocalDate.of(2041, Month.FEBRUARY, 7).atTime(0, 59, 59, 99999).toInstant(UTC),
                LocalDate.of(2041, Month.FEBRUARY, 7).atTime(0, 0, 0, 0).toInstant(UTC))).isTrue();
        assertThat(
                cut.areInSameTimeFrame(LocalDate.of(1752, Month.NOVEMBER, 15).atTime(21, 53, 59, 9231).toInstant(UTC),
                        LocalDate.of(1752, Month.NOVEMBER, 15).atTime(21, 35, 34, 1234).toInstant(UTC))).isTrue();

        assertThat(cut.areInSameTimeFrame(LocalDate.of(2041, Month.FEBRUARY, 7).atTime(0, 0, 0, 0).toInstant(UTC),
                LocalDate.of(2041, Month.FEBRUARY, 7).atTime(1, 0, 0, 0).toInstant(UTC))).isFalse();

        assertThat(cut.areInSameTimeFrame(LocalDate.of(2570, Month.NOVEMBER, 24).atTime(15, 47, 0, 0).toInstant(UTC),
                LocalDate.of(2570, Month.NOVEMBER, 25).atTime(15, 47, 0, 0).toInstant(UTC))).isFalse();
        assertThat(cut.areInSameTimeFrame(LocalDate.of(2501, Month.JANUARY, 24).atTime(20, 29, 0, 0).toInstant(UTC),
                LocalDate.of(1892, Month.JANUARY, 24).atTime(20, 29, 0, 0).toInstant(UTC))).isFalse();
        assertThat(cut.areInSameTimeFrame(LocalDate.of(2013, Month.JANUARY, 10).atTime(9, 0, 57, 0).toInstant(UTC),
                LocalDate.of(2013, Month.OCTOBER, 10).atTime(9, 0, 57, 0).toInstant(UTC))).isFalse();
    }

    /**
     * {@link H1#instantOfNextFrame(Instant)} should return the instant with the hour set to the earliest possible
     * {@link Instant} that is later than the {@link Instant}. All smaller fields than hours should be zero.
     */
    @Test
    public void h1InstantOfNextFrameShouldReturnTheNextHour() {
        final H1 cut = new H1();

        assertThat(cut.instantOfNextFrame(LocalDate.of(7821, Month.NOVEMBER, 24).atTime(4, 59, 0, 0).toInstant(UTC)))
                .isEqualTo(LocalDate.of(7821, Month.NOVEMBER, 24).atTime(5, 0, 0, 0).toInstant(UTC));
        assertThat(cut.instantOfNextFrame(LocalDate.of(1975, Month.FEBRUARY, 10).atTime(14, 59, 0, 0).toInstant(UTC)))
                .isEqualTo(LocalDate.of(1975, Month.FEBRUARY, 10).atTime(15, 00, 0, 0).toInstant(UTC));
        assertThat(cut.instantOfNextFrame(LocalDate.of(2047, Month.MARCH, 7).atTime(16, 29, 0, 0).toInstant(UTC)))
                .isEqualTo(LocalDate.of(2047, Month.MARCH, 7).atTime(17, 00, 0, 0).toInstant(UTC));
        assertThat(cut.instantOfNextFrame(LocalDate.of(2014, Month.SEPTEMBER, 16).atTime(21, 1, 0, 0).toInstant(UTC)))
                .isEqualTo(LocalDate.of(2014, Month.SEPTEMBER, 16).atTime(22, 00, 0, 0).toInstant(UTC));

        assertThat(cut.instantOfNextFrame(LocalDate.of(1257, Month.MAY, 24).atTime(10, 37, 0, 0).toInstant(UTC)))
                .isEqualTo(LocalDate.of(1257, Month.MAY, 24).atTime(11, 0, 0, 0).toInstant(UTC));
        assertThat(cut.instantOfNextFrame(LocalDate.of(2047, Month.NOVEMBER, 24).atTime(4, 0, 0, 0).toInstant(UTC)))
                .isEqualTo(LocalDate.of(2047, Month.NOVEMBER, 24).atTime(5, 0, 0, 0).toInstant(UTC));
        assertThat(cut.instantOfNextFrame(LocalDate.of(2104, Month.OCTOBER, 10).atTime(3, 59, 47, 0).toInstant(UTC)))
                .isEqualTo(LocalDate.of(2104, Month.OCTOBER, 10).atTime(4, 0, 0, 0).toInstant(UTC));
        assertThat(
                cut.instantOfNextFrame(LocalDate.of(2004, Month.FEBRUARY, 10).atTime(17, 59, 0, 67832).toInstant(UTC)))
                        .isEqualTo(LocalDate.of(2004, Month.FEBRUARY, 10).atTime(18, 0, 0, 0).toInstant(UTC));
        assertThat(cut.instantOfNextFrame(LocalDate.of(1947, Month.AUGUST, 10).atTime(13, 59, 25, 4578).toInstant(UTC)))
                .isEqualTo(LocalDate.of(1947, Month.AUGUST, 10).atTime(14, 0, 0, 0).toInstant(UTC));
    }
    
    // ////////
    // / D1 ///
    // ////////

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
