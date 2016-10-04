package de.voidnode.trading4j.api;

import de.voidnode.trading4j.domain.Volume;

/**
 * Allows to manage the {@link Volume} granted by the {@link MoneyManagement} for a single trade.
 * 
 * @author Raik Bieniek
 */
public interface UsedVolumeManagement {

    /**
     * The volume that was granted for the trade.
     * 
     * @return The volume
     */
    Volume getVolume();

    /**
     * Return the granted {@link Volume} back to the money management for further. 
     */
    void releaseVolume();
}
