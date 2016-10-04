package de.voidnode.trading4j.api;

import de.voidnode.trading4j.domain.marketdata.MarketData;

/**
 * An algorithm that needs information about the market situation of an asset to do its work.
 *
 * @author Raik Bieniek
 * @param <M>
 *            The required market data type
 */
public interface MarketDataListener<M extends MarketData<?>> {

    /**
     * New market data for the asset is available.
     * 
     * @param marketData
     *            The market data.
     */
    void newData(final M marketData);
}
