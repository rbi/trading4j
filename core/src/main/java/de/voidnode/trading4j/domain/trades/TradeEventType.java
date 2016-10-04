package de.voidnode.trading4j.domain.trades;

/**
 * All known types of events that can happen during a trade.
 *
 * @author Raik Bieniek
 */
public enum TradeEventType {

    /**
     * A pending order was placed at the broker.
     */
    PENDING_ORDER_PLACED,

    /**
     * A pending order that hasn't been opened yet was canceled.
     */
    PENDING_ORDER_CANCELD,

    /**
     * A pending order was opened and has therefore become an active trade.
     */
    PENDING_ORDER_OPENED,

    /**
     * The close conditions of a pending order or a trade have been changed.
     */
    CLOSE_CONDITIONS_CHANGED,

    /**
     * An opened trade was closed.
     */
    TRADE_CLOSED
}
