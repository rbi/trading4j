package de.voidnode.trading4j.server.reporting.implementations;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Sends E-mails.
 * 
 * @author Raik Bieniek
 */
class MailSender {

    private final Session session;

    /**
     * Initializes an instance with the default port.
     * 
     * @param mailServer
     *            The mail server that should be used to send mails.
     */
    MailSender(final String mailServer) {
        this(mailServer, 25);
    }

    /**
     * Initializes an instance with all required configuration.
     * 
     * @param mailServer
     *            The mail server that should be used to send mails.
     * @param port
     *            The SMTP port at the mail server that should be accessed.
     */
    MailSender(final String mailServer, final int port) {
        final Properties config = new Properties();
        config.setProperty("mail.smtp.host", mailServer);
        config.setProperty("mail.smtp.port", Integer.toString(port));
        this.session = Session.getInstance(config);
    }

    /**
     * Sends an email.
     * 
     * @param from
     *            The Email-address from the sender of this mail.
     * @param to
     *            The Email-address of the receiver of this mail.
     * @param subject
     *            The subject of the mail.
     * @param body
     *            The content of the mail.
     * @throws MailSendingException
     *             When sending the mail failed.
     */
    public void sendMail(final String from, final String to, final String subject, final String body)
            throws MailSendingException {
        final MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSentDate(new Date());
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
        } catch (final MessagingException e) {
            throw new MailSendingException("Sending an email failed.", e);
        }

    }

    /**
     * Indicates that sending a mail has failed.
     */
    public static class MailSendingException extends Exception {
        private static final long serialVersionUID = 1L;

        /**
         * Initializes the exception.
         * 
         * @param message
         *            The message describing the problem.
         * @param cause
         *            The causing exception.
         */
        MailSendingException(final String message, final MessagingException cause) {
            super(message, cause);
        }
    }
}
