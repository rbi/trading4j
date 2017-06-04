package de.voidnode.trading4j.domain.marketdata;

import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.timeframe.TimeFrame;

/**
 * All {@link MarketData} needed for an Open-High-Low-Close (OHLC) or candle stick chart.
 * 
 * @author Raik Bieniek
 */
public interface WithOhlc extends MarketData {

    /**
     * The value at the beginning of the time period this {@link MarketData} aggregates.
     * 
     * @return the opening value
     */
    Price getOpen();

    /**
     * The highest value in the time period this {@link MarketData} aggregates.
     * 
     * @return the highest value
     */
    Price getHigh();
    
    /**
     * The lowest value in the time period this {@link MarketData} aggregates.
     * 
     * @return the lowest value
     */
    Price getLow();

    /**
     * The absolute difference between the {@link #getHigh()} and the {@link #getLow()} price.
     * 
     * @return the volatility of this {@link WithOhlc OHLC} {@link MarketData}.
     */
    default Price getVolatility() {
        return getHigh().minus(getLow());
    }

    /**
     * The strongest market {@link Price} in the {@link TimeFrame} of this candle considering a given market direction.
     * 
     * <p>
     * If the {@link MarketDirection} is up this is the {@link #getHigh()} {@link Price}. If it is down this is the
     * {@link #getLow()} {@link Price}.
     * </p>
     * 
     * @param trend
     *            The {@link MarketDirection} to consider.
     * @return The strongest {@link Price}.
     */
    default Price getStrongest(final MarketDirection trend) {
        return trend == MarketDirection.UP ? getHigh() : getLow();
    }

    /**
     * The strongest market {@link Price} in the {@link TimeFrame} of this candle considering a given market direction.
     * 
     * <p>
     * If the {@link MarketDirection} is up this is the {@link #getLow()} {@link Price}. If it is down this is the
     * {@link #getHigh()} {@link Price}.
     * </p>
     * 
     * @param trend
     *            The {@link MarketDirection} to consider.
     * @return The weakest {@link Price}.
     */
    default Price getWeakest(final MarketDirection trend) {
        return trend == MarketDirection.UP ? getLow() : getHigh();
    }
}
