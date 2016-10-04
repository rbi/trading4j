package de.voidnode.trading4j.server.reporting;

import de.voidnode.trading4j.domain.trades.CompletedTrade;

/**
 * Provides functionality to notify a trader on certain events.
 * 
 * @author Raik Bieniek
 */
public interface TraderNotifier extends de.voidnode.trading4j.api.TraderNotifier {

    /**
     * Informs the trader of a completed trade.
     * 
     * @param trade
     *            The trade that was completed.
     */
    void tradeCompleted(CompletedTrade trade);
}
