package de.voidnode.trading4j.domain;

/**
 * The amount of a financial asset that should be traded.
 * 
 * @author Raik Bieniek
 */
public final class Volume {

    private static final long ACCURACY = 100000;

    private final long volume;

    /**
     * Constructs a volume instance.
     * 
     * @param volume
     *            The raw volume value that this instance should have.
     * @param unit
     *            The unit of the raw volume value used as input.
     */
    public Volume(final long volume, final VolumeUnit unit) {
        this.volume = volume * unit.getMultiplesOfBase();
    }

    /**
     * The raw value of this instance with the unit {@link VolumeUnit#BASE}.
     * 
     * @return the raw value
     */
    public long asAbsolute() {
        return volume;
    }

    /**
     * The raw value of this volume as a floating point value.
     * 
     * <p>
     * The unit of the returned value is {@link VolumeUnit#LOT}. The returned value is accurate to the 5th decimal
     * position (-10^-5). Smaller fractions are rounding errors resulting in the behavior of {@link Double} values.
     * </p>
     * 
     * @return the raw value as {@link VolumeUnit#LOT}.
     */
    public double asLot() {
        return (double) volume / ACCURACY;
    }

    /**
     * Two {@link Volume}s are equal when their raw {@link #asAbsolute()} value is the same.
     * 
     * <p>
     * Only {@link Volume} instances can can be equal to {@link Volume}s.
     * </p>
     * 
     * @param obj
     *            The instance to compare this {@link Volume} with.
     */
    // CHECKSTYLE:OFF eclipse generated
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Volume other = (Volume) obj;
        if (volume != other.volume) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (volume ^ (volume >>> 32));
        return result;
    }

    // CHECKSTYLE:ON

    @Override
    public String toString() {
        return "" + asLot() + " LOT";
    }
}
