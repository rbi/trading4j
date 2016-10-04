package de.voidnode.trading4j.examples;

import java.util.Optional;

import de.voidnode.trading4j.api.Failed;
import de.voidnode.trading4j.api.OrderFilter;
import de.voidnode.trading4j.domain.Ratio;
import de.voidnode.trading4j.domain.marketdata.CandleStick;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;

/**
 * Prevents trading when the volatility of the market is too low.
 *
 * @author Raik Bieniek
 * @param <C>
 *            The type of data that is used as input.
 */
public class LowVolatilityOrderFilter<C extends CandleStick<?>> implements OrderFilter<C> {

    private static final Optional<Failed> BLOCKED = Optional
            .of(new Failed("Trading is blocked because the volatility of the market is too low."));

    private Ratio requiredVolatility;
    private C candleStick;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param requiredVolatility
     *            The volatility required for trades to not beeing blocked relative to the open price of a
     *            {@link CandleStick}.
     */
    public LowVolatilityOrderFilter(final Ratio requiredVolatility) {
        this.requiredVolatility = requiredVolatility;
    }

    @Override
    public void updateMarketData(final C candleStick) {
        this.candleStick = candleStick;
    }

    @Override
    public Optional<Failed> filterOrder(final BasicPendingOrder order) {
        final Price absoluteRequieredVolatility = candleStick.getOpen().multiply(requiredVolatility);
        if (candleStick.getVolatility().isLessThan(absoluteRequieredVolatility)) {
            return BLOCKED;
        }
        return Optional.empty();
    }
}
