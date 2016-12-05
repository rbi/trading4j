package de.voidnode.trading4j.moneymanagement.standard;

import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.VolumeUnit;
import de.voidnode.trading4j.domain.monetary.AccuratePrice;
import de.voidnode.trading4j.domain.monetary.Money;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * Calculates the volume spend for a new trade.
 * 
 * @author Raik Bieniek
 */
class VolumeCalculator {

    /**
     * Calculate the volume for a single trade.
     *
     * @param pricePerPipette
     *            The worth of a single Pipette.
     * @param pipLostOnStopLoose
     *            The amount of PIP that are lost in case the trade is closed through stop loose.
     * @param moneyToRisk
     *            The {@link Money} that should be lost in case the trade is closed through stop loose.
     * @return The volume for the trade.
     */
    public Volume calculateVolumeForTrade(final AccuratePrice pricePerPipette, final Price pipLostOnStopLoose,
            final Money moneyToRisk) {
        final double rawMoney = (double) moneyToRisk.asRawValue() / 100;
        final double loosePer1Volume = pricePerPipette.asRawValue() * pipLostOnStopLoose.asPipette();
        return new Volume((long) (rawMoney / loosePer1Volume), VolumeUnit.BASE);
    }
}
