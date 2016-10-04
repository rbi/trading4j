package de.voidnode.trading4j.indicators;

import java.util.Optional;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.TimeFrame;
import de.voidnode.trading4j.domain.TimeFrame.D1;
import de.voidnode.trading4j.domain.marketdata.CandleStick;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.monetary.PriceLevels;
import de.voidnode.trading4j.domain.monetary.PriceUnit;

/**
 * Pivot Points indicate support and resistance levels for market prices.
 * 
 * <p>
 * Pivot points for the current {@link CandleStick} are calculated based on the last {@link CandleStick}. The most
 * common {@link TimeFrame}s for pivot points are {@link D1} or one week.
 * </p>
 * 
 * <p>
 * This indicator produces 5 {@link Price} levels. 2 support levels S1 and S2, a middle pivot point P and 2 resistance
 * levels R1 and R2. The are calculated with the following way.
 * </p>
 * 
 * <dl>
 * <dt>R2</dt>
 * <dd>=P + (high - low)</dd>
 * <dt>R1</dt>
 * <dd>=(P * 2) - low</dd>
 * <dt>P</dt>
 * <dd>=(high + low + close) / 3</dd>
 * <dt>S1</dt>
 * <dd>=(P * 2) - high</dd>
 * <dt>S2</dt>
 * <dd>=P - (high - low)</dd>
 * </dl>
 * 
 * <p>
 * The price of the main pivot point is rounded down to the next full {@link PriceUnit#PIPETTE}.
 * </p>
 *
 * @author Raik Bieniek
 * @param <C>
 *            The type of {@link CandleStick} that the indicator should be using for the calculations.
 */
public class PivotPointsIndicator<C extends CandleStick<?>> implements Indicator<PriceLevels, C> {

    @Override
    public Optional<PriceLevels> indicate(final C candle) {
        final long high = candle.getHigh().asPipette();
        final long low = candle.getLow().asPipette();
        final long close = candle.getClose().asPipette();

        final long p = (high + low + close) / 3;

        final long r1 = (2 * p) - low;
        final long r2 = p + (high - low);

        final long s1 = (2 * p) - high;
        final long s2 = p - (high - low);

        return Optional.of(new PriceLevels(new Price(s2), new Price(s1), new Price(p), new Price(r1), new Price(r2)));
    }

}
