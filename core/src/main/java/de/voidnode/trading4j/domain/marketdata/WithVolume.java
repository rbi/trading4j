package de.voidnode.trading4j.domain.marketdata;

import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.timeframe.TimeFrame;

/**
 * The volume of the security that was traded in the {@link TimeFrame} of this {@link MarketData}.
 * 
 * @author Raik Bieniek
 */
public interface WithVolume {

    /**
     * The volume of the security that was traded in the {@link TimeFrame} of this {@link MarketData}.
     * 
     * @return The volume
     */
    Volume getVolume();
}
