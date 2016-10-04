package de.voidnode.trading4j.domain;

import de.voidnode.trading4j.domain.monetary.PriceUnit;

/**
 * The unit in which {@link Volume}s of assets are measured.
 * 
 * <p>
 * Volumes are measuered in lots. Lots describe an amount of the base currency in a currency pair.
 * </p>
 * 
 * @author Raik Bieniek
 */
public enum VolumeUnit {

    /**
     * 1 Lot equals 100 000 units of the base currency.
     */
    LOT(100000),

    /**
     * 1 Mini-Lot equals 10 000 units of the base currency.
     * 
     * <p>
     * 1 Mini-Lot equals 0.1 Lot
     * </p>
     */

    MINI_LOT(10000),

    /**
     * 1 Micro-Lot equals 1 000 units of the base currency.
     * 
     * <p>
     * 1 Micro-Lot equals 0.01 Lot
     * </p>
     */
    MICRO_LOT(1000),

    /**
     * 1 Nano-Lot equals 100 units of the base currency.
     * 
     * <p>
     * 1 Nano-Lot equals 0.001 Lot
     * </p>
     */
    NANO_LOT(100),

    /**
     * The unit representing 1 {@link PriceUnit#MAJOR} of the base currency.
     * 
     * <p>
     * 1 Base equals 0.00001 Lot
     * </p>
     */
    BASE(1);

    private final int multiplesOfBase;

    /**
     * Initializes the enum constant.
     * 
     * @param multiplesOfBase
     *            see {@link #getMultiplesOfBase()}
     */
    VolumeUnit(final int multiplesOfBase) {
        this.multiplesOfBase = multiplesOfBase;
    }

    /**
     * The amount of {@link #BASE} that is equal to 1 of this unit.
     * 
     * @return The multiples of {@link #BASE}
     */
    int getMultiplesOfBase() {
        return multiplesOfBase;
    }
}
