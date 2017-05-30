package de.voidnode.trading4j.examples;

import java.time.Instant;
import java.util.Optional;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.VolumeUnit;
import de.voidnode.trading4j.domain.marketdata.FullMarketData;
import de.voidnode.trading4j.domain.marketdata.MutableFullMarketData;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.monetary.PriceUnit;
import de.voidnode.trading4j.domain.timeframe.M1;

import static de.voidnode.trading4j.domain.MarketDirection.DOWN;
import static de.voidnode.trading4j.domain.MarketDirection.UP;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.OngoingStubbing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Checks if {@link NMovingAveragesExpertAdvisor} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class NMovingAveragesExpertAdvisorTest {

    private static final long EMPTY = Long.MIN_VALUE;

    private static final FullMarketData<M1> SOME_CANDLE_STICK = new MutableFullMarketData<M1>().setOpen(new Price(200))
            .setHigh(new Price(300)).setLow(new Price(100)).setClose(new Price(200)).setSpread(new Price(20))
            .setTime(Instant.EPOCH).setVolume(new Volume(10, VolumeUnit.LOT)).setTickCount(252)
            .toImmutableFullMarketData();

    @Mock
    private Indicator<Price, FullMarketData<M1>> slow;

    @Mock
    private Indicator<Price, FullMarketData<M1>> medium;

    @Mock
    private Indicator<Price, FullMarketData<M1>> fast;

    private NMovingAveragesExpertAdvisor<FullMarketData<M1>> cut;

    /**
     * Sets up the cut and the default behavior of its dependencies.
     */
    @Before
    public void setUpCutAndMocks() {
        letMovingAverageReturn(slow, 10);
        letMovingAverageReturn(medium, 10);
        letMovingAverageReturn(fast, 10);
        cut = new NMovingAveragesExpertAdvisor<FullMarketData<M1>>(slow, medium, fast);
    }

    /**
     * An up signal is generated when:
     * 
     * <ul>
     * <li>with the current update the moving averages are in the order <code>slow &lt; medium &lt; fast</code></li>
     * <li>the moving averages where not in this order before {@link Price} of the current candle.</li>
     * </ul>
     */
    @Test
    public void signalIsUpWhenMaOrderSlowMediumFastWasCreatedWithCurrentCandle() {
        letMovingAverageReturn(slow, EMPTY, 500, 85, 85, 85, 300, 70);
        letMovingAverageReturn(medium, EMPTY, 300, 150, 150, 150, 150, 140);
        letMovingAverageReturn(fast, EMPTY, 400, 200, 200, 200, 200, 190);

        // moving averages empty - no signal
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getEntrySignal()).isEmpty();
        // wrong order - no signal
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getEntrySignal()).isEmpty();
        // correct order - signal
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getEntrySignal()).contains(UP);
        // still correct order but was correct before - no signal
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getEntrySignal()).isEmpty();
        // still correct order but was correct before - no signal
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getEntrySignal()).isEmpty();
        // wrong order - no signal
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getEntrySignal()).isEmpty();
        // correct order - signal
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getEntrySignal()).contains(UP);
    }

    /**
     * A down signal is generated when:
     * 
     * <ul>
     * <li>with the current update the moving averages are in the order <code>fast &lt; medium &lt; slow</code></li>
     * <li>the moving averages where not in this order before.</li>
     * </ul>
     */
    @Test
    public void signalIsDownWhenMaOrderFastMediumSlowWasCreatedWithCurrentCandle() {
        letMovingAverageReturn(slow, EMPTY, 300, 315, 315, 320, 300, 350);
        letMovingAverageReturn(medium, EMPTY, 500, 150, 160, 146, 301, 301);
        letMovingAverageReturn(fast, EMPTY, 400, 100, 120, 102, 200, 200);

        // moving averages empty - no signal
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getEntrySignal()).isEmpty();
        // wrong order - no signal
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getEntrySignal()).isEmpty();
        // correct order - signal
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getEntrySignal()).contains(DOWN);
        // still correct order but was correct before - no signal
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getEntrySignal()).isEmpty();
        // still correct order but was correct before - no signal
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getEntrySignal()).isEmpty();
        // wrong order - no signal
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getEntrySignal()).isEmpty();
        // correct order - signal
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getEntrySignal()).contains(DOWN);
    }

    /**
     * A signal is generated when the MAs change from being correctly aligned for one direction (e.g. UP) to being
     * correctly aligned to the other direction (e.g. DOWN).
     */
    @Test
    public void signalIsDetectedWhenMAsChangeWrongCorrectOrderForOneDirectionToCorrectOrderOfOtherDirection() {
        letMovingAverageReturn(slow, 100, 300, 300, 300, 100);
        letMovingAverageReturn(medium, 200, 200, 200, 200, 200);
        letMovingAverageReturn(fast, 300, 100, 100, 100, 300);

        // correct order for UP signal
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getEntrySignal()).contains(UP);
        // correct order for DOWN signal
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getEntrySignal()).contains(DOWN);
        // correct order didn't change
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getEntrySignal()).isEmpty();
        // correct order didn't change
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getEntrySignal()).isEmpty();
        // correct order for UP signal
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getEntrySignal()).contains(UP);
    }

    /**
     * When the low {@link Price} of the current candle is above the slow moving average {@link Price}, the trend is up.
     */
    @Test
    public void trendIsUpIfCurrentCandleLowIsAboveSlowMovingAverage() {
        letMovingAverageReturn(slow, EMPTY, 80);

        // slow moving average empty - no trend
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getTrend()).isEmpty();
        // slow moving average is below low - up trend
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getTrend()).contains(UP);
    }

    /**
     * When the high {@link Price} of the current candle is below the slow moving average {@link Price}, the trend is
     * down.
     */
    @Test
    public void trendIsDownIfCurrentCandleHighIsBelowSlowMovingAverage() {
        letMovingAverageReturn(slow, EMPTY, 400);

        // slow moving average empty - no trend
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getTrend()).isEmpty();
        // slow moving average is above high - down trend
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getTrend()).contains(DOWN);
    }

    /**
     * When the slow moving average goes through the current candle, the trend is unknown.
     */
    @Test
    public void trendIsUnknownIfSlowMovingAveragesGoesThroughTheCurrentCandle() {
        letMovingAverageReturn(slow, 200);

        // moving average is between high and low - no trend
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getTrend()).isEmpty();
    }

    /**
     * When an up signal is generated, the entry price is the last known close {@link Price} plus the spread plus 15
     * {@link PriceUnit#PIPETTE}.
     */
    @Test
    public void entryPriceEqualsTheLastClosePricePlusSpreadPlus5PipetteOnUpSignal() {
        letMovingAverageReturn(slow, 40, 85);
        letMovingAverageReturn(medium, 50, 150);
        letMovingAverageReturn(fast, 10, 200);

        // no signal - no entry price
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getEntryPrice()).isEmpty();
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getEntryPrice()).contains(new Price(235));
    }

    /**
     * When a down signal is generated, the entry price is the last known close {@link Price} minus 15
     * {@link PriceUnit#PIPETTE}.
     */
    @Test
    public void entryPriceEqualsTheLastClosePriceMinus5PipetteOnDownSignal() {
        letMovingAverageReturn(slow, 40, 315);
        letMovingAverageReturn(medium, 0, 150);
        letMovingAverageReturn(fast, 50, 100);

        // no signal - no entry price
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getEntryPrice()).isEmpty();
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getEntryPrice()).contains(new Price(185));
    }

    /**
     * A take profit price is not used by the cut.
     */
    @Test
    public void takeProfitIsDisabled() {
        assertThat(cut.getTakeProfit()).contains(new Price(0));
    }

    /**
     * The stop loose price is the price of the slow moving average when the trend is up.
     */
    @Test
    public void stopLooseIsSlowMovingAveragePriceWhenTrendIsUp() {
        letMovingAverageReturn(slow, 10, EMPTY, 42);

        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getStopLoose()).contains(new Price(10));
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getStopLoose()).isEmpty();
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getStopLoose()).contains(new Price(42));
    }

    /**
     * The stop loose price is the price of the slow moving average plus the spread when the trend is down.
     */
    @Test
    public void stopLooseIsSlowMovingAveragePriceWhenTrendIsDown() {
        letMovingAverageReturn(slow, 1000, EMPTY, 4200);

        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getStopLoose()).contains(new Price(1020));
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getStopLoose()).isEmpty();
        cut.update(SOME_CANDLE_STICK);
        assertThat(cut.getStopLoose()).contains(new Price(4220));
    }

    /**
     * The cut fails when no fast moving averages are passed in the constructor.
     */
    @Test(expected = IllegalArgumentException.class)
    public void failsWhenNoFastMovingAveragesArePassed() {
        new NMovingAveragesExpertAdvisor<FullMarketData<M1>>(slow);
    }

    private void letMovingAverageReturn(final Indicator<Price, FullMarketData<M1>> movingAverage,
            final long... values) {
        OngoingStubbing<Optional<Price>> toStub = when(movingAverage.indicate(any()));
        for (final long value : values) {
            if (value == EMPTY) {
                toStub = toStub.thenReturn(Optional.empty());
            } else {
                toStub = toStub.thenReturn(Optional.of(new Price(value)));
            }
        }
    }
}
