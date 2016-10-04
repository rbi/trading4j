package de.voidnode.trading4j.domain;

import static java.util.Arrays.asList;

import static de.voidnode.trading4j.testutils.assertions.Assertions.assertThat;

import org.assertj.core.data.Offset;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

/**
 * Checks if {@link Ratio} works as expected.
 * 
 * @author Raik Bieniek
 */
public class RatioTest {

    private static final Offset<Double> ALLOWED_OFFSET = offset(0.0000001);

    /**
     * A {@link Ratio} can be constructed with different units.
     */
    @Test
    public void canBeConstructedWithDifferentUnits() {
        assertThat(new Ratio(25.0, RatioUnit.PERCENT).asBasic()).isEqualTo(0.25, ALLOWED_OFFSET);
        assertThat(new Ratio(35.6, RatioUnit.PERMILLE).asBasic()).isEqualTo(0.0356, ALLOWED_OFFSET);
        assertThat(new Ratio(0.821, RatioUnit.BASIC).asBasic()).isEqualTo(0.821, ALLOWED_OFFSET);
    }

    /**
     * A {@link Ratio} is only equal to other {@link Ratio}s thats {@link Ratio#asBasic()} value is equal.
     */
    @Test
    public void shouldOnlyEqualOtherRatiosThatsRawValuesAreTheSame() {
        assertThat(new Ratio(1.0)).isEqualTo(new Ratio(1.0));
        assertThat(new Ratio(-1.0)).isEqualTo(new Ratio(-1.0));

        assertThat(new Ratio(1.0)).isNotEqualTo(new Ratio(2.0));
        assertThat(new Ratio(1.0)).isNotEqualTo("not a ratio");
        assertThat(new Ratio(1.0)).isNotEqualTo(null);
    }

    /**
     * Ratios can be compared with other ratios by using their base values.
     */
    @Test
    public void comparesToOtherInstancesUsingTheBaseValue() {
        assertThat(new Ratio(1.0).compareTo(new Ratio(2.0))).isNegative();
        assertThat(new Ratio(2.0).compareTo(new Ratio(1.0))).isPositive();
        assertThat(new Ratio(1.0).compareTo(new Ratio(1.0))).isZero();

        assertThat(new Ratio(-1.0).compareTo(new Ratio(1.0))).isNegative();
    }

    /**
     * The average {@link Ratio} of multiple {@link Ratio}s can be calculated.
     */
    @Test
    public void averageRatioOfMultipleRatiosCanBeCalculated() {
        assertThat(Ratio.average(asList(new Ratio(1.0), new Ratio(2.0), new Ratio(3.0), new Ratio(4.0))))
                .isApproximatelyEqualTo(new Ratio(2.5), ALLOWED_OFFSET);

        assertThat(Ratio.average(asList(new Ratio(1.0), new Ratio(-1.0)))).isApproximatelyEqualTo(new Ratio(0.0),
                ALLOWED_OFFSET);

        assertThat(Ratio.average(asList(new Ratio(-2.0), new Ratio(-4.0)))).isApproximatelyEqualTo(new Ratio(-3.0),
                ALLOWED_OFFSET);
    }

    /**
     * When the average of zero ratios should be calculated an exception is thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void averageRatioThrowsExceptionWhenIteratorDidNotContainElements() {
        Ratio.average(asList());
    }
}
