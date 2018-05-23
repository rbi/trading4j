package de.voidnode.trading4j.server.protocol;

import de.voidnode.trading4j.server.protocol.exceptions.CommunicationException;
import de.voidnode.trading4j.server.protocol.exceptions.ProtocolException;
import de.voidnode.trading4j.server.protocol.messages.RequestTradingAlgorithmMessage;

/**
 * Handles the communication to newly connected client.
 * 
 * @author Raik Bieniek
 */
public class ClientCommunicator {

    private final ProtocolFactory protocolFactory;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param protocolFactory
     *            Used to create the concrete protocol depending on the user selection.
     */
    public ClientCommunicator(final ProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
    }

    /**
     * Creates a handler for the communication with a new client.
     * 
     * @param client
     *            The connecting client
     * @return The runnable that starts the communication with the client.
     */
    public Runnable newClientCommunicationHandler(final ClientConnection client) {
        final MessageBasedClientConnection messageBasedClient = protocolFactory.newMessageBasedClientConnection(client);
        return () -> {
            try {
                final RequestTradingAlgorithmMessage algorithmMessage = messageBasedClient
                        .readMessage(RequestTradingAlgorithmMessage.class);
                switch (algorithmMessage.getAlgorithmType()) {
                    case TREND_INDICATOR:
                        protocolFactory.newIndicatorProtocol(messageBasedClient, algorithmMessage.getAlgorithmNumber())
                                .start();
                        break;
                    case EXPERT_ADVISOR:
                        protocolFactory
                                .newExpertAdvisorProtocol(messageBasedClient, algorithmMessage.getAlgorithmNumber())
                                .start();
                        break;
                    default:
                        throw new ProtocolException("Trading algorithms of type " + algorithmMessage.getAlgorithmType()
                                + " are not supported by this server.");

                }
            } catch (final CommunicationException e) {
                protocolFactory.newExceptionHandler(client).handleException(e);
            } catch (final RuntimeException e) {
                protocolFactory.newExceptionHandler(client).handleException(e);
            }
        };
    }
}
