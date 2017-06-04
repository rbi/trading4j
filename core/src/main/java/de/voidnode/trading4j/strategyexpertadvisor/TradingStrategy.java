package de.voidnode.trading4j.strategyexpertadvisor;

import java.util.Optional;

import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.marketdata.MarketData;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;
import de.voidnode.trading4j.domain.orders.ExecutionCondition;

/**
 * A strategy for trading assets.
 * 
 * <p>
 * A strategy tells when to enter and provides the values for an order.
 * </p>
 * 
 * @author Raik Bieniek
 *
 * @param <C>
 *            The concrete type of {@link MarketData}s that are used as input.
 */
public interface TradingStrategy<C extends MarketData> { 

    /**
     * Updates the values for potential {@link BasicPendingOrder}s based on new market data.
     * 
     * @param candle
     *            New market data.
     */
    void update(C candle);

    /**
     * The current signal that indicates the direction into which the market should be entered if there is currently a
     * signal.
     * 
     * @return The direction to enter the market if there is a signal and an empty {@link Optional} if there is no
     *         signal. Most times there wont be a signal.
     */
    Optional<MarketDirection> getEntrySignal();

    /**
     * The current {@link MarketDirection} of the market price for the asset.
     * 
     * <p>
     * Open orders and trades will be canceled when they do not match the trend.
     * </p>
     * 
     * @return The {@link MarketDirection} for the asset if the {@link MarketDirection} is known and an empty {@link Optional} if the
     *         {@link MarketDirection} is unknown.
     */
    Optional<MarketDirection> getTrend();

    /**
     * The currently suggested {@link Price} for {@link BasicPendingOrder}s at which the market should be entered.
     * 
     * @return The entry {@link Price} of some can be suggested and an empty {@link Optional} if not.
     */
    Optional<Price> getEntryPrice();

    /**
     * The currently suggested {@link Price} for {@link BasicPendingOrder}s at which the order should be closed for the
     * winning case.
     * 
     * <p>
     * A take profit {@link Price} is required to open a {@link BasicPendingOrder} if the strategy suggest to use no
     * take profit limit it should return a {@link Price} of 0.
     * </p>
     * 
     * @return The take profit {@link Price} of some can be suggested and an empty {@link Optional} if not.
     */
    Optional<Price> getTakeProfit();

    /**
     * The currently suggested {@link Price} for {@link BasicPendingOrder}s at which the order should be closed for the
     * loosing case.
     * 
     * @return The stop loose {@link Price} of some can be suggested and an empty {@link Optional} if not.
     */
    Optional<Price> getStopLoose();

    /**
     * The execution condition for new {@link BasicPendingOrder}.
     * 
     * @return The execution condition.
     */
    ExecutionCondition getEntryCondition();
}