package de.voidnode.trading4j.strategyexpertadvisor;

import java.time.Instant;
import java.util.Optional;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.ExpertAdvisor;
import de.voidnode.trading4j.api.Failed;
import de.voidnode.trading4j.api.OrderEventListener;
import de.voidnode.trading4j.domain.marketdata.MarketData;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;

/**
 * Creates and manages orders based on a strategy taken as input.
 *
 * @author Raik Bieniek
 * @param <C>
 *            The concrete type of {@link MarketData}s that are used as input.
 */
public class StrategyExpertAdvisor<C extends MarketData> implements ExpertAdvisor<C>, OrderEventListener {

    private final TradingStrategy<C> strategy;
    private final PendingOrderCreator creator;
    private final PendingOrderManager orderManager;
    private final TradeManager tradeManager;

    private State currentState = State.TRY_CREATE;
    private Optional<Order> currentOrder;

    /**
     * Initial the expert advisor.
     * 
     * @param strategy
     *            A calculator thats values should be updated when new market data arrives.
     * @param broker
     *            The broker that should be used to execute orders.
     */
    public StrategyExpertAdvisor(final TradingStrategy<C> strategy, final Broker<BasicPendingOrder> broker) {
        this.strategy = strategy;
        this.creator = new PendingOrderCreator(strategy, broker);
        this.orderManager = new PendingOrderManager(strategy, broker);
        this.tradeManager = new TradeManager(strategy);
    }

    /**
     * Initializes the expert advisor.
     * 
     * @param strategy
     *            A calculator thats values should be updated when new market data arrives.
     * @param creator
     *            Used to place pending orders.
     * @param orderManager
     *            Used to manage opened pending orders.
     * @param tradeManager
     *            Used to manage active trades.
     */
    StrategyExpertAdvisor(final TradingStrategy<C> strategy, final PendingOrderCreator creator,
            final PendingOrderManager orderManager, final TradeManager tradeManager) {
        this.strategy = strategy;
        this.creator = creator;
        this.orderManager = orderManager;
        this.tradeManager = tradeManager;
    }

    @Override
    public void newData(final C candleStick) {
        strategy.update(candleStick);
        switch (currentState) {
            case TRY_CREATE:
                currentOrder = creator.checkMarketEntry(this);
                if (currentOrder.isPresent()) {
                    currentState = State.MANAGE_ORDER;
                }
                break;
            case MANAGE_ORDER:
                currentOrder = orderManager.manageOrder(currentOrder.get(), this);
                if (!currentOrder.isPresent()) {
                    currentState = State.TRY_CREATE;
                }
                break;
            case MANAGE_TRADE:
                currentOrder = tradeManager.manageTrade(currentOrder.get());
                if (!currentOrder.isPresent()) {
                    currentState = State.TRY_CREATE;
                }
                break;
            default:
                throw new IllegalStateException("Unhandled state " + currentState);
        }
    }

    @Override
    public void orderRejected(final Failed failure) {
       currentState = State.TRY_CREATE;
    }

    @Override
    public void orderOpened(final Instant time, final Price price) {
        currentState = State.MANAGE_TRADE;
    }

    @Override
    public void orderClosed(final Instant time, final Price price) {
        currentState = State.TRY_CREATE;
    }

    /**
     * The current state the strategy is in.
     */
    private enum State {
        TRY_CREATE, MANAGE_ORDER, MANAGE_TRADE
    }
}
