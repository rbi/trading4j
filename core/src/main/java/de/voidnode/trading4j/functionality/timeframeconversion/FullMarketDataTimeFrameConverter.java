package de.voidnode.trading4j.functionality.timeframeconversion;

import de.voidnode.trading4j.domain.TimeFrame;
import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.VolumeUnit;
import de.voidnode.trading4j.domain.marketdata.DatedCandleStick;
import de.voidnode.trading4j.domain.marketdata.FullMarketData;
import de.voidnode.trading4j.domain.marketdata.MutableFullMarketData;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * Aggregates {@link FullMarketData}s or of a smaller {@link TimeFrame} to {@link FullMarketData}s of a larger
 * {@link TimeFrame}.
 * 
 * <p>
 * For a more detailed description see {@link TimeFrameConverter}. In addition to the fields of {@link DatedCandleStick}
 * this implementation also aggregates the additional fields of {@link FullMarketData}s as one whould expect.
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
public class FullMarketDataTimeFrameConverter<CIN extends FullMarketData<ITF>, ITF extends TimeFrame, OTF extends TimeFrame>
        extends TimeFrameConverter<CIN, FullMarketData<OTF>, ITF, OTF> {

    private int aggregateCount = 0;
    private long volume = 0;
    private long tickCount = 0;
    private long spreadSum = 0;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param inputTimeFrame
     *            The shorter {@link TimeFrame} that input candles have.
     * @param outputTimeFrame
     *            The longer {@link TimeFrame} that outputed candles will have.
     */
    public FullMarketDataTimeFrameConverter(final ITF inputTimeFrame, final OTF outputTimeFrame) {
        super(inputTimeFrame, outputTimeFrame);
    }

    @Override
    protected void aggregateAdditionalFields(final CIN candleStick, final boolean isOpen, final boolean isClose) {
        aggregateCount++;
        volume += candleStick.getVolume().asAbsolute();
        tickCount += candleStick.getTickCount();
        spreadSum += candleStick.getSpread().asPipette();
    }

    @Override
    protected FullMarketData<OTF> buildAggregatedStick(final MutableFullMarketData<OTF> aggregated) {
        aggregated.setVolume(new Volume(volume, VolumeUnit.BASE));
        aggregated.setTickCount(tickCount);
        // Aggregation count should never be 0. If it is this is a bug anyway.
        aggregated.setSpread(new Price(spreadSum / aggregateCount));

        aggregateCount = 0;
        volume = 0;
        tickCount = 0;
        spreadSum = 0;

        return aggregated.toImmutableFullMarketData();
    }

}
