package de.voidnode.trading4j.server.protocol.messages;

import de.voidnode.trading4j.domain.marketdata.impl.FullMarketData;
import de.voidnode.trading4j.domain.timeframe.M1;
import de.voidnode.trading4j.domain.timeframe.TimeFrame;

/**
 * A message containing new market data in form of a {@link FullMarketData} with the {@link M1} {@link TimeFrame}.
 * 
 * @author Raik Bieniek
 */
public class NewMarketDataExtendedMessage implements Message {

    private final FullMarketData<M1> candleStick;

    /**
     * Initializes the message.
     * 
     * <p>
     * The constructor is <code>package private</code> as this message is read-only for now.
     * </p>
     * 
     * @param candleStick
     *            The candle stick that was received.
     */
    public NewMarketDataExtendedMessage(final FullMarketData<M1> candleStick) {
        this.candleStick = candleStick;
    }

    /**
     * The new market data that is wrapped in this message.
     * 
     * @return The market data
     */
    public FullMarketData<M1> getCandleStick() {
        return candleStick;
    }
}
