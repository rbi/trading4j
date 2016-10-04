package de.voidnode.trading4j.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.time.Instant;

import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.VolumeUnit;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.ExecutionCondition;
import de.voidnode.trading4j.domain.orders.MutableCloseConditions;
import de.voidnode.trading4j.domain.orders.MutablePendingOrder;
import de.voidnode.trading4j.domain.orders.OrderType;
import de.voidnode.trading4j.domain.orders.PendingOrder;
import de.voidnode.trading4j.server.oio.OioServer;
import de.voidnode.trading4j.server.protocol.messages.MessageType;

/**
 * A client for the trading {@link OioServer}.
 * 
 * <p>
 * The client is running in the current thread.
 * </p>
 * 
 * @author Raik Bieniek
 */
class TradingClient {

    private static final String HOST = "localhost";
    private static final int PORT = 6474;

    private final Socket serverSocket;
    private final DataOutputStream toServer;
    private final DataInputStream fromServer;

    /**
     * Starts and connects the client.
     * 
     * @param readTimeout
     *            The time to wait in milliseconds for a read operation to finish.
     * @throws IOException
     *             When connecting failed.
     */
    TradingClient(final int readTimeout) throws IOException {
        serverSocket = new Socket(HOST, PORT);
        serverSocket.setSoTimeout(readTimeout);
        toServer = new DataOutputStream(serverSocket.getOutputStream());
        fromServer = new DataInputStream(serverSocket.getInputStream());
    }

    /**
     * Sends the number of the requested indicator or expert advisor to the server.
     * 
     * @param type
     *            The type of trading algorithm thas is requested.
     * @param indicatorNumber
     *            The number of the requested indicator or expert advisor.
     * @throws IOException
     *             When writing to the server failed.
     */
    public void sendTradingAlgorithmNumber(final int type, final int indicatorNumber) throws IOException {
        writeMessageNumber(MessageType.REQUEST_TRADING_ALGORITHM);
        toServer.writeByte(type);
        toServer.writeInt(indicatorNumber);
    }

    /**
     * Sends constant information about the asset that should be traded to the server.
     * 
     * @param brokerName
     *            The name of the broker where the account is registered.
     * @param accountNumber
     *            The number of the account.
     * @param accountCurrency
     *            The currency that the balance of the account is kept in.
     * @param symbol
     *            The forex pair that should be traded.
     * @param accountCurrencySymbol
     *            The forex par that transforms the account currency to the quote currency of the traded symbol.
     * @param margin
     *            The margin fee for the asset.
     * @param commission
     *            The commission fee per base unit of the traded symbol.
     * @param firstNonHistoricCandleTime
     *            The time of the first candle stick that is not historic data.
     * @param minimalVolume
     *            The minimal allowed volume.
     * @param stepSize
     *            The allowed step size for volumes.
     * @param maximalVolume
     *            The maximal allowed volume.
     * @throws IOException
     *             When writing to the server failed.
     */
    public void sendTradingEnvironmentInfo(final String brokerName, final long accountNumber,
            final String accountCurrency, final String symbol, final String accountCurrencySymbol, final Price margin,
            final Price commission, final long firstNonHistoricCandleTime, final Volume minimalVolume,
            final Volume stepSize, final Volume maximalVolume) throws IOException {
        writeMessageNumber(MessageType.TRADING_ENVIRONMENT_INFORMATION);
        toServer.writeUTF(brokerName);
        toServer.writeLong(accountNumber);
        toServer.writeUTF(accountCurrency);
        toServer.writeUTF(symbol);
        toServer.writeUTF(accountCurrencySymbol);
        toServer.writeInt((int) margin.asPipette());
        toServer.writeInt((int) commission.asPipette());
        toServer.writeLong(firstNonHistoricCandleTime);
        toServer.writeLong(minimalVolume.asAbsolute());
        toServer.writeLong(stepSize.asAbsolute());
        toServer.writeLong(maximalVolume.asAbsolute());
    }

    /**
     * Sends the data of a simple candle stick with time to the server.
     * 
     * @param time
     *            The time of the candle
     * @param open
     *            The opening price
     * @param high
     *            The high price
     * @param low
     *            The low price
     * @param close
     *            The closing price
     * @throws IOException
     *             When writing to the server failed.
     */
    public void sendCandle(final long time, final Price open, final Price high, final Price low, final Price close)
            throws IOException {
        writeMessageNumber(MessageType.NEW_MARKET_DATA_SIMPLE);
        toServer.writeLong(time);
        toServer.writeDouble(open.asDouble());
        toServer.writeDouble(high.asDouble());
        toServer.writeDouble(low.asDouble());
        toServer.writeDouble(close.asDouble());
    }

    /**
     * Sends the data of a candle with additional meta data to the server.
     * 
     * @param time
     *            The time of the candle
     * @param open
     *            The opening price
     * @param high
     *            The high price
     * @param low
     *            The low price
     * @param close
     *            The closing price
     * @param spread
     *            The spread
     * @param volume
     *            The volume
     * @param tickCount
     *            The tickCount
     * @throws IOException
     *             When writing to the server failed.
     */
    public void sendFatCandle(final long time, final Price open, final Price high, final Price low, final Price close,
            final Price spread, final Volume volume, final long tickCount) throws IOException {
        writeMessageNumber(MessageType.NEW_MARKET_DATA_EXTENDED);
        toServer.writeLong(time);
        toServer.writeDouble(open.asDouble());
        toServer.writeDouble(high.asDouble());
        toServer.writeDouble(low.asDouble());
        toServer.writeDouble(close.asDouble());
        toServer.writeInt((int) spread.asPipette());
        toServer.writeInt((int) volume.asAbsolute());
        toServer.writeInt((int) tickCount);
    }

    /**
     * Receives a trend from the server.
     * 
     * @return The trend received
     * @throws IOException
     *             When receiving failed
     */
    public MarketDirection reciveTrend() throws IOException {
        readMessageNumberOrFail(MessageType.TREND_FOR_MARKET_DATA);
        final int trend = fromServer.readByte();
        final MarketDirection[] trends = MarketDirection.values();
        if (trend < 0 && trend >= trends.length) {
            throw new RuntimeException("Invalid trend received from the server: " + trend);
        }
        return trends[trend];
    }

    /**
     * Receives a {@link PendingOrder} from the server.
     * 
     * @return The received pending order
     * @throws IOException
     *             When receiving failed
     */
    public PendingOrder recivePendingOrder() throws IOException {
        readMessageNumberOrFail(MessageType.PLACE_PENDING_ORDER);
        final MutablePendingOrder pendingOrder = new MutablePendingOrder();
        final MutableCloseConditions closeConditions = new MutableCloseConditions();
        final int flags = fromServer.readByte();
        final boolean hasExpirationDate = (flags >> 3) % 2 == 1;

        pendingOrder.setType(OrderType.values()[flags % 2])
                .setExecutionCondition(ExecutionCondition.values()[(flags >> 1) % 2])
                .setVolume(new Volume(fromServer.readInt(), VolumeUnit.BASE))
                .setEntryPrice(new Price(fromServer.readDouble()));

        closeConditions.setTakeProfit(new Price(fromServer.readDouble()))//
                .setStopLoose(new Price(fromServer.readDouble()));
        if (hasExpirationDate) {
            closeConditions.setExpirationDate(Instant.ofEpochMilli(fromServer.readLong()));
        }
        pendingOrder.setCloseConditions(closeConditions);

        return pendingOrder.toImmutablePendingOrder();
    }

    /**
     * Send to the client to indicate that the server does sent no more messages in response to the last event that was
     * send.
     * 
     * @throws IOException
     *             When the expected message could not be read.
     */
    public void reciveEventHandlingFinished() throws IOException {
        readMessageNumberOrFail(MessageType.EVENT_HANDLING_FINISHED);
    }

    /**
     * Acknowledges the successful receiving of a pending order and send back the assigned id.
     * 
     * @param id
     *            The id that was assigned to the pending order.
     * @throws IOException
     *             When writing to the server failed.
     */
    public void sendResponsePendingOrder(final int id) throws IOException {
        writeMessageNumber(MessageType.RESPONSE_PLACE_PENDING_ORDER);
        toServer.writeByte((byte) 0);
        toServer.writeInt(id);
    }

    /**
     * Informs that a previously received pending order has been executed.
     * 
     * @param pendingOrderId
     *            The id of the pending order that was executed.
     * @param executionTime
     *            The time in seconds since epoch when the order was opened.
     * @param executionMarketPrice
     *            The market price at which the pending order was executed.
     * @throws IOException
     *             When writing to the server failed.
     */
    public void sendPendingOrderExecuted(final int pendingOrderId, final long executionTime,
            final Price executionMarketPrice) throws IOException {
        writeMessageNumber(MessageType.PENDING_ORDER_CONDITIONALY_EXECUTED);
        toServer.writeInt(pendingOrderId);
        toServer.writeLong(executionTime);
        toServer.writeDouble(executionMarketPrice.asDouble());
    }

    /**
     * Informs that a previously received pending order has been executed.
     * 
     * @param pendingOrderId
     *            The id of the pending order that was executed.
     * @param closeTime
     *            The time in seconds since epoch when the order was closed.
     * @param closeMarketPrice
     *            The market price at which the pending order was executed.
     * @throws IOException
     *             When writing to the server failed.
     */
    public void sendPendingOrderClosed(final int pendingOrderId, final long closeTime, final Price closeMarketPrice)
            throws IOException {
        writeMessageNumber(MessageType.PENDING_ORDER_CONDITIONALY_CLOSED);
        toServer.writeInt(pendingOrderId);
        toServer.writeLong(closeTime);
        toServer.writeDouble(closeMarketPrice.asDouble());
    }

    /**
     * Informs that the balance available for trading has changed.
     * 
     * @param newBalance
     *            The new balance.
     * @throws IOException
     *             When writing to the server failed.
     */
    public void sendBalanceChanged(final long newBalance) throws IOException {
        writeMessageNumber(MessageType.BALANCE_CHANGED);
        toServer.writeLong(newBalance);
    }

    /**
     * Informs that the price of the Forex symbol for exchanging to account currency has changed.
     * 
     * @param newPrice
     *            The new {@link Price} for the symbol.
     * @throws IOException
     *             When writing to the server failed.
     */
    public void sendAccountCurrencyPriceChanged(final Price newPrice) throws IOException {
        writeMessageNumber(MessageType.ACCOUNT_CURRENCY_EXCHANGE_RATE_CHANGED);
        toServer.writeDouble(newPrice.asDouble());
    }

    /**
     * Disconnects from the server.
     * 
     * @throws IOException
     *             When shutting down the connection to the server failed.
     */
    public void disconnect() throws IOException {
        if (serverSocket.isClosed()) {
            throw new IllegalStateException("The connection should be closed but it has already been closed.");
        }
        serverSocket.close();
    }

    /**
     * Checks that the server terminated the connection.
     * 
     * @throws IOException
     *             When an I/O operation failed.
     */
    public void serverShouldHaveTerminatedTheConnection() throws IOException {
        if (serverSocket.isClosed()) {
            return;
        }

        try {
            fromServer.readByte();
        } catch (final EOFException e) {
            // the expected behavior.
            return;
        }

        throw new IllegalStateException("The server should have terminated the connection but isn't.");
    }

    private void writeMessageNumber(final MessageType type) throws IOException {
        toServer.writeByte(type.getMessageNumber());
    }

    private void readMessageNumberOrFail(final MessageType type) throws IOException {
        final int actual = (int) fromServer.readByte();
        if (actual != type.getMessageNumber()) {
            throw new IllegalStateException("Expected the message number " + type.getMessageNumber()
                    + " but received message number " + actual);
        }
    }
}
