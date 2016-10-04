package de.voidnode.trading4j.domain.monetary;

import static de.voidnode.trading4j.testutils.assertions.Assertions.assertThat;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link AccuratePrice} works as expected.
 * 
 * @author Raik Bieniek
 */
public class AccuratePriceTest {

    /**
     * An {@link AccuratePrice} can be constructed by passing its raw value.
     */
    @Test
    public void rawValueCanBeUsedForConstruction() {
        assertThat(new AccuratePrice(1.583).asRawValue()).isEqualTo(1.583);
        assertThat(new AccuratePrice(-5.81).asRawValue()).isEqualTo(-5.81);
    }

    /**
     * An {@link AccuratePrice} is equal only to other {@link AccuratePrice} with the same raw value.
     */
    @Test
    public void equalsOtherAccuratePricesWithSameValue() {
        assertThat(new AccuratePrice(123.45)).isEqualTo(new AccuratePrice(123.45));
        assertThat(new AccuratePrice(-582.58)).isEqualTo(new AccuratePrice(-582.58));

        assertThat(new AccuratePrice(123.45)).isNotEqualTo(new AccuratePrice(123.78));
        assertThat(new AccuratePrice(123.45)).isNotEqualTo(null);
        assertThat(new AccuratePrice(123.45)).isNotEqualTo("not a price");
    }
}
