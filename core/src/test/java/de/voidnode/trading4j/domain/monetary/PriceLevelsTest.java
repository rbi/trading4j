package de.voidnode.trading4j.domain.monetary;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if the {@link PriceLevels} class works as expected.
 * 
 * @author Raik Bieniek
 */
public class PriceLevelsTest {

    /**
     * The instance should return the next higher {@link Price} level of a price passed.
     */
    @Test
    public void shouldReturnNextHigherLevel() {
        final PriceLevels cut = new PriceLevels(new Price(1.0), new Price(5.1), new Price(0.9), new Price(4.3));

        assertThat(cut.nextHigherLevel(new Price(-5.6)).get()).isEqualTo(new Price(0.9));
        assertThat(cut.nextHigherLevel(new Price(2.0)).get()).isEqualTo(new Price(4.3));

        assertThat(cut.nextHigherLevel(new Price(4.3)).get()).isEqualTo(new Price(5.1));
        assertThat(cut.nextHigherLevel(new Price(68.5)).isPresent()).isFalse();
    }

    /**
     * The instance should return the next lower {@link Price} level of a price passed.
     */
    @Test
    public void shouldReturnNextLowerLevel() {
        final PriceLevels cut = new PriceLevels(new Price(5.4), new Price(3.5), new Price(6.9), new Price(-1.6));

        assertThat(cut.nextLowerLevel(new Price(7.853)).get()).isEqualTo(new Price(6.9));
        assertThat(cut.nextLowerLevel(new Price(4.9)).get()).isEqualTo(new Price(3.5));

        assertThat(cut.nextLowerLevel(new Price(3.5)).get()).isEqualTo(new Price(-1.6));
        assertThat(cut.nextLowerLevel(new Price(-5.0)).isPresent()).isFalse();
    }

    /**
     * The instance should return the correct count for the {@link Price} levels it holds.
     */
    @Test
    public void shouldReturnCorrectPriceLevelCount() {
        assertThat(new PriceLevels(new Price(1.0), new Price(2.0), new Price(3.0)).count()).isEqualTo(3);
        assertThat(new PriceLevels(new Price(2.0)).count()).isEqualTo(1);
        assertThat(new PriceLevels().count()).isEqualTo(0);
    }

    /**
     * The instance should return {@link Price} levels in the correct order.
     */
    @Test
    public void shouldReturnPriceLevelsInTheCorrectOrder() {
        final PriceLevels cut = new PriceLevels(new Price(5.4), new Price(3.5), new Price(6.9), new Price(-1.6));

        assertThat(cut.get(0)).isEqualTo(new Price(-1.6));
        assertThat(cut.get(1)).isEqualTo(new Price(3.5));
        assertThat(cut.get(2)).isEqualTo(new Price(5.4));
        assertThat(cut.get(3)).isEqualTo(new Price(6.9));
    }

    /**
     * A {@link PriceLevels} instance can check if a given {@link Price} is contained.
     */
    @Test
    public void canCheckIfAGivenPriceLevelIsContained() {
        final PriceLevels cut = new PriceLevels(new Price(1592), new Price(3951), new Price(-9423));
        
        assertThat(cut.contains(new Price(3951))).isTrue();
        assertThat(cut.contains(new Price(-9423))).isTrue();
        assertThat(cut.contains(new Price(81))).isFalse();
    }

    /**
     * Indices that are to high or are negative should throw an {@link IndexOutOfBoundsException}.
     */
    @Test
    public void shouldThrowIndexOutOfBoundsExceptionIfTheIndexIsToHighOrNegative() {
        final PriceLevels cut = new PriceLevels(new Price(5.4), new Price(3.5), new Price(6.9), new Price(-1.6));

        boolean firstCaught = false;
        boolean secondCaught = false;

        try {
            cut.get(-5);
        } catch (final IndexOutOfBoundsException e) {
            firstCaught = true;
        }

        try {
            cut.get(4);
        } catch (final IndexOutOfBoundsException e) {
            secondCaught = true;
        }

        assertThat(firstCaught).as("A negative index should have thrown an IndexOutOfBoundsException but didn't.")
                .isTrue();
        assertThat(secondCaught)
                .as("An index that is to great should have thrown an IndexOutOfBoundsException but didn't.").isTrue();
    }

    /**
     * When no {@link Price} levels are passed to the constructor the class should not crash.
     */
    @Test
    public void shouldNotFailForZeroPriceLevels() {
        final PriceLevels cut = new PriceLevels();
        assertThat(cut.nextHigherLevel(new Price(1.0)).isPresent()).isFalse();
        assertThat(cut.nextLowerLevel(new Price(1.0)).isPresent()).isFalse();
    }

    /**
     * When the {@link PriceLevels} are constructed from an array, the {@link PriceLevels} do not change when the
     * original array changes.
     */
    @Test
    public void doesNotChangeWhenArrayPassedAsInputChanges() {
        final Price[] prices = new Price[] { new Price(0), new Price(1), new Price(2), new Price(3) };

        final PriceLevels cut = new PriceLevels(prices);
        prices[2] = new Price(20);

        assertThat(cut.get(2)).isEqualTo(new Price(2));
    }

    /**
     * When the {@link PriceLevels} are constructed from a {@link List}, the {@link PriceLevels} do not change when the
     * original {@link List} changes.
     */
    @Test
    public void doesNotChangeWhenListPassedAsInputChanges() {
        final List<Price> prices = new ArrayList<>(4);
        prices.add(new Price(400));
        prices.add(new Price(500));
        prices.add(new Price(600));
        prices.add(new Price(700));

        final PriceLevels cut = new PriceLevels(prices);
        prices.set(1, new Price(1234));

        assertThat(cut.get(1)).isEqualTo(new Price(500));
        assertThat(cut.get(3)).isEqualTo(new Price(700));
    }
}
