package de.voidnode.trading4j.api;

import java.util.Optional;

import de.voidnode.trading4j.domain.TimeFrame;
import de.voidnode.trading4j.domain.marketdata.MarketData;

/**
 * A trading indicator that calculates various values (like a trend) for given market price data.
 * 
 * @author Raik Bieniek
 * @param <RESULT>
 *            The type of the indication results this indicator produces.
 * @param <INPUT>
 *            The type of {@link MarketData}s that is needed for the indicator.
 */
public interface Indicator<RESULT, INPUT extends MarketData<? extends TimeFrame>> {

    /**
     * The indication result at the time of a {@link MarketData}.
     * 
     * <p>
     * This method should be called for each new {@link MarketData} point that gets available in the correct sequence of
     * the {@link MarketData}s. This should even be done when the result of this indicator is currently not needed as
     * most indicators calculate their results based on multiple past {@link MarketData}s.
     * </p>
     * 
     * @param marketPrice
     *            The next marketPrice.
     * @return The next indication result if any is available. If an empty {@link Optional} is returned this could mean
     *         that the {@link Indicator} has not yet collected enough data to provide an indication result. It could
     *         also mean that the {@link Indicator} has no indication result for the current data point.
     */
    Optional<RESULT> indicate(final INPUT marketPrice);
}
