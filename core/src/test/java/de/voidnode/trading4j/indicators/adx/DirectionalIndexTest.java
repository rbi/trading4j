package de.voidnode.trading4j.indicators.adx;

import java.util.Optional;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.Ratio;
import de.voidnode.trading4j.domain.marketdata.MarketData;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.timeframe.M1;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Checks if {@link DirectionalIndex} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class DirectionalIndexTest {

    private static final MarketData<M1> SOME_MARKET_PRICE = new MarketData<>(new Price(1.0));
    private static final MarketData<M1> OTHER_MARKET_PRICE = new MarketData<>(new Price(2.0));

    @Mock
    private Indicator<Price, MarketData<M1>> dm;

    @Mock
    private Indicator<Price, MarketData<M1>> trueRange;

    private Indicator<Ratio, MarketData<M1>> cut;

    /**
     * Sets up the class under test and its dependencies.
     */
    @Before
    @SuppressWarnings("unchecked")
    public void setupCutAndMocks() {
        cut = new DirectionalIndex<>(dm, trueRange);

        when(dm.indicate(any(MarketData.class))).thenReturn(Optional.empty());
        when(trueRange.indicate(any(MarketData.class))).thenReturn(Optional.empty());
    }

    /**
     * All market data passed as input is passed through to the indicators.
     */
    @Test
    public void passesAllMarketDataToTheIndicators() {
        cut.indicate(SOME_MARKET_PRICE);
        cut.indicate(OTHER_MARKET_PRICE);

        verify(dm).indicate(SOME_MARKET_PRICE);
        verify(trueRange).indicate(SOME_MARKET_PRICE);

        verify(dm).indicate(OTHER_MARKET_PRICE);
        verify(trueRange).indicate(OTHER_MARKET_PRICE);
    }

    /**
     * If one of the indicators is empty, the cut returns empty.
     */
    @Test
    public void returnsEmptyIfOneIndicatorIsEmpty() {
        when(dm.indicate(SOME_MARKET_PRICE)).thenReturn(Optional.empty()).thenReturn(optPrice(1));
        when(trueRange.indicate(SOME_MARKET_PRICE)).thenReturn(optPrice(1)).thenReturn(Optional.empty());

        assertThat(cut.indicate(SOME_MARKET_PRICE)).isEmpty();
        assertThat(cut.indicate(SOME_MARKET_PRICE)).isEmpty();
    }

    /**
     * The directional index is calculated by dividing the directional movement through the true range.
     */
    @Test
    public void dividesDirectionalMovementThrougTrueRange() {
        when(dm.indicate(SOME_MARKET_PRICE)).thenReturn(optPrice(10)).thenReturn(optPrice(900));
        when(trueRange.indicate(SOME_MARKET_PRICE)).thenReturn(optPrice(20)).thenReturn(optPrice(30));

        assertThat(cut.indicate(SOME_MARKET_PRICE)).contains(new Ratio(0.5));
        assertThat(cut.indicate(SOME_MARKET_PRICE)).contains(new Ratio(30));
    }

    /**
     * When the true range is 0 and therefore devision by 0 would occur, the directional index is 0.
     */
    @Test
    public void returnsZeroWhenTrueRangeIsEmpty() {
        when(dm.indicate(SOME_MARKET_PRICE)).thenReturn(optPrice(50));
        when(trueRange.indicate(SOME_MARKET_PRICE)).thenReturn(optPrice(0));
        
        assertThat(cut.indicate(SOME_MARKET_PRICE)).contains(new Ratio(0));
    }

    private Optional<Price> optPrice(final int price) {
        return Optional.of(new Price(price));
    }
}
