package de.voidnode.trading4j.domain.monetary;

import java.util.Currency;

import static java.lang.Math.round;

import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.Ratio;
import de.voidnode.trading4j.domain.RatioUnit;
import de.voidnode.trading4j.domain.Volume;

/**
 * A market price for a financial asset.
 * 
 * <p>
 * The market price is stored as a fixed point decimal value with an accuracy of 10^-5 which is 1
 * {@link PriceUnit#PIPETTE}.
 * </p>
 * <p>
 * All {@link Price} instances are immutable. No operation in this class will change the value of an instance but will
 * return a new instance when needed.
 * </p>
 * 
 * @author Raik Bieniek
 */
public final class Price implements Comparable<Price> {

    private static final long ACCURACY = 100000;

    private final long price;

    /**
     * Constructs a price with a given value.
     * 
     * @param price
     *            The price as floating point representation. It is rounded mathematically correctly.
     */
    public Price(final double price) {
        this.price = round(price * ACCURACY);
    }

    /**
     * Constructs a price with a given value.
     * 
     * @param price
     *            The price in {@link PriceUnit#PIPETTE}s.
     */
    public Price(final long price) {
        this.price = price;
    }

    /**
     * Constructs a price with a given value.
     * 
     * @param value
     *            The value for the created price.
     * @param unit
     *            The unit of the value
     */
    public Price(final long value, final PriceUnit unit) {
        this.price = value * unit.getMultipleOfPipette();
    }

    /**
     * The value of this price in {@link PriceUnit#PIPETTE}s.
     * 
     * @return the price in {@link PriceUnit#PIPETTE}s
     */
    public long asPipette() {
        return price;
    }

    /**
     * The value of this price as a floating point value.
     * 
     * <p>
     * The unit of the returned value is the {@link PriceUnit#MAJOR} unit of the currency. The returned value is
     * accurate to the 5th decimal position (-10^-5). Smaller fractions are rounding errors resulting in the behavior of
     * {@link Double} values.
     * </p>
     * 
     * @return the price
     */
    public double asDouble() {
        return (double) price / ACCURACY;
    }

    /**
     * Adds the value of another price to the value of this price.
     * 
     * @param other
     *            The other {@link Price} to add to this one.
     * @return A new {@link Price} containing the result of this calculation.
     */
    public Price plus(final Price other) {
        return new Price(price + other.price);
    }

    /**
     * Adds a raw price interpreted as a given {@link PriceUnit} to this price and returns the result.
     * 
     * @param amount
     *            The raw price to add.
     * @param unit
     *            The unit of as which the raw price passed as argument should be interpreted.
     * @return A new {@link Price} containing the result of this calculation.
     */
    public Price plus(final int amount, final PriceUnit unit) {
        return new Price(price + amount * unit.getMultipleOfPipette());
    }

    /**
     * Adds a fraction of this {@link Price} to this {@link Price} and returns the result.
     * 
     * <p>
     * <b>WARNING:</b> As {@link Ratio}s are based on floating point numbers, results may be rounded. Rounding is done
     * mathematical correctly.
     * </p>
     * 
     * @param value
     *            The raw value of the ratio to add.
     * @param unit
     *            The unit for the raw value.
     * @return A new {@link Price} containing the result of this calculation.
     * @see Ratio#Ratio(double, RatioUnit)
     */
    public Price plus(final double value, final RatioUnit unit) {
        return plus(new Ratio(value, unit));
    }

    /**
     * Adds a fraction of this {@link Price} to this {@link Price} and returns the result.
     * 
     * <p>
     * <b>WARNING:</b> As {@link Ratio}s are based on floating point numbers, results may be rounded. Rounding is done
     * mathematical correctly.
     * </p>
     * 
     * @param ratio
     *            The fraction that should be added.
     * @return A new {@link Price} containing the result of this calculation.
     */
    public Price plus(final Ratio ratio) {
        return new Price(round((1.0 + ratio.asBasic()) * price));
    }

    /**
     * Subtract the value of another price from the value of this price.
     * 
     * @param other
     *            The other {@link Price} to subtract from this one.
     * @return A new {@link Price} containing the result of this calculation.
     */
    public Price minus(final Price other) {
        return new Price(price - other.price);
    }

    /**
     * Subtract a raw price interpreted as a given {@link PriceUnit} from this price and returns the result.
     * 
     * @param amount
     *            The raw price to subtract.
     * @param unit
     *            The unit of as which the raw price passed as argument should be interpreted.
     * @return A new {@link Price} containing the result of this calculation.
     */
    public Price minus(final int amount, final PriceUnit unit) {
        return new Price(price - amount * unit.getMultipleOfPipette());
    }

    /**
     * Subtract a fraction of this {@link Price} to this {@link Price} and returns the result.
     * 
     * <p>
     * <b>WARNING:</b> As {@link Ratio}s are based on floating point numbers, results may be rounded. Rounding is done
     * mathematical correctly.
     * </p>
     * 
     * @param value
     *            The raw value of the ratio to subtract.
     * @param unit
     *            The unit for the raw value.
     * @return A new {@link Price} containing the result of this calculation.
     * @see Ratio#Ratio(double, RatioUnit)
     */
    public Price minus(final double value, final RatioUnit unit) {
        return minus(new Ratio(value, unit));
    }

    /**
     * Subtract a fraction of this {@link Price} to this {@link Price} and returns the result.
     * 
     * <p>
     * <b>WARNING:</b> As {@link Ratio}s are based on floating point numbers, results may be rounded. Rounding is done
     * mathematical correctly.
     * </p>
     * 
     * @param ratio
     *            The ratio to subtract.
     * @return A new {@link Price} containing the result of this calculation.
     */
    public Price minus(final Ratio ratio) {
        return new Price(round((1.0 - ratio.asBasic()) * price));
    }

    /**
     * The additive inverse of this number.
     * 
     * <p>
     * That means that positive {@link Price}s are converted to negative {@link Price}s and negative {@link Price}s to
     * positive ones.
     * </p>
     * 
     * @return The additive inverse
     */
    public Price inverse() {
        return new Price(price * -1);
    }

    /**
     * The absolute version of this {@link Price}.
     * 
     * <p>
     * If this {@link Price} is positive it is returned as is. If it is negative the {@link #inverse()} is returned.
     * </p>
     * 
     * @return The absolute {@link Price}.
     */
    public Price absolute() {
        return price < 0 ? inverse() : this;
    }

    /**
     * Multiplies this {@link Price} with a given {@link Ratio}.
     * 
     * <p>
     * <b>WARNING:</b> As {@link Ratio}s are based on floating point numbers, results may be rounded. Rounding is done
     * mathematical correctly.
     * </p>
     * 
     * @param ratio
     *            The {@link Ratio} to multiply this {@link Price} with.
     * @return A new {@link Price} containing the result of this operation.
     */
    public Price multiply(final Ratio ratio) {
        return new Price(round(ratio.asBasic() * price));
    }

    /**
     * Multiplies this {@link Price} with a given {@link Volume} to create an absolute {@link Money} value.
     * 
     * <p>
     * <b>WARNING:</b> As {@link Money}s have less decimal places than {@link Price}s, results may be rounded. Rounding
     * is always done downwards.
     * </p>
     * 
     * @param volume
     *            The volume with which to multiply this price.
     * @param currency
     *            The currency of the resulting {@link Money}.
     * @return The {@link Money} which is the result of this calculation.
     */
    public Money multiply(final Volume volume, final Currency currency) {
        return new Money(price * volume.asAbsolute() / 1000, currency);
    }

    /**
     * Divides two {@link Price}s.
     * 
     * <p>
     * This {@link Price} is used as dividend and the passed {@link Price} as divisor.
     * </p>
     * 
     * <p>
     * <b>WARNING:</b> Results are rounded as it would occur when dividing two double values.
     * </p>
     * 
     * @param divisor
     *            The divisor which should be used for the division.
     * @return The quotient in form of a {@link Ratio}.
     */
    public Ratio divide(final Price divisor) {
        return new Ratio((double) price / (double) divisor.price);
    }

    /**
     * Checks if this {@link Price} is lesser than an other {@link Price}.
     * 
     * @param other
     *            The other {@link Price} to compare this {@link Price} with.
     * @return <code>true</code> if <code>this</code> is lesser than <code>other</code> and <code>false</code> if not.
     */
    public boolean isLessThan(final Price other) {
        return price < other.price;
    }

    /**
     * Checks if this {@link Price} is greater than an other {@link Price}.
     * 
     * @param other
     *            The other {@link Price} to compare this {@link Price} with.
     * @return <code>true</code> if <code>this</code> is greater than <code>other</code> and <code>false</code> if not.
     */
    public boolean isGreaterThan(final Price other) {
        return price > other.price;
    }

    /**
     * Checks if this {@link Price} is stronger than an other {@link Price} considering a given market direction.
     * 
     * @param other
     *            The other {@link Price} to compare this {@link Price} with.
     * @param direction
     *            The market direction that should be considered.
     * @return <code>true</code> if this {@link Price} is stronger and <code>false</code> if it isn't or if its equal to
     *         the other {@link Price}.
     */
    public boolean isStrongerThan(final Price other, final MarketDirection direction) {
        if (direction == MarketDirection.UP) {
            return isGreaterThan(other);
        } else {
            return isLessThan(other);
        }
    }

    /**
     * Checks if this {@link Price} is weaker than an other {@link Price} considering a given market direction.
     * 
     * @param other
     *            The other {@link Price} to compare this {@link Price} with.
     * @param direction
     *            The market direction that should be considered.
     * @return <code>true</code> if this {@link Price} is weaker and <code>false</code> if it isn't or if its equal to
     *         the other {@link Price}.
     */
    public boolean isWeakerThan(final Price other, final MarketDirection direction) {
        if (direction == MarketDirection.UP) {
            return isLessThan(other);
        } else {
            return isGreaterThan(other);
        }
    }

    /**
     * Checks if this {@link Price} is between two other {@link Price}s.
     * 
     * @param a
     *            The first {@link Price} of the range this {@link Price} should be in.
     * @param b
     *            The second {@link Price} of the range this {@link Price} should be in.
     * @return <code>true</code> if this {@link Price} equals <code>a</code> or <code>b</code> or is between these two
     *         prices.
     */
    public boolean isBetweenInclusive(final Price a, final Price b) {
        final long min = a.price < b.price ? a.price : b.price;
        final long max = a.price > b.price ? a.price : b.price;

        return price >= min && price <= max;
    }

    /**
     * A {@link Price} is equal only to other {@link Price}s thats {@link #asPipette()} is the same.
     * 
     * <p>
     * Only other {@link Price} objects can be equal to a {@link Price} object.
     * </p>
     * 
     * @param other
     *            The object to compare with.
     * @return <code>true</code> when <code>this</code> and <code>obj</code> are equal and <code>false</code> if not.
     */
    @Override
    public boolean equals(final Object other) {
        if (other == null || !(other instanceof Price)) {
            return false;
        }

        final Price otherPrice = (Price) other;
        if (price == otherPrice.price) {
            return true;
        }

        return false;
    }

    /**
     * A {@link Price} is lower than, equal to or greater than another {@link Price} when its {@link #asPipette()} value
     * is lower than, equal to or greater than the {@link #asPipette()} value of the other price.
     * 
     * @param other
     *            The other price to compare this price to.
     * @return A value &lt;0 if this price is lesser, 0 when this price equal to and a value &gt;0 if this price is
     *         greater then the other price.
     */
    @Override
    public int compareTo(final Price other) {
        return price < other.price ? -1 : price == other.price ? 0 : 1;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (price ^ (price >>> 32));
        return result;
    }

    /**
     * Converts this {@link Price} to a string that has always a sign even when its positive.
     * 
     * @return The {@link String} representation of this {@link Price}.
     */
    public String toStringWithSign() {
        final long absolute = absolute().price;
        return (price < 0 ? "-" : "+") + absolute / ACCURACY + "." + String.format("%05d", absolute % ACCURACY);
    }

    @Override
    public String toString() {
        final long absolute = absolute().price;
        return (price < 0 ? "-" : "") + absolute / ACCURACY + "." + String.format("%05d", absolute % ACCURACY);
    }
}
