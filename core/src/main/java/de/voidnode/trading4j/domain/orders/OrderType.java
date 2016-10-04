package de.voidnode.trading4j.domain.orders;

/**
 * Describes the trade direction of an order.
 * 
 * @author Raik Bieniek
 */
public enum OrderType {

    /**
     * Instructs to buy a given asset.
     * 
     * <p>
     * For Forex trading this means buying the base currency of a currency pair and paying with the other currency of
     * the pair.
     * </p>
     */
    BUY,

    /**
     * Instructs to sell a given asset.
     * 
     * <p>
     * For Forex trading this means paying with the base currency of a currency pair for buying the other currency of
     * the pair.
     * </p>
     */
    SELL
}
