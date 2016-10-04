package de.voidnode.trading4j.server.reporting.implementations;

import de.voidnode.trading4j.domain.trades.CompletedTrade;
import de.voidnode.trading4j.server.reporting.AdmininstratorNotifier;
import de.voidnode.trading4j.server.reporting.TraderNotifier;
import de.voidnode.trading4j.server.reporting.implementations.MailSender.MailSendingException;
import de.voidnode.trading4j.tradetracker.CompletedTradeMarkDownFormater;

/**
 * Sends events for traders as mail.
 * 
 * @author Raik Bieniek
 */
class MailNotifier implements TraderNotifier {

    private final MailSender mailSender;
    private final CompletedTradeMarkDownFormater formater;
    private final AdmininstratorNotifier admin;
    private final String receiver;
    private String sender;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param mailSender
     *            The sender for mails that should be used.
     * @param formater
     *            Used to format incoming {@link CompletedTrade}s for the user.
     * @param admin
     *            Used to inform the administrator when sending mails fails.
     * @param sender
     *          The Email-address that should be used as sender address of the mails.
     * @param receiver
     *            The Email-address of the receiver of the mails.
     */
    MailNotifier(final MailSender mailSender, final CompletedTradeMarkDownFormater formater,
            final AdmininstratorNotifier admin, final String sender, final String receiver) {
        this.mailSender = mailSender;
        this.formater = formater;
        this.admin = admin;
        this.sender = sender;
        this.receiver = receiver;
    }

    @Override
    public void tradeCompleted(final CompletedTrade trade) {
        final String subject = "Trade finished (" + trade.getType() + " " + trade.getSymbol() + " "
                + trade.getRelativeProfit().map(profit -> profit.toStringWithSign()).orElse("canceled") + ")";

        safeSendMail(subject, formater.format(trade));
    }

    private void safeSendMail(final String subject, final String message) {
        try {
            mailSender.sendMail(sender, receiver, subject, message);
        } catch (final MailSendingException e) {
            admin.unexpectedEvent("Sending an email failed.", e);
        }
    }

    @Override
    public void unexpectedEvent(final String message) {
        safeSendMail("UNEXPECTED TRADE EVENT", message);
    }
}
