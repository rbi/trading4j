package de.voidnode.trading4j.server.protocol;

import java.util.Optional;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.api.TrendIndicatorFactory;
import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.marketdata.DatedCandleStick;
import de.voidnode.trading4j.domain.timeframe.M1;
import de.voidnode.trading4j.server.protocol.messages.Message;
import de.voidnode.trading4j.server.protocol.messages.MessageBasedClientConnection;
import de.voidnode.trading4j.server.protocol.messages.NewMarketDataSimpleMessage;
import de.voidnode.trading4j.server.protocol.messages.TrendForMarketDataMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Checks if {@link IndicatorProtocol} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class IndicatorProtocolTest {

    private static final byte EXAMPLE_INDICATOR = 10;
    private static final byte UNKNOWN_INDICATOR = 20;

    @Mock
    private MessageBasedClientConnection messageIo;

    @Mock
    private TrendIndicatorFactory factory;

    private IndicatorProtocol cut;

    @Mock
    private Indicator<MarketDirection, DatedCandleStick<M1>> exampleIndicator;

    @Mock
    private NewMarketDataSimpleMessage exampleMarketData;

    @Mock
    private DatedCandleStick<M1> exampleCandleStick;

    @Captor
    private ArgumentCaptor<DatedCandleStick<M1>> candlePassedToIndicator;

    @Captor
    private ArgumentCaptor<Message> sentMessage;

    /**
     * Wires up the mocks.
     */
    @Before
    public void setUpMocks() {
        when(factory.newIndicatorByNumber(EXAMPLE_INDICATOR)).thenReturn(Optional.of(exampleIndicator));
        when(factory.newIndicatorByNumber(UNKNOWN_INDICATOR)).thenReturn(Optional.empty());
    }

    /**
     * Sets up the protocol to test.
     */
    @Before
    public void setUpProtocol() {
        cut = new IndicatorProtocol(messageIo, factory, EXAMPLE_INDICATOR);
    }

    // /////////////////////////////
    // / protocol initialization ///
    // /////////////////////////////

    /**
     * The protocol should use the indicator factory to create the correct indicator for the given indicator number.
     * 
     * @throws CommunicationException
     *             not expected to leave the test method
     */
    @Test
    public void shouldRequestCorrectIndicatorBasedOnTheIndicatorNumber() throws CommunicationException {
        // simulate a client side close of the connection
        when(messageIo.readMessage(NewMarketDataSimpleMessage.class)).thenThrow(new SimulateClientSideClose());

        startCutUntilSimulatedClose();

        verify(factory).newIndicatorByNumber(EXAMPLE_INDICATOR);
    }

    /**
     * If no indicator is known for the received number, the protocol should close the connection and throw a
     * ProtocolException.
     * 
     * @throws Exception
     *             not expected to leave the test method
     */
    @Test
    public void whenIndicatorIsUnknownTheProtocolShouldFailWithAProtocolException() throws Exception {
        boolean exceptionCatched = false;
        try {
            new IndicatorProtocol(messageIo, factory, UNKNOWN_INDICATOR).start();
        } catch (final ProtocolException e) {
            exceptionCatched = true;
            assertThat(e.getMessage()).contains("indicator").contains("number");
        }

        assertThat(exceptionCatched).as("Expected to catch a protocol exception but didn't.").isTrue();
        verifyNoMoreInteractions(messageIo);
    }

    // ////////////////////////////////////
    // / protocol request/response loop ///
    // ////////////////////////////////////

    /**
     * After indicator indicator initialization has finished, the protocol should receive a candle, send it to the
     * indicator and send the results of the indicator back to the client.
     * 
     * @throws Exception
     *             not expected to leave the test method
     */
    @Test
    public void shouldRecivedCandleSendItToIndicatorAndSendTheTrendBackToTheClient() throws Exception {
        when(exampleMarketData.getCandleStick()).thenReturn(exampleCandleStick);
        when(exampleIndicator.indicate(any())).thenReturn(Optional.of(MarketDirection.DOWN));
        when(messageIo.readMessage(NewMarketDataSimpleMessage.class)).thenReturn(exampleMarketData).thenThrow(
                new SimulateClientSideClose());

        startCutUntilSimulatedClose();

        verify(exampleIndicator).indicate(candlePassedToIndicator.capture());
        assertThat(candlePassedToIndicator.getValue()).isEqualTo(exampleCandleStick);

        verify(messageIo).sendMessage(sentMessage.capture());
        assertThat(sentMessage.getValue()).isInstanceOf(TrendForMarketDataMessage.class);
        final Optional<MarketDirection> trendInMessage = ((TrendForMarketDataMessage) sentMessage.getValue()).getTrend();
        assertThat(trendInMessage.isPresent()).isTrue();
        assertThat(trendInMessage.get()).isEqualTo(MarketDirection.DOWN);
    }

    /**
     * After the trend for a candle was send back to the client, the protocol should be waiting to receive the next
     * candle.
     * 
     * @throws Exception
     *             not expected to leave the test method
     */
    @Test
    public void shouldReciveNextCandleAfterTrendWasSend() throws Exception {
        when(exampleMarketData.getCandleStick()).thenReturn(exampleCandleStick);
        when(exampleIndicator.indicate(any())).thenReturn(Optional.of(MarketDirection.DOWN));
        when(messageIo.readMessage(NewMarketDataSimpleMessage.class)).thenReturn(exampleMarketData)
                .thenReturn(exampleMarketData).thenThrow(new SimulateClientSideClose());

        startCutUntilSimulatedClose();

        final InOrder inOrder = inOrder(messageIo, exampleIndicator);

        inOrder.verify(messageIo).readMessage(NewMarketDataSimpleMessage.class);
        inOrder.verify(exampleIndicator).indicate(any());
        inOrder.verify(messageIo).sendMessage(any(TrendForMarketDataMessage.class));

        inOrder.verify(messageIo).readMessage(NewMarketDataSimpleMessage.class);
        inOrder.verify(exampleIndicator).indicate(any());
        inOrder.verify(messageIo).sendMessage(any(TrendForMarketDataMessage.class));

        inOrder.verify(messageIo).readMessage(NewMarketDataSimpleMessage.class);
        inOrder.verifyNoMoreInteractions();
    }

    private void startCutUntilSimulatedClose() throws CommunicationException {
        try {
            cut.start();
            // CHECKSTYLE:OFF not an exceptional state
        } catch (final SimulateClientSideClose e) {
            // CHECKSTYLE:ON
            // do noting
        }
    }

    /**
     * Used in tests to simulate a client side close of the connection.
     */
    private static class SimulateClientSideClose extends CommunicationException {
        private static final long serialVersionUID = 1L;
    }
}
