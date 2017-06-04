package de.voidnode.trading4j.expertadvisorfactory;

import java.util.Optional;

import de.voidnode.trading4j.api.BasicExpertAdvisorFactory;
import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.ExpertAdvisor;
import de.voidnode.trading4j.api.ExpertAdvisorFactory;
import de.voidnode.trading4j.api.MoneyManagement;
import de.voidnode.trading4j.api.VolumeLender;
import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.environment.TradingEnvironmentInformation;
import de.voidnode.trading4j.domain.marketdata.impl.FullMarketData;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;
import de.voidnode.trading4j.domain.orders.PendingOrder;
import de.voidnode.trading4j.domain.timeframe.M1;
import de.voidnode.trading4j.functionality.broker.OrderFilteringBroker;
import de.voidnode.trading4j.functionality.expertadvisor.MarketDataDistributor;

/**
 * Upgrades an {@link BasicExpertAdvisorFactory} to an {@link ExpertAdvisorFactory} by implementing several default
 * functionalities.
 * 
 * <p>
 * This implementation wraps a {@link BasicExpertAdvisorFactory} and augments the {@link ExpertAdvisor}s it creates with
 * functionalities. For each {@link BasicPendingOrder} such an {@link ExpertAdvisor} sends to the broker, this
 * implementation will request a {@link Volume} from the {@link MoneyManagement}. With the volume a concrete
 * {@link PendingOrder} is created which is then send to a real {@link Broker}.
 * </p>
 * 
 * <p>
 * All trades of {@link ExpertAdvisor}s are filtered as long as historic market data is received. That way the
 * {@link BasicExpertAdvisorFactory} passed in does not need to distinguish between historic data and live data.
 * </p>
 * 
 * 
 * @author Raik Bieniek
 *
 */
public class DefaultExpertAdvisorFactory implements ExpertAdvisorFactory {

    private final BasicExpertAdvisorFactory factory;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param factory
     *            The factory that should be augmented with several features.
     */
    public DefaultExpertAdvisorFactory(final BasicExpertAdvisorFactory factory) {
        this.factory = factory;
    }

    @Override
    public Optional<ExpertAdvisor<FullMarketData<M1>>> newExpertAdvisor(final int expertAdvisorNumber,
            final Broker<PendingOrder> broker, final VolumeLender volumeLender,
            final TradingEnvironmentInformation environment) {

        final StrategyMoneyManagement<FullMarketData<M1>> strategyMoneyManagement = new StrategyMoneyManagement<>(
                broker, volumeLender, environment.getTradeSymbol(),
                environment.getVolumeConstraints().getAllowedStepSize());
        final OrderFilteringBroker<FullMarketData<M1>> blockedOnHistoricData = new OrderFilteringBroker<FullMarketData<M1>>(
                strategyMoneyManagement, new HistoricDataOrderFilter<>(environment.getNonHistoricTime()));

        return factory.newExpertAdvisor(expertAdvisorNumber, blockedOnHistoricData, environment).map(advisor -> {
            final ExpertAdvisor<FullMarketData<M1>> dataDistributor = new MarketDataDistributor<FullMarketData<M1>>(
                    strategyMoneyManagement, blockedOnHistoricData, advisor);

            return dataDistributor;
        });
    }
}
