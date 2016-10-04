package de.voidnode.trading4j.server.protocol.expertadvisor;

import java.util.Optional;

import de.voidnode.trading4j.api.Failed;

/**
 * An error that occurred in MetaTrader.
 * 
 * @author Raik Bieniek
 */
class MetaTraderFailure extends Failed {

    /**
     * Initializes an instance.
     * 
     * @param errorCode
     *            see {@link #getErrorCode()}
     */
    MetaTraderFailure(final int errorCode) {
        super("Meta Trader failed to execute an action: " + errorCode + " - "
                + MetaTraderError.byErrorCode(errorCode).map(error -> error.getReason()).orElse("unknown error"));
    }

    /**
     * All known meta trader errors.
     *
     * <p>
     * This list is taken from the <a href="http://book.mql4.com/appendix/errors">MQL4 book</a>.
     * </p>
     */
    private enum MetaTraderError {

        ERR_NO_ERROR(0, "No error returned."),

        ERR_NO_RESULT(1, "No error returned, but the result is unknown."),

        ERR_COMMON_ERROR(2, "Common error."),

        ERR_INVALID_TRADE_PARAMETERS(3, "Invalid trade parameters."),

        ERR_SERVER_BUSY(4, "Trade server is busy."),

        ERR_OLD_VERSION(5, "Old version of the client terminal."),

        ERR_NO_CONNECTION(6, "No connection with trade server."),

        ERR_NOT_ENOUGH_RIGHTS(7, "Not enough rights."),

        ERR_TOO_FREQUENT_REQUESTS(8, "Too frequent requests."),

        ERR_MALFUNCTIONAL_TRADE(9, "Malfunctional trade operation."),

        ERR_ACCOUNT_DISABLED(64, "Account disabled."),

        ERR_INVALID_ACCOUNT(65, "Invalid account."),

        ERR_TRADE_TIMEOUT(128, "Trade timeout."),

        ERR_INVALID_PRICE(129, "Invalid price."),

        ERR_INVALID_STOPS(130, "Invalid stops."),

        ERR_INVALID_TRADE_VOLUME(131, "Invalid trade volume."),

        ERR_MARKET_CLOSED(132, "Market is closed."),

        ERR_TRADE_DISABLED(133, "Trade is disabled."),

        ERR_NOT_ENOUGH_MONEY(134, "Not enough money."),

        ERR_PRICE_CHANGED(135, "Price changed."),

        ERR_OFF_QUOTES(136, "Off quotes."),

        ERR_BROKER_BUSY(137, "Broker is busy."),

        ERR_REQUOTE(138, "Requote."),

        ERR_ORDER_LOCKED(139, "Order is locked."),

        ERR_LONG_POSITIONS_ONLY_ALLOWED(140, "Long positions only allowed."),

        ERR_TOO_MANY_REQUESTS(141, "Too many requests."),

        ERR_TRADE_MODIFY_DENIED(145, "Modification denied because an order is too close to market."),

        ERR_TRADE_CONTEXT_BUSY(146, "Trade context is busy."),

        ERR_TRADE_EXPIRATION_DENIED(147, "Expirations are denied by broker."),

        ERR_TRADE_TOO_MANY_ORDERS(148,
                "The amount of opened and pending orders has reached the limit set by a broker. ");

        private final int errorCode;
        private final String reason;

        MetaTraderError(final int errorCode, final String reason) {
            this.errorCode = errorCode;
            this.reason = reason;

        }

        public String getReason() {
            return reason;
        }

        public static Optional<MetaTraderError> byErrorCode(final int errorCode) {
            for (final MetaTraderError value : MetaTraderError.values()) {
                if (value.errorCode == errorCode) {
                    return Optional.of(value);
                }
            }
            return Optional.empty();
        }
    }
}
