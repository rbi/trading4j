package de.voidnode.trading4j.api;

import java.util.Optional;

import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.marketdata.DatedCandleStick;
import de.voidnode.trading4j.domain.timeframe.M1;

/**
 * Creates trend {@link Indicator}s.
 * 
 * @author Raik Bieniek
 */
public interface TrendIndicatorFactory {

    /**
     * Creates an {@link Indicator} for trends denoted by its given number.
     * 
     * @param indicatorNumber
     *            The number of the {@link Indicator} to create.
     * @return The created {@link Indicator} or an empty {@link Optional} when no {@link Indicator} with the given
     *         number is known.
     */
    Optional<Indicator<MarketDirection, DatedCandleStick<M1>>> newIndicatorByNumber(int indicatorNumber);
}
