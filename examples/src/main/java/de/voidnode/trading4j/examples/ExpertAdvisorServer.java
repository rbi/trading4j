package de.voidnode.trading4j.examples;

import java.util.Optional;

import de.voidnode.trading4j.api.BasicExpertAdvisorFactory;
import de.voidnode.trading4j.api.MoneyManagement;
import de.voidnode.trading4j.domain.Ratio;
import de.voidnode.trading4j.domain.environment.TradingEnvironmentInformation;
import de.voidnode.trading4j.moneymanagement.standard.DefaultMoneyManagement;
import de.voidnode.trading4j.server.TradingServerBuilder;

import static de.voidnode.trading4j.domain.RatioUnit.PERCENT;

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

        // This factory is used to create new instances of the user implemented expert advisors. A new instance will be
        // created for each traded forex pair and for each backtesting run.
        final BasicExpertAdvisorFactory expertAdvisors = (expertAdvisorNumber, broker, environment) -> {

            // Just in case ...
            failIfRealMoney(environment);

            // This will create a new instance of an exemplary expert advisor.
            return Optional.of(NMovingAveragesExpertAdvisorFactory.createNew(broker));

            // If multiple expert advisors should be served you can evaluate the expertAdvisorNumber to select the
            // correct advisor. This number can be configured in the expert advisor options in meta trader.
        };

        // This money management implementation will risk a fixed ratio of the money available on the trading account
        // for each trade. The DefaultMoneyManagement is available as part of Trading4j but it is also possible to
        // implement a custom one.
        final MoneyManagement moneyManagement = new DefaultMoneyManagement(new Ratio(2, PERCENT));

        // Configure and start the server for the expert advisors.
        new TradingServerBuilder().expertAdvisors(expertAdvisors).moneyManagement(moneyManagement).build().start();
    }

    private static void failIfRealMoney(final TradingEnvironmentInformation environment) {
        if (!"Backtest".equals(environment.getAccountInformation().getBrokerName())) {
            throw new IllegalStateException(
                    "Trading with real money is not supported with the exemplary expert advisor."
                            + " It is intended to be used for back testing only.");
        }
    }
}
