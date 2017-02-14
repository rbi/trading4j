package de.voidnode.trading4j.strategyexpertadvisor;

import java.util.Optional;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.OrderEventListener;
import de.voidnode.trading4j.api.OrderManagement;
import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;
import de.voidnode.trading4j.domain.orders.MutableCloseConditions;
import de.voidnode.trading4j.domain.orders.MutablePendingOrder;

import static de.voidnode.trading4j.domain.MarketDirection.DOWN;
import static de.voidnode.trading4j.domain.MarketDirection.UP;
import static de.voidnode.trading4j.domain.orders.OrderType.BUY;
import static de.voidnode.trading4j.domain.orders.OrderType.SELL;

/**
 * Manages opened {@link BasicPendingOrder}s by adjusting its prices to the current market situation.
 * 
 * @author Raik Bieniek
 */
class PendingOrderManager {

    private final TradingStrategy<?> strategy;
    private final Broker<BasicPendingOrder> broker;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param strategy
     *            Used to get currently best values for {@link BasicPendingOrder}s.
     * @param broker
     *            Used to change the state of placed orders.
     */
    PendingOrderManager(final TradingStrategy<?> strategy, final Broker<BasicPendingOrder> broker) {
        this.strategy = strategy;
        this.broker = broker;
    }

    /**
     * Manages an opened order.
     * 
     * @param order
     *            The order to manage
     * @param eventListener
     *            A listener for events on orders this instance opens.
     * @return The order active after this managing. This is the adjusted order if it was changed, the orderManager
     *         passed in as parameter if it hasn't been changed and an empty {@link Optional} if the order has been
     *         closed.
     */
    public Optional<Order> manageOrder(final Order order, final OrderEventListener eventListener) {
        if (strategy.getTrend().isPresent()) {
            final MarketDirection trend = strategy.getTrend().get();
            if (trend == UP && order.getPendingOrder().getType() == SELL) {
                order.getOrderManagement().closeOrCancelOrder();
                return Optional.empty();
            } else if (trend == DOWN && order.getPendingOrder().getType() == BUY) {
                order.getOrderManagement().closeOrCancelOrder();
                return Optional.empty();
            }
        }

        if (stopLooseDifferent(order.getPendingOrder()) || entryPriceDifferent(order.getPendingOrder())) {
            order.getOrderManagement().closeOrCancelOrder();
            return openUpdatedPendingOrder(order.getPendingOrder(), eventListener);
        }
        return Optional.of(order);
    }

    private boolean entryPriceDifferent(final BasicPendingOrder order) {
        return strategy.getEntryPrice().isPresent() && !strategy.getEntryPrice().get().equals(order.getEntryPrice());
    }

    private boolean stopLooseDifferent(final BasicPendingOrder order) {
        return strategy.getStopLoose().isPresent()
                && !strategy.getStopLoose().get().equals(order.getCloseConditions().getStopLoose());
    }

    private Optional<Order> openUpdatedPendingOrder(final BasicPendingOrder order, final OrderEventListener eventListener) {

        if (!(strategy.getEntryPrice().isPresent() && strategy.getStopLoose().isPresent() && strategy.getTakeProfit()
                .isPresent())) {
            return Optional.empty();
        }

        final BasicPendingOrder newOrder = new MutablePendingOrder(order)
                .setEntryPrice(strategy.getEntryPrice().get())
                .setCloseConditions(
                        new MutableCloseConditions().setStopLoose(strategy.getStopLoose().get()).setTakeProfit(
                                order.getCloseConditions().getTakeProfit())).toImmutableBasicPendingOrder();

        final OrderManagement orderManagement = broker.sendOrder(newOrder, eventListener);
        return Optional.of(new Order(newOrder, orderManagement));
    }
}
