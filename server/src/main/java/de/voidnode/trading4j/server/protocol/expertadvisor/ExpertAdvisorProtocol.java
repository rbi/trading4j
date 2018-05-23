package de.voidnode.trading4j.server.protocol.expertadvisor;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.ExpertAdvisor;
import de.voidnode.trading4j.moneymanagement.SharedMoneyManagement.ReleasableMoneyManagement;
import de.voidnode.trading4j.server.protocol.MessageBasedClientConnection;
import de.voidnode.trading4j.server.protocol.ProtocolFactory;
import de.voidnode.trading4j.server.protocol.exceptions.CommunicationException;
import de.voidnode.trading4j.server.protocol.exceptions.ProtocolException;
import de.voidnode.trading4j.server.protocol.messages.EventHandlingFinishedMessage;
import de.voidnode.trading4j.server.protocol.messages.TradingEnvironmentInformationMessage;

/**
 * A network protocol for the communication between a {@link Broker} on the remote side and an {@link ExpertAdvisor} on
 * the local side.
 * 
 * <p>
 * One instance handles the communication to a single client.
 * </p>
 * 
 * <p>
 * This protocol is basically a loop that reads messages from a {@link MessageBasedClientConnection}. Messages received
 * are executed by invoking the appropriate methods of an {@link ExpertAdvisor}. {@link ExpertAdvisor}s can use this
 * {@link Broker} implementation to react on the received events.
 * </p>
 * 
 * @author Raik Bieniek
 */
public class ExpertAdvisorProtocol {

    private final MessageBasedClientConnection clientConnection;
    private final ProtocolFactory factory;
    private final int expertAdvisorNumber;

    private LocalExpertAdvisor expertAdvisor;

    /**
     * Initializes the protocol.
     * 
     * @param clientConnection
     *            Used to communicate with the remote broker.
     * @param expertAdvisorNumber
     *            The number for the {@link ExpertAdvisor} requested by the client.
     * @param protocolFactory
     *            Used to create the {@link LocalExpertAdvisor} to serve the {@link ExpertAdvisor} requested by the
     *            client.
     */
    public ExpertAdvisorProtocol(final MessageBasedClientConnection clientConnection,
            final ProtocolFactory protocolFactory, final int expertAdvisorNumber) {
        this.clientConnection = clientConnection;
        this.factory = protocolFactory;
        this.expertAdvisorNumber = expertAdvisorNumber;
    }

    /**
     * Starts the execution of the protocol.
     * 
     * @throws CommunicationException
     *             When a network error occurred or the client closed the connection.
     */
    public void start() throws CommunicationException {
        final TradingEnvironmentInformationMessage environment = clientConnection
                .readMessage(TradingEnvironmentInformationMessage.class);

        final ReleasableMoneyManagement moneyManagement = factory.newSharedMoneyManagementInstance();
        expertAdvisor = factory
                .newLocalExpertAdvisorByNumber(expertAdvisorNumber, clientConnection, moneyManagement,
                        environment.getInformation())
                .orElseThrow(() -> new ProtocolException("Recieved an request for the expert advisor with the number "
                        + expertAdvisorNumber + " which is unknown."));

        while (true) {
            try {
                expertAdvisor.handleMessage(clientConnection.readMessage());
                clientConnection.sendMessage(new EventHandlingFinishedMessage());
            } catch (LoopThroughCommunicationException e) {
                moneyManagement.realeaseAllAquieredVolume();
                throw e.getWrappedException();
            } catch (final LoopThroughIllegalStateException e) {
                moneyManagement.realeaseAllAquieredVolume();
                throw e.getWrappedException();
            } catch (final Exception e) {
                moneyManagement.realeaseAllAquieredVolume();
                throw e;
            }
        }
    }
}
