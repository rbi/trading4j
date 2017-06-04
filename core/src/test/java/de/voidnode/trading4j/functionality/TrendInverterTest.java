package de.voidnode.trading4j.functionality;

import java.util.Optional;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.marketdata.MarketData;
import de.voidnode.trading4j.domain.marketdata.impl.BasicMarketData;

import static de.voidnode.trading4j.domain.MarketDirection.DOWN;
import static de.voidnode.trading4j.domain.MarketDirection.UP;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Checks if {@link TrendInverter} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class TrendInverterTest {

    private static final MarketData SOME_PRICE = new BasicMarketData(1.0);

    @Mock
    private Indicator<MarketDirection, MarketData> original;

    @InjectMocks
    private TrendInverter<MarketData> cut;

    /**
     * When the original {@link Indicator}r does not indicate a {@link MarketDirection}, the cut indicates no {@link MarketDirection} too.
     */
    @Test
    public void emptyTrendIsIndicatedWhenOriginalTrendIsEmpty() {
        when(original.indicate(any())).thenReturn(Optional.empty());
        assertThat(cut.indicate(SOME_PRICE)).isEmpty();
    }

    /**
     * When the original {@link Indicator} indicates a down {@link MarketDirection}, the cut indicates an up {@link MarketDirection}.
     */
    @Test
    public void upTrendIsIndicatedWhenOriginalTrendIsDown() {
        when(original.indicate(any())).thenReturn(Optional.of(DOWN));
        assertThat(cut.indicate(SOME_PRICE)).contains(UP);
    }

    /**
     * When the original {@link Indicator} indicates a up{@link MarketDirection}, the cut indicates an down {@link MarketDirection}.
     */
    @Test
    public void downTrendIsIndicatedWhenOriginalTrendIsUp() {
        when(original.indicate(any())).thenReturn(Optional.of(UP));
        assertThat(cut.indicate(SOME_PRICE)).contains(DOWN);
    }
}
