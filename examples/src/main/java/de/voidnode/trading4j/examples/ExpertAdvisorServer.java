package de.voidnode.trading4j.examples;

import java.util.Optional;

import de.voidnode.trading4j.api.BasicExpertAdvisorFactory;
import de.voidnode.trading4j.domain.environment.TradingEnvironmentInformation;
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
     *            No command line args are evaluated.
     */
    public static void main(final String[] args) {

        final BasicExpertAdvisorFactory expertAdvisors = (expertAdvisorNumber, broker, environment) -> {

            // Just in case ...
            failIfRealMoney(environment);

            // This will create a new instance of an exemplary expert advisor.
            return Optional.of(NMovingAveragesExpertAdvisorFactory.createNew(broker));

            // If multiple expert advisors should be served you can evaluate the expertAdvisorNumber to select the
            // correct advisor. This number can be configured in the expert advisor options in meta trader.
        };

        // Configure and start the server for the expert advisors.
        new TradingServerBuilder().expertAdvisors(expertAdvisors).build().start();
    }

    private static void failIfRealMoney(final TradingEnvironmentInformation environment) {
        if (!"Backtest".equals(environment.getAccountInformation().getBrokerName())) {
            throw new IllegalStateException(
                    "Trading with real money is not supported with the exemplary expert advisor."
                            + " It is intended to be used for back testing only.");
        }
    }
}
