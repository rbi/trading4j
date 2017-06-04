package de.voidnode.trading4j.server.protocol.messages;

import java.time.Instant;
import java.util.Currency;
import java.util.Optional;

import de.voidnode.trading4j.domain.ForexSymbol;
import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.VolumeUnit;
import de.voidnode.trading4j.domain.environment.AccountInformation;
import de.voidnode.trading4j.domain.environment.SpecialFeesInformation;
import de.voidnode.trading4j.domain.environment.TradingEnvironmentInformation;
import de.voidnode.trading4j.domain.environment.VolumeConstraints;
import de.voidnode.trading4j.domain.marketdata.impl.DatedCandleStick;
import de.voidnode.trading4j.domain.marketdata.impl.FullMarketData;
import de.voidnode.trading4j.domain.marketdata.impl.MutableFullMarketData;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.CloseConditions;
import de.voidnode.trading4j.domain.orders.PendingOrder;
import de.voidnode.trading4j.domain.timeframe.M1;
import de.voidnode.trading4j.server.protocol.ClientConnection;
import de.voidnode.trading4j.server.protocol.CommunicationException;
import de.voidnode.trading4j.server.protocol.messages.RequestTradingAlgorithmMessage.AlgorithmType;

/**
 * Reads and writes {@link Message}s from and to a {@link ClientConnection}.
 * 
 * @author Raik Bieniek
 */
public class MessageBasedClientConnection {

    private final ClientConnection connection;

    /**
     * Initializes the message reader and writer with its dependencies.
     * 
     * @param connection
     *            The connection to read and write messages from and to.
     */
    public MessageBasedClientConnection(final ClientConnection connection) {
        this.connection = connection;
    }

    /**
     * Reads the next message from the client.
     *
     * @return The read message.
     * @throws CommunicationException
     *             When no message could not be read from the {@link ClientConnection}.
     */
    public Message readMessage() throws CommunicationException {
        return readMessage(readMessageTypeOrFail());
    }

    /**
     * Reads a message with a given type from the client.
     * 
     * @param messageClass
     *            The message to read.
     * @param <M>
     *            The return type of the message which is the same as <code>messageClass</code>
     * @return The read message
     * @throws CommunicationException
     *             When no message of the given type could not be read from the {@link ClientConnection}.
     */
    @SuppressWarnings("unchecked")
    public <M extends Message> M readMessage(final Class<M> messageClass) throws CommunicationException {
        final MessageType expectedType = MessageType.forMessageClass(messageClass);
        final MessageType acutalMessageType = readMessageTypeOrFail();

        if (expectedType != acutalMessageType) {
            throw new MessageReadException("Expected the next message to read to be of type '" + expectedType
                    + "' but it is of type '" + acutalMessageType + "'");
        }

        return (M) readMessage(acutalMessageType);
    }

    private Message readMessage(final MessageType messageType) throws CommunicationException {
        switch (messageType) {
            case REQUEST_TRADING_ALGORITHM:
                return readRequestTradingAlgorithmMessage();
            case NEW_MARKET_DATA_SIMPLE:
                return readNewMarketDataSimpleMessage();
            case NEW_MARKET_DATA_EXTENDED:
                return readNewMarketDataExtendedMessage();
            case ACCOUNT_CURRENCY_EXCHANGE_RATE_CHANGED:
                return readAccountCurrencyExchangeRateChangedMessage();
            case RESPONSE_PLACE_PENDING_ORDER:
                return readResponsePlacePendingOrderMessage();
            case PENDING_ORDER_CONDITIONALY_CLOSED:
                return readPendingOrderConditionalyClosedMessage();
            case PENDING_ORDER_CONDITIONALY_EXECUTED:
                return readPendingOrderConditionalyExecutedMessage();
            case TRADING_ENVIRONMENT_INFORMATION:
                return readTradingEnvironmentInformationMessage();
            case RESPONSE_CHANGE_CLOSE_CONDITIONS:
                return readResponseChangeCloseConditionsMessage();
            case BALANCE_CHANGED:
                return readBalanceChangedMessage();
            default:
                throw new UnsupportedOperationException("Reading of " + messageType + " messages is not supported.");
        }
    }

    private MessageType readMessageTypeOrFail() throws CommunicationException {
        final byte number = connection.tryReceiveByte();
        final Optional<MessageType> messageType = MessageType.forMessageNumber(number);
        return messageType.orElseThrow(() -> new MessageReadException(
                "Read the message number " + (int) number + " which is not assigned to any known message type."));
    }

    private RequestTradingAlgorithmMessage readRequestTradingAlgorithmMessage() throws CommunicationException {
        final AlgorithmType type = RequestTradingAlgorithmMessage.AlgorithmType
                .getAlgorithmTypeByNumber(connection.tryReceiveByte()).orElseThrow(
                        () -> new MessageReadException("Received a request for an unknown trading algorithm type."));

        return new RequestTradingAlgorithmMessage(type, connection.tryReceiveInteger());
    }

    private Message readNewMarketDataSimpleMessage() throws CommunicationException {
        final Instant time = Instant.ofEpochSecond(connection.tryReceiveLong());
        final DatedCandleStick<M1> candleStick = new DatedCandleStick<>(time, connection.tryReceiveDouble(),
                connection.tryReceiveDouble(), connection.tryReceiveDouble(), connection.tryReceiveDouble());
        return new NewMarketDataSimpleMessage(candleStick);
    }

    private NewMarketDataExtendedMessage readNewMarketDataExtendedMessage() throws CommunicationException {
        final Instant time = Instant.ofEpochSecond(connection.tryReceiveLong());
        final FullMarketData<M1> candleStick = new MutableFullMarketData<M1>().setTime(time)
                .setOpen(connection.tryReceiveDouble()).setHigh(connection.tryReceiveDouble())
                .setLow(connection.tryReceiveDouble()).setClose(connection.tryReceiveDouble())
                .setSpread(new Price(connection.tryReceiveInteger()))
                .setVolume(connection.tryReceiveInteger(), VolumeUnit.BASE).setTickCount(connection.tryReceiveInteger())
                .toImmutableFullMarketData();
        return new NewMarketDataExtendedMessage(candleStick);
    }

    private Message readAccountCurrencyExchangeRateChangedMessage() throws CommunicationException {
        return new AccountCurrencyExchangeRateChangedMessage(new Price(connection.tryReceiveDouble()));
    }

    private ResponsePlacePendingOrderMessage readResponsePlacePendingOrderMessage() throws CommunicationException {
        final boolean success = connection.tryReceiveByte() == 0;
        return new ResponsePlacePendingOrderMessage(success, connection.tryReceiveInteger());
    }

    private PendingOrderConditionalyExecutedMessage readPendingOrderConditionalyExecutedMessage()
            throws CommunicationException {
        return new PendingOrderConditionalyExecutedMessage(connection.tryReceiveInteger(),
                Instant.ofEpochSecond(connection.tryReceiveLong()), new Price(connection.tryReceiveDouble()));
    }

    private PendingOrderConditionalyClosedMessage readPendingOrderConditionalyClosedMessage()
            throws CommunicationException {
        return new PendingOrderConditionalyClosedMessage(connection.tryReceiveInteger(),
                Instant.ofEpochSecond(connection.tryReceiveLong()), new Price(connection.tryReceiveDouble()));
    }

    private TradingEnvironmentInformationMessage readTradingEnvironmentInformationMessage()
            throws CommunicationException {
        final String brokerName = connection.tryReceiveString();
        final long accountNumber = connection.tryReceiveLong();
        final String rawCurrency = connection.tryReceiveString();
        final AccountInformation accountInformation;
        try {
            accountInformation = new AccountInformation(brokerName, accountNumber, Currency.getInstance(rawCurrency));
        } catch (final IllegalArgumentException e) {
            throw new MessageReadException("The account currency is \"" + rawCurrency + "\" which is not valid.");
        }
        return new TradingEnvironmentInformationMessage(new TradingEnvironmentInformation(accountInformation,
                new ForexSymbol(connection.tryReceiveString()), new ForexSymbol(connection.tryReceiveString()),
                new SpecialFeesInformation(new Price(connection.tryReceiveInteger()),
                        new Price(connection.tryReceiveInteger())),
                Instant.ofEpochSecond(connection.tryReceiveLong()),
                new VolumeConstraints(new Volume(connection.tryReceiveLong(), VolumeUnit.BASE),
                        new Volume(connection.tryReceiveLong(), VolumeUnit.BASE),
                        new Volume(connection.tryReceiveLong(), VolumeUnit.BASE))));
    }

    private ResponseChangeCloseConditionsMessage readResponseChangeCloseConditionsMessage()
            throws CommunicationException {
        final boolean succeed = connection.tryReceiveByte() == (byte) 0;
        if (succeed) {
            return new ResponseChangeCloseConditionsMessage();
        } else {
            return new ResponseChangeCloseConditionsMessage(connection.tryReceiveInteger());
        }
    }

    private BalanceChangedMessage readBalanceChangedMessage() throws CommunicationException {
        return new BalanceChangedMessage(connection.tryReceiveLong());
    }

    /**
     * Sends a message to the client.
     * 
     * @param message
     *            The message to send.
     * @throws CommunicationException
     *             When sending the message over the {@link ClientConnection} failed.
     */
    public void sendMessage(final Message message) throws CommunicationException {
        final MessageType messageType = MessageType.forMessageClass(message.getClass());
        writeMessageNumber(messageType);
        switch (messageType) {
            case TREND_FOR_MARKET_DATA:
                writeMessage((TrendForMarketDataMessage) message);
                break;
            case CLOSE_OR_CANCEL_PENDING_ORDER:
                writeMessage((CloseOrCancelPendingOrderMessage) message);
                break;
            case PLACE_PENDING_ORDER:
                writeMessage((PlacePendingOrderMessage) message);
                break;
            case CHANGE_CLOSE_CONDITIONS:
                writeMessage((ChangeCloseConditionsMessage) message);
                break;
            case EVENT_HANDLING_FINISHED:
                // This message has no additional data to write.
                break;
            default:
                throw new UnsupportedOperationException("Writing of " + messageType + " messages is not supported.");
        }
    }

    private void writeMessageNumber(final MessageType messageType) throws CommunicationException {
        connection.trySendByte(messageType.getMessageNumber());
    }

    private void writeMessage(final TrendForMarketDataMessage message) throws CommunicationException {
        connection.trySendByte(message.getTrend().map(t -> t == MarketDirection.UP ? 0 : 1).orElse(2).byteValue());
    }

    private void writeMessage(final CloseOrCancelPendingOrderMessage message) throws CommunicationException {
        connection.trySendInteger(message.getId());
    }

    private void writeMessage(final PlacePendingOrderMessage message) throws CommunicationException {
        final PendingOrder order = message.getPendingOrder();
        final CloseConditions closeConditions = order.getCloseConditions();

        final int type = order.getType().ordinal();
        final int condition = order.getExecutionCondition().ordinal();
        final int hasExpiration = closeConditions.getExpirationDate().isPresent() ? 1 : 0;
        final byte flags = (byte) (type | condition << 1 | hasExpiration << 3);

        connection.trySendByte(flags);
        connection.trySendInteger((int) order.getVolume().asAbsolute());
        connection.trySendDouble(order.getEntryPrice().asDouble());
        connection.trySendDouble(closeConditions.getTakeProfit().asDouble());
        connection.trySendDouble(closeConditions.getStopLoose().asDouble());

        if (closeConditions.getExpirationDate().isPresent()) {
            connection.trySendLong(closeConditions.getExpirationDate().get().getEpochSecond());
        }
    }

    private void writeMessage(final ChangeCloseConditionsMessage message) throws CommunicationException {
        final CloseConditions closeConditions = message.getNewCloseConditions();

        connection.trySendByte(closeConditions.getExpirationDate().isPresent() ? (byte) 1 : (byte) 0);
        connection.trySendInteger(message.getId());
        connection.trySendDouble(closeConditions.getTakeProfit().asDouble());
        connection.trySendDouble(closeConditions.getStopLoose().asDouble());

        if (closeConditions.getExpirationDate().isPresent()) {
            connection.trySendLong(closeConditions.getExpirationDate().get().getEpochSecond());
        }
    }

    @Override
    public String toString() {
        return connection.toString();
    }
}
