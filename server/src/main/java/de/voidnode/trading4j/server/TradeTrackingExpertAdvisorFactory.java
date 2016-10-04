package de.voidnode.trading4j.server;

import java.time.Instant;
import java.util.Optional;

import de.voidnode.trading4j.api.AccountingExpertAdvisor;
import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.ExpertAdvisor;
import de.voidnode.trading4j.api.ExpertAdvisorFactory;
import de.voidnode.trading4j.api.MoneyManagement;
import de.voidnode.trading4j.domain.TimeFrame.M1;
import de.voidnode.trading4j.domain.environment.TradingEnvironmentInformation;
import de.voidnode.trading4j.domain.marketdata.FullMarketData;
import de.voidnode.trading4j.domain.monetary.Money;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.PendingOrder;
import de.voidnode.trading4j.domain.trades.CompletedTrade;
import de.voidnode.trading4j.server.reporting.CombinedNotifier;
import de.voidnode.trading4j.tradetracker.FullCompletedTradeTracker;
import de.voidnode.trading4j.tradetracker.TradeEventListener;

/**
 * Tracks all finished trades done by {@link ExpertAdvisor}s produced by a factory.
 * 
 * @author Raik Bieniek
 */
class TradeTrackingExpertAdvisorFactory implements ExpertAdvisorFactory {

    private final ExpertAdvisorFactory factory;
    private final CombinedNotifier productiveNotifier;
    private final CombinedNotifier backtestNotifier;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param factory
     *            The factory that produces the {@link ExpertAdvisor}s thats trades should be tracked.
     * @param productiveNotifier
     *            Used to notify on completed trades when doing live trading.
     * @param backtestNotifier
     *            Used to notify on completed trades when doing backtesting.
     */
    TradeTrackingExpertAdvisorFactory(final ExpertAdvisorFactory factory, final CombinedNotifier productiveNotifier,
            final CombinedNotifier backtestNotifier) {
        this.factory = factory;
        this.productiveNotifier = productiveNotifier;
        this.backtestNotifier = backtestNotifier;
    }

    @Override
    public Optional<AccountingExpertAdvisor<FullMarketData<M1>>> newExpertAdvisor(final int expertAdvisorNumber,
            final Broker<PendingOrder> broker, final MoneyManagement moneyManagement,
            final TradingEnvironmentInformation environment) {

        final FullCompletedTradeTracker<FullMarketData<M1>> tradeTracker = new FullCompletedTradeTracker<FullMarketData<M1>>(
                broker, () -> Instant.now(), environment.getTradeSymbol());

        final TradeEventListener<CompletedTrade> tradeEventListener = "Backtest"
                .equals(environment.getAccountInformation().getBrokerName()) ? backtestNotifier::tradeCompleted
                        : productiveNotifier::tradeCompleted;
        tradeTracker.setEventListener(tradeEventListener);

        final Optional<AccountingExpertAdvisor<FullMarketData<M1>>> expertAdvisor = factory
                .newExpertAdvisor(expertAdvisorNumber, tradeTracker, moneyManagement, environment);

        return expertAdvisor.<AccountingExpertAdvisor<FullMarketData<M1>>>map(
                advisor -> new AccountingExpertAdvisor<FullMarketData<M1>>() {
                    @Override
                    public void newData(final FullMarketData<M1> marketData) {
                        tradeTracker.newData(marketData);
                        advisor.newData(marketData);

                    }

                    @Override
                    public void balanceChanged(final Money money) {
                        advisor.balanceChanged(money);
                    }

                    @Override
                    public void accountCurrencyPriceChanged(final Price newPrice) {
                        advisor.accountCurrencyPriceChanged(newPrice);
                    }
                });

    }

}
