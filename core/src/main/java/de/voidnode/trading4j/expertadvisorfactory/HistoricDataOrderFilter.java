package de.voidnode.trading4j.expertadvisorfactory;

import java.time.Instant;
import java.util.Optional;

import de.voidnode.trading4j.api.Failed;
import de.voidnode.trading4j.api.OrderFilter;
import de.voidnode.trading4j.domain.marketdata.MarketData;
import de.voidnode.trading4j.domain.marketdata.WithTime;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;

/**
 * Prevents trading while historic data is received.
 * 
 * <p>
 * This is useful when for live trading, initially historic market data is send to allow the expert advisor to
 * initialize itself.
 * </p>
 * 
 * @author Raik Bieniek
 * @param <C>
 *            The type of market data that is received.
 */
class HistoricDataOrderFilter<C extends MarketData & WithTime> implements OrderFilter<C> {

    private static final Optional<Failed> BLOCKED = Optional.of(new Failed("Trading is blocked because historic data is still received"));
    private Instant nonHistoricTime;
    private C marketData;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param nonHistoricTime
     *            The time from which on market data will be treated as live data.
     */
    HistoricDataOrderFilter(final Instant nonHistoricTime) {
        this.nonHistoricTime = nonHistoricTime;
    }

    @Override
    public void updateMarketData(final C marketData) {
        this.marketData = marketData;
    }

    @Override
    public Optional<Failed> filterOrder(final BasicPendingOrder order) {
        if (nonHistoricTime == null) {
            return Optional.empty();
        }
        if (marketData.getTime().isBefore(nonHistoricTime)) {
            return BLOCKED;
        }
        nonHistoricTime = null;
        return Optional.empty();
    }
}
