package de.voidnode.trading4j.tradetracker;

import de.voidnode.trading4j.domain.trades.BasicCompletedTrade;

/**
 * A listener for completed trade events.
 * 
 * @author Raik Bieniek
 * @param <CT>
 *            The kind of {@link BasicCompletedTrade} that is listened for.
 */
public interface TradeEventListener<CT extends BasicCompletedTrade> {

    /**
     * The event of a completed trade.
     * 
     * @param trade
     *            The trade that was completed.
     */
    void tradeCompleted(CT trade);

}
