package de.voidnode.trading4j.server;

import java.util.Optional;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.marketdata.MarketData;

/**
 * A dummy {@link MarketDirection} {@link Indicator} that switches between {@link MarketDirection#UP} and {@link MarketDirection#DOWN} at every call
 * of {@link Indicator#indicate(MarketData)}.
 * 
 * <p>
 * The first {@link MarketDirection} will be {@link MarketDirection#UP}
 * </p>
 *
 * @author Raik Bieniek
 * @param <C>
 *            The type of {@link MarketData} that the {@link MarketDirection} should base on.
 */
class DummyIndicator<C extends MarketData> implements Indicator<MarketDirection, C> {

    private boolean up = false;

    @Override
    public Optional<MarketDirection> indicate(final C candle) {
        up = !up;
        return Optional.of(up ? MarketDirection.UP : MarketDirection.DOWN);
    }
}
