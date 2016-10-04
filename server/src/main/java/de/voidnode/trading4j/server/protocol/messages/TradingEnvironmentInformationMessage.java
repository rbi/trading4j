package de.voidnode.trading4j.server.protocol.messages;

import de.voidnode.trading4j.domain.environment.TradingEnvironmentInformation;

/**
 * Transports {@link TradingEnvironmentInformation} form the broker to the client.
 * 
 * @author Raik Bieniek
 */
public class TradingEnvironmentInformationMessage implements Message {

    private final TradingEnvironmentInformation information;

    /**
     * Initializes the message.
     * 
     * <p>
     * The constructor is <code>package private</code> as this message is read-only for now.
     * </p>
     * 
     * @param information
     *            The information that where received.
     */
    TradingEnvironmentInformationMessage(final TradingEnvironmentInformation information) {
        this.information = information;
    }

    /**
     * The information that is wrapped in this message.
     * 
     * @return the information
     */
    public TradingEnvironmentInformation getInformation() {
        return information;
    }
}
