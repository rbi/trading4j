package de.voidnode.trading4j.strategyexpertadvisor;

import java.util.Optional;

import de.voidnode.trading4j.api.Failed;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;
import de.voidnode.trading4j.domain.orders.CloseConditions;
import de.voidnode.trading4j.domain.orders.MutableCloseConditions;
import de.voidnode.trading4j.domain.orders.MutablePendingOrder;

import static de.voidnode.trading4j.domain.orders.OrderType.BUY;

/**
 * Manages active trades by adjusting there price values according to the current market situation.
 * 
 * @author Raik Bieniek
 */
class TradeManager {

    private final TradingStrategy<?> strategy;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param strategy
     *            Used to check if changing {@link CloseConditions} is necessary.
     */
    TradeManager(final TradingStrategy<?> strategy) {
        this.strategy = strategy;
    }

    /**
     * Manages an active trade.
     * 
     * @param trade
     *            The trade to manage.
     * @return The instance to manage the trade if the trade is still active and an empty {@link Optional} if the trade
     *         has been closed.
     */
    public Optional<Order> manageTrade(final Order trade) {
        return strategy
                .getStopLoose()
                .map(stopLoose -> {
                    final BasicPendingOrder currentTrade = trade.getPendingOrder();
                    boolean changeStopLoose = false;
                    boolean changeTakeProfit = false;
                    if (currentTrade.getType() == BUY) {
                        if (stopLoose.isGreaterThan(currentTrade.getCloseConditions().getStopLoose())) {
                            changeStopLoose = true;
                        }
                    } else {
                        if (stopLoose.isLessThan(currentTrade.getCloseConditions().getStopLoose())) {
                            changeStopLoose = true;
                        }
                    }
                    if (strategy.getTakeProfit().isPresent()) {
                        if (!strategy.getTakeProfit().get().equals(currentTrade.getCloseConditions().getTakeProfit())) {

                            changeTakeProfit = true;
                        }
                    }
                    if (changeStopLoose || changeTakeProfit) {
                        return changeCloseConditions(trade, changeStopLoose ? stopLoose : currentTrade
                                .getCloseConditions().getStopLoose(), changeTakeProfit ? strategy.getTakeProfit().get()
                                : currentTrade.getCloseConditions().getTakeProfit());
                    } else {
                        return Optional.of(trade);
                    }
                }).orElse(Optional.of(trade));
    }

    private Optional<Order> changeCloseConditions(final Order trade, final Price stopLoose, final Price takeProfit) {
        final CloseConditions newCloseConditions = new MutableCloseConditions(trade.getPendingOrder()
                .getCloseConditions()).setStopLoose(stopLoose).setTakeProfit(takeProfit).toImmutable();

        final Optional<Failed> changeFailed = trade.getOrderManagement().changeCloseConditionsOfOrder(
                newCloseConditions);

        if (changeFailed.isPresent()) {
            trade.getOrderManagement().closeOrCancelOrder();
            return Optional.empty();
        } else {
            return Optional.of(new Order(new MutablePendingOrder(trade.getPendingOrder()).setCloseConditions(
                    newCloseConditions).toImmutableBasicPendingOrder(), trade.getOrderManagement()));
        }
    }
}
