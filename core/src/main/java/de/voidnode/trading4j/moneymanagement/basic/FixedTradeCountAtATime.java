package de.voidnode.trading4j.moneymanagement.basic;

import de.voidnode.trading4j.domain.ForexSymbol;

/**
 * Allows only a fixed amount of trades to be active at a time.
 * 
 * @author Raik Bieniek
 */
class FixedTradeCountAtATime implements TradeBlocker {

    private final int maximalAllowedTrades;
    private int activeTrades;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param maximalAllowedTrades
     *            The maximal amount of trades that are allowed to be active at the same time.
     */
    FixedTradeCountAtATime(final int maximalAllowedTrades) {
        this.maximalAllowedTrades = maximalAllowedTrades;
    }

    @Override
    public boolean isTradingAllowed(final ForexSymbol symbol) {
        return activeTrades < maximalAllowedTrades;
    }

    @Override
    public void blockCurrencies(final ForexSymbol symbol) {
        activeTrades++;
    }

    @Override
    public void unblockCurrencies(final ForexSymbol symbol) {
        activeTrades--;
    }

}
