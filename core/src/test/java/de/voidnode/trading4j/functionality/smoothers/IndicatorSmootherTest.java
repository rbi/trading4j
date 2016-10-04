package de.voidnode.trading4j.functionality.smoothers;

import java.util.Optional;

import static java.util.Optional.empty;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.TimeFrame.M1;
import de.voidnode.trading4j.domain.marketdata.MarketData;
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
 * Checks if {@link IndicatorSmoother} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class IndicatorSmootherTest {

    private static final MarketData<M1> SOME_MARKET_DATA = new MarketData<>(8.6);
    private static final MarketData<M1> OTHER_MARKET_DATA = new MarketData<>(42.1337);

    @Mock
    private Indicator<Price, MarketData<M1>> indicator;

    @Mock
    private Smoother<Price> smoother;

    @InjectMocks
    private IndicatorSmoother<Price, MarketData<M1>> cut;

    /**
     * The cut passes data provided as input to the indicator.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void passesInputDataToIndicator() {
        when(indicator.indicate(any(MarketData.class))).thenReturn(empty());

        cut.indicate(SOME_MARKET_DATA);
        verify(indicator).indicate(SOME_MARKET_DATA);

        cut.indicate(OTHER_MARKET_DATA);
        verify(indicator).indicate(OTHER_MARKET_DATA);
    }

    /**
     * The values of the indicator are passed to the smoother and its results are returned.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void passesNonEmptyIndicatorDataToSmootherAndReturnsResult() {
        when(indicator.indicate(any(MarketData.class))).thenReturn(optPrice(56)).thenReturn(empty())
                .thenReturn(optPrice(92));
        when(smoother.smooth(new Price(56))).thenReturn(optPrice(560));
        when(smoother.smooth(new Price(92))).thenReturn(optPrice(920));

        assertThat(cut.indicate(SOME_MARKET_DATA)).contains(new Price(560));
        assertThat(cut.indicate(SOME_MARKET_DATA)).isEmpty();
        assertThat(cut.indicate(SOME_MARKET_DATA)).contains(new Price(920));
    }

    private Optional<Price> optPrice(final long price) {
        return Optional.of(new Price(price));
    }
}
