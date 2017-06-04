package de.voidnode.trading4j.domain.marketdata;

import de.voidnode.trading4j.domain.timeframe.TimeFrame;

/**
 * The length of the time frame of this {@link MarketData}.
 * 
 * @author Raik Bieniek
 *
 * @param <T>
 *            The time frame for this {@link MarketData}.
 */
public interface WithTimeFrame<T extends TimeFrame> extends MarketData {
}
