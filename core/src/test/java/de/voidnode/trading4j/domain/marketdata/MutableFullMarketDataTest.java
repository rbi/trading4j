package de.voidnode.trading4j.domain.marketdata;

import java.time.Instant;

import de.voidnode.trading4j.api.UnrecoverableProgrammingError;
import de.voidnode.trading4j.domain.TimeFrame.M1;
import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.VolumeUnit;
import de.voidnode.trading4j.domain.monetary.Price;

import static de.voidnode.trading4j.domain.VolumeUnit.LOT;
import static de.voidnode.trading4j.domain.VolumeUnit.MINI_LOT;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link MutableFullMarketData} works as expected.
 * 
 * @author Raik Bieniek
 */
public class MutableFullMarketDataTest {

    private static final double DUMMY_VALUE = 1.0;

    // Sun, 27.7.2014 16:01:58
    private static final Instant SOME_TIME = Instant.ofEpochSecond(1406476918L);

    // /////////////////
    // / CandleStick ///
    // /////////////////

    /**
     * The instance should build {@link CandleStick}s with the values that where passed to it.
     */
    @Test
    public void shouldBuildCorrectCandleSticksWithPassedValues() {
        final CandleStick<?> firstCandleStick = new MutableFullMarketData<>().setOpen(1.0).setHigh(2.0).setLow(3.0)
                .setClose(4.0).toImmutableCandleStick();
        assertThat(firstCandleStick).isEqualTo(new CandleStick<>(1.0, 2.0, 3.0, 4.0));

        final CandleStick<?> secondCandleStick = new MutableFullMarketData<>().setOpen(new Price(5.0))
                .setHigh(new Price(6.0)).setLow(new Price(7.0)).setClose(new Price(8.0)).toImmutableCandleStick();
        assertThat(secondCandleStick).isEqualTo(new CandleStick<>(5.0, 6.0, 7.0, 8.0));
    }

    /**
     * If a required value is missing to build an immutable {@link CandleStick}, an exception should be thrown.
     */
    @Test
    public void shouldFailWhenNotAllRequiredValuesForCandleSticksArePassed() {
        int exceptionsCaught = 0;

        try {
            new MutableFullMarketData<>().setHigh(DUMMY_VALUE).setLow(DUMMY_VALUE).setClose(DUMMY_VALUE)
                    .toImmutableCandleStick();
        } catch (final UnrecoverableProgrammingError e) {
            exceptionsCaught++;
        }
        try {
            new MutableFullMarketData<>().setOpen(DUMMY_VALUE).setLow(DUMMY_VALUE).setClose(DUMMY_VALUE)
                    .toImmutableCandleStick();
        } catch (final UnrecoverableProgrammingError e) {
            exceptionsCaught++;
        }
        try {
            new MutableFullMarketData<>().setOpen(DUMMY_VALUE).setHigh(DUMMY_VALUE).setClose(DUMMY_VALUE)
                    .toImmutableCandleStick();
        } catch (final UnrecoverableProgrammingError e) {
            exceptionsCaught++;
        }
        try {
            new MutableFullMarketData<>().setOpen(DUMMY_VALUE).setHigh(DUMMY_VALUE).setLow(DUMMY_VALUE)
                    .toImmutableCandleStick();
        } catch (final UnrecoverableProgrammingError e) {
            exceptionsCaught++;
        }

        assertThat(exceptionsCaught)
                .as("Expected the immutable conversion to fail 4 times but it failed %d times.", exceptionsCaught)
                .isEqualTo(4);
    }

    // //////////////////////
    // / DatedCandleStick ///
    // //////////////////////

    /**
     * The instance should build {@link DatedCandleStick}s with the values that where passed to it.
     */
    @Test
    public void shouldBuildCorrectDatedCandleSticksWithPassedValues() {
        final DatedCandleStick<?> buildCandleStick1 = new MutableFullMarketData<>().setTime(SOME_TIME).setOpen(1.0)
                .setHigh(2.0).setLow(3.0).setClose(4.0).toImmutableDatedCandleStick();
        assertThat(buildCandleStick1).isEqualTo(new DatedCandleStick<>(SOME_TIME, 1.0, 2.0, 3.0, 4.0));

        final DatedCandleStick<?> buildCandleStick2 = new MutableFullMarketData<>().setTime(SOME_TIME)
                .setOpen(new Price(1.0)).setHigh(new Price(2.0)).setLow(new Price(3.0)).setClose(new Price(4.0))
                .toImmutableDatedCandleStick();

        assertThat(buildCandleStick2).isEqualTo(new DatedCandleStick<>(SOME_TIME, 1.0, 2.0, 3.0, 4.0));
    }

    /**
     * If a required value is missing to build an immutable {@link DatedCandleStick}, an exception should be thrown.
     */
    @Test
    public void shouldFailWhenNotAllRequiredValuesForDatedCandleSticksArePassed() {
        int exceptionsCaught = 0;

        try {
            new MutableFullMarketData<>().setOpen(DUMMY_VALUE).setHigh(DUMMY_VALUE).setLow(DUMMY_VALUE)
                    .setClose(DUMMY_VALUE).toImmutableDatedCandleStick();
        } catch (final UnrecoverableProgrammingError e) {
            exceptionsCaught++;
        }
        try {
            new MutableFullMarketData<>().setTime(SOME_TIME).setHigh(DUMMY_VALUE).setLow(DUMMY_VALUE)
                    .setClose(DUMMY_VALUE).toImmutableDatedCandleStick();
        } catch (final UnrecoverableProgrammingError e) {
            exceptionsCaught++;
        }
        try {
            new MutableFullMarketData<>().setTime(SOME_TIME).setOpen(DUMMY_VALUE).setLow(DUMMY_VALUE)
                    .setClose(DUMMY_VALUE).toImmutableDatedCandleStick();
        } catch (final UnrecoverableProgrammingError e) {
            exceptionsCaught++;
        }
        try {
            new MutableFullMarketData<>().setTime(SOME_TIME).setOpen(DUMMY_VALUE).setHigh(DUMMY_VALUE)
                    .setClose(DUMMY_VALUE).toImmutableDatedCandleStick();
        } catch (final UnrecoverableProgrammingError e) {
            exceptionsCaught++;
        }
        try {
            new MutableFullMarketData<>().setTime(SOME_TIME).setOpen(DUMMY_VALUE).setHigh(DUMMY_VALUE)
                    .setLow(DUMMY_VALUE).toImmutableDatedCandleStick();
        } catch (final UnrecoverableProgrammingError e) {
            exceptionsCaught++;
        }

        assertThat(exceptionsCaught)
                .as("Expected the immutable conversion to fail 5 times but it failed %d times.", exceptionsCaught)
                .isEqualTo(5);
    }

    // ////////////////////
    // / FullMarketData ///
    // ////////////////////

    /**
     * The instance should build {@link FullMarketData}s with the values that where passed to it.
     */
    @Test
    public void shouldBuildCorrectFullMarketDataInstancesWithPassedValues() {
        final FullMarketData<?> buildCandleStick1 = new MutableFullMarketData<>().setTime(SOME_TIME).setOpen(1.0)
                .setHigh(2.0).setLow(3.0).setClose(4.0).setSpread(5.0).setVolume(50, MINI_LOT).setTickCount(15)
                .toImmutableFullMarketData();
        assertThat(buildCandleStick1).isEqualTo(new FullMarketData<>(SOME_TIME, new Price(1.0), new Price(2.0),
                new Price(3.0), new Price(4.0), new Price(5.0), new Volume(5, LOT), 15));
    }

    /**
     * If a required value is missing to build an immutable {@link FullMarketData}, an exception should be thrown.
     */
    @Test
    public void shouldFailWhenNotAllRequiredValuesForFatCandleSticksArePassed() {
        expectUnrecoverableProgrammingError(() -> {
            new MutableFullMarketData<>().setOpen(DUMMY_VALUE).setHigh(DUMMY_VALUE).setLow(DUMMY_VALUE)
                    .setClose(DUMMY_VALUE).setSpread(DUMMY_VALUE).setVolume(50, MINI_LOT).setTickCount(5)
                    .toImmutableFullMarketData();
        });
        expectUnrecoverableProgrammingError(() -> {
            new MutableFullMarketData<>().setTime(SOME_TIME).setHigh(DUMMY_VALUE).setLow(DUMMY_VALUE)
                    .setClose(DUMMY_VALUE).setSpread(DUMMY_VALUE).setVolume(50, MINI_LOT).setTickCount(5)
                    .toImmutableFullMarketData();
        });
        expectUnrecoverableProgrammingError(() -> {
            new MutableFullMarketData<>().setTime(SOME_TIME).setOpen(DUMMY_VALUE).setLow(DUMMY_VALUE)
                    .setClose(DUMMY_VALUE).setSpread(DUMMY_VALUE).setVolume(50, MINI_LOT).setTickCount(5)
                    .toImmutableFullMarketData();
        });
        expectUnrecoverableProgrammingError(() -> {
            new MutableFullMarketData<>().setTime(SOME_TIME).setOpen(DUMMY_VALUE).setHigh(DUMMY_VALUE)
                    .setClose(DUMMY_VALUE).setSpread(DUMMY_VALUE).setVolume(50, MINI_LOT).setTickCount(5)
                    .toImmutableFullMarketData();
        });
        expectUnrecoverableProgrammingError(() -> {
            new MutableFullMarketData<>().setTime(SOME_TIME).setOpen(DUMMY_VALUE).setHigh(DUMMY_VALUE)
                    .setLow(DUMMY_VALUE).setSpread(DUMMY_VALUE).setVolume(50, MINI_LOT).setTickCount(5)
                    .toImmutableFullMarketData();
        });
        expectUnrecoverableProgrammingError(() -> {
            new MutableFullMarketData<>().setTime(SOME_TIME).setOpen(DUMMY_VALUE).setHigh(DUMMY_VALUE)
                    .setLow(DUMMY_VALUE).setClose(DUMMY_VALUE).setVolume(50, MINI_LOT).setTickCount(5)
                    .toImmutableFullMarketData();
        });
        expectUnrecoverableProgrammingError(() -> {
            new MutableFullMarketData<>().setTime(SOME_TIME).setOpen(DUMMY_VALUE).setHigh(DUMMY_VALUE)
                    .setLow(DUMMY_VALUE).setClose(DUMMY_VALUE).setSpread(DUMMY_VALUE).setTickCount(5)
                    .toImmutableFullMarketData();
        });
        expectUnrecoverableProgrammingError(() -> {
            new MutableFullMarketData<>().setTime(SOME_TIME).setOpen(DUMMY_VALUE).setHigh(DUMMY_VALUE)
                    .setLow(DUMMY_VALUE).setClose(DUMMY_VALUE).setSpread(DUMMY_VALUE).setVolume(50, MINI_LOT)
                    .toImmutableFullMarketData();
        });
    }

    // ///////////////////////
    // / copy constructors ///
    // ///////////////////////

    /**
     * The cut can copy data of another {@link FullMarketData} class into its own fields.
     */
    @Test
    public void canCopyDataOfOtherFullMarketData() {
        final FullMarketData<M1> baseData = new FullMarketData<>(SOME_TIME, new Price(1), new Price(2), new Price(3),
                new Price(4), new Price(42), new Volume(52, VolumeUnit.MICRO_LOT), 62);

        final MutableFullMarketData<M1> cut = new MutableFullMarketData<>(baseData);

        assertThat(cut.getTime()).contains(SOME_TIME);
        assertThat(cut.getOpen()).contains(new Price(1));
        assertThat(cut.getHigh()).contains(new Price(2));
        assertThat(cut.getLow()).contains(new Price(3));
        assertThat(cut.getClose()).contains(new Price(4));
        assertThat(cut.getSpread()).contains(new Price(42));
        assertThat(cut.getVolume()).contains(new Volume(52, VolumeUnit.MICRO_LOT));
        assertThat(cut.getTickCount()).contains(62L);
    }

    private void expectUnrecoverableProgrammingError(final Runnable runnable) {
        try {
            runnable.run();
        } catch (final UnrecoverableProgrammingError e) {
            consume(e);
            return;
        }
        throw new AssertionError("Expected the immutable conversion to fail but it succeed.");
    }

    private void consume(final UnrecoverableProgrammingError e) {
        // fake handling exception which are expected in tests
    }
}
