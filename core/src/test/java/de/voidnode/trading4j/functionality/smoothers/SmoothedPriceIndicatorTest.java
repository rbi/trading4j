package de.voidnode.trading4j.functionality.smoothers;

import java.util.Optional;
import java.util.stream.Stream;

import de.voidnode.trading4j.domain.marketdata.CandleStick;
import de.voidnode.trading4j.domain.marketdata.MarketData;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.timeframe.M1;

import static de.voidnode.trading4j.testutils.CandleStickStreams.candleStickStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Checks if {@link SmoothedPriceIndicator} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class SmoothedPriceIndicatorTest {

    private static final CandleStick<M1> SOME_CANDLE = new CandleStick<>(1.0, 1.0, 1.0, 1.0);

    @Mock
    private Smoother<Price> smoother;

    @InjectMocks
    private SmoothedPriceIndicator<CandleStick<M1>> cut;

    /**
     * The cut passes the close {@link Price} of {@link MarketData} passed as input to the {@link Smoother}.
     */
    @Test
    public void passesClosePriceOfMarketDataToSmoother() {
        final Stream<CandleStick<M1>> candleData = candleStickStream(new double[][] {
                // open, high, low, close
                { 1.42072, 1.42222, 1.42053, 1.42128 }, // 0
                { 1.42134, 1.42233, 1.42025, 1.42032 }, // 1
                { 1.42055, 1.42252, 1.42016, 1.42142 }, // 2
        });

        candleData.forEach(candle -> cut.indicate(candle));

        verify(smoother).smooth(new Price(1.42128));
        verify(smoother).smooth(new Price(1.42032));
        verify(smoother).smooth(new Price(1.42142));
    }

    /**
     * The cut returns the value of the {@link Smoother}.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void returnsValueOfSmoother() {
        when(smoother.smooth(any(Price.class))).thenReturn(Optional.empty(), Optional.of(new Price(259)),
                Optional.of(new Price(924)));

        assertThat(cut.indicate(SOME_CANDLE)).isEmpty();
        assertThat(cut.indicate(SOME_CANDLE)).contains(new Price(259));
        assertThat(cut.indicate(SOME_CANDLE)).contains(new Price(924));
    }
}
