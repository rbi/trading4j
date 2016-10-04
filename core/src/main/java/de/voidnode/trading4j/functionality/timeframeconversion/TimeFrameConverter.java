package de.voidnode.trading4j.functionality.timeframeconversion;

import java.util.Optional;

import de.voidnode.trading4j.domain.TimeFrame;
import de.voidnode.trading4j.domain.marketdata.DatedCandleStick;
import de.voidnode.trading4j.domain.marketdata.MutableFullMarketData;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * Aggregates {@link DatedCandleStick}s or of a smaller {@link TimeFrame} to {@link DatedCandleStick}s of a larger
 * {@link TimeFrame}.
 * 
 * <p>
 * This base implementation does not know how to actually build concrete {@link DatedCandleStick} instances or its
 * subclasses. Use the concrete implementations for this.
 * </p>
 * 
 * <p>
 * This converter drops all {@link DatedCandleStick} until a {@link DatedCandleStick} is passed that is in a different
 * time frame than the previous frame. This way the converter produces {@link DatedCandleStick} that aggregates all
 * {@link DatedCandleStick} from the smaller input time frame with the first {@link DatedCandleStick} produced as
 * output.
 * </p>
 * 
 * @author Raik Bieniek
 * @param <CIN>
 *            The type of {@link DatedCandleStick} that is used as input.
 * @param <COUT>
 *            The type of {@link DatedCandleStick} that is produced.
 * @param <ITF>
 *            The {@link TimeFrame} that the {@link DatedCandleStick}s passed as input have.
 * @param <OTF>
 *            The {@link TimeFrame} that the aggregated {@link DatedCandleStick}s should have.
 */
public abstract class TimeFrameConverter<CIN extends DatedCandleStick<ITF>, COUT extends DatedCandleStick<OTF>, ITF extends TimeFrame, OTF extends TimeFrame> {

    private final ITF inputTf;
    private final OTF outputTf;

    private final MutableFullMarketData<OTF> aggregatedCandleStick = new MutableFullMarketData<>();

    private boolean initialisationPhase = true;
    private CIN lastCandleStick;
    private Optional<COUT> nextReturnVal = Optional.empty();
    private boolean currentIsStart;
    private boolean nextIsStart;

    /**
     * Initializes the converter.
     * 
     * @param inputTimeFrame
     *            The {@link TimeFrame} that the original {@link DatedCandleStick}s have.
     * @param outputTimeFrame
     *            The {@link TimeFrame} that is converted to.
     */
    public TimeFrameConverter(final ITF inputTimeFrame, final OTF outputTimeFrame) {
        this.inputTf = inputTimeFrame;
        this.outputTf = outputTimeFrame;
    }

    /**
     * Aggregates an other input {@link DatedCandleStick} to the current {@link DatedCandleStick} of the output
     * {@link TimeFrame}.
     * 
     * @param candle
     *            The {@link DatedCandleStick} to aggregate.
     * @return If a {@link DatedCandleStick} of the output {@link TimeFrame} is completed with <code>input</code> than
     *         it is returned. If the next {@link DatedCandleStick} of the output {@link TimeFrame} is not yet completed
     *         than an empty {@link Optional} is returned.
     */
    public Optional<COUT> aggregate(final CIN candle) {
        if (lastCandleStick == null) {
            lastCandleStick = candle;
            return Optional.empty();
        }

        this.currentIsStart = !outputTf.areInSameTimeFrame(lastCandleStick.getTime(), candle.getTime());
        this.nextIsStart = !outputTf.areInSameTimeFrame(candle.getTime(), inputTf.instantOfNextFrame(candle.getTime()));

        final Optional<COUT> returnVal = initialisationPhase ? initialisationRound(candle)
                : handleLateAggregatedCandles(normalRound(candle));

        this.lastCandleStick = candle;
        return returnVal;
    }

    /**
     * Aggregates additional fields of a {@link DatedCandleStick} sub class that are <b>not</b> already present
     * {@link DatedCandleStick}.
     * 
     * <p>
     * The implementation should store intermediary result of the aggregation internally until
     * {@link #buildAggregatedStick(MutableFullMarketData)} is called. If a sub class contains a field
     * <code>spread</code> in an aggregated version probably the average spread needs to be calculated.
     * </p>
     * 
     * @param candleStick
     *            The current unaggregated data of the input candle stick with the lower time frame.
     * @param isOpen
     *            <code>true</code> when this is the first data point for an aggregated candle stick.
     * @param isClose
     *            <code>true</code> when this is the last data point for an aggregated candle stick.
     */
    protected abstract void aggregateAdditionalFields(final CIN candleStick, final boolean isOpen,
            final boolean isClose);

    /**
     * Build the immutable candle stick based on previously aggregated data.
     * 
     * <p>
     * The implementation should pass the aggregation results of all
     * {@link #aggregateAdditionalFields(DatedCandleStick, boolean, boolean)} calls since the last call to this method
     * to the {@link MutableFullMarketData}. It should than call the appropriate builder function of
     * {@link MutableFullMarketData} and return the result.
     * </p>
     * <p>
     * The implementation must not change any fields of {@link MutableFullMarketData} that are also present in
     * {@link DatedCandleStick}.
     * </p>
     * 
     * @param aggregated
     *            The aggregation results of {@link DatedCandleStick} fields to which own aggregation results should be
     *            added.
     * @return The aggregated candle stick.
     */
    protected abstract COUT buildAggregatedStick(final MutableFullMarketData<OTF> aggregated);

    private Optional<COUT> handleLateAggregatedCandles(final Optional<COUT> aggregated) {
        if (nextReturnVal.isPresent()) {
            final Optional<COUT> returnVal = nextReturnVal;
            if (aggregated.isPresent()) {
                nextReturnVal = aggregated;
            } else {
                nextReturnVal = Optional.empty();
            }
            return returnVal;
        }
        return aggregated;
    }

    private Optional<COUT> initialisationRound(final CIN candle) {
        if (currentIsStart) {
            initialisationPhase = false;
            aggregateCandleStick(candle, true, nextIsStart);
            if (nextIsStart) {
                return Optional.of(finishTimeFrame());
            }
        }
        return Optional.empty();
    }

    private Optional<COUT> normalRound(final CIN candle) {
        COUT returnVal = null;

        if (currentIsStart) {
            final boolean lastWasClosed = !outputTf.areInSameTimeFrame(lastCandleStick.getTime(),
                    inputTf.instantOfNextFrame(lastCandleStick.getTime()));
            if (!lastWasClosed) {
                aggregateCandleStick(lastCandleStick, false, true);
                returnVal = finishTimeFrame();
            }
            aggregateCandleStick(candle, true, nextIsStart);
            if (!lastWasClosed && nextIsStart) {
                nextReturnVal = Optional.of(returnVal);
                return Optional.of(finishTimeFrame());
            }
        } else {
            aggregateCandleStick(candle, false, nextIsStart);
        }

        if (nextIsStart) {
            returnVal = finishTimeFrame();
        }
        return Optional.ofNullable(returnVal);
    }

    private void aggregateCandleStick(final CIN candleStick, final boolean isOpen, final boolean isClose) {
        if (isOpen) {
            aggregatedCandleStick.setTime(candleStick.getTime()).setOpen(candleStick.getOpen());
        }

        if (isClose) {
            aggregatedCandleStick.setClose(candleStick.getClose());
        }

        final Optional<Price> minLow = aggregatedCandleStick.getLow();
        final Price currentLow = candleStick.getLow();
        if (!minLow.isPresent() || currentLow.isLessThan(minLow.get())) {
            aggregatedCandleStick.setLow(currentLow);
        }

        final Optional<Price> maxHigh = aggregatedCandleStick.getHigh();
        final Price currentHigh = candleStick.getHigh();
        if (!maxHigh.isPresent() || currentHigh.isGreaterThan(maxHigh.get())) {
            aggregatedCandleStick.setHigh(currentHigh);
        }

        aggregateAdditionalFields(candleStick, isOpen, isClose);
    }

    private COUT finishTimeFrame() {
        final COUT aggregated = buildAggregatedStick(aggregatedCandleStick);
        aggregatedCandleStick.setHigh(Double.MIN_VALUE).setLow(Double.MAX_VALUE);
        return aggregated;
    }
}
