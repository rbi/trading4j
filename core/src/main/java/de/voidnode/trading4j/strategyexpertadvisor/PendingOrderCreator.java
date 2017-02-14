package de.voidnode.trading4j.strategyexpertadvisor;

import java.util.Optional;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.OrderEventListener;
import de.voidnode.trading4j.api.OrderManagement;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;
import de.voidnode.trading4j.domain.orders.MutableCloseConditions;
import de.voidnode.trading4j.domain.orders.MutablePendingOrder;
import de.voidnode.trading4j.domain.orders.OrderType;

import static de.voidnode.trading4j.domain.MarketDirection.UP;
import static de.voidnode.trading4j.domain.orders.OrderType.BUY;
import static de.voidnode.trading4j.domain.orders.OrderType.SELL;

/**
 * Creates {@link BasicPendingOrder} when an appropriate entry into the market was found.
 * 
 * @author Raik Bieniek
 */
class PendingOrderCreator {

    private final Broker<BasicPendingOrder> broker;
    private final TradingStrategy<?> strategy;

    /**
     * Initializes an instances.
     * 
     * @param strategy
     *            The values for potential pending orders.
     * @param broker
     *            The broker that should be used to place orders.
     */
    PendingOrderCreator(final TradingStrategy<?> strategy, final Broker<BasicPendingOrder> broker) {
        this.strategy = strategy;
        this.broker = broker;
    }

    /**
     * Checks if the time is right to enter the market and does so if it is.
     *
     * @param eventListener
     *            A listener for events on orders this instance opens.
     * @return If a pending order was placed, an util to manage this order and an empty {@link Optional} if not.
     */
    public Optional<Order> checkMarketEntry(final OrderEventListener eventListener) {
        if (!(strategy.getEntrySignal().isPresent() && strategy.getEntryPrice().isPresent()
                && strategy.getTakeProfit().isPresent() && strategy.getStopLoose().isPresent())) {
            return Optional.empty();
        }

        if (!strategy.getTrend().equals(strategy.getEntrySignal())) {
            return Optional.empty();
        }

        final OrderType type = strategy.getEntrySignal().get() == UP ? BUY : SELL;

        final BasicPendingOrder pendingOrder = new MutablePendingOrder().setType(type)
                .setExecutionCondition(strategy.getEntryCondition())
                .setEntryPrice(strategy.getEntryPrice().get()).setCloseConditions(new MutableCloseConditions()
                        .setTakeProfit(strategy.getTakeProfit().get()).setStopLoose(strategy.getStopLoose().get()))
                .toImmutableBasicPendingOrder();

        final OrderManagement orderManagement = broker.sendOrder(pendingOrder, eventListener);
        return Optional.of(new Order(pendingOrder, orderManagement));
    }
}
