package de.voidnode.trading4j.testutils.assertions;

import de.voidnode.trading4j.domain.MarketDirection;

import org.assertj.core.api.AbstractAssert;

/**
 * Assertions for {@link MarketDirection}s.
 * 
 * @author Raik Bieniek
 */
public class TrendAssert extends AbstractAssert<TrendAssert, MarketDirection> {

    /**
     * Initializes an instance with its dependencies.
     * 
     * @param actual
     *            The {@link MarketDirection} on which assertions should be executed.
     */
    TrendAssert(final MarketDirection actual) {
        super(actual, TrendAssert.class);
    }

    /**
     * Checks if the actual trend is the same as the expected one.
     * 
     * @param other
     *            The trend that this one should be equal to.
     * @return this assertions
     */
    public TrendAssert is(final MarketDirection other) {
        isNotNull();

        if (actual != other) {
            failWithMessage("Expected the trend to be <%s> but was <%s>", other.toString(), actual.toString());
        }

        return this;
    }
}
