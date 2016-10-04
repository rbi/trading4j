package de.voidnode.trading4j.domain.monetary;

/**
 * Similar to {@link Price} but with no fixed decimal place count.
 * 
 * <p>
 * This class is well suited for interim results of calculations with {@link Price}s. Internally it is based on a
 * {@link Double} precision floating point value.
 * </p>
 * 
 * @author Raik Bieniek
 */
public final class AccuratePrice {

    private final double rawPrice;

    /**
     * Initializes an instance.
     * 
     * @param rawPrice
     *            The raw price value. This value is interpreted as {@link PriceUnit#MAJOR}.
     */
    public AccuratePrice(final double rawPrice) {
        this.rawPrice = rawPrice;
    }

    /**
     * The raw value of this {@link AccuratePrice}.
     * 
     * @return The raw value with the unit {@link PriceUnit#MAJOR}.
     */
    public double asRawValue() {
        return rawPrice;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        final long temp = Double.doubleToLongBits(rawPrice);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AccuratePrice)) {
            return false;
        }
        final AccuratePrice other = (AccuratePrice) obj;
        if (Double.doubleToLongBits(rawPrice) != Double.doubleToLongBits(other.rawPrice)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "" + rawPrice;
    }
}
