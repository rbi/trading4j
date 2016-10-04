package de.voidnode.trading4j.functionality.smoothers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import de.voidnode.trading4j.domain.Ratio;

import static de.voidnode.trading4j.testutils.assertions.Assertions.assertThat;

import org.assertj.core.data.Offset;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link SimpleMovingAverageRatio} works as expected.
 * 
 * @author Raik Bieniek
 */
public class SimpleMovingAverageRatioTest {

    private static final Offset<Double> ALLOWED_OFFSET = Offset.offset(0.000001);

    private final Stream<Ratio> inputRatios = stream(new double[] { 0.01, 0.02, 0.03, 0.52, 0.16 })
            .mapToObj(p -> new Ratio(p));

    /**
     * The cut provides the simple average of the n most recent {@link Ratio}s.
     */
    @Test
    public void providesTheAverageOfTheMostRecentRatios() {
        final SimpleMovingAverageRatio cut = new SimpleMovingAverageRatio(3);

        final List<Optional<Ratio>> ratios = inputRatios.map(p -> cut.smooth(p)).collect(toList());

        assertThat(ratios.get(0)).isEmpty();
        assertThat(ratios.get(1)).isEmpty();
        assertThat(ratios.get(2).get()).isApproximatelyEqualTo(new Ratio(0.02), ALLOWED_OFFSET);
        assertThat(ratios.get(3).get()).isApproximatelyEqualTo(new Ratio(0.19), ALLOWED_OFFSET);
        assertThat(ratios.get(4).get()).isApproximatelyEqualTo(new Ratio(0.236666), ALLOWED_OFFSET);
    }

    /**
     * The amount of ratios to aggregate can be configured.
     */
    @Test
    public void supportsDifferentAmountOfRatiosToAggregate() {
        final SimpleMovingAverageRatio cut = new SimpleMovingAverageRatio(4);

        final List<Optional<Ratio>> ratios = inputRatios.map(p -> cut.smooth(p)).collect(toList());

        assertThat(ratios.get(0)).isEmpty();
        assertThat(ratios.get(1)).isEmpty();
        assertThat(ratios.get(2)).isEmpty();
        assertThat(ratios.get(3).get()).isApproximatelyEqualTo(new Ratio(0.145), ALLOWED_OFFSET);
        assertThat(ratios.get(4).get()).isApproximatelyEqualTo(new Ratio(0.1825), ALLOWED_OFFSET);
    }
}
