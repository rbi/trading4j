package de.voidnode.trading4j.examples;

import de.voidnode.trading4j.api.OrderFilter;
import de.voidnode.trading4j.domain.Ratio;
import de.voidnode.trading4j.domain.marketdata.impl.CandleStick;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;

import static de.voidnode.trading4j.domain.RatioUnit.PERCENT;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Checks if {@link LowVolatilityOrderFilter} works as expected.
 * 
 * @author Raik Bieniek
 */
public class LowVolatilityOrderFilterTest {

    private final OrderFilter<CandleStick> requiresLowVolatility = new LowVolatilityOrderFilter<>(
            new Ratio(10, PERCENT));
    private final OrderFilter<CandleStick> requiresHighVolatility = new LowVolatilityOrderFilter<>(
            new Ratio(50, PERCENT));

    /**
     * When the volatility of the input {@link CandleStick} is high enough, trading is allowed.
     */
    @Test
    public void allowsTradesWhenCandleVolatilityIsHigherOrEqualToRequieredVolatility() {
        requiresHighVolatility.updateMarketData(new CandleStick(1.0, 1.3, 0.79, 1.0));
        assertThat(requiresHighVolatility.filterOrder(mock(BasicPendingOrder.class))).isEmpty();
        
        requiresLowVolatility.updateMarketData(new CandleStick(1.0, 1.0, 0.89, 1.0));
        assertThat(requiresLowVolatility.filterOrder(mock(BasicPendingOrder.class))).isEmpty();
    }

    /**
     * When the volatility of the input {@link CandleStick} is too low, trading is blocked.
     */
    @Test
    public void blockTradesWhenCandleVolatilityIsLessThanRequieredVolatility() {
        requiresHighVolatility.updateMarketData(new CandleStick(1.0, 1.5, 1.01, 1.0));
        assertThat(requiresHighVolatility.filterOrder(mock(BasicPendingOrder.class))).isPresent();
        
        requiresLowVolatility.updateMarketData(new CandleStick(1.0, 1.0, 0.91, 1.0));
        assertThat(requiresLowVolatility.filterOrder(mock(BasicPendingOrder.class))).isPresent();
    }
}
