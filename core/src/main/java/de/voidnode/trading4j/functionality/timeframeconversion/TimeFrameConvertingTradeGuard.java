package de.voidnode.trading4j.functionality.timeframeconversion;

import java.util.Optional;

import de.voidnode.trading4j.api.Failed;
import de.voidnode.trading4j.api.OrderFilter;
import de.voidnode.trading4j.domain.TimeFrame;
import de.voidnode.trading4j.domain.marketdata.CandleStick;
import de.voidnode.trading4j.domain.marketdata.DatedCandleStick;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;

/**
 * A wrapper for continues {@link OrderFilter}s that converts the {@link TimeFrame} of the input {@link CandleStick}s.
 * 
 * <p>
 * The wrapped {@link OrderFilter} will
 * </p>
 *
 * @author Raik Bieniek
 * @param <CIN>
 *            The type of {@link CandleStick} that is used as input.
 * @param <COUT>
 *            The type of {@link CandleStick} that is expected by the original {@link OrderFilter}.
 * @param <ITF>
 *            The {@link TimeFrame} of the {@link CandleStick} that are used as input
 * @param <OTF>
 *            The {@link TimeFrame} of the {@link CandleStick} that is converted to.
 */
public class TimeFrameConvertingTradeGuard<CIN extends DatedCandleStick<ITF>, COUT extends DatedCandleStick<OTF>, ITF extends TimeFrame, OTF extends TimeFrame>
        implements OrderFilter<CIN> {

    private static final Optional<Failed> NO_CANDLE_COMPLETED = Optional
            .of(new Failed("Trade was blocked because no complete candle stick was aggregated yet."));
    private final TimeFrameConverter<CIN, COUT, ITF, OTF> timeFrameConverter;
    private final OrderFilter<DatedCandleStick<OTF>> orig;

    private boolean firstCandleCompleted;

    /**
     * Initializes the instance.
     * 
     * @param orig
     *            The original trade guard that the time frame is converted for.
     * @param timeFrameConverter
     *            The converter doing the time frame conversion.
     */
    public TimeFrameConvertingTradeGuard(final OrderFilter<DatedCandleStick<OTF>> orig,
            final TimeFrameConverter<CIN, COUT, ITF, OTF> timeFrameConverter) {
        this.timeFrameConverter = timeFrameConverter;
        this.orig = orig;
    }

    @Override
    public void updateMarketData(final CIN marketData) {
        timeFrameConverter.aggregate(marketData).ifPresent(aggregated -> {
            firstCandleCompleted = true;
            orig.updateMarketData(aggregated);
        });
    }

    @Override
    public Optional<Failed> filterOrder(final BasicPendingOrder order) {
        if (!firstCandleCompleted) {
            return NO_CANDLE_COMPLETED;
        }
        return orig.filterOrder(order);
    }
}
