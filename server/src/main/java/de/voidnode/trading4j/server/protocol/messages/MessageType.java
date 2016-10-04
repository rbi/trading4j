package de.voidnode.trading4j.server.protocol.messages;

import java.util.Optional;

/**
 * Enumerates all known messages.
 * 
 * @author Raik Bieniek
 */
public enum MessageType {

    /**
     * Indicates a {@link RequestTradingAlgorithmMessage}.
     */
    REQUEST_TRADING_ALGORITHM(0, RequestTradingAlgorithmMessage.class),

    /**
     * Indicates a {@link NewMarketDataSimpleMessage}.
     */
    NEW_MARKET_DATA_SIMPLE(1, NewMarketDataSimpleMessage.class),

    /**
     * Indicates a {@link TrendForMarketDataMessage}.
     */
    TREND_FOR_MARKET_DATA(2, TrendForMarketDataMessage.class),

    /**
     * Indicates a {@link PlacePendingOrderMessage}.
     */
    PLACE_PENDING_ORDER(3, PlacePendingOrderMessage.class),

    /**
     * Indicates a {@link ResponsePlacePendingOrderMessage}.
     */
    RESPONSE_PLACE_PENDING_ORDER(4, ResponsePlacePendingOrderMessage.class),

    /**
     * Indicates a {@link PendingOrderConditionalyExecutedMessage}.
     */
    PENDING_ORDER_CONDITIONALY_EXECUTED(5, PendingOrderConditionalyExecutedMessage.class),

    /**
     * Indicates a {@link PendingOrderConditionalyClosedMessage}.
     */
    PENDING_ORDER_CONDITIONALY_CLOSED(6, PendingOrderConditionalyClosedMessage.class),

    /**
     * Indicates a {@link CloseOrCancelPendingOrderMessage}.
     */
    CLOSE_OR_CANCEL_PENDING_ORDER(7, CloseOrCancelPendingOrderMessage.class),

    /**
     * Indicates a {@link EventHandlingFinishedMessage}.
     */
    EVENT_HANDLING_FINISHED(8, EventHandlingFinishedMessage.class),

    /**
     * Indicates a {@link NewMarketDataExtendedMessage}.
     */
    NEW_MARKET_DATA_EXTENDED(9, NewMarketDataExtendedMessage.class),

    /**
     * Indicates a {@link TradingEnvironmentInformationMessage}.
     */
    TRADING_ENVIRONMENT_INFORMATION(10, TradingEnvironmentInformationMessage.class),

    /**
     * Indicates a {@link ChangeCloseConditionsMessage}.
     */
    CHANGE_CLOSE_CONDITIONS(11, ChangeCloseConditionsMessage.class),

    /**
     * Indicates a {@link ResponseChangeCloseConditionsMessage}.
     */
    RESPONSE_CHANGE_CLOSE_CONDITIONS(12, ResponseChangeCloseConditionsMessage.class),

    /**
     * Indicates a {@link BalanceChangedMessage}.
     */
    BALANCE_CHANGED(13, BalanceChangedMessage.class),

    /**
     * Indicates a {@link AccountCurrencyExchangeRateChangedMessage}.
     */
    ACCOUNT_CURRENCY_EXCHANGE_RATE_CHANGED(14, AccountCurrencyExchangeRateChangedMessage.class);

    private final byte messageNumber;
    private final Class<? extends Message> messageClass;

    /**
     * Initializes the enum.
     * 
     * @param messageNumber
     *            see {@link #getMessageNumber()}
     * @param messageClass
     *            The {@link Message} this enum is targeted for.
     */
    MessageType(final int messageNumber, final Class<? extends Message> messageClass) {
        this.messageNumber = (byte) messageNumber;
        this.messageClass = messageClass;
    }

    /**
     * A unique id for messages of this type identifying them on the wire.
     * 
     * @return The id
     */
    public byte getMessageNumber() {
        return messageNumber;
    }

    /**
     * Returns the {@link Enum} instance for the message type passed.
     * 
     * @param clazz
     *            The message class thats {@link Enum} constant should be returned.
     * @return The {@link Enum} constant for the class.
     * @throws IllegalArgumentException
     *             When the {@link Message} {@link Class} passed is not known to this enumartion.
     */
    public static MessageType forMessageClass(final Class<? extends Message> clazz) throws IllegalArgumentException {
        for (final MessageType candidate : values()) {
            if (candidate.messageClass.equals(clazz)) {
                return candidate;
            }
        }
        throw new IllegalArgumentException(
                "The message class " + clazz + " is unknown to the MessageType enumeration.");
    }

    /**
     * Returns the message type that has the given number.
     * 
     * @param number
     *            The number of the message type to return.
     * @return The {@link MessageType} if the number is assigned to any known {@link MessageType} or an empty
     *         {@link Optional} if not.
     */
    public static Optional<MessageType> forMessageNumber(final byte number) {
        for (final MessageType candidate : values()) {
            if (candidate.messageNumber == number) {
                return Optional.of(candidate);
            }
        }
        return Optional.empty();
    }
}
