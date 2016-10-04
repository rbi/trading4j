package de.voidnode.trading4j.server.protocol.expertadvisor;

import java.util.Optional;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.ExpertAdvisor;
import de.voidnode.trading4j.api.MoneyManagement;
import de.voidnode.trading4j.api.UnrecoverableProgrammingError;
import de.voidnode.trading4j.domain.environment.TradingEnvironmentInformation;
import de.voidnode.trading4j.moneymanagement.SharedMoneyManagement.ReleasableMoneyManagement;
import de.voidnode.trading4j.server.protocol.CommunicationException;
import de.voidnode.trading4j.server.protocol.ProtocolException;
import de.voidnode.trading4j.server.protocol.ProtocolFactory;
import de.voidnode.trading4j.server.protocol.messages.EventHandlingFinishedMessage;
import de.voidnode.trading4j.server.protocol.messages.Message;
import de.voidnode.trading4j.server.protocol.messages.MessageBasedClientConnection;
import de.voidnode.trading4j.server.protocol.messages.TradingEnvironmentInformationMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Checks if {@link ExpertAdvisorProtocol} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class ExpertAdvisorProtocolTest {

    private static final int EXAMPLE_EXPERT_ADVISOR = 200;
    private static final int UNKNOWN_EXPERT_ADVISOR = 210;

    @Mock
    private MessageBasedClientConnection client;

    @Mock
    private ProtocolFactory factory;

    private ExpertAdvisorProtocol cut;

    @Mock
    private LocalExpertAdvisor exampleLocalExpertAdvisor;

    @Mock
    private TradingEnvironmentInformationMessage exemplaryEnvironmentInformationMessage;

    @Mock
    private Message exampleMessage;

    @Mock
    private TradingEnvironmentInformation environmentInformation;

    @Mock
    private ReleasableMoneyManagement releasableMoneyManagement;

    /**
     * Wires up the mocks and set up the class to test.
     * 
     * @throws CommunicationException
     *             not expected to leave the setup method
     */
    @Before
    public void setUpMocksAndCut() throws CommunicationException {
        cut = new ExpertAdvisorProtocol(client, factory, EXAMPLE_EXPERT_ADVISOR);

        when(exemplaryEnvironmentInformationMessage.getInformation()).thenReturn(environmentInformation);
        when(client.readMessage(TradingEnvironmentInformationMessage.class))
                .thenReturn(exemplaryEnvironmentInformationMessage);

        when(factory.newSharedMoneyManagementInstance()).thenReturn(releasableMoneyManagement);
        when(factory.newLocalExpertAdvisorByNumber(EXAMPLE_EXPERT_ADVISOR, client, releasableMoneyManagement,
                environmentInformation)).thenReturn(Optional.of(exampleLocalExpertAdvisor));
        when(factory.newLocalExpertAdvisorByNumber(eq(UNKNOWN_EXPERT_ADVISOR), any(), any(),
                eq(environmentInformation))).thenReturn(Optional.empty());

    }

    // /////////////////////////////
    // / Protocol Initialization ///
    // /////////////////////////////

    /**
     * When the protocol is started, the {@link LocalExpertAdvisor} wrapping the correct {@link ExpertAdvisor} should be
     * created through the {@link ProtocolFactory}.
     * 
     * <p>
     * The {@link TradingEnvironmentInformation} that where read should be passed to the factory.
     * </p>
     * 
     * @throws CommunicationException
     *             not expected to leave the test.
     */
    @Test
    public void shouldRequestCorrectExpertAdvisorBasedOnTheAlgorithmNumber() throws CommunicationException {
        // simulate a client side close of the connection
        when(client.readMessage()).thenThrow(new SimulateClientSideClose());

        startCutUntilSimulatedClose();

        verify(factory).newLocalExpertAdvisorByNumber(EXAMPLE_EXPERT_ADVISOR, client, releasableMoneyManagement,
                environmentInformation);
    }

    /**
     * When an {@link ExpertAdvisor} number was passed to the protocol which is unknown, the protocol should fail with a
     * {@link ProtocolException}.
     * 
     * @throws CommunicationException
     *             not expected to leave the test.
     */
    @Test
    public void whenExpertAdvisorIsUnknownTheProtocolShouldFailWithAProtocolException() throws CommunicationException {
        boolean exceptionCatched = false;
        try {
            new ExpertAdvisorProtocol(client, factory, UNKNOWN_EXPERT_ADVISOR).start();
        } catch (final ProtocolException e) {
            exceptionCatched = true;
            assertThat(e.getMessage()).contains("expert").contains("advisor").contains("number");
        }

        assertThat(exceptionCatched).as("Expected to catch a protocol exception but didn't.").isTrue();
        verify(client, times(0)).readMessage();
    }

    // ///////////////////
    // / Base Protocol ///
    // ///////////////////

    /**
     * After an incoming {@link Message} was handled by the {@link LocalExpertAdvisor} the cut should send an
     * {@link EventHandlingFinishedMessage} message to indicate to the remote {@link Broker} that no more
     * {@link Message}s are send in response to the received one.
     * 
     * @throws CommunicationException
     *             not expected to leave the test.
     */
    @Test
    public void shouldNotifyClientOfFinishedMessageHandling() throws CommunicationException {
        when(client.readMessage()).thenReturn(exampleMessage).thenThrow(new SimulateClientSideClose());

        startCutUntilSimulatedClose();

        final InOrder inOrder = inOrder(client, exampleLocalExpertAdvisor);
        inOrder.verify(client).readMessage();
        inOrder.verify(exampleLocalExpertAdvisor).handleMessage(exampleMessage);
        inOrder.verify(client).sendMessage(any(EventHandlingFinishedMessage.class));
    }

    /**
     * When a message was read and the appropriate actions executed, the next message should be read.
     * 
     * @throws CommunicationException
     *             not expected to leave the test.
     */
    @Test
    public void shouldReadNextMessageAfterAReadMessageWasEvaluated() throws CommunicationException {
        when(client.readMessage()).thenReturn(exampleMessage).thenReturn(exampleMessage)
                .thenThrow(new SimulateClientSideClose());

        startCutUntilSimulatedClose();

        final InOrder inOrder = inOrder(client);
        inOrder.verify(client).readMessage();
        inOrder.verify(client).sendMessage(any(EventHandlingFinishedMessage.class));
        inOrder.verify(client).readMessage();
        inOrder.verify(client).sendMessage(any(EventHandlingFinishedMessage.class));
    }

    /**
     * Fatal {@link CommunicationException} that where wrapped in a {@link RuntimeException} by the remote
     * {@link Broker} should be unpacked and re-thrown.
     * 
     * @throws CommunicationException
     *             not expected to leave the test.
     */
    @Test
    public void communicationExceptionThrownInSendMethodsShouldBeRethrownInTheEventLoop()
            throws CommunicationException {
        when(client.readMessage()).thenReturn(exampleMessage);
        doThrow(new LoopThroughCommunicationException(new ExemplaryFatalCommunicationException()))
                .when(exampleLocalExpertAdvisor).handleMessage(exampleMessage);

        boolean exceptionCatched = false;
        try {
            cut.start();
        } catch (final ExemplaryFatalCommunicationException e) {
            exceptionCatched = true;
        }

        assertThat(exceptionCatched)
                .as("The 'SimulateWriteFailed' exception was expected to be re-thrown out of the start() method but it wasn't")
                .isTrue();
    }

    /**
     * All volume that was lent from the {@link MoneyManagement} is returned when the connection closes expectedly or
     * unexpectedly.
     * 
     * @throws Exception
     *             not expected to leave the test.
     */
    @Test
    public void lentedVolumeIsReturnedWhenClosingTheConnection() throws Exception {
        when(client.readMessage())
                .thenThrow(new LoopThroughCommunicationException(new ExemplaryFatalCommunicationException()))
                .thenThrow(
                        new LoopThroughProgrammingErrorException(new UnrecoverableProgrammingError("test exception")))
                .thenThrow(new RuntimeException("some exception"));

        startCutSwallowExpectedExceptions();
        startCutSwallowExpectedExceptions();
        startCutSwallowExpectedExceptions();

        verify(releasableMoneyManagement, times(3)).realeaseAllAquieredVolume();
    }

    private void startCutUntilSimulatedClose() {
        try {
            cut.start();
            // CHECKSTYLE:OFF not an exceptional state
        } catch (final SimulateClientSideClose e) {
            // CHECKSTYLE:ON
            // do noting
        } catch (final CommunicationException e) {
            throw new RuntimeException(e);
        }

    }

    private void startCutSwallowExpectedExceptions() {
        boolean catched = false;
        try {
            cut.start();
        } catch (final Exception e) {
            catched = true;
        }
        if (!catched) {
            fail("Expected to cut to throw an exception but it didn't.");
        }
    }

    /**
     * Used in tests to simulate a client side close of the connection.
     */
    private static class SimulateClientSideClose extends CommunicationException {
        private static final long serialVersionUID = 1L;
    }

    /**
     * Used in tests to simulate that writing to a client failed.
     */
    private static class ExemplaryFatalCommunicationException extends CommunicationException {
        private static final long serialVersionUID = 1L;
    }
}
