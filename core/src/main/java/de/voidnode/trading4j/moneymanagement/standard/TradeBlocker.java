package de.voidnode.trading4j.moneymanagement.standard;

import de.voidnode.trading4j.domain.ForexSymbol;

/**
 * Blocks supplying volume to a client for a trade under certain circumstances.
 * 
 * @author Raik Bieniek
 */
interface TradeBlocker {

    /**
     * Checks if trading a given symbol is currently allowed.
     * 
     * @param symbol
     *            The symbol that should be checked.
     * @return <code>true</code> if trading with this symbol is allowed and <code>false</code> if not.
     */
    boolean isTradingAllowed(ForexSymbol symbol);

    /**
     * Blocks the currencies of a symbol for further trading.
     * 
     * @param symbol
     *            The symbol thats currencies should be blocked.
     * @throws IllegalArgumentException
     *             When one or both currencies of the passed symbol are already blocked.
     */
    void blockCurrencies(ForexSymbol symbol);

    /**
     * Unblocks the currencies of a symbol for further trading.
     * 
     * @param symbol
     *            The symbol thats currencies should be unblocked.
     * @throws IllegalArgumentException
     *             When one or both currencies of the passed symbol where not blocked.
     */
    void unblockCurrencies(ForexSymbol symbol);

}