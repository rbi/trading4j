package de.voidnode.trading4j.server.reporting.implementations;

import java.util.Optional;

import de.voidnode.trading4j.domain.trades.CompletedTrade;
import de.voidnode.trading4j.server.reporting.AdmininstratorNotifier;
import de.voidnode.trading4j.server.reporting.implementations.MailSender.MailSendingException;
import de.voidnode.trading4j.tradetracker.CompletedTradeMarkDownFormater;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Checks if {@link MailNotifier} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class MailNotifierTest {

    private static final String SOME_SENDER_ADDRESS = "any@bo.dy";
    private static final String SOME_RECIVER_ADDRESS = "some@one.de";
    private static final String SOME_TEXT = "some body";

    @Mock
    private MailSender mailSender;

    @Mock
    private CompletedTradeMarkDownFormater formater;

    @Mock
    private AdmininstratorNotifier adminNotifier;

    private MailNotifier cut;

    @Mock
    private CompletedTrade someTrade;

    @Mock
    private MailSendingException someException;

    /**
     * Initializes the cut.
     */
    @Before
    public void setUpCut() {
        when(someTrade.getRelativeProfit()).thenReturn(Optional.empty());
        when(formater.format(someTrade)).thenReturn(SOME_TEXT);
        cut = new MailNotifier(mailSender, formater, adminNotifier, SOME_SENDER_ADDRESS, SOME_RECIVER_ADDRESS);
    }

    /**
     * Trade events are send to the configured E-Mail address.
     * 
     * @throws MailSendingException
     *             Not expected in the test.
     */
    @Test
    public void traderEventsAreSendToConfiguredMailAddress() throws MailSendingException {
        cut.tradeCompleted(someTrade);

        verify(mailSender).sendMail(eq(SOME_SENDER_ADDRESS), eq(SOME_RECIVER_ADDRESS), anyString(), eq(SOME_TEXT));
    }

    /**
     * Unexpected trade events are send to the configured mail address.
     * 
     * @throws MailSendingException
     *             Not expected in the test.
     */
    @Test
    public void unexpectedTradeEventsAreSendToConfiguredMailAddress() throws MailSendingException {
        cut.unexpectedEvent(SOME_TEXT);
        
        verify(mailSender).sendMail(eq(SOME_SENDER_ADDRESS), eq(SOME_RECIVER_ADDRESS), anyString(), eq(SOME_TEXT));
    }

    /**
     * When sending an email fails the administrator is notified.
     * 
     * @throws MailSendingException
     *             Not expected to leve the test
     */
    @Test
    public void errorIsSendToTheAdministratorWhenSendingEmailsFailed() throws MailSendingException {
        doThrow(someException).when(mailSender).sendMail(anyString(), anyString(), anyString(), anyString());

        cut.tradeCompleted(someTrade);

        verify(adminNotifier).unexpectedEvent(anyString(), eq(someException));
    }
}
