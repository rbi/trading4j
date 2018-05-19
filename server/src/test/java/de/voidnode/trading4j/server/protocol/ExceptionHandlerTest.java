package de.voidnode.trading4j.server.protocol;

import de.voidnode.trading4j.server.protocol.exceptions.AbnormalCloseException;
import de.voidnode.trading4j.server.protocol.exceptions.CommunicationException;
import de.voidnode.trading4j.server.protocol.exceptions.MessageReadException;
import de.voidnode.trading4j.server.protocol.exceptions.NormalCloseException;
import de.voidnode.trading4j.server.protocol.exceptions.ProtocolException;
import de.voidnode.trading4j.server.reporting.AdmininstratorNotifier;
import de.voidnode.trading4j.server.reporting.DeveloperNotifier;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests if {@link ExceptionHandler} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class ExceptionHandlerTest {

    @Mock
    private ClientConnection client;

    @Mock
    private AdmininstratorNotifier admin;

    @Mock
    private DeveloperNotifier developer;

    @InjectMocks
    private ExceptionHandler cut;

    /**
     * When the connection was terminated normally, the admin should be informed.
     */
    @Test
    public void shouldInformAdminOfNormalyTerminatedConnections() {
        cut.handleException(new NormalCloseException());

        final ArgumentCaptor<String> message = ArgumentCaptor.forClass(String.class);
        verify(admin).informalEvent(message.capture());
        assertThat(message.getValue()).contains("client").contains("connection").contains("close");
    }

    /**
     * When the connection was terminated abnormally, the admin should be warned.
     */
    @Test
    public void shouldWarnAdminOfUnexpectetlyClosedConnections() {
        final AbnormalCloseException exampleAbnormalException = new AbnormalCloseException();
        cut.handleException(exampleAbnormalException);

        final ArgumentCaptor<String> message = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Throwable> cause = ArgumentCaptor.forClass(Throwable.class);

        verify(admin).unexpectedEvent(message.capture(), cause.capture());
        assertThat(message.getValue()).contains("client").contains("connection").contains("close");
        assertThat(cause.getValue()).isSameAs(exampleAbnormalException);
    }

    /**
     * When a {@link ProtocolException} occurred the connection should be closed and the admin should be warned.
     * 
     * @throws Exception
     *             Not expected to leave the method.
     */
    @Test
    public void whenAProtocolExceptionOccurredTheConnectionShouldBeClosedAndTheAdminBeWarned() throws Exception {
        final ProtocolException exampleProtocolException = new ProtocolException("very bad");
        cut.handleException(exampleProtocolException);

        final ArgumentCaptor<String> message = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Throwable> cause = ArgumentCaptor.forClass(Throwable.class);

        verify(admin).unexpectedEvent(message.capture(), cause.capture());
        assertThat(message.getValue()).contains("protocol").contains("client").contains("connection")
                .containsIgnoringCase("closing");
        assertThat(cause.getValue()).isSameAs(exampleProtocolException);
        verify(client).close();
    }

    /**
     * When a {@link MessageReadException} occurred the connection should be closed and the admin should be warned.
     * 
     * @throws Exception
     *             Not expected to leave the method.
     */
    @Test
    public void whenAMessageReadExceptionOccurredTheConnectionShouldBeClosedAndTheAdminBeWarned() throws Exception {
        final MessageReadException exampleMessageReadException = new MessageReadException("even worse");
        cut.handleException(exampleMessageReadException);

        final ArgumentCaptor<String> message = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Throwable> cause = ArgumentCaptor.forClass(Throwable.class);

        verify(admin).unexpectedEvent(message.capture(), cause.capture());
        assertThat(message.getValue()).contains("message").contains("client").contains("connection")
                .containsIgnoringCase("closing");
        assertThat(cause.getValue()).isSameAs(exampleMessageReadException);
        verify(client).close();
    }

    /**
     * When any {@link RuntimeException} occurs, the connection should be closed, the developer should be notified on
     * this error and the admin should be warned.
     * 
     * @throws Exception
     *             Not expected to leave the method.
     */
    @Test
    public void whenARuntimeExceptionOccurredTheConnectionShouldBeClosedAndTheDeveloperBeWarned() throws Exception {
        final RuntimeException exampleRuntimeException = new RuntimeException(
                "The answer to everything could not be found.");
        cut.handleException(exampleRuntimeException);

        final ArgumentCaptor<Throwable> cause = ArgumentCaptor.forClass(Throwable.class);

        final ArgumentCaptor<String> developerMessage = ArgumentCaptor.forClass(String.class);
        verify(developer).unrecoverableProgrammingError(developerMessage.capture(), cause.capture());
        assertThat(developerMessage.getValue()).contains("runtime").contains("exception").contains("unhandled");
        assertThat(cause.getValue()).isSameAs(exampleRuntimeException);

        final ArgumentCaptor<String> adminMessage = ArgumentCaptor.forClass(String.class);
        verify(admin).unexpectedEvent(adminMessage.capture());
        assertThat(adminMessage.getValue()).contains("internal").contains("server").contains("client")
                .contains("connection").containsIgnoringCase("closing");

        verify(client).close();
    }

    /**
     * When closing the connection failed the admin should be warned.
     * 
     * @throws Exception
     *             Not expected to leave the method.
     */
    @Test
    public void whenClosingTheConnectionFailedTheAdminShouldBeWarned() throws Exception {
        final RuntimeException closeException = new RuntimeException("418 I'm a teapot");
        doThrow(closeException).when(client).close();
        final MessageReadException exampleMessageReadException = new MessageReadException("worst error ever");
        cut.handleException(exampleMessageReadException);

        final ArgumentCaptor<String> message = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Throwable> cause = ArgumentCaptor.forClass(Throwable.class);

        final InOrder inOrder = Mockito.inOrder(admin, client);
        // the original exception
        inOrder.verify(admin, times(1)).unexpectedEvent(any(), any());
        inOrder.verify(client).close();
        // the exception for the failed connection close.
        inOrder.verify(admin, times(1)).unexpectedEvent(message.capture(), cause.capture());
        assertThat(message.getValue()).contains("close").contains("connection").contains("failed");
        assertThat(cause.getValue()).isSameAs(closeException);
    }

    /**
     * When the {@link ExceptionHandler} should handle an unknown {@link CommunicationException} the developer should be
     * notified of this error, the admin should be warned and the connection should be closed.
     * 
     * @throws Exception
     *             Not expected to leave the method.
     */
    @Test
    public void whenAnUnknownCommunicationExceptionShouldBeHandledTheDeveloperShouldBeWarned() throws Exception {
        final UnknownCommunicationException exampleUnknownCommunicationException = new UnknownCommunicationException();
        cut.handleException(exampleUnknownCommunicationException);

        final ArgumentCaptor<String> developerMessage = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> adminMessage = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Throwable> cause = ArgumentCaptor.forClass(Throwable.class);

        verify(developer).unrecoverableProgrammingError(developerMessage.capture(), cause.capture());
        assertThat(developerMessage.getValue()).contains("unknown").containsIgnoringCase("communication")
                .containsIgnoringCase("exception");
        assertThat(cause.getValue()).isSameAs(exampleUnknownCommunicationException);

        verify(admin).unexpectedEvent(adminMessage.capture());
        assertThat(adminMessage.getValue()).contains("communication").contains("client").contains("connection")
                .containsIgnoringCase("closing");
        verify(client).close();
    }

    /**
     * An exemplary communication exception that is unknown to the {@link ExceptionHandler}.
     */
    private static class UnknownCommunicationException extends CommunicationException {
        private static final long serialVersionUID = 1L;
    }
}
