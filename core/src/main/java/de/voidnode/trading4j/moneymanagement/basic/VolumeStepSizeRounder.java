package de.voidnode.trading4j.moneymanagement.basic;

import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.VolumeUnit;

/**
 * Rounds {@link Volume}s down to match fixed {@link Volume} step sizes.
 * 
 * @author Raik Bieniek
 */
class VolumeStepSizeRounder {

    /**
     * Rounds a {@link Volume} down to the nearest multiple of a given step size.
     * 
     * @param volumeToRound
     *            The {@link Volume} to round.
     * @param stepSize
     *            The allowed step size.
     * @return The rounded {@link Volume} .
     */
    public Volume round(final Volume volumeToRound, final Volume stepSize) {
        return new Volume(volumeToRound.asAbsolute() - volumeToRound.asAbsolute() % stepSize.asAbsolute(),
                VolumeUnit.BASE);
    }
}
