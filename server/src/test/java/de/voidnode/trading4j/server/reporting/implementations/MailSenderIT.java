package de.voidnode.trading4j.server.reporting.implementations;

import java.text.ParseException;
import java.time.Instant;

import static java.time.ZoneOffset.UTC;

import javax.mail.internet.MailDateFormat;

import de.voidnode.trading4j.server.reporting.implementations.MailSender.MailSendingException;

import com.dumbster.smtp.MailMessage;
import com.dumbster.smtp.ServerOptions;
import com.dumbster.smtp.SmtpServer;
import com.dumbster.smtp.SmtpServerFactory;
import org.junit.After;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link MailSender} works as expected.
 * 
 * @author Raik Bieniek
 */
public class MailSenderIT {

    private static final int TEST_PORT = 18429;

    private SmtpServer server;

    /**
     * Shuts the test mail server down.
     */
    @After
    public void stopFailMailServer() {
        if (server != null) {
            server.stop();
        }
    }

    /**
     * The cut can send mails.
     * 
     * @throws MailSendingException
     *             not expected to leave the test.
     * @throws ParseException
     *             When the date of the mail could not be parsed.
     */
    @Test
    public void canSendMails() throws MailSendingException, ParseException {
        final ServerOptions options = new ServerOptions();
        options.port = TEST_PORT;
        server = SmtpServerFactory.startServer(options);
        final MailSender cut = new MailSender("localhost", TEST_PORT);
        final Instant beforeSent = Instant.now();

        cut.sendMail("some@body", "some@one", "some subject", "some text");

        assertThat(server.getEmailCount()).isEqualTo(1);
        final MailMessage mail = server.getMessage(0);
        assertThat(mail.getFirstHeaderValue("From")).isEqualTo("some@body");
        assertThat(mail.getFirstHeaderValue("To")).isEqualTo("some@one");
        assertThat(mail.getFirstHeaderValue("Subject")).isEqualTo("some subject");
        assertThat(mail.getBody()).isEqualTo("some text");

        assertCorrectDate(mail.getFirstHeaderValue("Date"), beforeSent, Instant.now());
    }

    private void assertCorrectDate(final String mailDate, final Instant minimalRaw, final Instant maximalRaw)
            throws ParseException {
        final Instant actual = new MailDateFormat().parse(mailDate).toInstant();

        final Instant minimal = minimalRaw.atOffset(UTC).withNano(0).minusNanos(1).toInstant();
        final Instant maximal = maximalRaw.atOffset(UTC).withNano(1).plusSeconds(1).toInstant();
        assertThat(actual.isAfter(minimal))
                .overridingErrorMessage("The date of the mail should be after %s but was %s.", minimal, actual)
                .isTrue();
        assertThat(actual.isBefore(maximal))
                .overridingErrorMessage("The date of the mail should be before %s but was %s.", maximal, actual)
                .isTrue();
    }

    /**
     * The cut throws an exception when sending a mail failed.
     * 
     * @throws MailSendingException
     *             The expected failure.
     */
    @Test(expected = MailSendingException.class)
    public void throwsAnExceptionWhenSendingAMailFailed() throws MailSendingException {
        final MailSender cut = new MailSender("localhost", TEST_PORT + 1);

        cut.sendMail("some@body", "some@one", "some subject", "some text");
    }
}
