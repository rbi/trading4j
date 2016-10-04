package de.voidnode.trading4j.domain;

/**
 * A unit for the {@link Ratio}.
 * 
 * @author Raik Bieniek
 */
public enum RatioUnit {

    /**
     * The "none" unit which the pure ratio has when dividing one unit through another.
     */
    BASIC(1),

    /**
     * The ratio expressed in percentage.
     */
    PERCENT(100),

    /**
     * The ratio expressed in per mille.
     */
    PERMILLE(1000),

    /**
     * The ratio expressed in multiples of 1 PIP:1 Base Unit.
     */
    RELATIVE_PIP(10000),

    /**
     * The ratio expressed in multiples of 1 PIPETTE:1 Base Unit.
     */
    RELATIVE_PIPETTE(100000);

    private final int fraction;

    /**
     * Initializes the enum.
     * 
     * @param fraction
     *            #see {@link #getFraction()}
     */
    RatioUnit(final int fraction) {
        this.fraction = fraction;
    }

    /**
     * The value with which the ratio with the {@link #BASE} unit has to be multiplied.
     * 
     * @return The fraction
     */
    int getFraction() {
        return fraction;
    }
}
