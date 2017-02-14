package de.voidnode.trading4j.tradetracker;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.Failed;
import de.voidnode.trading4j.api.MarketDataListener;
import de.voidnode.trading4j.api.OrderEventListener;
import de.voidnode.trading4j.api.OrderManagement;
import de.voidnode.trading4j.domain.TimeFrame.M1;
import de.voidnode.trading4j.domain.marketdata.FullMarketData;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;
import de.voidnode.trading4j.domain.orders.CloseConditions;
import de.voidnode.trading4j.domain.orders.OrderType;

import static de.voidnode.trading4j.domain.orders.OrderType.BUY;

/**
 * Simulates a real broker by receiving {@link BasicPendingOrder} orders and notifying when a real broker would have
 * opened or closed them.
 * 
 * @author Raik Bieniek
 *
 * @param <C>
 *            The type of market data that is received.
 */
public class SimulatedBroker<C extends FullMarketData<M1>> implements Broker<BasicPendingOrder>, MarketDataListener<C> {

    private List<Order> orders = new LinkedList<>();

    @Override
    public OrderManagement sendOrder(final BasicPendingOrder order,
            final OrderEventListener eventListener) {
        final Order newOrder = new Order(order, eventListener);
        orders.add(newOrder);
        return newOrder;
    }

    @Override
    public void newData(final C candleStick) {
        final Iterator<Order> it = orders.iterator();
        while (it.hasNext()) {
            final Order order = it.next();
            if (order.opened) {
                if (order.type == BUY) {
                    checkClosingBuyOrder(order, candleStick, it);
                } else {
                    checkClosingSellOrder(order, candleStick, it);
                }
            } else {
                if (order.type == BUY) {
                    checkOpeningBuyOrder(order, candleStick);
                } else {
                    checkOpeningSellOrder(order, candleStick);
                }
            }
        }
    }

    private void checkOpeningBuyOrder(final Order order, final C candleStick) {
        final Price spread = candleStick.getSpread();
        if (order.entryPrice
                .isBetweenInclusive(candleStick.getOpen().plus(spread), candleStick.getClose().plus(spread))) {
            order.opened = true;
            order.eventListener.orderOpened(candleStick.getTime(), order.entryPrice);
        }
    }

    private void checkOpeningSellOrder(final Order order, final C candleStick) {
        if (order.entryPrice.isBetweenInclusive(candleStick.getOpen(), candleStick.getClose())) {
            order.opened = true;
            order.eventListener.orderOpened(candleStick.getTime(), order.entryPrice);
        }
    }

    private void checkClosingBuyOrder(final Order order, final C candleStick, final Iterator<Order> it) {
        final Price open = candleStick.getOpen();
        final Price close = candleStick.getClose();
        final Price takeProfit = order.closeConditions.getTakeProfit();
        final Price stopLoose = order.closeConditions.getStopLoose();

        checkClose(open, close, takeProfit, stopLoose).ifPresent(closePrice -> {
            it.remove();
            order.eventListener.orderClosed(candleStick.getTime(), closePrice);
        });
    }

    private void checkClosingSellOrder(final Order order, final C candleStick, final Iterator<Order> it) {
        final Price open = candleStick.getOpen().plus(candleStick.getSpread());
        final Price close = candleStick.getClose().plus(candleStick.getSpread());
        final Price takeProfit = order.closeConditions.getTakeProfit();
        final Price stopLoose = order.closeConditions.getStopLoose();

        checkClose(open, close, takeProfit, stopLoose).ifPresent(closePrice -> {
            it.remove();
            order.eventListener.orderClosed(candleStick.getTime(), closePrice);
        });
    }

    private Optional<Price> checkClose(final Price open, final Price close, final Price takeProfit,
            final Price stopLoose) {
        if (takeProfit.isBetweenInclusive(open, close)) {
            return Optional.of(takeProfit);
        } else if (stopLoose.isBetweenInclusive(open, close)) {
            return Optional.of(stopLoose);
        }
        return Optional.empty();
    }

    /**
     * Contains all data for a single order placed at this broker.
     */
    private class Order implements OrderManagement {

        private final OrderEventListener eventListener;
        private final Price entryPrice;
        private final OrderType type;
        private CloseConditions closeConditions;

        private boolean opened;

        Order(final BasicPendingOrder order, final OrderEventListener eventListener) {
            this.entryPrice = order.getEntryPrice();
            this.type = order.getType();
            this.closeConditions = order.getCloseConditions();
            this.eventListener = eventListener;
        }

        @Override
        public void closeOrCancelOrder() {
            orders.remove(this);
        }

        @Override
        public Optional<Failed> changeCloseConditionsOfOrder(final CloseConditions conditions) {
            this.closeConditions = conditions;
            return Optional.empty();
        }
    }
}
