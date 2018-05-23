package de.voidnode.trading4j.server.protocol;

import java.util.Optional;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.ExpertAdvisor;
import de.voidnode.trading4j.api.ExpertAdvisorFactory;
import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.api.MoneyManagement;
import de.voidnode.trading4j.api.TrendIndicatorFactory;
import de.voidnode.trading4j.domain.environment.TradingEnvironmentInformation;
import de.voidnode.trading4j.moneymanagement.SharedMoneyManagement;
import de.voidnode.trading4j.moneymanagement.SharedMoneyManagement.ReleasableMoneyManagement;
import de.voidnode.trading4j.server.protocol.expertadvisor.ExpertAdvisorProtocol;
import de.voidnode.trading4j.server.protocol.expertadvisor.LocalExpertAdvisor;
import de.voidnode.trading4j.server.protocol.expertadvisor.PendingOrderMapper;
import de.voidnode.trading4j.server.protocol.expertadvisor.RemoteBroker;
import de.voidnode.trading4j.server.reporting.CombinedNotifier;

/**
 * Creates {@link ClientConnection} scoped objects necessary for the communication with the client.
 * 
 * @author Raik Bieniek
 */
public class ProtocolFactory {

    private final CombinedNotifier fullNotifier;
    private final TrendIndicatorFactory indicatorFactory;
    private final ExpertAdvisorFactory expertAdvisorFactory;
    private final SharedMoneyManagement moneyManagement;

    /**
     * Initializes the factory with the dependencies that are independent of the concrete {@link ClientConnection}.
     * 
     * @param indicatorFactory
     *            Used to create new {@link Indicator}s.
     * @param expertAdvisorFactory
     *            Used to create new {@link ExpertAdvisor}s.
     * @param moneyManagement
     *            Used to manage the amount of money that is invested in each trade.
     * @param notifier
     *            Used to notify on different events.
     */
    public ProtocolFactory(final TrendIndicatorFactory indicatorFactory,
            final ExpertAdvisorFactory expertAdvisorFactory, final SharedMoneyManagement moneyManagement,
            final CombinedNotifier notifier) {
        this.indicatorFactory = indicatorFactory;
        this.expertAdvisorFactory = expertAdvisorFactory;
        this.moneyManagement = moneyManagement;
        this.fullNotifier = notifier;
    }

    /**
     * Creates a new {@link MessageBasedClientConnection} for a given {@link ClientConnection}.
     * 
     * @param clientConnection
     *            The connection to wrap.
     * @return The message based version
     */
    public MessageBasedClientConnection newMessageBasedClientConnection(final ClientConnection clientConnection) {
        return new MessageBasedClientConnection(clientConnection);
    }

    /**
     * Creates a new {@link IndicatorProtocol} handler for a given client.
     * 
     * @param clientConnection
     *            The client with that the {@link IndicatorProtocol} should handle the communication.
     * @param indicatorNumber
     *            The number of the indicator that was requested by the user.
     * @return The protocol handler.
     */
    public IndicatorProtocol newIndicatorProtocol(final MessageBasedClientConnection clientConnection,
            final int indicatorNumber) {
        return new IndicatorProtocol(clientConnection, indicatorFactory, indicatorNumber);
    }

    /**
     * Creates a new {@link ExpertAdvisorProtocol} handler for the given client.
     * 
     * @param clientConnection
     *            The client with that the {@link ExpertAdvisorProtocol} should handle the communication.
     * @param expertAdvisorNumber
     *            The number of the {@link ExpertAdvisor} that was requested by the user.
     * @return The protocol handler.
     */
    public ExpertAdvisorProtocol newExpertAdvisorProtocol(final MessageBasedClientConnection clientConnection,
            final int expertAdvisorNumber) {
        return new ExpertAdvisorProtocol(clientConnection, this, expertAdvisorNumber);
    }

    /**
     * Creates a handler for potential {@link Exception}s that can occur during the communication with the client.
     * 
     * @param clientConnection
     *            The client thats occurring {@link Exception}s should be handled.
     * @return The {@link ExceptionHandler}.
     */
    public ExceptionHandler newExceptionHandler(final ClientConnection clientConnection) {
        return new ExceptionHandler(clientConnection, fullNotifier, fullNotifier);
    }

    /**
     * Creates a new {@link LocalExpertAdvisor} for the given number if the given number is assigned to any
     * {@link ExpertAdvisor}.
     * 
     * @param expertAdvisorNumber
     *            The number of the requested expert advisor.
     * @param clientConnection
     *            The connection to the remote {@link Broker}.
     * @param moneyManagement
     *            The {@link MoneyManagement} that should be used for the new expert advisor. Usually that should be
     *            created with {@link #newSharedMoneyManagementInstance()}.
     * @param information
     *            Basic information about the state of the remote {@link Broker}.
     * @return The {@link LocalExpertAdvisor} wrapping the {@link ExpertAdvisor} for the given number if there is any
     *         {@link ExpertAdvisor} assigned to this number and an empty {@link Optional} if not.
     */
    public Optional<LocalExpertAdvisor> newLocalExpertAdvisorByNumber(final int expertAdvisorNumber,
            final MessageBasedClientConnection clientConnection, final MoneyManagement moneyManagement,
            final TradingEnvironmentInformation information) {
        final PendingOrderMapper orderMapper = new PendingOrderMapper();
        final RemoteBroker broker = new RemoteBroker(clientConnection, orderMapper);

        return expertAdvisorFactory.newExpertAdvisor(expertAdvisorNumber, broker, moneyManagement, information)
                .map(ea -> new LocalExpertAdvisor(ea, moneyManagement, orderMapper,
                        information.getAccountInformation().getAccountCurrency(), information.getAccountSymbol()));
    }

    /**
     * Creates a new instance that may request and return volume from the shared {@link MoneyManagement}.
     * 
     * @return The new instance
     */
    public ReleasableMoneyManagement newSharedMoneyManagementInstance() {
        return moneyManagement.newConnection();
    }
}
