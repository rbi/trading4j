package de.voidnode.trading4j.server.protocol;

import de.voidnode.trading4j.server.protocol.expertadvisor.ExpertAdvisorProtocol;
import de.voidnode.trading4j.server.protocol.messages.MessageBasedClientConnection;
import de.voidnode.trading4j.server.protocol.messages.RequestTradingAlgorithmMessage;
import de.voidnode.trading4j.server.protocol.messages.RequestTradingAlgorithmMessage.AlgorithmType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests if {@link ClientCommunicator} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class ClientCommunicatorTest {

    private static final int EXAMPLE_INDICATOR_NUMBER = 9516;
    private static final int EXAMPLE_EXPERT_ADVISOR_NUMBER = -36;

    // global dependencies and class to test

    @Mock
    private ProtocolFactory protocolFactory;

    @InjectMocks
    private ClientCommunicator cut;

    // client connection scoped dependencies

    @Mock
    private ClientConnection exampleClientConnection;

    @Mock
    private MessageBasedClientConnection exampleMessageBasedClientConnection;

    @Mock
    private IndicatorProtocol exampleIndicatorProtocol;

    @Mock
    private ExpertAdvisorProtocol exampleExpertAdvisorProtocol;

    @Mock
    private ExceptionHandler exampleExceptionHandler;

    /**
     * Wires up the behavior of the dependency mocks.
     */
    @Before
    public void wireUpDependencyMocks() {
        when(protocolFactory.newMessageBasedClientConnection(exampleClientConnection))
                .thenReturn(exampleMessageBasedClientConnection);
        when(protocolFactory.newIndicatorProtocol(exampleMessageBasedClientConnection, EXAMPLE_INDICATOR_NUMBER))
                .thenReturn(exampleIndicatorProtocol);
        when(protocolFactory.newExpertAdvisorProtocol(exampleMessageBasedClientConnection,
                EXAMPLE_EXPERT_ADVISOR_NUMBER)).thenReturn(exampleExpertAdvisorProtocol);
        when(protocolFactory.newExceptionHandler(exampleClientConnection)).thenReturn(exampleExceptionHandler);
    }

    /**
     * When the client requests a trading algorithm of the type trend indicator, the {@link IndicatorProtocol}
     * should be used.
     * 
     * @throws CommunicationException
     *             not expected to leave the test.
     */
    @Test
    public void shouldCreateAndStartIndicatorProtocolWhenAnIndicatorIsRequested() throws CommunicationException {
        final RequestTradingAlgorithmMessage msg = new RequestTradingAlgorithmMessage(AlgorithmType.TREND_INDICATOR,
                EXAMPLE_INDICATOR_NUMBER);
        when(exampleMessageBasedClientConnection.readMessage(RequestTradingAlgorithmMessage.class)).thenReturn(msg);

        cut.newClientCommunicationHandler(exampleClientConnection).run();

        verify(exampleIndicatorProtocol).start();
        verifyNoMoreInteractions(exampleExceptionHandler);
    }

    /**
     * When the client request a trading algorithm of type expert advisor, the {@link ExpertAdvisorProtocol} should be
     * used.
     * 
     * @throws CommunicationException
     *             not expected to leave the test.
     */
    @Test
    public void shouldCreateAndStartExpertAdvisorProtocolWhenExpertAdvisorIsRequested() throws CommunicationException {
        final RequestTradingAlgorithmMessage msg = new RequestTradingAlgorithmMessage(AlgorithmType.EXPERT_ADVISOR,
                EXAMPLE_EXPERT_ADVISOR_NUMBER);
        when(exampleMessageBasedClientConnection.readMessage(RequestTradingAlgorithmMessage.class)).thenReturn(msg);

        cut.newClientCommunicationHandler(exampleClientConnection).run();

        verify(exampleExpertAdvisorProtocol).start();
        verifyNoMoreInteractions(exampleExceptionHandler);
    }

    /**
     * When the protocol execution ends with a {@link CommunicationException} it should be passed to the
     * {@link ExceptionHandler} for proper handling.
     * 
     * @throws CommunicationException
     *             not expected to leave the test.
     */
    @Test
    public void shouldPassCommunicationExceptionsToExceptionHandler() throws CommunicationException {
        final RequestTradingAlgorithmMessage msg = new RequestTradingAlgorithmMessage(AlgorithmType.TREND_INDICATOR,
                EXAMPLE_INDICATOR_NUMBER);
        final CommunicationException exampleCommunicationException = mock(CommunicationException.class);
        when(exampleMessageBasedClientConnection.readMessage(RequestTradingAlgorithmMessage.class)).thenReturn(msg);

        doThrow(exampleCommunicationException).when(exampleIndicatorProtocol).start();

        cut.newClientCommunicationHandler(exampleClientConnection).run();

        verify(exampleExceptionHandler).handleException(exampleCommunicationException);
    }

    /**
     * When the protocol execution ends with a {@link RuntimeException} it should be passed to the
     * {@link ExceptionHandler} for proper handling.
     * 
     * @throws CommunicationException
     *             not expected to leave the test.
     */
    @Test
    public void shouldPassRuntimeExceptionToExceptionHandler() throws CommunicationException {
        final RequestTradingAlgorithmMessage msg = new RequestTradingAlgorithmMessage(AlgorithmType.TREND_INDICATOR,
                EXAMPLE_INDICATOR_NUMBER);
        final RuntimeException exampleRuntimeException = new RuntimeException();
        when(exampleMessageBasedClientConnection.readMessage(RequestTradingAlgorithmMessage.class)).thenReturn(msg);

        doThrow(exampleRuntimeException).when(exampleIndicatorProtocol).start();

        cut.newClientCommunicationHandler(exampleClientConnection).run();

        verify(exampleExceptionHandler).handleException(exampleRuntimeException);
    }
}
