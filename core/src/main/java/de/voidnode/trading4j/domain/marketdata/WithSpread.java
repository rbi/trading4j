package de.voidnode.trading4j.domain.marketdata;

import de.voidnode.trading4j.domain.monetary.Price;

/**
 * The difference between the ask and the bid {@link Price} of a security during the time frame of this
 * {@link MarketData}.
 * 
 * @author Raik Bieniek
 */
public interface WithSpread {

    /**
     * The difference between the ask {@link Price}, the {@link Price} for that the security can be bought, and the bid
     * {@link Price}, the {@link Price} for that the asset can be sold.
     * 
     * <p>
     * Prices of {@link WithOhlc#getOpen() open}, {@link WithOhlc#getHigh() high}, {@link WithOhlc#getLow() low} and
     * {@link WithOhlc#getClose() close} are the bid {@link Price}. The spread has to be added to them to get the ask
     * {@link Price}. If there is any markup fee it is contained in this spread.
     * </p>
     * 
     * @return The spread.
     */
    Price getSpread();
}
