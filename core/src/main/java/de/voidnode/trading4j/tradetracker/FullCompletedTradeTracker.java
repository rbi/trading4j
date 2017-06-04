package de.voidnode.trading4j.tradetracker;

import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.domain.ForexSymbol;
import de.voidnode.trading4j.domain.marketdata.MarketData;
import de.voidnode.trading4j.domain.marketdata.WithSpread;
import de.voidnode.trading4j.domain.orders.PendingOrder;
import de.voidnode.trading4j.domain.trades.CompletedTrade;
import de.voidnode.trading4j.domain.trades.TradeEvent;

/**
 * Tracks completed trades with additional data about the trade.
 * 
 * @author Raik Bieniek
 * @param <C>
 *            The type of candle sticks that is passed as input.
 */
public class FullCompletedTradeTracker<C extends MarketData & WithSpread>
        extends TradeTracker<C, PendingOrder, CompletedTrade> {

    private final ForexSymbol symbol;

    /**
     * Creates an instance with all its dependencies.
     * 
     * @param broker
     *            see {@link TradeTracker#TradeTracker(Broker, Supplier)}
     * @param currentTime
     *            see {@link TradeTracker#TradeTracker(Broker, Supplier)}
     * @param symbol
     *            The forex symbol that was traded.
     */
    public FullCompletedTradeTracker(final Broker<PendingOrder> broker, final Supplier<Instant> currentTime,
            final ForexSymbol symbol) {
        super(broker, currentTime);
        this.symbol = symbol;
    }

    @Override
    protected CompletedTrade createCompletedTrade(final PendingOrder originalOrder, final C lastCandleStick,
            final List<TradeEvent> events) {
        return new CompletedTrade(originalOrder.getType(), originalOrder.getExecutionCondition(), symbol,
                originalOrder.getVolume(), lastCandleStick.getSpread(), events);
    }

}
