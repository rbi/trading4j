package de.voidnode.trading4j.domain.timeframe;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;

import static java.time.ZoneOffset.UTC;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if the {@link TimeFrame} {@link M1} works as expected.
 * 
 * @author Raik Bieniek
 */
public class M1Test {

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
}
