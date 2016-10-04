package de.voidnode.trading4j.functionality.smoothers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

import de.voidnode.trading4j.domain.Ratio;

import org.assertj.core.data.Offset;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link ExponentialMovingAverageRatio} works as expected.
 * 
 * @author Raik Bieniek
 */
public class ExponentialMovingAverageRatioTest {
    
    private static final Offset<Double> OFFSET = Offset.offset(0.000011);
    private final Stream<Ratio> inputData = Arrays.stream(
            new double[] { 1.42128, 1.42032, 1.42142, 1.42278, 1.42370, 1.42293, 1.42312, 1.42248, 1.42354, 1.42416 })
            .mapToObj(p -> new Ratio(p));

    /**
     * The {@link ExponentialMovingAverageRatio} is calculated as described in its class-level JavaDoc.
     */
    @Test
    public void caclucatesEMAcorrectly() {
        final ExponentialMovingAverageRatio cut = new ExponentialMovingAverageRatio(5);
        final List<Optional<Ratio>> ratios = inputData.map(c -> cut.smooth(c)).collect(toList());

        for (int i = 0; i < 4; i++) {
            assertThat(ratios.get(i)).isEmpty(); // 0-3
        }

        assertThat(ratio(ratios, 4)).isEqualTo(1.42235, OFFSET); // 4
        assertThat(ratio(ratios, 5)).isEqualTo(1.42254, OFFSET); // 5
        assertThat(ratio(ratios, 6)).isEqualTo(1.42273, OFFSET); // 6
        assertThat(ratio(ratios, 7)).isEqualTo(1.42265, OFFSET); // 7
        assertThat(ratio(ratios, 8)).isEqualTo(1.42295, OFFSET); // 8
        assertThat(ratio(ratios, 9)).isEqualTo(1.42335, OFFSET); // 9
    }

    /**
     * The {@link ExponentialMovingAverageRatio} can be calculated for different weighting factors.
     */
    @Test
    public void supportsDifferentCandleStickAmounts() {
        final ExponentialMovingAverageRatio cut = new ExponentialMovingAverageRatio(6);
        final List<Optional<Ratio>> ratios = inputData.map(c -> cut.smooth(c)).collect(toList());

        for (int i = 0; i < 5; i++) {
            assertThat(ratios.get(i)).isEmpty(); // 0-4
        }

        assertThat(ratio(ratios, 5)).isEqualTo(1.42241, OFFSET); // 5
        assertThat(ratio(ratios, 6)).isEqualTo(1.42261, OFFSET); // 6
        assertThat(ratio(ratios, 7)).isEqualTo(1.42257, OFFSET); // 7
        assertThat(ratio(ratios, 8)).isEqualTo(1.42285, OFFSET); // 8
        assertThat(ratio(ratios, 9)).isEqualTo(1.42322, OFFSET); // 9
    }

    private double ratio(final List<Optional<Ratio>> ratios, final int index) {
        final Optional<Ratio> ratio = ratios.get(index);
        assertThat(ratio).isPresent();
        return ratio.get().asBasic();
    }
}
