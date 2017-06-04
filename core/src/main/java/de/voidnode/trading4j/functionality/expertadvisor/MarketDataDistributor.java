package de.voidnode.trading4j.functionality.expertadvisor;

import static java.util.Arrays.asList;

import de.voidnode.trading4j.api.ExpertAdvisor;
import de.voidnode.trading4j.api.MarketDataListener;
import de.voidnode.trading4j.domain.marketdata.MarketData;

/**
 * Distributes received {@link MarketData} data to all {@link MarketDataListener}s passed in the constructor.
 * 
 * <p>
 * The {@link MarketDataListener}s will receive the data in the order they where passed in in the constructor.
 * </p>
 * 
 * @author Raik Bieniek
 *
 * @param <C>
 *            The concrete type of {@link MarketData}s used.
 */
public class MarketDataDistributor<C extends MarketData> implements ExpertAdvisor<C> {

    private final Iterable<MarketDataListener<C>> advisors;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param advisors
     *            All {@link MarketDataListener} this instance should distribute received data to.
     */
    public MarketDataDistributor(final Iterable<MarketDataListener<C>> advisors) {
        this.advisors = advisors;
    }
    
    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param advisors
     *            All {@link MarketDataListener} this instance should distribute received data to.
     */
    @SafeVarargs
    public MarketDataDistributor(final MarketDataListener<C>... advisors) {
        this.advisors = asList(advisors);
    }

    @Override
    public void newData(final C candleStick) {
        advisors.forEach(advisor -> advisor.newData(candleStick));
    }
}
