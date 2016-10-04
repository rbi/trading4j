package de.voidnode.trading4j.domain;

import java.util.Optional;

import de.voidnode.trading4j.api.UnrecoverableProgrammingError;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * A direction for {@link Price}s in the market.
 * 
 * @author Raik Bieniek
 */
public enum MarketDirection {
    /**
     * Indicates that the market price for an asset tends to get higher.
     */
    UP,
    /**
     * Indicates that the market price for an asset tends to get lower.
     */
    DOWN;
    
    /**
     * A convenience field for an Optional with {@link #UP} direction.
     */
    public static final Optional<MarketDirection> UP_IN_OPTIONAL = Optional.of(UP);

    /**
     * A convenience field for an Optional with {@link #DOWN} direction.
     */
    public static final Optional<MarketDirection> DOWN_IN_OPTIONAL = Optional.of(DOWN);

    /**
     * The inverse of this {@link MarketDirection}.
     * 
     * @return The inverse.
     */
    public MarketDirection inverted() {
        switch (this) {
            case UP:
                return MarketDirection.DOWN;
            case DOWN:
                return MarketDirection.UP;
            default:
                throw new UnrecoverableProgrammingError("A trend that is neither UP nor DOWN can not be converted.");
        }
    }
}
