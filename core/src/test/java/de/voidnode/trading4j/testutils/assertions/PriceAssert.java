package de.voidnode.trading4j.testutils.assertions;

import de.voidnode.trading4j.domain.monetary.Price;

import org.assertj.core.api.AbstractAssert;

/**
 * Assertions for {@link Price}s.
 * 
 * @author Raik Bieniek
 */
public class PriceAssert extends AbstractAssert<PriceAssert, Price> {

    /**
     * Initializes the assertions.
     * 
     * @param actual
     *            The price on which assertions should be made.
     */
    PriceAssert(final Price actual) {
        super(actual, PriceAssert.class);
    }

    /**
     * Asserts that a price is less than an other price.
     * 
     * @param other
     *            the other price to compare with.
     * @return This assertion instance for providing a fluent API.
     */
    public PriceAssert isLessThan(final Price other) {
        isNotNull();

        if (!actual.isLessThan(other)) {
            failWithMessage("Expected the price <%s> to be less than price <%s> but it isn't.", actual.toString(),
                    other.toString());
        }

        return this;
    }

    /**
     * Asserts that a price is greater than an other price.
     * 
     * @param other
     *            the other price to compare with.
     * @return This assertion instance for providing a fluent API.
     */
    public PriceAssert isGreaterThan(final Price other) {
        isNotNull();

        if (!actual.isGreaterThan(other)) {
            failWithMessage("Expected the price <%s> to be greater than price <%s> but it isn't.", actual.toString(),
                    other.toString());
        }

        return this;
    }
}
