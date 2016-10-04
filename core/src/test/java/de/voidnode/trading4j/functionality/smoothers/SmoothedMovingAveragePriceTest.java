package de.voidnode.trading4j.functionality.smoothers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

import de.voidnode.trading4j.domain.monetary.Price;

import org.assertj.core.data.Offset;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link SmoothedMovingAveragePrice} works as expected.
 * 
 * @author Raik Bieniek
 */
public class SmoothedMovingAveragePriceTest {

    private static final Offset<Double> OFFSET = Offset.offset(0.00001);

    private final Stream<Price> inputData = Arrays.stream(
            new double[] { 1.46180, 1.45701, 1.45291, 1.44868, 1.44578, 1.44435, 1.44335, 1.43906, 1.43376, 1.42200 })
            .mapToObj(p -> new Price(p));

    private final SmoothedMovingAveragePrice cut = new SmoothedMovingAveragePrice(3);

    /**
     * Calculates the smoothed moving average correctly.
     */
    @Test
    public void calculatesSmoothedMovingAverageCorrectly() {
        final List<Optional<Price>> prices = inputData.map(c -> cut.smooth(c)).collect(toList());

        for (int i = 0; i < 2; i++) {
            assertThat(prices.get(i)).isEmpty(); // 0-1
        }

        assertThat(price(prices, 2)).isEqualTo(1.45724, OFFSET); // 2
        assertThat(price(prices, 3)).isEqualTo(1.45439, OFFSET); // 3
        assertThat(price(prices, 4)).isEqualTo(1.45151, OFFSET); // 4
        assertThat(price(prices, 5)).isEqualTo(1.44913, OFFSET); // 5
        assertThat(price(prices, 6)).isEqualTo(1.44720, OFFSET); // 6
        assertThat(price(prices, 7)).isEqualTo(1.44449, OFFSET); // 7
        assertThat(price(prices, 8)).isEqualTo(1.44090, OFFSET); // 8
        assertThat(price(prices, 9)).isEqualTo(1.43460, OFFSET); // 9O
    }

    private double price(final List<Optional<Price>> prices, final int index) {
        final Optional<Price> price = prices.get(index);
        assertThat(price).isPresent();
        return price.get().asDouble();
    }
}
