package de.voidnode.trading4j.api;

import de.voidnode.trading4j.domain.marketdata.MarketData;

/**
 * An algorithm that trades various assets based on market data.
 * 
 * @author Raik Bieniek
 *
 * @param <C>
 *            The type of {@link MarketData}s that is required as input.
 */
public interface ExpertAdvisor<C extends MarketData> extends MarketDataListener<C> {

}
