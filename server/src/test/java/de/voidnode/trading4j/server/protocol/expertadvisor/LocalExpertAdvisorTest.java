package de.voidnode.trading4j.server.protocol.expertadvisor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Currency;

import de.voidnode.trading4j.api.AccountBalanceManager;
import de.voidnode.trading4j.api.ExpertAdvisor;
import de.voidnode.trading4j.api.OrderEventListener;
import de.voidnode.trading4j.domain.ForexSymbol;
import de.voidnode.trading4j.domain.marketdata.impl.FullMarketData;
import de.voidnode.trading4j.domain.monetary.Money;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.PendingOrder;
import de.voidnode.trading4j.domain.timeframe.M1;
import de.voidnode.trading4j.server.protocol.CommunicationException;
import de.voidnode.trading4j.server.protocol.ProtocolException;
import de.voidnode.trading4j.server.protocol.messages.AccountCurrencyExchangeRateChangedMessage;
import de.voidnode.trading4j.server.protocol.messages.BalanceChangedMessage;
import de.voidnode.trading4j.server.protocol.messages.Message;
import de.voidnode.trading4j.server.protocol.messages.MessageBasedClientConnection;
import de.voidnode.trading4j.server.protocol.messages.NewMarketDataExtendedMessage;
import de.voidnode.trading4j.server.protocol.messages.PendingOrderConditionalyClosedMessage;
import de.voidnode.trading4j.server.protocol.messages.PendingOrderConditionalyExecutedMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Checks if {@link LocalExpertAdvisor} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class LocalExpertAdvisorTest {
    private static final int EXAMPLE_PENDING_ORDER_ID = 42;

    private static final Currency TEST_CURRENCY = Currency.getInstance("AUD");
    private static final ForexSymbol TEST_SYMBOL = new ForexSymbol(TEST_CURRENCY, Currency.getInstance("CAD"));

    @Mock
    private ExpertAdvisor<FullMarketData<M1>> expertAdvisor;

    @Mock
    private AccountBalanceManager balanceManager;

    @Mock
    private PendingOrderMapper pendingOrderMapper;

    private LocalExpertAdvisor cut;

    @Mock
    private OrderEventListener orderEventListener;

    @Mock
    private NewMarketDataExtendedMessage exampleNewMarketDataMessage;

    @Mock
    private BalanceChangedMessage exampleBalanceChangedMessage;

    @Mock
    private AccountCurrencyExchangeRateChangedMessage exampleAccountCurrencyExchangeRatChangedMessage;

    @Mock
    private FullMarketData<M1> exampleFatCandleStick;

    @Mock
    private PendingOrder examplePendingOrder;

    /**
     * Sets up the default behavior of the mocks.
     */
    @Before
    public void setUpMocksAndCut() {
        when(pendingOrderMapper.has(EXAMPLE_PENDING_ORDER_ID)).thenReturn(true);
        when(pendingOrderMapper.get(EXAMPLE_PENDING_ORDER_ID)).thenReturn(orderEventListener);
        when(exampleNewMarketDataMessage.getCandleStick()).thenReturn(exampleFatCandleStick);

        cut = new LocalExpertAdvisor(expertAdvisor, balanceManager, pendingOrderMapper, TEST_CURRENCY, TEST_SYMBOL);
    }

    /**
     * When the {@link MessageBasedClientConnection} read a message that was not expected to be read for the
     * {@link ExpertAdvisorProtocol}, a {@link ProtocolException} should be thrown.
     * 
     * @throws CommunicationException
     *             not expected to leave the test.
     */
    @Test
    public void shouldFailWithAProtcolExceptionWhenUnexpectedMessageWasReceived() throws CommunicationException {
        boolean exceptionCatched = false;
        try {
            cut.handleMessage(new UnexpectedMessage());
        } catch (final ProtocolException e) {
            exceptionCatched = true;
            assertThat(e.getMessage()).contains("not").contains("expected").contains("message")
                    .contains("UnexpectedMessage");
        }

        assertThat(exceptionCatched).as("Expected to catch a protocol exception but didn't.").isTrue();
    }

    /**
     * When a {@link NewMarketDataExtendedMessage} was received the expert advisor should be notified.
     * 
     * @throws CommunicationException
     *             not expected to leave the test.
     */
    @Test
    public void shouldInformExpertAdvisorOnNewMarketData() throws CommunicationException {
        cut.handleMessage(exampleNewMarketDataMessage);

        verify(expertAdvisor).newData(exampleFatCandleStick);
    }

    /**
     * When a {@link BalanceChangedMessage} was received the expert advisor should be notified.
     * 
     * @throws CommunicationException
     *             not expected to leave the test.
     */
    @Test
    public void shouldInformBalanceManagerOnChangedBalance() throws CommunicationException {
        when(exampleBalanceChangedMessage.getNewBalance()).thenReturn(42L);

        cut.handleMessage(exampleBalanceChangedMessage);

        // The currency to use is passed in the constructor.
        verify(balanceManager).updateBalance(new Money(0, 42, TEST_CURRENCY));
    }

    /**
     * When a {@link AccountCurrencyExchangeRateChangedMessage} was received the expert advisor should be notified.
     *
     * @throws CommunicationException
     *             not expected to leave the test.
     */
    @Test
    public void shouldInformTheBalanceManagerOnAccountCurrencyExchangeRateChanged() throws CommunicationException {
        when(exampleAccountCurrencyExchangeRatChangedMessage.getNewRate()).thenReturn(new Price(1234));

        cut.handleMessage(exampleAccountCurrencyExchangeRatChangedMessage);

        verify(balanceManager).updateExchangeRate(TEST_SYMBOL, new Price(1234));
    }

    /**
     * When a pending order was opened the {@link ExpertAdvisor} should be notified.
     * 
     * @throws CommunicationException
     *             not expected to leave the test.
     */
    @Test
    public void shouldInformExpertAdvisorOnOpenedPendingOrders() throws CommunicationException {
        final Price examplePrice = new Price(5.48);
        final Instant exampleTime = LocalDateTime.of(2014, 07, 13, 15, 32, 23).toInstant(ZoneOffset.UTC);
        final PendingOrderConditionalyExecutedMessage message = new PendingOrderConditionalyExecutedMessage(
                EXAMPLE_PENDING_ORDER_ID, exampleTime, examplePrice);

        cut.handleMessage(message);

        verify(orderEventListener).orderOpened(exampleTime, examplePrice);
    }

    /**
     * When a {@link PendingOrderConditionalyExecutedMessage} message was received for an unknown {@link PendingOrder} a
     * {@link ProtocolException} should be thrown.
     * 
     * @throws CommunicationException
     *             not expected to leave the test.
     */
    @Test(expected = ProtocolException.class)
    public void shouldFailWhenUnknownOrderWasOpened() throws CommunicationException {
        when(pendingOrderMapper.has(EXAMPLE_PENDING_ORDER_ID)).thenReturn(false);

        final Price examplePrice = new Price(5.48);
        final Instant exampleTime = LocalDateTime.of(2014, 07, 13, 15, 32, 23).toInstant(ZoneOffset.UTC);
        final PendingOrderConditionalyExecutedMessage message = new PendingOrderConditionalyExecutedMessage(
                EXAMPLE_PENDING_ORDER_ID, exampleTime, examplePrice);

        cut.handleMessage(message);
    }

    /**
     * When a pending order was closed because it's closing conditions are met, the {@link ExpertAdvisor} should be
     * notified.
     * 
     * @throws CommunicationException
     *             not expected to leave the test.
     */
    @Test
    public void shouldInformExpertAdvisorOnClosedPendingOrders() throws CommunicationException {
        final Price examplePrice = new Price(5.48);
        final Instant exampleTime = LocalDateTime.of(2014, 07, 13, 15, 32, 23).toInstant(ZoneOffset.UTC);
        final PendingOrderConditionalyClosedMessage message = new PendingOrderConditionalyClosedMessage(
                EXAMPLE_PENDING_ORDER_ID, exampleTime, examplePrice);

        cut.handleMessage(message);

        verify(orderEventListener).orderClosed(exampleTime, examplePrice);
    }

    /**
     * When a {@link PendingOrderConditionalyClosedMessage} message was received for an unknown {@link PendingOrder} a
     * {@link ProtocolException} should be thrown.
     * 
     * @throws CommunicationException
     *             not expected to leave the test.
     */
    @Test(expected = ProtocolException.class)
    public void shouldFailWhenUnknownOrderWasClosed() throws CommunicationException {
        when(pendingOrderMapper.has(EXAMPLE_PENDING_ORDER_ID)).thenReturn(false);

        final Price examplePrice = new Price(5.48);
        final Instant exampleTime = LocalDateTime.of(2014, 07, 13, 15, 32, 23).toInstant(ZoneOffset.UTC);
        final PendingOrderConditionalyClosedMessage message = new PendingOrderConditionalyClosedMessage(
                EXAMPLE_PENDING_ORDER_ID, exampleTime, examplePrice);

        cut.handleMessage(message);
    }

    /**
     * To prevent memory leaks, closed {@link PendingOrder}s should be removed from the order-to-id mapping.
     * 
     * @throws ProtocolException
     *             not expected to leave the test.
     */
    @Test
    public void shoulRemoveClosedPendingOrdersFromTheMapping() throws ProtocolException {
        final Price examplePrice = new Price(5.48);
        final Instant exampleTime = LocalDateTime.of(2014, 07, 13, 15, 32, 23).toInstant(ZoneOffset.UTC);
        final PendingOrderConditionalyClosedMessage message = new PendingOrderConditionalyClosedMessage(
                EXAMPLE_PENDING_ORDER_ID, exampleTime, examplePrice);

        cut.handleMessage(message);

        verify(pendingOrderMapper).remove(EXAMPLE_PENDING_ORDER_ID);
    }

    /**
     * A {@link Message} that is unknown to the {@link ExpertAdvisorProtocol}.
     */
    private static class UnexpectedMessage implements Message {

    }
}
