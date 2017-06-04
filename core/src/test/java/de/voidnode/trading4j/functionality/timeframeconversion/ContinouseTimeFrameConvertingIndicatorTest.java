package de.voidnode.trading4j.functionality.timeframeconversion;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.marketdata.impl.CandleStick;
import de.voidnode.trading4j.domain.marketdata.impl.DatedCandleStick;
import de.voidnode.trading4j.domain.timeframe.M30;
import de.voidnode.trading4j.domain.timeframe.M5;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Checks if {@link ContinouseTimeFrameConvertingIndicator} works as expected.
 *
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class ContinouseTimeFrameConvertingIndicatorTest {

    private static final Optional<MarketDirection> UP = Optional.of(MarketDirection.UP);
    private static final Optional<MarketDirection> DOWN = Optional.of(MarketDirection.DOWN);
    private static final Optional<MarketDirection> UNKNOWN = Optional.empty();

    private static final DatedCandleStick<M5> DUMMY_INPUT_CANDLE_STICK = new DatedCandleStick<>(Instant.MIN, 1.0, 1.0,
            1.0, 1.0);
    private static final DatedCandleStick<M30> DUMMY_AGGREGATED_CANDLE_STICK = new DatedCandleStick<>(Instant.MIN, 1.0,
            1.0, 1.0, 1.0);

    @Mock
    private Indicator<MarketDirection, DatedCandleStick<M30>> orig;

    @Mock
    private TimeFrameConverter<DatedCandleStick<M5>, DatedCandleStick<M30>, M5, M30> timeFrameConverter;

    @InjectMocks
    private ContinouseTimeFrameConvertingIndicator<MarketDirection, DatedCandleStick<M5>, DatedCandleStick<M30>, M5, M30> cut;

    // //////////////////
    // / normal cases ///
    // //////////////////

    /**
     * While the next {@link CandleStick} is aggregated, the last trend should be returned.
     */
    @Test
    public void shouldReturnResultsOfLastTrendUntilNextOutputTfIsCompleted() {
        when(timeFrameConverter.aggregate(any())).thenReturn(Optional.of(DUMMY_AGGREGATED_CANDLE_STICK))
                .thenReturn(empty()).thenReturn(empty()).thenReturn(Optional.of(DUMMY_AGGREGATED_CANDLE_STICK))
                .thenReturn(empty());
        when(orig.indicate(any())).thenReturn(UP, DOWN, UNKNOWN);

        final List<Optional<MarketDirection>> trends = range(0, 5).mapToObj(i -> cut.indicate(DUMMY_INPUT_CANDLE_STICK))
                .collect(toList());
        assertThat(trends).containsExactly(UP, UP, UP, DOWN, DOWN);
    }

    /**
     * Until the first {@link DatedCandleStick} of the output time frame is completed, the trend should be unknown.
     */
    @Test
    public void shouldReturnUnknownUntilFirstOutputTfIsCompleted() {
        when(timeFrameConverter.aggregate(any())).thenReturn(empty()).thenReturn(empty())
                .thenReturn(Optional.of(DUMMY_AGGREGATED_CANDLE_STICK));
        when(orig.indicate(any())).thenReturn(UP, DOWN);

        final List<Optional<MarketDirection>> trends = range(0, 3).mapToObj(i -> cut.indicate(DUMMY_INPUT_CANDLE_STICK))
                .collect(toList());
        assertThat(trends).containsExactly(UNKNOWN, UNKNOWN, UP);
    }
}
