package de.voidnode.trading4j.testutils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.MINUTES;

import de.voidnode.trading4j.domain.TimeFrame;
import de.voidnode.trading4j.domain.TimeFrame.M1;
import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.VolumeUnit;
import de.voidnode.trading4j.domain.marketdata.CandleStick;
import de.voidnode.trading4j.domain.marketdata.DatedCandleStick;
import de.voidnode.trading4j.domain.marketdata.FullMarketData;
import de.voidnode.trading4j.domain.marketdata.MutableFullMarketData;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * Builds streams of {@link CandleStick}s and its subclasses.
 * 
 * @author Raik Bieniek
 */
public final class CandleStickStreams {

    private CandleStickStreams() {

    }

    /**
     * Creates a stream of candle sticks from raw values.
     * 
     * @param data
     *            An arbitrary amount of double arrays of length 4. The values for a single candle stick are expected in
     *            the following order: <code>high, low, open, close</code>
     * @param <T>
     *            The {@link TimeFrame} the generated {@link CandleStick}s should have.
     * @return the stream of constructed candle sticks
     */
    public static <T extends TimeFrame> Stream<CandleStick<T>> candleStickStream(final double[][] data) {
        return Arrays.stream(data).map(CandleStickStreams::<T>build);
    }

    /**
     * Creates a stream of {@link DatedCandleStick}s from raw values.
     * 
     * @param time
     *            The time of the candle sticks to build.
     * @param data
     *            An arbitrary amount of double arrays of length 4. The values for a single candle stick are expected in
     *            the following order: <code>high, low, open, close</code>
     * @param <T>
     *            The {@link TimeFrame} the generated {@link DatedCandleStick}s should have.
     * @return the stream of constructed candle sticks
     */
    public static <T extends TimeFrame> Stream<DatedCandleStick<T>> datedCandleStickStream(final Instant[] time,
            final double[][] data) {
        final int streamLength = time.length < data.length ? time.length : data.length;
        final List<DatedCandleStick<T>> sticks = new ArrayList<>(streamLength);
        for (int i = 0; i < streamLength; i++) {
            sticks.add(build(time[i], data[i]));
        }
        return sticks.stream();
    }

    /**
     * Creates a stream of random {@link M1} {@link DatedCandleStick}s.
     * 
     * @param seed
     *            A {@link DatedCandleStick} that is used as both, the first element of the stream, as seed for the
     *            random price values of the following element and as basis for the variability of the prices of the
     *            generated stream.
     * @return the stream of random candle sticks
     */
    public static Stream<DatedCandleStick<M1>> randomDatedCandleStickStream(final DatedCandleStick<M1> seed) {
        return Stream.generate(new RandomCandleStickSupplier(seed));

    }

    /**
     * Creates a stream of random {@link M1} {@link FullMarketData}s.
     * 
     * @param seed
     *            A {@link FullMarketData} that is used as both, the first element of the stream, as seed for the random
     *            values of the following element and as basis for the variability of the prices of the generated
     *            stream.
     * @return the stream of random candle sticks
     */
    public static Stream<FullMarketData<M1>> randomFatCandleStickStream(final FullMarketData<M1> seed) {
        final Random random = new Random(
                seed.getVolume().asAbsolute() * seed.getSpread().asPipette() * seed.getTickCount());
        final long seedSpread = seed.getSpread().asPipette();
        final long seedVolume = seed.getVolume().asAbsolute();
        final long seedTickCount = seed.getTickCount();
        return randomDatedCandleStickStream(seed).map((in) -> {
            final Price spread = new Price(
                    seedSpread + (long) (random.nextInt((int) (seedSpread * 0.1)) - seedSpread * 0.05));
            final Volume volume = new Volume(
                    seedVolume + (long) (random.nextInt((int) (seedVolume * 0.4)) - seedVolume * 0.2), VolumeUnit.BASE);
            final long tickCount = seedTickCount
                    + (long) (random.nextInt((int) (seedTickCount * 0.4)) - seedTickCount * 0.2);
            return new MutableFullMarketData<M1>().setTime(in.getTime()).setOpen(in.getOpen()).setHigh(in.getHigh())
                    .setLow(in.getLow()).setClose(in.getClose()).setSpread(spread).setVolume(volume)
                    .setTickCount(tickCount).toImmutableFullMarketData();
        });
    }

    /**
     * Builds a single {@link DatedCandleStick} from raw data.
     * 
     * @param time
     *            The time of the candle stick to build.
     * @param input
     *            The values for the candle stick in the following order: <code>high, low, open, close</code>
     * @param <T>
     *            The {@link TimeFrame} the generated {@link DatedCandleStick} should have.
     * @return The candle stick that was build.
     * @throws IllegalArgumentException
     *             When the array for the values of a candle stick does not have the size 4.
     */
    private static <T extends TimeFrame> DatedCandleStick<T> build(final Instant time, final double[] input) {
        if (input.length != 4) {
            throw new IllegalArgumentException(
                    "A candle stick needs 4 double vaules but got " + Arrays.toString(input));
        }
        return new DatedCandleStick<T>(time, input[0], input[1], input[2], input[3]);
    }

    /**
     * Builds a single candle stick from raw data.
     * 
     * @param input
     *            The values for the candle stick in the following order: <code>high, low, open, close</code>
     * @param <T>
     *            The {@link TimeFrame} the generated {@link CandleStick}sshould have.
     * @return The candle stick that was build.
     * @throws IllegalArgumentException
     *             When the array for the values of a candle stick does not have the size 4.
     */
    private static <T extends TimeFrame> CandleStick<T> build(final double[] input) {
        if (input.length != 4) {
            throw new IllegalArgumentException(
                    "A candle stick needs 4 double vaules but got " + Arrays.toString(input));
        }
        return new CandleStick<T>(input[0], input[1], input[2], input[3]);
    }

    /**
     * A supplier for random {@link CandleStick}s.
     *
     * @see CandleStickStreams#randomDatedCandleStickStream(DatedCandleStick);
     */
    private static class RandomCandleStickSupplier implements Supplier<DatedCandleStick<M1>> {

        private final DatedCandleStick<M1> seed;
        private final double highLowVariability;
        private final double openToCloseVariability;
        private final Random random;

        private final MutableFullMarketData<M1> lastCandleStick;

        RandomCandleStickSupplier(final DatedCandleStick<M1> seed) {
            this.seed = seed;
            this.highLowVariability = seed.getHigh().asDouble() - seed.getLow().asDouble();
            this.openToCloseVariability = (seed.getHigh().asDouble() - seed.getLow().asDouble()) * 0.1;
            this.random = new Random(toSeed(seed));

            this.lastCandleStick = new MutableFullMarketData<>();
        }

        @Override
        public DatedCandleStick<M1> get() {
            if (!lastCandleStick.getClose().isPresent()) {
                lastCandleStick.setTime(seed.getTime()).setClose(seed.getClose());
                return seed;
            }
            return lastCandleStick.setTime(lastCandleStick.getTime().get().plus(1, MINUTES)).setOpen(randomOpen())
                    .setHigh(randomHigh()).setLow(randomLow()).setClose(randomClose()).toImmutableDatedCandleStick();
        }

        private double randomOpen() {
            return asDouble(lastCandleStick.getClose()) + (1 - random.nextDouble()) * openToCloseVariability;
        }

        private double randomHigh() {
            return asDouble(lastCandleStick.getOpen()) + highLowVariability * random.nextDouble();
        }

        private double randomLow() {
            return asDouble(lastCandleStick.getOpen()) - highLowVariability * random.nextDouble();
        }

        private double randomClose() {
            return asDouble(lastCandleStick.getLow())
                    + (asDouble(lastCandleStick.getHigh()) - asDouble(lastCandleStick.getLow())) * random.nextDouble();
        }

        private long toSeed(final DatedCandleStick<M1> seed) {
            // Possibly the worst seed function ever but it's not used for strong cryptography anyway.
            return seed.getOpen().asPipette() + seed.getHigh().asPipette() + seed.getLow().asPipette()
                    + seed.getClose().asPipette() * seed.getTime().toEpochMilli();
        }

        private double asDouble(final Optional<Price> price) {
            return price.get().asDouble();
        }
    }
}
