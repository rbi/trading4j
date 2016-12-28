package de.voidnode.trading4j.indicators.adx;

import java.util.Optional;

import static java.lang.Math.abs;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.Ratio;
import de.voidnode.trading4j.domain.marketdata.CandleStick;

/**
 * The direction movement index (DMI) can be used to detect trend changes.
 * 
 * <p>
 * Usually the DMI is only used in the smoothed version average directional movement index (ADX). Therefore this class
 * is package private.
 * </p>
 * 
 * <p>
 * The DMI is in many sources calculated as follows:
 * </p>
 * <ul>
 * <li><code>DMI(t) = abs(+DI(t) - -DI(t)) / (+DI(t) + -DI(t))</code></li>
 * <li><code>+DI(t) = (+DM(t)) / TrueRange(t)</code></li>
 * <li><code>-DI(t) = (-DM(-)) / TrueRange(t)</code></li>
 * </ul>
 * 
 * <p>
 * The <code>TrueRange</code> can however be canceled out. It seams only be necessary if the <code>DI</code> values are
 * actually of interest by them selves. The simplified formula is the following:
 * </p>
 * 
 * <code>DMI(t) = abs(+DM(t) - -DM(t)) / (+DM(t) + -DM(t))</code>
 * 
 * @author Raik Bieniek
 * @param <C>
 *            The concrete candle stick type that is used as input.
 */
class DirectionalMovementIndex<C extends CandleStick<?>> implements Indicator<Ratio, C> {

    private final Indicator<Ratio, C> averagePlusDi;
    private final Indicator<Ratio, C> averageMinusDi;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param averagePlusDi
     *            The average positive directional indicator.
     * @param averageMinusDi
     *            The average negative directional indicator.
     */
    DirectionalMovementIndex(final Indicator<Ratio, C> averagePlusDi, final Indicator<Ratio, C> averageMinusDi) {
        this.averagePlusDi = averagePlusDi;
        this.averageMinusDi = averageMinusDi;
    }

    @Override
    public Optional<Ratio> indicate(final C marketPrice) {
        final Optional<Ratio> optPlusDi = averagePlusDi.indicate(marketPrice);
        final Optional<Ratio> optMinusDi = averageMinusDi.indicate(marketPrice);

        return optPlusDi.flatMap(plusDi -> optMinusDi.map(minusDi -> new Ratio(
                abs((plusDi.asBasic() - minusDi.asBasic()) / (plusDi.asBasic() + minusDi.asBasic())))));
    }

}
