package de.voidnode.trading4j.moneymanagement.basic;

import de.voidnode.trading4j.api.UnrecoverableProgrammingError;
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
     * @throws UnrecoverableProgrammingError
     *             When one or both currencies of the passed symbol are already blocked.
     */
    void blockCurrencies(ForexSymbol symbol) throws UnrecoverableProgrammingError;

    /**
     * Unblocks the currencies of a symbol for further trading.
     * 
     * @param symbol
     *            The symbol thats currencies should be unblocked.
     * @throws UnrecoverableProgrammingError
     *             When one or both currencies of the passed symbol where not blocked.
     */
    void unblockCurrencies(ForexSymbol symbol) throws UnrecoverableProgrammingError;

}