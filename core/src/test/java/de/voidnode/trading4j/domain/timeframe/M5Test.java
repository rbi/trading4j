package de.voidnode.trading4j.domain.timeframe;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;

import static java.time.ZoneOffset.UTC;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if the {@link TimeFrame} {@link M5} works as expected.
 * 
 * @author Raik Bieniek
 */
public class M5Test {

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
}
