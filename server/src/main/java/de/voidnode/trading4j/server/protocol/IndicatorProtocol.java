package de.voidnode.trading4j.server.protocol;

import java.util.Optional;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.api.TrendIndicatorFactory;
import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.marketdata.impl.DatedCandleStick;
import de.voidnode.trading4j.domain.timeframe.M1;
import de.voidnode.trading4j.server.protocol.exceptions.CommunicationException;
import de.voidnode.trading4j.server.protocol.exceptions.ProtocolException;
import de.voidnode.trading4j.server.protocol.messages.NewMarketDataSimpleMessage;
import de.voidnode.trading4j.server.protocol.messages.TrendForMarketDataMessage;

/**
 * The server side of the protocol for transferring {@link Indicator}s to the client.
 * 
 * <p>
 * One instance handles the communication to a single client.
 * </p>
 * 
 * @author Raik Bieniek
 */
class IndicatorProtocol {

    private final TrendIndicatorFactory factory;
    private final MessageBasedClientConnection client;
    private final int indicatorNumber;

    private Indicator<MarketDirection, DatedCandleStick<M1>> indicator;

    /**
     * Initializes the protocol but does not start it.
     * 
     * @param client
     *            used to send and receive data.
     * @param factory
     *            used to create the indicator requested from the client.
     * @param indicatorNumber
     *            The number of the indicator that should be served by this instance
     */
    IndicatorProtocol(final MessageBasedClientConnection client, final TrendIndicatorFactory factory,
            final int indicatorNumber) {
        this.client = client;
        this.factory = factory;
        this.indicatorNumber = indicatorNumber;
    }

    /**
     * Starts the execution of the protocol.
     * 
     * @throws CommunicationException
     *             When the communication protocol was violated or the network failed.
     */
    public void start() throws CommunicationException {
        indicator = factory.newIndicatorByNumber(indicatorNumber).orElseThrow(
                () -> new ProtocolException("Recieved an request for the indicator with the number " + indicatorNumber
                        + " which is unknown."));
        startRequestResponseLoop();
    }

    private void startRequestResponseLoop() throws CommunicationException {
        while (true) {
            final DatedCandleStick<M1> candleStick = client.readMessage(NewMarketDataSimpleMessage.class)
                    .getCandleStick();
            final Optional<MarketDirection> trend = indicator.indicate(candleStick);
            client.sendMessage(new TrendForMarketDataMessage(trend));
        }
    }
}
