package de.voidnode.trading4j.functionality.timeframeconversion;

import java.util.Optional;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.marketdata.impl.DatedCandleStick;
import de.voidnode.trading4j.domain.timeframe.TimeFrame;

/**
 * A wrapper for continues {@link Indicator}s that converts the {@link TimeFrame} of the input {@link DatedCandleStick}s.
 * 
 * <p>
 * Until more {@link DatedCandleStick}s are collected to complete a {@link DatedCandleStick} of the output
 * {@link TimeFrame} the last trend of the original {@link Indicator} is returned.
 * </p>
 *
 * @author Raik Bieniek
 * @param <IT>
 *            The type of the indication result produced by the original {@link Indicator}.
 * @param <CIN>
 *            The type of {@link DatedCandleStick} that is used as input.
 * @param <COUT>
 *            The type of {@link DatedCandleStick} that the original indicator expects.
 * @param <ITF>
 *            The {@link TimeFrame} of the {@link DatedCandleStick} that are used as input
 * @param <OTF>
 *            The {@link TimeFrame} of the {@link DatedCandleStick} inputs that the original {@link Indicator} has.
 */
public class ContinouseTimeFrameConvertingIndicator<IT, CIN extends DatedCandleStick<ITF>, COUT extends DatedCandleStick<OTF>, ITF extends TimeFrame, OTF extends TimeFrame>
        implements Indicator<IT, CIN> {

    private final Indicator<IT, COUT> orig;
    private final TimeFrameConverter<CIN, COUT, ITF, OTF> timeFrameConverter;

    private Optional<IT> lastTrend = Optional.empty();

    /**
     * Initializes the instance.
     * 
     * @param orig
     *            The original indicator that the time frame is converted for.
     * @param timeFrameConverter
     *            The converter doing the time frame conversion.
     */
    public ContinouseTimeFrameConvertingIndicator(final Indicator<IT, COUT> orig,
            final TimeFrameConverter<CIN, COUT, ITF, OTF> timeFrameConverter) {
        this.timeFrameConverter = timeFrameConverter;
        this.orig = orig;
    }

    @Override
    public Optional<IT> indicate(final CIN candle) {
        lastTrend = timeFrameConverter.aggregate(candle).map(c -> orig.indicate(c)).orElse(lastTrend);
        return lastTrend;
    }
}
