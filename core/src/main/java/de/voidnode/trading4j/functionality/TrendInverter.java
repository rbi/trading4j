package de.voidnode.trading4j.functionality;

import java.util.Optional;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.marketdata.MarketData;

import static de.voidnode.trading4j.domain.MarketDirection.DOWN;
import static de.voidnode.trading4j.domain.MarketDirection.UP;

/**
 * Inverts the {@link MarketDirection} of a {@link MarketDirection} indicating {@link Indicator}.
 *
 * @author Raik Bieniek
 * @param <MP>
 *            The type of {@link MarketData} the original {@link Indicator} uses.
 */
public class TrendInverter<MP extends MarketData<?>>
        implements Indicator<MarketDirection, MP> {

    private final Indicator<MarketDirection, MP> original;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param original
     *            The original indicator thats trend should be inverted.
     */
    public TrendInverter(final Indicator<MarketDirection, MP> original) {
        this.original = original;
    }

    @Override
    public Optional<MarketDirection> indicate(final MP marketPrice) {
        return original.indicate(marketPrice).map(signal -> signal == UP ? DOWN : UP);
    }
}
