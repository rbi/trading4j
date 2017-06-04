package de.voidnode.trading4j.domain.marketdata;

import de.voidnode.trading4j.domain.timeframe.TimeFrame;

/**
 * The amount of individual trades during the {@link TimeFrame} of this {@link MarketData}.
 * 
 * @author Raik Bieniek
 */
public interface WithTickCount {

    /**
     * The amount of individual trades that where executed in the {@link TimeFrame} of this {@link MarketData}.
     * 
     * @return The trade amount.
     */
    long getTickCount();
}
