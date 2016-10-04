package de.voidnode.trading4j.server.protocol.messages;

import java.util.Optional;

import de.voidnode.trading4j.domain.MarketDirection;

/**
 * A message containing the {@link MarketDirection} that was calculated for the last {@link NewMarketDataSimpleMessage} received.
 * 
 * @author Raik Bieniek
 */
public class TrendForMarketDataMessage implements Message {

    private final Optional<MarketDirection> trend;

    /**
     * Constructs the massage from a given trend.
     * 
     * @param trend
     *            The {@link MarketDirection} to construct the message from or an empty {@link Optional} if this message should
     *            indicate that no {@link MarketDirection} is available.
     */
    public TrendForMarketDataMessage(final Optional<MarketDirection> trend) {
        this.trend = trend;
    }

    /**
     * The trend wrapped in this message if any.
     * 
     * @return The trend
     */
    public Optional<MarketDirection> getTrend() {
        return trend;
    }
}
