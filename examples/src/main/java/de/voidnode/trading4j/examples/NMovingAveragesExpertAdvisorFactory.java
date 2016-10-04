package de.voidnode.trading4j.examples;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.ExpertAdvisor;
import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.api.OrderFilter;
import de.voidnode.trading4j.domain.Ratio;
import de.voidnode.trading4j.domain.RatioUnit;
import de.voidnode.trading4j.domain.TimeFrame.M1;
import de.voidnode.trading4j.domain.marketdata.CandleStick;
import de.voidnode.trading4j.domain.marketdata.FullMarketData;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;
import de.voidnode.trading4j.functionality.broker.OrderFilteringBroker;
import de.voidnode.trading4j.functionality.expertadvisor.MarketDataDistributor;
import de.voidnode.trading4j.indicators.MovingAverageIndicatorFactory;
import de.voidnode.trading4j.strategyexpertadvisor.StrategyExpertAdvisor;
import de.voidnode.trading4j.strategyexpertadvisor.TradingStrategy;

/**
 * Creates trading strategies based on three moving averages indicating a trend in the same direction.
 * 
 * @author Raik Bieniek
 */
public final class NMovingAveragesExpertAdvisorFactory {

    private NMovingAveragesExpertAdvisorFactory() {

    }

    /**
     * Creates a new strategy.
     * 
     * @param broker
     *            Used to execute the orders produced by this expert advisor.
     * @param <C>
     *            the concrete type of {@link CandleStick}s that should be used..
     * @return The created expert advisor.
     */
    public static <C extends FullMarketData<M1>> ExpertAdvisor<C> createNew(final Broker<BasicPendingOrder> broker) {
        final MovingAverageIndicatorFactory mAFactory = new MovingAverageIndicatorFactory();
        final Indicator<Price, C> fast = mAFactory.createExponentialMovingAverage(14);
        final Indicator<Price, C> slow = mAFactory.createSmoothedMovingAverage(28);
        
        final TradingStrategy<C> strategy = new NMovingAveragesExpertAdvisor<C>(slow, fast);

        // order filter
        final OrderFilter<C> lowVolatilityOrderFilter = new LowVolatilityOrderFilter<>(
                new Ratio(10, RatioUnit.RELATIVE_PIP));
        final OrderFilteringBroker<C> filteringBroker = new OrderFilteringBroker<C>(broker, lowVolatilityOrderFilter);

        return new MarketDataDistributor<C>(filteringBroker, new StrategyExpertAdvisor<>(strategy, filteringBroker));
    }
}
