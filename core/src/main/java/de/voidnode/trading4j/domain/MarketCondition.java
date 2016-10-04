package de.voidnode.trading4j.domain;

/**
 * The information if a market is currently clearly moving in one direction or if it is moving sidewards.
 * 
 * @author Raik Bieniek
 */
public enum MarketCondition {

    /**
     * The market is currently not moving into a particular direction (up or down) but fluctuating around an average
     * price.
     */
    RANGING,

    /**
     * The market is currently heading into a particular direction (up or down).
     */
    TRENDING
}
