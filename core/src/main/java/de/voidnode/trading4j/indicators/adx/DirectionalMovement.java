package de.voidnode.trading4j.indicators.adx;

import java.util.Optional;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.marketdata.CandleStick;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * The positive and negative directional movement (+DM and -DM) which are part of the directional movement index.
 * 
 * @author Raik Bieniek
 * @param <C>
 *            The concrete type of data that is used as input.
 */
class DirectionalMovement<C extends CandleStick<?>> implements Indicator<Price, C> {

    private static final Optional<Price> ZERO = Optional.of(new Price(0));

    private final MarketDirection direction;

    private C lastCandle;

    /**
     * Initializes an instance with all required configuration.
     * 
     * @param direction
     *            The direction of the directional movement that should be used. Use {@link MarketDirection#UP} for the +DM and
     *            {@link MarketDirection#DOWN} for the -DM.
     */
    DirectionalMovement(final MarketDirection direction) {
        this.direction = direction;
    }

    @Override
    public Optional<Price> indicate(final C candle) {
        if (lastCandle == null) {
            lastCandle = candle;
            return Optional.empty();
        }

        final Price lastStrongest = lastCandle.getStrongest(direction);
        final Price lastWeakest = lastCandle.getWeakest(direction);
        final Price currentStrongest = candle.getStrongest(direction);
        lastCandle = candle;

        if (!currentStrongest.isStrongerThan(lastStrongest, direction)) {
            return ZERO;
        }

        final long strongestDiff = Math.abs(lastStrongest.asPipette() - currentStrongest.asPipette());

        final Price currentWeakest = candle.getWeakest(direction);
        if (lastWeakest.isStrongerThan(currentWeakest, direction.inverted())) {
            return Optional.of(new Price(strongestDiff));
        }

        final long weakestDiff = Math.abs(lastWeakest.asPipette() - currentWeakest.asPipette());

        return weakestDiff > strongestDiff ? ZERO : Optional.of(new Price(strongestDiff));
    }
}
