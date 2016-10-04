package de.voidnode.trading4j.examples;

import java.util.Optional;

import de.voidnode.trading4j.api.BasicExpertAdvisorFactory;
import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.ExpertAdvisor;
import de.voidnode.trading4j.domain.TimeFrame.M1;
import de.voidnode.trading4j.domain.environment.TradingEnvironmentInformation;
import de.voidnode.trading4j.domain.marketdata.FullMarketData;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;
import de.voidnode.trading4j.server.TradingServerBuilder;

/**
 * Demonstrates how to set up a trading strategy server for the Trading4j client for MetaTrader.
 * 
 * @author Raik Bieniek;
 */
public final class ExpertAdvisorServer {

    private ExpertAdvisorServer() {

    }

    /**
     * Starts the server.
     * 
     * @param args
     *            No command line args are supported.
     */
    public static void main(final String[] args) {

        final BasicExpertAdvisorFactory expertAdvisors = new BasicExpertAdvisorFactory() {
            @Override
            public Optional<ExpertAdvisor<FullMarketData<M1>>> newExpertAdvisor(final int expertAdvisorNumber,
                    final Broker<BasicPendingOrder> broker, final TradingEnvironmentInformation environment) {
                return Optional.of(NMovingAveragesExpertAdvisorFactory.createNew(broker));
            }
        };
        new TradingServerBuilder().expertAdvisors(expertAdvisors).build().start();
    }

}
