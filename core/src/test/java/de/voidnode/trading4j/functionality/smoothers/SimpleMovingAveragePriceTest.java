package de.voidnode.trading4j.functionality.smoothers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;

import de.voidnode.trading4j.domain.marketdata.impl.CandleStick;
import de.voidnode.trading4j.domain.monetary.Price;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link SimpleMovingAveragePrice} works as expected.
 * 
 * @author Raik Bieniek
 */
public class SimpleMovingAveragePriceTest {

    /**
     * The cut should return the average of the close {@link Price}s of the most recent {@link CandleStick}s passed as
     * input.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void providesTheAverageOfTheMostRecentCandleStickClosePrices() {
        final Stream<Price> inputPrices = stream(new double[] { 1.0, 2.0, 3.0, 52.0, 16.0 })
                .mapToObj(p -> new Price(p));

        final SimpleMovingAveragePrice cut = new SimpleMovingAveragePrice(3);
        final List<Optional<Price>> prices = inputPrices.map(p -> cut.smooth(p)).collect(toList());
        assertThat(prices).containsExactly(empty(), empty(), opt(new Price(2.0)), opt(new Price(19.0)),
                opt(new Price(23.66666)));
    }

    /**
     * The amount of candle sticks to aggregate can be configured.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void supportsDifferentAmountOfCandleSticksToAggregate() {
        final Stream<Price> inputPrices = stream(new double[] { 1.0, 2.0, 3.0, 52.0, 16.0 })
                .mapToObj(p -> new Price(p));

        final SimpleMovingAveragePrice cut = new SimpleMovingAveragePrice(4);
        final List<Optional<Price>> prices = inputPrices.map(p -> cut.smooth(p)).collect(toList());
        assertThat(prices).containsExactly(empty(), empty(), empty(), opt(new Price(14.5)), opt(new Price(18.25)));
    }

    private <T> Optional<T> opt(final T value) {
        return Optional.of(value);
    }
}
