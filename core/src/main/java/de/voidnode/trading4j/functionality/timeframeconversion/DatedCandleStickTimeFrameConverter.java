package de.voidnode.trading4j.functionality.timeframeconversion;

import de.voidnode.trading4j.domain.marketdata.impl.DatedCandleStick;
import de.voidnode.trading4j.domain.marketdata.impl.MutableFullMarketData;
import de.voidnode.trading4j.domain.timeframe.TimeFrame;

/**
 * Aggregates {@link DatedCandleStick}s or of a smaller {@link TimeFrame} to {@link DatedCandleStick}s of a larger
 * {@link TimeFrame}.
 * 
 * <p>
 * For a more detailed description see {@link TimeFrameConverter}. This implementation aggregates only fields of
 * {@link DatedCandleStick}s.
 * </p>
 * 
 * @author Raik Bieniek
 *
 * @param <CIN>
 *            The type of {@link DatedCandleStick} that is used as input.
 * @param <ITF>
 *            The {@link TimeFrame} that the {@link DatedCandleStick}s passed as input have.
 * @param <OTF>
 *            The {@link TimeFrame} that the aggregated {@link DatedCandleStick}s should have.
 */
public class DatedCandleStickTimeFrameConverter<CIN extends DatedCandleStick<ITF>, ITF extends TimeFrame, OTF extends TimeFrame>
        extends TimeFrameConverter<CIN, DatedCandleStick<OTF>, ITF, OTF> {

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param inputTimeFrame
     *            The shorter {@link TimeFrame} that input candles have.
     * @param outputTimeFrame
     *            The longer {@link TimeFrame} that outputed candles will have.
     */
    public DatedCandleStickTimeFrameConverter(final ITF inputTimeFrame, final OTF outputTimeFrame) {
        super(inputTimeFrame, outputTimeFrame);
    }

    @Override
    protected DatedCandleStick<OTF> buildAggregatedStick(final MutableFullMarketData<OTF> aggregated) {
        return aggregated.toImmutableDatedCandleStick();
    }

    @Override
    protected void aggregateAdditionalFields(final CIN candleStick, final boolean isOpen, final boolean isClose) {
        // There are no additional fields besides the fields aggregated by the super class.
    }

}
