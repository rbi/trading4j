package de.voidnode.trading4j.testutils.assertions;

import de.voidnode.trading4j.domain.monetary.AccuratePrice;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.data.Offset;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Assertions for {@link AccuratePrice}s.
 * 
 * @author Raik Bieniek
 */
public class AccuratePriceAssert extends AbstractAssert<AccuratePriceAssert, AccuratePrice> {

    /**
     * Initializes an instance with the {@link AccuratePrice} to do assertions on.
     * 
     * @param actual
     *            The {@link AccuratePrice} to do assertions on.
     */
    AccuratePriceAssert(final AccuratePrice actual) {
        super(actual, AccuratePriceAssert.class);
    }

    /**
     * Checks if an {@link AccuratePrice} is equal to another {@link AccuratePrice} within some given bounds.
     * 
     * @param price
     *            The price that should be nearly equal to the <code>actual</code> price.
     * @param allowedOffset
     *            The allowed offset at which the price will still be interpreted as equal.
     */
    public void isEqualTo(final AccuratePrice price, final Offset<Double> allowedOffset) {
        assertThat(price.asRawValue()).isEqualTo(actual.asRawValue(), allowedOffset);
    }
}
