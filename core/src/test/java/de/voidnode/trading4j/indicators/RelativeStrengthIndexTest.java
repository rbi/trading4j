package de.voidnode.trading4j.indicators;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.stream.IntStream.range;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.Ratio;
import de.voidnode.trading4j.domain.marketdata.MarketData;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.timeframe.M1;

import static de.voidnode.trading4j.domain.RatioUnit.PERCENT;
import static de.voidnode.trading4j.testutils.assertions.Assertions.assertThat;

import org.assertj.core.data.Offset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Checks if {@link RelativeStrengthIndex} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class RelativeStrengthIndexTest {

    private static final MarketData<M1> CANDLE = new MarketData<M1>(1.0);

    private static final Offset<Double> OFFSET = offset(0.00000001);

    @Mock
    private Indicator<Price, MarketData<M1>> upMa;

    @Mock
    private Indicator<Price, MarketData<M1>> downMa;

    private Indicator<Ratio, MarketData<M1>> cut;

    /**
     * Sets up the class under test and the default behavior for the mocks.
     */
    @Before
    public void setupCutAndMocks() {
        when(upMa.indicate(any())).thenReturn(opt(new Price(10)));
        when(downMa.indicate(any())).thenReturn(opt(new Price(10)));

        this.cut = new RelativeStrengthIndex<>(upMa, downMa);
    }

    /**
     * The cut indicates nothing when the moving averages indicate nothing.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void indicatesNothingWhenMovingAveragesIndicateNothing() {
        when(upMa.indicate(any())).thenReturn(empty(), empty(), opt(new Price(10)), empty());
        when(downMa.indicate(any())).thenReturn(empty(), empty(), empty(), opt(new Price(10)));

        range(0, 5).forEach(i -> assertThat(cut.indicate(CANDLE)).isEmpty());
    }

    /**
     * The cut passes the upward changes to the upMa.
     */
    @Test
    public void passesMarketDataWithTheUpwardChangesAsClosePriceToTheUpMovingAverage() {
        cut.indicate(marketPrice(10));
        cut.indicate(marketPrice(11));
        cut.indicate(marketPrice(15));
        cut.indicate(marketPrice(15));

        final InOrder upMaValues = inOrder(upMa);
        upMaValues.verify(upMa).indicate(marketPrice(1));
        upMaValues.verify(upMa).indicate(marketPrice(4));
        upMaValues.verify(upMa).indicate(marketPrice(0));
        upMaValues.verifyNoMoreInteractions();

        verify(downMa, times(3)).indicate(marketPrice(0));
        verifyNoMoreInteractions(downMa);
    }

    /**
     * The cut passes the downward changes to the downMa.
     */
    @Test
    public void passesAMarketWithTheDownwardChangesAsClosePriceToTheUpMovingAverage() {
        cut.indicate(marketPrice(53));
        cut.indicate(marketPrice(21));
        cut.indicate(marketPrice(10));
        cut.indicate(marketPrice(10));

        final InOrder downMaValues = inOrder(downMa);
        downMaValues.verify(downMa).indicate(marketPrice(32));
        downMaValues.verify(downMa).indicate(marketPrice(11));
        downMaValues.verify(downMa).indicate(marketPrice(0));
        downMaValues.verifyNoMoreInteractions();

        verify(upMa, times(3)).indicate(marketPrice(0));
        verifyNoMoreInteractions(upMa);
    }

    /**
     * Given the cut passes the correct prices to the correct moving averages it calculates the RSI correctly.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void calculatesRSIcorrectly() {
        when(upMa.indicate(any())).thenReturn(opt(new Price(100)), opt(new Price(150)), opt(new Price(95)),
                opt(new Price(90)), opt(new Price(10)));
        when(downMa.indicate(any())).thenReturn(opt(new Price(100)), opt(new Price(100)), opt(new Price(94)),
                opt(new Price(40)), opt(new Price(6)));

        assertThat(cut.indicate(CANDLE)).isEmpty();
        assertThat(cut.indicate(CANDLE).get()).isApproximatelyEqualTo(new Ratio(50, PERCENT), OFFSET);
        assertThat(cut.indicate(CANDLE).get()).isApproximatelyEqualTo(new Ratio(60, PERCENT), OFFSET);
        assertThat(cut.indicate(CANDLE).get()).isApproximatelyEqualTo(new Ratio(50.26455, PERCENT), OFFSET);
        assertThat(cut.indicate(CANDLE).get()).isApproximatelyEqualTo(new Ratio(69.23077, PERCENT), OFFSET);
        assertThat(cut.indicate(CANDLE).get()).isApproximatelyEqualTo(new Ratio(62.5, PERCENT), OFFSET);
    }

    /**
     * When the cut gets 0 from the down moving average it does not try to divide through zero.
     */
    @Test
    public void ifDownMAReturns0TheCutDoesNotDivideThroughZero() {
        // provoke division by zero
        when(downMa.indicate(any())).thenReturn(opt(new Price(0)));

        range(0, 2).forEach(i -> cut.indicate(CANDLE));
    }

    private Optional<Price> opt(final Price price) {
        return Optional.of(price);
    }

    private MarketData<M1> marketPrice(final int pipettes) {
        return new MarketData<>(new Price(pipettes));
    }
}
