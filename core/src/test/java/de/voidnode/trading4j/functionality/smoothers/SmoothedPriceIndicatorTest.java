package de.voidnode.trading4j.functionality.smoothers;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import de.voidnode.trading4j.domain.marketdata.MarketData;
import de.voidnode.trading4j.domain.marketdata.impl.BasicMarketData;
import de.voidnode.trading4j.domain.monetary.Price;

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

    private static final MarketData SOME_CANDLE = new BasicMarketData(1.0);

    @Mock
    private Smoother<Price> smoother;

    @InjectMocks
    private SmoothedPriceIndicator<MarketData> cut;

    /**
     * The cut passes the close {@link Price} of {@link MarketData} passed as input to the {@link Smoother}.
     */
    @Test
    public void passesClosePriceOfMarketDataToSmoother() {
        final Stream<MarketData> marketData = Arrays.stream(new double[] {1.42128, 1.42032, 1.42142}).mapToObj(BasicMarketData::new);
        
        marketData.forEach(candle -> cut.indicate(candle));

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
