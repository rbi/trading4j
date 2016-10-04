package de.voidnode.trading4j.server.reporting.implementations;

import java.time.ZoneId;
import java.util.Optional;

import static java.util.Arrays.asList;

import de.voidnode.trading4j.server.reporting.CombinedNotifier;
import de.voidnode.trading4j.server.reporting.TraderNotifier;
import de.voidnode.trading4j.tradetracker.CompletedTradeMarkDownFormater;

/**
 * Creates different kinds of event notifiers.
 * 
 * @author Raik Bieniek
 */
public class NotifierFactory {

    private final CombinedNotifier consoleNotifier;

    /**
     * Initializes all notifiers that may be produced.
     */
    public NotifierFactory() {
        final Optional<CombinedNotifier> console = Optional.of(new ConsoleNotifier());
        consoleNotifier = new BackgroundSerializingNotifier(console, console, console);
    }

    /**
     * A notifier that prints most information to the console and sends important information per mail.
     * 
     * @param server
     *            The SMTP server that should be used to send emails. This must be a DNS name or IP address. Optionally
     *            a port can be specified by separating it with a : from the server name.
     * @param from
     *            The email address that should be used in the "from" field.
     * @param to
     *            The email address to which the emails should be send.
     * @return The notifier
     */
    public CombinedNotifier createMailAndConsoleNotifier(final String server, final String from, final String to) {
        final Optional<TraderNotifier> mail = Optional.of(new MailNotifier(new MailSender(server),
                new CompletedTradeMarkDownFormater(ZoneId.systemDefault()), consoleNotifier, from, to));
        final CombinedNotifier mailNotifier = new BackgroundSerializingNotifier(mail, Optional.empty(), Optional.empty());
        

        return new CombiningNotifier(asList(consoleNotifier, mailNotifier), asList(consoleNotifier),
                asList(consoleNotifier)); 
    }

    /**
     * A notifier that prints information only to the console.
     * 
     * @return The notifier.
     */
    public CombinedNotifier getConsoleOnlyNotifier() {
        return consoleNotifier;
    }
}
