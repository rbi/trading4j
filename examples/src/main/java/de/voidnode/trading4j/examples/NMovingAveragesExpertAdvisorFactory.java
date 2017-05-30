package de.voidnode.trading4j.examples;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.ExpertAdvisor;
import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.api.OrderFilter;
import de.voidnode.trading4j.domain.Ratio;
import de.voidnode.trading4j.domain.RatioUnit;
import de.voidnode.trading4j.domain.marketdata.FullMarketData;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;
import de.voidnode.trading4j.domain.timeframe.M1;
import de.voidnode.trading4j.domain.timeframe.M5;
import de.voidnode.trading4j.functionality.broker.OrderFilteringBroker;
import de.voidnode.trading4j.functionality.expertadvisor.MarketDataDistributor;
import de.voidnode.trading4j.functionality.timeframeconversion.FullMarketDataTimeFrameConverter;
import de.voidnode.trading4j.functionality.timeframeconversion.TimeFrameConvertingExpertAdvisor;
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
     * @return The created expert advisor.
     */
    public static ExpertAdvisor<FullMarketData<M1>> createNew(final Broker<BasicPendingOrder> broker) {
        
        // Create indicators needed for the trading strategy. Trading4j already implements some well-known indicators
        // but it is also easy to implement new ones.
        final MovingAverageIndicatorFactory mAFactory = new MovingAverageIndicatorFactory();
        final Indicator<Price, FullMarketData<M5>> fast = mAFactory.createExponentialMovingAverage(14);
        final Indicator<Price, FullMarketData<M5>> slow = mAFactory.createSmoothedMovingAverage(28);

        // Create the actual trading strategy. Like with the indicators the following line defines that market data of
        // the time frame M5 should be used as input. Market data contains, besides others, all values for a candle stick
        // chart of the given time frame.
        final TradingStrategy<FullMarketData<M5>> strategy = new NMovingAveragesExpertAdvisor<>(slow, fast);

        // To keep the code of the trading strategy focused, side aspects are implemented in order filters. The
        // following filter will prevent trading when the volatility of a single candle stick chart in the time frame M5
        // is to low.
        final OrderFilter<FullMarketData<M5>> lowVolatilityOrderFilter = new LowVolatilityOrderFilter<>(
                new Ratio(10, RatioUnit.RELATIVE_PIP));
        // Order filtering is wrapped around the original broker as an aspect.
        final OrderFilteringBroker<FullMarketData<M5>> filteringBroker = new OrderFilteringBroker<>(broker,
                lowVolatilityOrderFilter);

        // Many expert advisors are based on a state machine. Trading4j provides a default implementation for such a
        // state machine which manages the states "try placing a new order", "manage the pending order", "manage the
        // opened order". This state machine is used in the following line. Using this implementation is fully optional.
        // It is also possible to implement the ExpertAdvisor interface without using it.
        final ExpertAdvisor<FullMarketData<M5>> expertAdvisor = new StrategyExpertAdvisor<>(strategy, filteringBroker);
        
        // Like the expert advisor, the order filtering broker needs the current market data too so the following combines both.
        final ExpertAdvisor<FullMarketData<M5>> combinedExpertAdvisor = new MarketDataDistributor<>(filteringBroker, expertAdvisor);
        
        // Until now everything was configured to use market data with a time frame M5. The provided market data will be
        // of time frame M1. Therefore the following will aggregate market data of the time frame M1 to market data of
        // the time frame M5 before it is passed to the expert advisor and order filter configured above.
        final ExpertAdvisor<FullMarketData<M1>> convertedExpertAdvisor = new TimeFrameConvertingExpertAdvisor<>(
                combinedExpertAdvisor, new FullMarketDataTimeFrameConverter<>(new M1(), new M5()));
        
        return convertedExpertAdvisor;
    }
}
