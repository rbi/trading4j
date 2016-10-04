package de.voidnode.trading4j.server.protocol.messages;

import java.time.Instant;
import java.util.Currency;
import java.util.Optional;

import de.voidnode.trading4j.domain.ForexSymbol;
import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.marketdata.DatedCandleStick;
import de.voidnode.trading4j.domain.marketdata.MutableFullMarketData;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.CloseConditions;
import de.voidnode.trading4j.domain.orders.MutableCloseConditions;
import de.voidnode.trading4j.domain.orders.MutablePendingOrder;
import de.voidnode.trading4j.server.protocol.ClientConnection;
import de.voidnode.trading4j.server.protocol.CommunicationException;

import static de.voidnode.trading4j.domain.VolumeUnit.BASE;
import static de.voidnode.trading4j.domain.VolumeUnit.MICRO_LOT;
import static de.voidnode.trading4j.domain.orders.ExecutionCondition.DIRECT;
import static de.voidnode.trading4j.domain.orders.ExecutionCondition.STOP;
import static de.voidnode.trading4j.domain.orders.OrderType.BUY;
import static de.voidnode.trading4j.domain.orders.OrderType.SELL;
import static de.voidnode.trading4j.testutils.assertions.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests if {@link MessageBasedClientConnection} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class MessageBasedClientConnectionTest {

    private static final byte UNKNOWN_MESSAGE_NUMBER = (byte) 255;

    @Mock
    private ClientConnection client;

    @InjectMocks
    private MessageBasedClientConnection cut;

    // /////////////
    // / Reading ///
    // /////////////

    /**
     * The class should be able to read {@link RequestTradingAlgorithmMessage}s correctly.
     * 
     * @throws CommunicationException
     *             Not expected to leave the test method.
     */
    @Test
    public void shouldReadAlgorithmRequestMessages() throws CommunicationException {
        when(client.tryReceiveByte()).thenReturn(MessageType.REQUEST_TRADING_ALGORITHM.getMessageNumber())
                .thenReturn((byte) 0).thenReturn(MessageType.REQUEST_TRADING_ALGORITHM.getMessageNumber())
                .thenReturn((byte) 1);
        when(client.tryReceiveInteger()).thenReturn(4813).thenReturn(-816);

        final RequestTradingAlgorithmMessage message1 = cut.readMessage(RequestTradingAlgorithmMessage.class);
        assertThat(message1.getAlgorithmNumber()).isEqualTo(4813);
        assertThat(message1.getAlgorithmType()).isEqualTo(RequestTradingAlgorithmMessage.AlgorithmType.EXPERT_ADVISOR);

        final RequestTradingAlgorithmMessage message2 = cut.readMessage(RequestTradingAlgorithmMessage.class);
        assertThat(message2.getAlgorithmNumber()).isEqualTo(-816);
        assertThat(message2.getAlgorithmType()).isEqualTo(RequestTradingAlgorithmMessage.AlgorithmType.TREND_INDICATOR);
    }

    /**
     * The class should be able to read {@link NewMarketDataSimpleMessage} correctly.
     * 
     * @throws CommunicationException
     *             Not expected to leave the test method.
     */
    @Test
    public void shouldReadNewMarketDataSimpleMessages() throws CommunicationException {
        when(client.tryReceiveByte()).thenReturn(MessageType.NEW_MARKET_DATA_SIMPLE.getMessageNumber());
        // This is Sat, 19 Jul 2014 15:09:35 GMT
        when(client.tryReceiveLong()).thenReturn(1405782575L);
        when(client.tryReceiveDouble()).thenReturn(1.0, 2.0, 3.0, 4.0);

        final NewMarketDataSimpleMessage readMessage = cut.readMessage(NewMarketDataSimpleMessage.class);
        assertThat(readMessage.getCandleStick())
                .isEqualTo(new DatedCandleStick<>(Instant.ofEpochSecond(1405782575L), 1.0, 2.0, 3.0, 4.0));
    }

    /**
     * The class should be able to read {@link NewMarketDataExtendedMessage} correctly.
     * 
     * @throws CommunicationException
     *             Not expected to leave the test method.
     */
    @Test
    public void shouldReadNewMarketDataExtendedMessages() throws CommunicationException {
        when(client.tryReceiveByte()).thenReturn(MessageType.NEW_MARKET_DATA_EXTENDED.getMessageNumber());
        // This is Sat, 19 Jul 2014 15:09:35 GMT
        when(client.tryReceiveLong()).thenReturn(1405782575L);
        when(client.tryReceiveDouble()).thenReturn(1.0, 2.0, 3.0, 4.0);
        when(client.tryReceiveInteger()).thenReturn(50, 2000, 42);

        final NewMarketDataExtendedMessage readMessage = cut.readMessage(NewMarketDataExtendedMessage.class);
        assertThat(readMessage.getCandleStick()).isEqualTo(new MutableFullMarketData<>()
                .setTime(Instant.ofEpochSecond(1405782575L)).setOpen(1.0).setHigh(2.0).setLow(3.0).setClose(4.0)
                .setSpread(new Price(50)).setVolume(2, MICRO_LOT).setTickCount(42).toImmutableFullMarketData());
    }

    /**
     * The reader should be able to read {@link ResponsePlacePendingOrderMessage} messages correctly.
     * 
     * @throws CommunicationException
     *             Not expected to leave the test method.
     */
    @Test
    public void shouldReadResponsePlacePendingOrderMessagesCorrectly() throws CommunicationException {
        when(client.tryReceiveByte()).thenReturn(MessageType.RESPONSE_PLACE_PENDING_ORDER.getMessageNumber(), (byte) 0,
                MessageType.RESPONSE_PLACE_PENDING_ORDER.getMessageNumber(), (byte) 1);
        when(client.tryReceiveInteger()).thenReturn(42).thenReturn(83);

        final Message messageSuccess = cut.readMessage();
        final Message messageFailed = cut.readMessage();

        assertThat(messageSuccess).isInstanceOf(ResponsePlacePendingOrderMessage.class);
        assertThat(((ResponsePlacePendingOrderMessage) messageSuccess).getId()).isPresent().contains(42);

        assertThat(messageFailed).isInstanceOf(ResponsePlacePendingOrderMessage.class);
        assertThat(((ResponsePlacePendingOrderMessage) messageFailed).getErrorCode()).isPresent().contains(83);
    }

    /**
     * The reader should be able to read {@link PendingOrderConditionalyExecutedMessage} messages correctly.
     * 
     * @throws CommunicationException
     *             Not expected to leave the test method.
     */
    @Test
    public void shouldReadPendingOrderConditionalyExecutedMessagesCorrectly() throws CommunicationException {
        when(client.tryReceiveByte()).thenReturn(MessageType.PENDING_ORDER_CONDITIONALY_EXECUTED.getMessageNumber());
        when(client.tryReceiveInteger()).thenReturn(32);
        when(client.tryReceiveLong()).thenReturn(12584246L);
        when(client.tryReceiveDouble()).thenReturn(2.54);

        final PendingOrderConditionalyExecutedMessage message = cut
                .readMessage(PendingOrderConditionalyExecutedMessage.class);
        assertThat(message.getOrderId()).isEqualTo(32);
        assertThat(message.getTime().getEpochSecond()).isEqualTo(12584246L);
        assertThat(message.getPrice()).isEqualTo(new Price(2.54));
    }

    /**
     * The reader should be able to read {@link PendingOrderConditionalyClosedMessage} messages correctly.
     * 
     * @throws CommunicationException
     *             Not expected to leave the test method.
     */
    @Test
    public void shouldReadPendingOrderConditionalyClosedMessagesCorrectly() throws CommunicationException {
        when(client.tryReceiveByte()).thenReturn(MessageType.PENDING_ORDER_CONDITIONALY_CLOSED.getMessageNumber());
        when(client.tryReceiveInteger()).thenReturn(6554);
        when(client.tryReceiveLong()).thenReturn(3421342L);
        when(client.tryReceiveDouble()).thenReturn(32.421);

        final PendingOrderConditionalyClosedMessage message = cut
                .readMessage(PendingOrderConditionalyClosedMessage.class);
        assertThat(message.getOrderId()).isEqualTo(6554);
        assertThat(message.getTime().getEpochSecond()).isEqualTo(3421342L);
        assertThat(message.getPrice()).isEqualTo(new Price(32.421));
    }

    /**
     * The reader should be able to read {@link TradingEnvironmentInformationMessage}s correctly.
     * 
     * @throws CommunicationException
     *             Not expected to leave the test method.
     */
    @Test
    public void shouldReadTradingEnvironmentMessagesCorreclty() throws CommunicationException {
        when(client.tryReceiveString())
                // first read
                .thenReturn("Some Broker").thenReturn("EUR").thenReturn("EURUSD").thenReturn("EURUSD")
                // second read
                .thenReturn("Other Broker").thenReturn("GBP").thenReturn("CHFJPY").thenReturn("GBPJPY");
        when(client.tryReceiveByte()).thenReturn(MessageType.TRADING_ENVIRONMENT_INFORMATION.getMessageNumber());
        when(client.tryReceiveInteger())
                // margin than commission for first
                .thenReturn(5).thenReturn(3)
                // margin than commission for second
                .thenReturn(216).thenReturn(8);
        when(client.tryReceiveLong())
                // account number, non historic time, min-, step-, max-volume for first
                .thenReturn(42L).thenReturn(20L).thenReturn(10L).thenReturn(1000L).thenReturn(3L)
                // account number, non historic time, min-, step-, max-volume for second
                .thenReturn(80L).thenReturn(582L).thenReturn(1000L).thenReturn(2931868L).thenReturn(5L);

        final TradingEnvironmentInformationMessage message1 = cut
                .readMessage(TradingEnvironmentInformationMessage.class);
        final TradingEnvironmentInformationMessage message2 = cut
                .readMessage(TradingEnvironmentInformationMessage.class);

        assertThat(message1.getInformation().getAccountInformation().getBrokerName()).isEqualTo("Some Broker");
        assertThat(message1.getInformation().getAccountInformation().getAccountNumber()).isEqualTo(42L);
        assertThat(message1.getInformation().getAccountInformation().getAccountCurrency())
                .isEqualTo(Currency.getInstance("EUR"));
        assertThat(message1.getInformation().getTradeSymbol()).isEqualTo(new ForexSymbol("EURUSD"));
        assertThat(message1.getInformation().getAccountSymbol()).isEqualTo(new ForexSymbol("EURUSD"));
        assertThat(message1.getInformation().getSpecialFeesInformation().getMarkup()).isEqualTo(new Price(5));
        assertThat(message1.getInformation().getSpecialFeesInformation().getCommission()).isEqualTo(new Price(3));
        assertThat(message1.getInformation().getNonHistoricTime().getEpochSecond()).isEqualTo(20L);
        assertThat(message1.getInformation().getVolumeConstraints().getMinimalVolume())
                .isEqualTo(new Volume(10L, BASE));
        assertThat(message1.getInformation().getVolumeConstraints().getMaximalVolume())
                .isEqualTo(new Volume(1000L, BASE));
        assertThat(message1.getInformation().getVolumeConstraints().getAllowedStepSize())
                .isEqualTo(new Volume(3L, BASE));

        assertThat(message2.getInformation().getAccountInformation().getBrokerName()).isEqualTo("Other Broker");
        assertThat(message2.getInformation().getAccountInformation().getAccountNumber()).isEqualTo(80L);
        assertThat(message2.getInformation().getAccountInformation().getAccountCurrency())
                .isEqualTo(Currency.getInstance("GBP"));
        assertThat(message2.getInformation().getTradeSymbol()).isEqualTo(new ForexSymbol("CHFJPY"));
        assertThat(message2.getInformation().getAccountSymbol()).isEqualTo(new ForexSymbol("GBPJPY"));
        assertThat(message2.getInformation().getSpecialFeesInformation().getMarkup()).isEqualTo(new Price(216));
        assertThat(message2.getInformation().getSpecialFeesInformation().getCommission()).isEqualTo(new Price(8));
        assertThat(message2.getInformation().getNonHistoricTime().getEpochSecond()).isEqualTo(582L);
        assertThat(message2.getInformation().getVolumeConstraints().getMinimalVolume())
                .isEqualTo(new Volume(1000L, BASE));
        assertThat(message2.getInformation().getVolumeConstraints().getMaximalVolume())
                .isEqualTo(new Volume(2931868L, BASE));
        assertThat(message2.getInformation().getVolumeConstraints().getAllowedStepSize())
                .isEqualTo(new Volume(5L, BASE));
    }

    /**
     * The reader should be able to read {@link ResponseChangeCloseConditionsMessage}s correctly.
     * 
     * @throws CommunicationException
     *             Not expected to leave the test method.
     */
    @Test
    public void shouldReadResponseChangeCloseConditionsMessagesCorrectly() throws CommunicationException {
        when(client.tryReceiveByte()).thenReturn(MessageType.RESPONSE_CHANGE_CLOSE_CONDITIONS.getMessageNumber(),
                (byte) 0, MessageType.RESPONSE_CHANGE_CLOSE_CONDITIONS.getMessageNumber(), (byte) 1);
        when(client.tryReceiveInteger()).thenReturn(868);

        final ResponseChangeCloseConditionsMessage changeSucceed = cut
                .readMessage(ResponseChangeCloseConditionsMessage.class);
        assertThat(changeSucceed.getErrorCode()).isEmpty();

        final ResponseChangeCloseConditionsMessage changeFailed = cut
                .readMessage(ResponseChangeCloseConditionsMessage.class);
        assertThat(changeFailed.getErrorCode()).isPresent().contains(868);
    }

    /**
     * The reader should be able to read {@link BalanceChangedMessage}s correctly.
     * 
     * @throws CommunicationException
     *             Not expected to leave the test method.
     */
    @Test
    public void shouldReadBalanceChangedMessagesCorrectly() throws CommunicationException {
        when(client.tryReceiveByte()).thenReturn(MessageType.BALANCE_CHANGED.getMessageNumber());
        when(client.tryReceiveLong()).thenReturn(12345678L);

        final BalanceChangedMessage message = cut.readMessage(BalanceChangedMessage.class);
        assertThat(message.getNewBalance()).isEqualTo(12345678L);
    }

    /**
     * The reader should be able to read {@link AccountCurrencyExchangeRateChangedMessage}s correctly.
     * 
     * @throws CommunicationException
     *             Not expected to leave the test method.
     */
    @Test
    public void shouldReadAccountCurrencyExchangeRateChangedMessageCorrectly() throws CommunicationException {
        when(client.tryReceiveByte()).thenReturn(MessageType.ACCOUNT_CURRENCY_EXCHANGE_RATE_CHANGED.getMessageNumber());
        when(client.tryReceiveDouble()).thenReturn(1.5);

        final AccountCurrencyExchangeRateChangedMessage message = cut
                .readMessage(AccountCurrencyExchangeRateChangedMessage.class);
        assertThat(message.getNewRate()).isEqualTo(new Price(1.5));
    }

    // /////////////
    // / Writing ///
    // /////////////

    /**
     * The class should be able to write {@link TrendForMarketDataMessage} correctly.
     * 
     * @throws CommunicationException
     *             Not expected to leave the test method.
     */
    @Test
    public void shouldWriteTrendForMarketDataMessages() throws CommunicationException {
        final TrendForMarketDataMessage trend1 = new TrendForMarketDataMessage(Optional.of(MarketDirection.DOWN));
        final TrendForMarketDataMessage trend2 = new TrendForMarketDataMessage(Optional.of(MarketDirection.UP));

        cut.sendMessage(trend1);
        cut.sendMessage(trend2);

        final InOrder inOrder = inOrder(client);
        inOrder.verify(client).trySendByte(MessageType.TREND_FOR_MARKET_DATA.getMessageNumber());
        inOrder.verify(client).trySendByte((byte) 1);
        inOrder.verify(client).trySendByte(MessageType.TREND_FOR_MARKET_DATA.getMessageNumber());
        inOrder.verify(client).trySendByte((byte) 0);
    }

    /**
     * The class should be able to write {@link PlacePendingOrderMessage} messages correctly.
     * 
     * @throws CommunicationException
     *             Not expected to leave the test method.
     */
    @Test
    public void shouldWritePlacePendingOrderMessagesCorrectly() throws CommunicationException {
        final MutablePendingOrder order = new MutablePendingOrder().setType(BUY).setExecutionCondition(STOP)
                .setVolume(new Volume(20, MICRO_LOT)).setEntryPrice(new Price(45.58))
                .setCloseConditions(new MutableCloseConditions().setTakeProfit(new Price(54.48))
                        .setStopLoose(new Price(43.91)).setExpirationDate(Instant.ofEpochSecond(522891)));

        cut.sendMessage(new PlacePendingOrderMessage(order.toImmutablePendingOrder()));

        final InOrder inOrder = inOrder(client);
        inOrder.verify(client).trySendByte(MessageType.PLACE_PENDING_ORDER.getMessageNumber());
        // flags: 1: has expiration date, 10: condition is STOP, 0: type is BUY
        inOrder.verify(client).trySendByte((byte) 0b1100);
        inOrder.verify(client).trySendInteger(20000);
        inOrder.verify(client).trySendDouble(45.58);
        inOrder.verify(client).trySendDouble(54.48);
        inOrder.verify(client).trySendDouble(43.91);
        inOrder.verify(client).trySendLong(522891);

        order.setType(SELL);
        order.setExecutionCondition(DIRECT);
        order.setCloseConditions(
                new MutableCloseConditions().setTakeProfit(new Price(54.48)).setStopLoose(new Price(43.91)));
        cut.sendMessage(new PlacePendingOrderMessage(order.toImmutablePendingOrder()));
        inOrder.verify(client).trySendByte(MessageType.PLACE_PENDING_ORDER.getMessageNumber());
        // flags: 0: no expiration date, 00: condition is DIRECT, 1: type is SELL
        inOrder.verify(client).trySendByte((byte) 0b0001);
    }

    /**
     * The class should be able to write {@link CloseOrCancelPendingOrderMessage} messages correctly.
     * 
     * @throws CommunicationException
     *             Not expected to leave the test method.
     */
    @Test
    public void shouldWriteCloseOrCancelPendingOrderMessagesCorrectly() throws CommunicationException {
        final CloseOrCancelPendingOrderMessage message = new CloseOrCancelPendingOrderMessage(42);

        cut.sendMessage(message);
        final InOrder inOrder = inOrder(client);
        inOrder.verify(client).trySendByte(MessageType.CLOSE_OR_CANCEL_PENDING_ORDER.getMessageNumber());
        inOrder.verify(client).trySendInteger(42);
    }

    /**
     * The class should be able to write {@link EventHandlingFinishedMessage} messages correctly.
     * 
     * @throws CommunicationException
     *             Not expected to leave the test method.
     */
    @Test
    public void shouldWriteEventHandlingFinishedMessagesCorrectly() throws CommunicationException {
        cut.sendMessage(new EventHandlingFinishedMessage());

        verify(client).trySendByte(MessageType.EVENT_HANDLING_FINISHED.getMessageNumber());
        verifyNoMoreInteractions(client);
    }

    /**
     * The class should be able to write {@link ChangeCloseConditionsMessage} messages correctly.
     * 
     * @throws CommunicationException
     *             Not expected to leave the test method.
     */
    @Test
    public void shouldWriteChangeCloseConditionMessagesCorrectly() throws CommunicationException {
        final CloseConditions closeConditionsWithoutDate = new CloseConditions(new Price(1.0), new Price(2.0));
        cut.sendMessage(new ChangeCloseConditionsMessage(10, closeConditionsWithoutDate));

        final Instant exampleDate = Instant.ofEpochSecond(6000);
        final CloseConditions closeConditionsWithDate = new CloseConditions(new Price(3.0), new Price(4.0),
                exampleDate);
        cut.sendMessage(new ChangeCloseConditionsMessage(20, closeConditionsWithDate));

        final InOrder inOrder = inOrder(client);
        // first message
        inOrder.verify(client).trySendByte(MessageType.CHANGE_CLOSE_CONDITIONS.getMessageNumber());
        inOrder.verify(client).trySendByte((byte) 0); // no date
        inOrder.verify(client).trySendInteger(10);
        inOrder.verify(client).trySendDouble(1.0);
        inOrder.verify(client).trySendDouble(2.0);

        // second message
        inOrder.verify(client).trySendByte(MessageType.CHANGE_CLOSE_CONDITIONS.getMessageNumber());
        inOrder.verify(client).trySendByte((byte) 1); // has date
        inOrder.verify(client).trySendInteger(20);
        inOrder.verify(client).trySendDouble(3.0);
        inOrder.verify(client).trySendDouble(4.0);
        inOrder.verify(client).trySendLong(6000);
    }

    // ////////////////////
    // / Error Handling ///
    // ////////////////////

    /**
     * The class should throw an exception when the read message number is not the correct message number for the
     * message that should be read next.
     * 
     * @throws CommunicationException
     *             if it is a {@link MessageReadException} it is expected, all other sub-types indicate a test failure.
     */
    @Test(expected = MessageReadException.class)
    public void shouldFailWhenReadingAWrongMessageNumber() throws CommunicationException {
        when(client.tryReceiveByte()).thenReturn(UNKNOWN_MESSAGE_NUMBER);

        cut.readMessage(RequestTradingAlgorithmMessage.class);
    }

    /**
     * Should fail when an arbitrary message should be read next but the message number that was read is unknown.
     * 
     * @throws CommunicationException
     *             if it is a {@link MessageReadException} it is expected, all other sub-types indicate a test failure.
     */
    @Test(expected = MessageReadException.class)
    public void shouldFailWhenReadingAnUnknownMessageNumber() throws CommunicationException {
        when(client.tryReceiveByte()).thenReturn(UNKNOWN_MESSAGE_NUMBER);

        cut.readMessage();
    }

    /**
     * Should fail when a trading environment message with an unknown forex pair was received.
     * 
     * @throws CommunicationException
     *             if it is a {@link MessageReadException} it is expected, all other sub-types indicate a test failure.
     */
    @Test(expected = MessageReadException.class)
    public void shouldFailWhenTradingEnvironmentMessagesContainsUnknownForexPair() throws CommunicationException {
        when(client.tryReceiveByte()).thenReturn(MessageType.TRADING_ENVIRONMENT_INFORMATION.getMessageNumber());
        when(client.tryReceiveString()).thenReturn("sample unknown forex pair");
        when(client.tryReceiveInteger()).thenReturn(5);
        when(client.tryReceiveLong()).thenReturn(20L);

        cut.readMessage(TradingEnvironmentInformationMessage.class);
    }
}
