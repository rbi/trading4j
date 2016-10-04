package de.voidnode.trading4j.api;

import java.util.Optional;

import de.voidnode.trading4j.domain.marketdata.MarketData;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;

/**
 * Checks if condition are given that should prevent from sending orders to a broker.
 * 
 * @author Raik Bieniek
 * @param <C>
 *            The concrete {@link MarketData} type that is needed for checking market conditions.
 */
public interface OrderFilter<C extends MarketData<?>> {

    /**
     * Updates the {@link OrderFilter} with the latest MarketData.
     * 
     * <p>
     * It is guaranteed that all available {@link MarketData} will be passed in chronological correct order to this
     * method. Therefore {@link MarketData} passed in this method may be consulted when deciding if a given
     * {@link BasicPendingOrder} passed to {@link #filterOrder(BasicPendingOrder)} should be blocked or not.
     * </p>
     * 
     * @param marketData
     *            The current market data.
     */
    void updateMarketData(C marketData);

    /**
     * Checks if a given new {@link BasicPendingOrder} should send to a broker or should be blocked.
     * 
     * @param order
     *            The order that should be checked.
     * @return An empty {@link Optional} if sending the order is allowed and {@link Failed} when the order is blocked.
     */
    Optional<Failed> filterOrder(BasicPendingOrder order);
}
