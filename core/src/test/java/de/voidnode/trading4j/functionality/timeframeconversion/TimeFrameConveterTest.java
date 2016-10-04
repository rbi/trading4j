package de.voidnode.trading4j.functionality.timeframeconversion;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.stream.Collectors.toList;

import de.voidnode.trading4j.domain.TimeFrame;
import de.voidnode.trading4j.domain.TimeFrame.M30;
import de.voidnode.trading4j.domain.TimeFrame.M5;
import de.voidnode.trading4j.domain.marketdata.DatedCandleStick;
import de.voidnode.trading4j.testutils.CandleStickStreams;

import static de.voidnode.trading4j.testutils.CandleStickStreams.datedCandleStickStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Checks if {@link TimeFrameConverter} and its specialization {@link TimeFrameConveterTest} works as
 * expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class TimeFrameConveterTest {

    private static final int CANDLE_STICK_COUNT = 7;

    private Instant[] testCandleStickTimes;

    @Mock
    private M5 m5Tf;

    @Mock
    private M30 m30Tf;

    private TimeFrameConverter<DatedCandleStick<M5>, DatedCandleStick<M30>, M5, M30> cut;

    /**
     * Sets up the class to test.
     */
    @Before
    public void setUpClassToTest() {
        cut = new DatedCandleStickTimeFrameConverter<DatedCandleStick<M5>, TimeFrame.M5, TimeFrame.M30>(m5Tf, m30Tf);
    }

    /**
     * Sets up the test data and mocks.
     */
    @Before
    public void setUpTestDataAndMocks() {
        // Sun, 27.7.2014 14:36:55 UTC
        final Instant base = Instant.ofEpochSecond(1406471815L);
        testCandleStickTimes = new Instant[CANDLE_STICK_COUNT];
        for (int i = 0; i < CANDLE_STICK_COUNT; i++) {
            testCandleStickTimes[i] = base.plus(i, MINUTES);
        }

        for (int i = 0; i < CANDLE_STICK_COUNT - 1; i++) {
            when(m5Tf.instantOfNextFrame(testCandleStickTimes[i])).thenReturn(testCandleStickTimes[i + 1]);
        }
    }

    // //////////////////
    // / normal cases ///
    // //////////////////

    /**
     * The {@link TimeFrameConverter} should aggregate candleSticks correctly.
     */
    @Test
    public void shouldAggregateCandleSticksCorreclty() {
        final Stream<DatedCandleStick<M5>> candleData = datedCandleStickStream(testCandleStickTimes,
                // candle stick data
                new double[][] {
                        // open, high, low, close
                        { 1.1, 1.2, 0.9, 1.2 }, // 0
                        { 1.2, 1.3, 1.2, 1.2 }, // 1
                        { 1.0, 1.1, 1.3, 1.5 }, // 2
                        { 1.2, 1.2, 1.4, 1.1 }, // 3
                        { 1.0, 1.1, 1.9, 1.3 }, // 4
                        { 1.1, 1.5, 1.0, 1.4 }, // 5
        });

        final boolean[] areInSameTimeFrame = new boolean[] { false, true, false, true, true, false };
        for (int i = 0; i < areInSameTimeFrame.length - 1; i++) {
            when(m30Tf.areInSameTimeFrame(testCandleStickTimes[i], testCandleStickTimes[i + 1]))
                    .thenReturn(areInSameTimeFrame[i]);
        }

        final List<DatedCandleStick<M30>> output = candleData.map(in -> cut.aggregate(in)).filter(Optional::isPresent)
                .map(out -> out.get()).collect(toList());

        assertThat(output).hasSize(2);
        assertThat(output.get(0)).isEqualTo(new DatedCandleStick<>(testCandleStickTimes[1], 1.2, 1.3, 1.2, 1.5));
        assertThat(output.get(1)).isEqualTo(new DatedCandleStick<>(testCandleStickTimes[3], 1.2, 1.5, 1.0, 1.4));
    }

    // //////////////////
    // / corner cases ///
    // //////////////////

    /**
     * Until the first {@link DatedCandleStick} is in a different time frame shown by
     * {@link TimeFrame#areInSameTimeFrame(Instant, Instant)}, all input {@link DatedCandleStick}s should be discarded.
     */
    @Test
    public void shouldDiscardPassedCandleStickUntilFirstOfNewFrameOccures() {
        final Stream<DatedCandleStick<M5>> candleData = datedCandleStickStream(testCandleStickTimes,
                // candle stick data
                new double[][] {
                        // open, high, low, close
                        { 1.1, 1.2, 0.9, 1.2 }, // 0
                        { 1.2, 1.3, 1.2, 1.2 }, // 1
                        { 1.0, 1.1, 1.3, 1.5 }, // 2
                        { 1.2, 1.2, 1.4, 1.1 }, // 3
                        { 1.0, 1.1, 1.9, 1.3 }, // 4
        });

        final boolean[] areInSameTimeFrame = new boolean[] { true, false, true, true, false };
        for (int i = 0; i < areInSameTimeFrame.length - 1; i++) {
            when(m30Tf.areInSameTimeFrame(testCandleStickTimes[i], testCandleStickTimes[i + 1]))
                    .thenReturn(areInSameTimeFrame[i]);
        }

        final List<DatedCandleStick<M30>> output = candleData.map(in -> cut.aggregate(in)).filter(Optional::isPresent)
                .map(out -> out.get()).collect(toList());

        assertThat(output).hasSize(1);
        assertThat(output.get(0)).isEqualTo(new DatedCandleStick<>(testCandleStickTimes[2], 1.0, 1.2, 1.3, 1.3));
    }

    // /////////////////
    // / error cases ///
    // /////////////////

    /**
     * When the {@link TimeFrame#instantOfNextFrame(Instant)} of the smaller time frame in call <code>n</code> should be
     * in the same time frame as time of the current {@link DatedCandleStick} but the time of the
     * {@link DatedCandleStick} of call <code>n + 1</code> is in a different time frame than the time of of the
     * {@link DatedCandleStick} <code>n</code> the time frame should be closed at the candle <code>n</code> an the
     * result should be returned in call <code>n + 1</code>.
     */
    @Test
    public void shouldCloseOldTimeFrameIfNewCandleIsUnexpectetlyInDifferentTimeFrame() {
        final List<DatedCandleStick<M5>> input = CandleStickStreams.<M5>datedCandleStickStream(testCandleStickTimes,
                // candle stick data
                new double[][] {
                        // open, high, low, close
                        { 1.1, 1.2, 0.9, 1.2 }, // 0
                        { 1.2, 1.3, 1.2, 1.2 }, // 1
                        { 1.0, 1.1, 1.3, 1.5 }, // 2
                        { 1.5, 1.8, 1.3, 1.6 }, // 3 skipped
                        { 1.2, 2.0, 1.2, 1.7 }, // 4
                        { 1.4, 1.7, 1.1, 1.3 }, // 5
        }).collect(toList());
        final List<DatedCandleStick<M30>> output = new LinkedList<>();

        final boolean[] areInSameTimeFrame = new boolean[] { false, true, true, false, true, false };
        for (int i = 0; i < areInSameTimeFrame.length - 1; i++) {
            when(m30Tf.areInSameTimeFrame(testCandleStickTimes[i], testCandleStickTimes[i + 1]))
                    .thenReturn(areInSameTimeFrame[i]);
        }
        when(m30Tf.areInSameTimeFrame(testCandleStickTimes[2], testCandleStickTimes[4])).thenReturn(false);

        cut.aggregate(input.get(0)).ifPresent(c -> output.add(c));
        cut.aggregate(input.get(1)).ifPresent(c -> output.add(c));
        cut.aggregate(input.get(2)).ifPresent(c -> output.add(c));
        cut.aggregate(input.get(4)).ifPresent(c -> output.add(c));
        cut.aggregate(input.get(5)).ifPresent(c -> output.add(c));

        assertThat(output).hasSize(2);
        assertThat(output.get(0)).isEqualTo(new DatedCandleStick<>(testCandleStickTimes[1], 1.2, 1.3, 1.2, 1.5));
        assertThat(output.get(1)).isEqualTo(new DatedCandleStick<>(testCandleStickTimes[4], 1.2, 2.0, 1.1, 1.3));
    }

    /**
     * At initialization phase, if at call <code>n</code> the {@link TimeFrame#instantOfNextFrame(Instant)} should be in
     * the same time frame but the time of the {@link DatedCandleStick} in call <code>n + 1</code> is in a different
     * time frame, the time frame aggregation should start anyway.
     */
    @Test
    public void shouldStartCollectingAFrameEvenIfNextInstantWasNotAnnouncedToBeInADifferentFrame() {
        final List<DatedCandleStick<M5>> input = CandleStickStreams.<M5>datedCandleStickStream(testCandleStickTimes,
                // candle stick data
                new double[][] {
                        // open, high, low, close
                        { 1.1, 1.2, 0.9, 1.2 }, // 0
                        { 1.2, 1.3, 1.2, 1.2 }, // 1 skipped
                        { 1.0, 1.1, 1.3, 1.5 }, // 2
                        { 1.5, 1.8, 1.3, 1.6 }, // 3
        }).collect(toList());
        final List<DatedCandleStick<M30>> output = new LinkedList<>();

        final boolean[] areInSameTimeFrame = new boolean[] { true, false, true, false };
        for (int i = 0; i < areInSameTimeFrame.length - 1; i++) {
            when(m30Tf.areInSameTimeFrame(testCandleStickTimes[i], testCandleStickTimes[i + 1]))
                    .thenReturn(areInSameTimeFrame[i]);
        }
        when(m30Tf.areInSameTimeFrame(testCandleStickTimes[0], testCandleStickTimes[2])).thenReturn(false);

        cut.aggregate(input.get(0)).ifPresent(c -> output.add(c));
        cut.aggregate(input.get(2)).ifPresent(c -> output.add(c));
        cut.aggregate(input.get(3)).ifPresent(c -> output.add(c));

        assertThat(output).hasSize(1);
        assertThat(output.get(0)).isEqualTo(new DatedCandleStick<>(testCandleStickTimes[2], 1.0, 1.8, 1.3, 1.6));
    }

    /**
     * Same as {@link #shouldCloseOldTimeFrameIfNewCandleIsUnexpectetlyInDifferentTimeFrame()} but with a new time frame
     * at call <code>n + 2</code> too. In this case the candle of <code>n + 1</code> should be returned.
     */
    @Test
    public void ifNewCandleIsUnexpectetlyInDifferentTimeFrameAndNextTooFirstUnexpecetShouldNoDataShouldBeDropped() {

        final List<DatedCandleStick<M5>> input = CandleStickStreams.<M5>datedCandleStickStream(testCandleStickTimes,
                // candle stick data
                new double[][] {
                        // open, high, low, close
                        { 1.1, 1.2, 0.9, 1.2 }, // 0
                        { 1.2, 1.3, 1.2, 1.2 }, // 1
                        { 1.0, 1.1, 1.3, 1.5 }, // 2 skipped
                        { 1.5, 1.8, 1.3, 1.6 }, // 3
                        { 1.2, 1.9, 1.1, 1.4 }, // 4
                        { 1.8, 1.2, 0.9, 1.6 }, // 5
        }).collect(toList());
        final List<DatedCandleStick<M30>> output = new LinkedList<>();

        final boolean[] areInSameTimeFrame = new boolean[] { false, true, false, false, true, false };
        for (int i = 0; i < areInSameTimeFrame.length - 1; i++) {
            when(m30Tf.areInSameTimeFrame(testCandleStickTimes[i], testCandleStickTimes[i + 1]))
                    .thenReturn(areInSameTimeFrame[i]);
        }
        when(m30Tf.areInSameTimeFrame(testCandleStickTimes[1], testCandleStickTimes[3])).thenReturn(false);

        cut.aggregate(input.get(0)).ifPresent(c -> output.add(c));
        cut.aggregate(input.get(1)).ifPresent(c -> output.add(c));
        cut.aggregate(input.get(3)).ifPresent(c -> output.add(c));
        cut.aggregate(input.get(4)).ifPresent(c -> output.add(c));
        cut.aggregate(input.get(5)).ifPresent(c -> output.add(c));

        assertThat(output).hasSize(3);
        assertThat(output.get(0)).isEqualTo(new DatedCandleStick<>(testCandleStickTimes[1], 1.2, 1.3, 1.2, 1.2));
        assertThat(output.get(1)).isEqualTo(new DatedCandleStick<>(testCandleStickTimes[3], 1.5, 1.8, 1.3, 1.6));
        assertThat(output.get(2)).isEqualTo(new DatedCandleStick<>(testCandleStickTimes[4], 1.2, 1.9, 0.9, 1.6));
    }
}
