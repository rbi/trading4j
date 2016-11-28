package de.voidnode.trading4j.api;

import java.util.Optional;

import de.voidnode.trading4j.domain.ForexSymbol;
import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * Lends volume that for the trades of {@link ExpertAdvisor}s.
 * 
 * <p>
 * Implementations of this interface will decide them self how many {@link Volume} they will lend. Expert advisors can
 * not request a fixed {@link Volume}.
 * </p>
 * 
 * <p>
 * This is usually implemented together with an {@link AccountBalanceManager} to provide a
 * {@link MoneyManagement}.
 * </p>
 * 
 * @author Raik Bieniek
 */
public interface VolumeLender {

    /**
     * Request a {@link Volume} for a single trade.
     * 
     * 
     * @param symbol
     *            The symbol that should be traded.
     * @param currentPrice
     *            The last known price of the <code>symbol</code> that should be traded.
     * @param pipLostOnStopLoose
     *            The {@link Price} that the <code>symbol</code> will have fallen in case the trade is closed by the
     *            stop loose limit.
     * @param allowedStepSize
     *            The allowed step size for {@link Volume}s. All {@link Volume}s used for trading should be multiples of
     *            this step size.
     * @return An instance to manage the {@link Volume} that was granted for the trade or an empty {@link Optional} if
     *         no {@link Volume} was granted.
     */
    Optional<UsedVolumeManagement> requestVolume(ForexSymbol symbol, Price currentPrice, Price pipLostOnStopLoose,
            Volume allowedStepSize);
}
