package de.voidnode.trading4j.domain.monetary;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.copyOf;
import static java.util.Arrays.sort;

/**
 * A collection for multiple related {@link Price} levels ordered from the lowest do the highest level.
 * 
 * <p>
 * Price levels can be used e.g. for support and resistance levels.
 * </p>
 * 
 * @author Raik Bieniek
 */
public class PriceLevels {

    private final Price[] priceLevels;

    /**
     * Initializes a new instance.
     * 
     * @param priceLevels
     *            The price levels of this collection. They do not need to be in the correct order.
     */
    public PriceLevels(final Price... priceLevels) {
        this.priceLevels = copyOf(priceLevels, priceLevels.length);
        sort(this.priceLevels);
    }

    /**
     * Initializes a new instance.
     * 
     * @param priceLevels
     *            The price levels of this collection. They do not need to be in the correct order.
     */
    public PriceLevels(final List<Price> priceLevels) {
        this.priceLevels = copy(priceLevels);
    }

    /**
     * Returns the next {@link Price} level that is higher than the input.
     * 
     * @param price
     *            The {@link Price} thats next higher level should be returned.
     * @return The {@link Price} of the next higher {@link Price} level or an empty {@link Optional} when there is no
     *         higher {@link Price} level.
     */
    public Optional<Price> nextHigherLevel(final Price price) {
        final int levelCount = priceLevels.length;
        for (int i = 0; i < levelCount; i++) {
            if (priceLevels[i].isGreaterThan(price)) {
                return Optional.of(priceLevels[i]);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the next {@link Price} level that is lower than the input.
     * 
     * @param price
     *            The {@link Price} thats next lower level should be returned.
     * @return The {@link Price} of the next lower {@link Price} level or an empty {@link Optional} when there is no
     *         lower {@link Price} level.
     */
    public Optional<Price> nextLowerLevel(final Price price) {
        final int levelCount = priceLevels.length;
        for (int i = levelCount - 1; i >= 0; i--) {
            if (priceLevels[i].isLessThan(price)) {
                return Optional.of(priceLevels[i]);
            }
        }
        return Optional.empty();
    }

    /**
     * The amount of {@link Price} levels that this collection contains.
     * 
     * @return The {@link Price} level amount
     */
    public int count() {
        return priceLevels.length;
    }

    /**
     * Returns the {@link Price} level at a given index.
     * 
     * <p>
     * The {@link Price} levels of this collection are ordered from the lowest {@link Price} level to the highest.
     * </p>
     * 
     * @param index
     *            The index of the {@link Price} level that should be returned.
     * @return The requested {@link Price} level.
     * @throws IndexOutOfBoundsException
     *             When the <code>index</code> passed is to large or negative.
     */
    public Price get(final int index) throws IndexOutOfBoundsException {
        return priceLevels[index];
    }

    /**
     * Checks if the given price is contained in this {@link PriceLevels} instance.
     * 
     * @param price
     *            The price that should be checked.
     * @return <code>true</code> if <code>price</code> is contained and <code>false</code> if not.
     */
    public boolean contains(final Price price) {
        for (final Price level : priceLevels) {
            if (level.equals(price)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "PriceLevels " + Arrays.toString(priceLevels);
    }

    private Price[] copy(final List<Price> priceLevels) {
        final Price[] copy = new Price[priceLevels.size()];
        for (int i = 0; i < copy.length; i++) {
            copy[i] = priceLevels.get(i);
        }
        return copy;
    }
}
