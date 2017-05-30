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
public class M15Test {

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
}
