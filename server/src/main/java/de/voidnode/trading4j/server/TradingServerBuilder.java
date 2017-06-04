package de.voidnode.trading4j.server;

import java.util.Optional;

import de.voidnode.trading4j.api.BasicExpertAdvisorFactory;
import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.ExpertAdvisor;
import de.voidnode.trading4j.api.ExpertAdvisorFactory;
import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.api.MoneyManagement;
import de.voidnode.trading4j.api.TrendIndicatorFactory;
import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.environment.TradingEnvironmentInformation;
import de.voidnode.trading4j.domain.marketdata.impl.DatedCandleStick;
import de.voidnode.trading4j.domain.marketdata.impl.FullMarketData;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;
import de.voidnode.trading4j.domain.timeframe.M1;
import de.voidnode.trading4j.expertadvisorfactory.DefaultExpertAdvisorFactory;
import de.voidnode.trading4j.moneymanagement.SharedMoneyManagement;
import de.voidnode.trading4j.moneymanagement.ThreadSafeMoneyManagement;
import de.voidnode.trading4j.moneymanagement.standard.DefaultMoneyManagement;
import de.voidnode.trading4j.server.oio.OioServer;
import de.voidnode.trading4j.server.protocol.ClientCommunicator;
import de.voidnode.trading4j.server.protocol.ProtocolFactory;
import de.voidnode.trading4j.server.reporting.CombinedNotifier;
import de.voidnode.trading4j.server.reporting.implementations.NotifierFactory;

/**
 * Sets up a server for the trading strategies.
 * 
 * @author Raik Bieniek
 */
public class TradingServerBuilder {

    private NotifierFactory notifierFactory = new NotifierFactory();

    private BasicExpertAdvisorFactory expertAdvisors;
    private TrendIndicatorFactory trendIndicators;
    private CombinedNotifier emailNotifier;
    private MoneyManagement moneyManagement;

    /**
     * The expert advisors that should be served by the server.
     *
     * <p>
     * The factory that is passed in will be wrapped with {@link DefaultExpertAdvisorFactory}. See JavaDoc of
     * {@link DefaultExpertAdvisorFactory} for a description of the features this adds to the factory that is passed in.
     * </p>
     * 
     * @param expertAdvisors
     *            The factory for the expert advisors that should be served.
     * @return This builder for a fluent API.
     */
    public TradingServerBuilder expertAdvisors(final BasicExpertAdvisorFactory expertAdvisors) {
        this.expertAdvisors = expertAdvisors;
        return this;
    }

    /**
     * The trend indicators that should be served by the server.
     * 
     * @param trendIndicators
     *            The factory for the trend indicators that should be served.
     * @return This builder for a fluent API.
     */
    public TradingServerBuilder trendIndicators(final TrendIndicatorFactory trendIndicators) {
        this.trendIndicators = trendIndicators;
        return this;
    }

    /**
     * The money management that should be used for managing the money {@link ExpertAdvisor}s are allowed to trade.
     * 
     * <p>
     * The same {@link MoneyManagement} instance will be used for all {@link ExpertAdvisor}s that are created by
     * {@link #expertAdvisors(BasicExpertAdvisorFactory) expert advisor factory}. It is guaranteed that only one thread
     * will access the {@link MoneyManagement} at the same time.
     * </p>
     * 
     * <p>
     * If a money management is not explicitly configured, the {@link DefaultMoneyManagement} will be used.
     * </p>
     * 
     * @param moneyManagement
     *            The money management that should be used.
     * @return This builder for a fluent API.
     */
    public TradingServerBuilder moneyManagement(final MoneyManagement moneyManagement) {
        this.moneyManagement = moneyManagement;
        return this;
    }

    /**
     * Sends an email for every completed trade during live trading.
     * 
     * <p>
     * During backtesting no emails are send. To use this feature ensure that you add a dependency to the javax.mail API
     * and an implementation to your project.
     * </p>
     * 
     * @param server
     *            The SMTP server that should be used to send emails. This must be a DNS name or IP address. Optionally
     *            a port can be specified by separating it with a : from the server name.
     * @param from
     *            The email address that should be used in the "from" field.
     * @param to
     *            The email address to which the emails should be send.
     * @return This builder for a fluent API.
     */
    public TradingServerBuilder sendEmailOnCompletedTrades(final String server, final String from, final String to) {
        emailNotifier = notifierFactory.createMailAndConsoleNotifier(server, from, to);
        return this;
    }

    /**
     * Builds the server that was configured with this builder.
     * 
     * @return The built server.
     */
    public OioServer build() {
        final CombinedNotifier consoleOnlyNotifier = notifierFactory.getConsoleOnlyNotifier();
        final CombinedNotifier fullNotifier = emailNotifier != null ? emailNotifier : consoleOnlyNotifier;

        final SharedMoneyManagement moneyManagement = new SharedMoneyManagement(
                new ThreadSafeMoneyManagement(getOrCreateMoneyManagement()), consoleOnlyNotifier);

        final ExpertAdvisorFactory expertAdvisorFactory = new DefaultExpertAdvisorFactory(
                getOrCreateBasicExpertAdvisorFactory());
        final ExpertAdvisorFactory loggingExpertAdvisorFactory = new TradeTrackingExpertAdvisorFactory(
                expertAdvisorFactory, fullNotifier, consoleOnlyNotifier);

        final ProtocolFactory protocolFactory = new ProtocolFactory(getOrCreateIndicatorFactory(),
                loggingExpertAdvisorFactory, moneyManagement, fullNotifier);
        final ClientCommunicator clientCommunicator = new ClientCommunicator(protocolFactory);

        return new OioServer(clientCommunicator, fullNotifier);
    }

    private BasicExpertAdvisorFactory getOrCreateBasicExpertAdvisorFactory() {
        return this.expertAdvisors != null ? this.expertAdvisors : new NoExpertAdvisorsFactory();
    }

    private TrendIndicatorFactory getOrCreateIndicatorFactory() {
        return this.trendIndicators != null ? this.trendIndicators : new NoExpertAdvisorsFactory();
    }

    private MoneyManagement getOrCreateMoneyManagement() {
        return this.moneyManagement != null ? this.moneyManagement : new DefaultMoneyManagement();
    }

    /**
     * A factory that builds no indicators and expert advisors at all.
     */
    private static class NoExpertAdvisorsFactory implements BasicExpertAdvisorFactory, TrendIndicatorFactory {

        @Override
        public Optional<ExpertAdvisor<FullMarketData<M1>>> newExpertAdvisor(final int expertAdvisorNumber,
                final Broker<BasicPendingOrder> broker, final TradingEnvironmentInformation environment) {
            return Optional.empty();
        }

        @Override
        public Optional<Indicator<MarketDirection, DatedCandleStick<M1>>> newIndicatorByNumber(
                final int indicatorNumber) {
            return Optional.empty();
        }
    }
}
