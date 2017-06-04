package de.voidnode.trading4j.domain.marketdata;

import java.time.Instant;

/**
 * The starting time of the time frame of a {@link MarketData} instance.
 * 
 * @author Raik Bieniek
 */
public interface WithTime extends MarketData {


    /**
     * The starting time of the time frame this {@link MarketData} aggregates.
     * 
     * @return the time.
     */
    Instant getTime();
}
