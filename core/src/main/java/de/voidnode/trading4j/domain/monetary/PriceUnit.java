package de.voidnode.trading4j.domain.monetary;

/**
 * Units in which {@link Price}s are measured.
 * 
 * <p>
 * These unit definitions are independent of the concrete currency pair that is traded or the broker where it is traded.
 * E.g. that means that the definition of a {@link #PIP} also apply for the Japanese Yen.
 * </p>
 * 
 * @author Raik Bieniek
 */
public enum PriceUnit {

    /**
     * The major unit of a currency.
     * 
     * <p>
     * Examples are <em>Euro</em> or <em>US Dollar</em>.
     * </p>
     */
    MAJOR(100000),

    /**
     * The minor unit of a currency.
     * 
     * <p>
     * Examples are <em>Euro Cents</em>. The value of 1 <em>minor</em> is always equal to 1 * 10^-2 <em>major</em>,
     * regardless of the currency.
     * </p>
     */
    MINOR(1000),

    /**
     * A unit smaller than the minor unit of a currency.
     * 
     * <p>
     * The value of 1 <em>pip</em> is always equal to 1 * 10^-4 <em>major</em>, regardless of the currency.
     * </p>
     */
    PIP(10),

    /**
     * A unit smaller than a pip.
     * 
     * <p>
     * The value of 1 <em>pipette</em> is always equal to 1 * 10^-5 <em>major</em>, regardless of the currency.
     * </p>
     */
    PIPETTE(1);

    private final int multipleOfPipette;

    /**
     * Initializes the enum.
     * 
     * @param multipleOfPipette
     *            see {@link #getMultipleOfPipette()}
     */
    PriceUnit(final int multipleOfPipette) {
        this.multipleOfPipette = multipleOfPipette;
    }

    /**
     * How many {@link #PIPETTE}s are an equal value to 1 of this unit.
     * 
     * @return The multiple of a pipette.
     */
    int getMultipleOfPipette() {
        return multipleOfPipette;
    }
}
