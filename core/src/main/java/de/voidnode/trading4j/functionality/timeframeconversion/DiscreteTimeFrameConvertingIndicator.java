package de.voidnode.trading4j.functionality.timeframeconversion;

import java.util.Optional;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.marketdata.CandleStick;
import de.voidnode.trading4j.domain.marketdata.DatedCandleStick;
import de.voidnode.trading4j.domain.timeframe.TimeFrame;

/**
 * A wrapper for discontinues {@link Indicator}s that converts the {@link TimeFrame} of the input {@link CandleStick} s.
 * 
 * <p>
 * A {@link MarketDirection} is only produced for the passed {@link DatedCandleStick} that completed a {@link DatedCandleStick} of
 * the output {@link TimeFrame} if the original {@link Indicator} produced a {@link MarketDirection} for the aggregated
 * {@link DatedCandleStick}s for all other passed {@link DatedCandleStick} an empty {@link Optional} is returned;
 * </p>
 * 
 * @author Raik Bieniek
 * @param <IT>
 *            The type of the indication result produced by the original {@link Indicator}.
 * @param <CIN>
 *            The type of {@link CandleStick} that is used as input.
 * @param <COUT>
 *            The type of {@link CandleStick} that the original indicator expects.
 * @param <ITF>
 *            The {@link TimeFrame} of the {@link CandleStick} that are used as input
 * @param <OTF>
 *            The {@link TimeFrame} of the {@link CandleStick} inputs that the original {@link Indicator} has.
 */
public class DiscreteTimeFrameConvertingIndicator<IT, CIN extends DatedCandleStick<ITF>, COUT extends DatedCandleStick<OTF>, ITF extends TimeFrame, OTF extends TimeFrame>
        implements Indicator<IT, CIN> {

    private final Indicator<IT, DatedCandleStick<OTF>> orig;
    private final TimeFrameConverter<CIN, COUT, ITF, OTF> timeFrameConverter;

    /**
     * Initializes the instance.
     * 
     * @param orig
     *            The original indicator that the time frame is converted for.
     * @param timeFrameConverter
     *            The converter doing the time frame conversion.
     */
    public DiscreteTimeFrameConvertingIndicator(final Indicator<IT, DatedCandleStick<OTF>> orig,
            final TimeFrameConverter<CIN, COUT, ITF, OTF> timeFrameConverter) {
        this.timeFrameConverter = timeFrameConverter;
        this.orig = orig;
    }

    @Override
    public Optional<IT> indicate(final CIN candle) {
        return timeFrameConverter.aggregate(candle).flatMap(c -> orig.indicate(c));
    }
}
