package de.voidnode.trading4j.functionality.timeframeconversion;

import de.voidnode.trading4j.api.ExpertAdvisor;
import de.voidnode.trading4j.domain.TimeFrame;
import de.voidnode.trading4j.domain.marketdata.DatedCandleStick;

/**
 * An {@link ExpertAdvisor} wrapper that aggregates {@link DatedCandleStick} of smaller time frames for
 * {@link ExpertAdvisor} that expect higher time frames.
 * 
 * @author Raik Bieniek
 * @param <CIN>
 *            The type of {@link DatedCandleStick} that is used as input.
 * @param <COUT>
 *            The type of {@link DatedCandleStick} that is expected as input for the original {@link ExpertAdvisor}.
 * @param <ITF>
 *            The {@link TimeFrame} of the {@link DatedCandleStick} that are used as input
 * @param <OTF>
 *            The {@link TimeFrame} of the {@link DatedCandleStick} that is converted to.
 */
public class TimeFrameConvertingExpertAdvisor<CIN extends DatedCandleStick<ITF>, COUT extends DatedCandleStick<OTF>, ITF extends TimeFrame, OTF extends TimeFrame>
        implements ExpertAdvisor<CIN> {

    private final ExpertAdvisor<COUT> orig;
    private final TimeFrameConverter<CIN, COUT, ITF, OTF> converter;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param orig
     *            The original expert advisor with the shorter time frame.
     * @param converter
     *            The converter that is suited to convert time frames.
     */
    public TimeFrameConvertingExpertAdvisor(final ExpertAdvisor<COUT> orig,
            final TimeFrameConverter<CIN, COUT, ITF, OTF> converter) {
        this.orig = orig;
        this.converter = converter;
    }

    @Override
    public void newData(final CIN marketData) {
        converter.aggregate(marketData).ifPresent(aggregated -> orig.newData(aggregated));
    }
}
