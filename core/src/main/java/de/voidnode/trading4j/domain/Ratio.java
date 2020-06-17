package de.voidnode.trading4j.domain;

/**
 * The relationship between two numbers of the same kind. [
 * <a href="http://en.wikipedia.org/w/index.php?title=Ratio&amp;oldid=653446692" >Wikipedia</a>]
 * 
 * @author Raik Bieniek
 */
public final class Ratio implements Comparable<Ratio> {

    /**
     * The smallest possible ratio.
     * 
     * <p>
     * Contrary to {@link Double#MIN_VALUE} this is a negative value.
     * </p>
     */
    public static final Ratio MIN_VALUE = new Ratio(-Double.MAX_VALUE);

    private final double value;

    /**
     * Creates a ratio without a unit (uses {@link RatioUnit#BASIC}.
     * 
     * @param value
     *            The raw value for this ratio.
     */
    public Ratio(final double value) {
        this.value = value;
    }

    /**
     * Creates a ratio with a given unit.
     * 
     * @param value
     *            The raw value of for this ratio.
     * @param unit
     *            The unit as which the <code>value</code> should be interpreted.
     */
    public Ratio(final double value, final RatioUnit unit) {
        this.value = value / unit.getFraction();
    }

    /**
     * The raw value of this ratio as {@link RatioUnit#BASIC}.
     * 
     * @return The raw value
     */
    public double asBasic() {
        return value;
    }

    /**
     * Calculates the {@link Ratio} of all {@link Ratio} passed as input.
     * 
     * @param rates
     *            The {@link Ratio}s thats average {@link Ratio} should be calculated.
     * @return The average {@link Ratio}.
     * @throws IllegalArgumentException
     *             When the <code>rates</code> had no elements.
     */
    public static Ratio average(final Iterable<Ratio> rates) throws IllegalArgumentException {
        double sum = 0;
        int count = 0;

        for (final Ratio rate : rates) {
            count += 1;
            sum += rate.value;
        }

        if (count == 0) {
            throw new IllegalArgumentException(
                    "The iterator containing the ratios thats average should be calculated contained no ratios.");
        }
        return new Ratio(sum / count);
    }

    @Override
    public int compareTo(final Ratio other) {
        return Double.compare(value, other.value);
    }

    /**
     * A {@link Ratio} is only equal to other {@link Ratio}s thats {@link #asBasic()} values are equal.
     * 
     * <p>
     * As value which is compared {@link #asBasic()} is a double value use this method with care.
     * </p>
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Ratio)) {
            return false;
        }
        final Ratio other = (Ratio) obj;
        if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        final long temp = Double.doubleToLongBits(value);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Ratio [" + value + "]";
    }
}
