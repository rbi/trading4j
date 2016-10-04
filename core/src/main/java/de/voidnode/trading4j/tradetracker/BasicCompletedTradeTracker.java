package de.voidnode.trading4j.tradetracker;

import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.domain.TimeFrame.M1;
import de.voidnode.trading4j.domain.marketdata.CandleStick;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;
import de.voidnode.trading4j.domain.trades.BasicCompletedTrade;
import de.voidnode.trading4j.domain.trades.TradeEvent;

/**
 * Keeps track and notifies a listener of all complete trades that are executed at a broker.
 *
 * @author Raik Bieniek
 * @param <C>
 *            The concrete type of data passed as input.
 */
public class BasicCompletedTradeTracker<C extends CandleStick<M1>>
        extends TradeTracker<C, BasicPendingOrder, BasicCompletedTrade> {

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param broker
     *            A broker that can execute the orders.
     * @param currentTime
     *            A supplier that returns the current time.
     */
    public BasicCompletedTradeTracker(final Broker<BasicPendingOrder> broker, final Supplier<Instant> currentTime) {
        super(broker, currentTime);
    }

    @Override
    protected BasicCompletedTrade createCompletedTrade(final BasicPendingOrder originalOrder, final C lastCandleStick,
            final List<TradeEvent> events) {
        return new BasicCompletedTrade(originalOrder.getType(), originalOrder.getExecutionCondition(), events);
    }
}
