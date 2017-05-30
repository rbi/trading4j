package de.voidnode.trading4j.server;

import java.time.Instant;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.ExpertAdvisor;
import de.voidnode.trading4j.api.Failed;
import de.voidnode.trading4j.api.OrderEventListener;
import de.voidnode.trading4j.domain.marketdata.CandleStick;
import de.voidnode.trading4j.domain.marketdata.MarketData;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;
import de.voidnode.trading4j.domain.orders.ExecutionCondition;
import de.voidnode.trading4j.domain.orders.MutableCloseConditions;
import de.voidnode.trading4j.domain.orders.MutablePendingOrder;
import de.voidnode.trading4j.domain.orders.OrderType;
import de.voidnode.trading4j.domain.timeframe.M1;

import static de.voidnode.trading4j.domain.monetary.PriceUnit.PIP;
import static de.voidnode.trading4j.domain.orders.ExecutionCondition.LIMIT;
import static de.voidnode.trading4j.domain.orders.ExecutionCondition.STOP;
import static de.voidnode.trading4j.domain.orders.OrderType.BUY;
import static de.voidnode.trading4j.domain.orders.OrderType.SELL;

/**
 * An {@link ExpertAdvisor} that places a new order on every new {@link MarketData}.
 * 
 * @author Raik Bieniek
 * @param <C>
 *            The {@link CandleStick} type to use as input.
 */
class OrderOnEveryTickExpertAdvisor<C extends CandleStick<M1>> implements ExpertAdvisor<C> {

    private final Broker<BasicPendingOrder> broker;
    private final OrderEventListener eventListener = new OrderEventNonListener();

    /**
     * Initializes the expert advisor.
     * 
     * @param broker
     *            The broker where the orders should be send to.
     */
    OrderOnEveryTickExpertAdvisor(final Broker<BasicPendingOrder> broker) {
        this.broker = broker;
    }

    @Override
    public void newData(final C marketData) {
        final Price close = marketData.getClose();

        final OrderType type = OrderType.BUY;
        final ExecutionCondition condition = ExecutionCondition.LIMIT;

        final MutablePendingOrder pendingOrder = new MutablePendingOrder();

        pendingOrder.setType(type).setExecutionCondition(condition)
                .setEntryPrice((type == BUY && condition == STOP) || (type == SELL && condition == LIMIT)
                        ? close.plus(2, PIP) : close.minus(2, PIP))
                .setCloseConditions(new MutableCloseConditions()//
                        .setTakeProfit(type == BUY ? close.plus(10, PIP) : close.minus(10, PIP))//
                        .setStopLoose(type == BUY ? close.minus(10, PIP) : close.plus(10, PIP)));

        broker.sendOrder(pendingOrder.toImmutableBasicPendingOrder(), eventListener);
    }

    /**
     * An implementation that does nothing on order events.
     */
    private static class OrderEventNonListener implements OrderEventListener {

        @Override
        public void orderRejected(final Failed failure) {
            // do nothing
        }

        @Override
        public void orderOpened(final Instant time, final Price price) {
            // do nothing
        }

        @Override
        public void orderClosed(final Instant time, final Price price) {
            // do nothing
        }
    }

}
