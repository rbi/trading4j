package de.voidnode.trading4j.server.protocol;

import static java.lang.String.format;

import de.voidnode.trading4j.server.protocol.exceptions.AbnormalCloseException;
import de.voidnode.trading4j.server.protocol.exceptions.CommunicationException;
import de.voidnode.trading4j.server.protocol.exceptions.MessageReadException;
import de.voidnode.trading4j.server.protocol.exceptions.NormalCloseException;
import de.voidnode.trading4j.server.protocol.exceptions.ProtocolException;
import de.voidnode.trading4j.server.reporting.AdmininstratorNotifier;
import de.voidnode.trading4j.server.reporting.DeveloperNotifier;

/**
 * Dispatches different types of communication {@link Exception}s to the parties interested in them and terminates the
 * communication if neccessary.
 * 
 * @author Raik Bieniek
 */
class ExceptionHandler {

    private final ClientConnection client;
    private final AdmininstratorNotifier admin;
    private final DeveloperNotifier developer;

    /**
     * Initializes the exception handler.
     * 
     * @param client
     *            The connection to the client for which the {@link Exception} occurred.
     * @param admin
     *            Used to inform on events the administrator is interested in.
     * @param developer
     *            Used to inform on events the developer is interested in.
     */
    ExceptionHandler(final ClientConnection client, final AdmininstratorNotifier admin,
            final DeveloperNotifier developer) {
        this.client = client;
        this.admin = admin;
        this.developer = developer;
    }

    /**
     * Handles a {@link CommunicationException}.
     * 
     * @param exception
     *            The exception to handle.
     */
    public void handleException(final CommunicationException exception) {
        if (exception instanceof NormalCloseException) {
            admin.informalEvent(format("The connection to the client '%s' was closed.", client));
        } else if (exception instanceof AbnormalCloseException) {
            admin.unexpectedEvent(format("The connection to the client '%s' was closed unexpectetly.", client),
                    exception);
        } else if (exception instanceof ProtocolException) {
            admin.unexpectedEvent(
                    format("A violation of the communication protocol with client '%s' occurred. Closing the connection to that client.",
                            client), exception);
            closeConnection();
        } else if (exception instanceof MessageReadException) {
            admin.unexpectedEvent(
                    format("Failed to read an expected message from the connection to the client '%s'. Closing the connection to that client.",
                            client), exception);
            closeConnection();
        } else {
            developer.unrecoverableProgrammingError(
                    "A CommunicationException occurred that is unknown to the ExceptionHandler.", exception);
            admin.unexpectedEvent(format(
                    "An unspecified error in the communication with the client '%s' occurred. Closing the connection to this client.",
                    client));
            closeConnection();
        }
    }

    /**
     * Handles a {@link RuntimeException}.
     * 
     * @param exception
     *            The exception to handle.
     */
    public void handleException(final RuntimeException exception) {
        developer.unrecoverableProgrammingError("An unhandled runtime exception occured: ", exception);
        admin.unexpectedEvent(format(
                "An internal server error occured in the communication with the client '%s'. Closing the connection to this client.",
                client));
        closeConnection();
    }

    private void closeConnection() {
        try {
            client.close();
            admin.informalEvent(format("The connection to the client '%s' was closed.", client));
        } catch (final Exception e) {
            admin.unexpectedEvent(
                    format("Closing the connection to the client '%s' failed. Assuming it is closed anyway.", client),
                    e);
        }
    }
}
