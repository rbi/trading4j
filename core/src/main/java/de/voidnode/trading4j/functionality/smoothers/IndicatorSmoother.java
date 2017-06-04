package de.voidnode.trading4j.functionality.smoothers;

import java.util.Optional;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.marketdata.MarketData;

/**
 * Smoothes the output value of an {@link Indicator}.
 * 
 * <p>
 * If the {@link Indicator} that should be smoothed does not provide a result for a given {@link MarketData}, this
 * indicator will be empty too.
 * </p>
 * 
 * @author Raik Bieniek
 * @param <V>
 *            The type of the value that the {@link Indicator} produces.
 * @param <MP>
 *            The concrete type of {@link MarketData} that is passed as input.
 */
public class IndicatorSmoother<V, MP extends MarketData> implements Indicator<V, MP> {

    private final Indicator<V, MP> indicator;
    private final Smoother<V> smoother;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param indicator
     *            The indicator thats value should be smoothed.
     * @param smoother
     *            The algorithm that should be used to smooth the value.
     */
    public IndicatorSmoother(final Indicator<V, MP> indicator, final Smoother<V> smoother) {
        this.indicator = indicator;
        this.smoother = smoother;
    }

    @Override
    public Optional<V> indicate(final MP marketPrice) {
        return indicator.indicate(marketPrice).flatMap(value -> smoother.smooth(value));
    }
}
