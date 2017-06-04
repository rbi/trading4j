package de.voidnode.trading4j.functionality.timeframeconversion;

import java.time.Instant;
import java.util.Optional;

import static java.util.Optional.empty;

import de.voidnode.trading4j.api.Failed;
import de.voidnode.trading4j.api.OrderFilter;
import de.voidnode.trading4j.domain.marketdata.impl.CandleStick;
import de.voidnode.trading4j.domain.marketdata.impl.DatedCandleStick;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;
import de.voidnode.trading4j.domain.timeframe.M30;
import de.voidnode.trading4j.domain.timeframe.M5;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Checks if {@link TimeFrameConvertingTradeGuard} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class TimeFrameConvertingTradeGuardTest {

    private static final DatedCandleStick<M5> DUMMY_INPUT_CANDLE_STICK = new DatedCandleStick<>(Instant.EPOCH, 1.0, 1.0,
            1.0, 1.0);
    private static final DatedCandleStick<M30> DUMMY_AGGREGATED_CANDLE_STICK = new DatedCandleStick<>(Instant.EPOCH,
            1.0, 1.0, 1.0, 1.0);

    @Mock
    private OrderFilter<DatedCandleStick<M30>> orig;

    @Mock
    private TimeFrameConverter<DatedCandleStick<M5>, DatedCandleStick<M30>, M5, M30> timeFrameConverter;

    @InjectMocks
    private TimeFrameConvertingTradeGuard<DatedCandleStick<M5>, DatedCandleStick<M30>, M5, M30> cut;

    @Mock
    private BasicPendingOrder somePendingOrder;

    @Mock
    private Failed someFailed;

    /**
     * The cut should pass completely aggregated {@link CandleStick} to the original {@link OrderFilter}.
     */
    @Test
    public void passesCompletlyAggregatedCandlSticksToTheOriginalTradeGuard() {
        when(timeFrameConverter.aggregate(any())).thenReturn(Optional.of(DUMMY_AGGREGATED_CANDLE_STICK))
                .thenReturn(empty()).thenReturn(empty()).thenReturn(Optional.of(DUMMY_AGGREGATED_CANDLE_STICK))
                .thenReturn(empty());

        cut.updateMarketData(DUMMY_INPUT_CANDLE_STICK);
        verify(orig).updateMarketData(DUMMY_AGGREGATED_CANDLE_STICK);

        cut.updateMarketData(DUMMY_INPUT_CANDLE_STICK);
        verifyNoMoreInteractions(orig);

        cut.updateMarketData(DUMMY_INPUT_CANDLE_STICK);
        verifyNoMoreInteractions(orig);

        cut.updateMarketData(DUMMY_INPUT_CANDLE_STICK);
        verify(orig, times(2)).updateMarketData(DUMMY_AGGREGATED_CANDLE_STICK);

        cut.updateMarketData(DUMMY_INPUT_CANDLE_STICK);
        verifyNoMoreInteractions(orig);
    }

    /**
     * All trades are blocked as long as no complete candle stick was aggregated.
     */
    @Test
    public void blocksTradesAsLongAsNoCompleteCandleStickWasGenerated() {
        when(orig.filterOrder(somePendingOrder)).thenReturn(empty());
        when(timeFrameConverter.aggregate(any())).thenReturn(empty()).thenReturn(empty())
                .thenReturn(Optional.of(DUMMY_AGGREGATED_CANDLE_STICK));

        cut.updateMarketData(DUMMY_INPUT_CANDLE_STICK);
        assertThat(cut.filterOrder(somePendingOrder)).isPresent();
        cut.updateMarketData(DUMMY_INPUT_CANDLE_STICK);
        assertThat(cut.filterOrder(somePendingOrder)).isPresent();
        cut.updateMarketData(DUMMY_INPUT_CANDLE_STICK);
        assertThat(cut.filterOrder(somePendingOrder)).isEmpty();
    }

    /**
     * Checks if specific orders should be blocked should be passed to the underlying {@link OrderFilter} as soon as the
     * first complete candle stick was received.
     */
    @Test
    public void blockingChecksForOrdersArePassedToTheOriginalTradeGuardAfterFirstCompleteCandleStick() {
        when(orig.filterOrder(somePendingOrder)).thenReturn(empty());
        when(timeFrameConverter.aggregate(any())).thenReturn(Optional.of(DUMMY_AGGREGATED_CANDLE_STICK));
        cut.updateMarketData(DUMMY_INPUT_CANDLE_STICK);

        final Optional<Failed> someBlockingStatus = Optional.of(someFailed);
        when(orig.filterOrder(somePendingOrder)).thenReturn(someBlockingStatus);

        assertThat(cut.filterOrder(somePendingOrder)).isEqualTo(someBlockingStatus);
    }
}
