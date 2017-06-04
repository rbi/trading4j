package de.voidnode.trading4j.indicators.adx;

import java.util.Optional;

import static java.lang.Math.max;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.marketdata.MarketData;
import de.voidnode.trading4j.domain.marketdata.WithOhlc;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * Calculates the "true" trading range by considering gaps between close and open prices.
 * 
 * @author Raik Bieniek
 * @param <C>
 *            The concrete type of data that is used as input.
 */
class TrueRange<C extends MarketData & WithOhlc> implements Indicator<Price, C> {

    private Price lastClose;

    @Override
    public Optional<Price> indicate(final C current) {
        if (lastClose == null) {
            lastClose = current.getClose();
            return Optional.empty();
        }

        final long volatility = current.getVolatility().asPipette();
        final long closeToHigh = current.getHigh().asPipette() - lastClose.asPipette();
        final long closeToLow = lastClose.asPipette() - current.getLow().asPipette();

        lastClose = current.getClose();
        return Optional.of(new Price(max(volatility, max(closeToHigh, closeToLow))));
    }
}
