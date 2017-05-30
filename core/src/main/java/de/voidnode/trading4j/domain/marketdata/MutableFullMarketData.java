package de.voidnode.trading4j.domain.marketdata;

import java.time.Instant;
import java.util.Optional;

import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.VolumeUnit;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.timeframe.M1;
import de.voidnode.trading4j.domain.timeframe.TimeFrame;

/**
 * A mutable version of {@link CandleStick}.
 * 
 * @author Raik Bieniek
 *
 * @param <T>
 *            The {@link TimeFrame} of this candle stick.
 */
public class MutableFullMarketData<T extends TimeFrame> {

    private Instant time;
    private Price open;
    private Price high;
    private Price low;
    private Price close;
    private Price spread;
    private Volume volume;
    private Long tickCount;

    /**
     * Creates an instance with no values set for the fields.
     * 
     * <p>
     * <b>WARNING:</b> Calling the <code>toImmutable*</code> methods without having all required fields set will result
     * in an exception to be thrown. See the respective method JavaDoc to learn what the required fields are for each
     * order type.
     * </p>
     */
    public MutableFullMarketData() {

    }

    /**
     * A copy-constructor that pre-files all fields of the new instance with the respective values of a given
     * {@link FullMarketData} instance.
     * 
     * @param marketData
     *            The data basis that should be copied.
     */
    public MutableFullMarketData(final FullMarketData<M1> marketData) {
        time = marketData.getTime();
        open = marketData.getOpen();
        high = marketData.getHigh();
        low = marketData.getLow();
        close = marketData.getClose();
        spread = marketData.getSpread();
        volume = marketData.getVolume();
        tickCount = marketData.getTickCount();
    }

    /**
     * See {@link DatedCandleStick#getTime()}.
     * 
     * @return see DatedCandleStick#getTime() if the time is set and an empty {@link Optional} if not.
     * @see DatedCandleStick#getTime()
     */
    public Optional<Instant> getTime() {
        return Optional.ofNullable(time);
    }

    /**
     * See {@link DatedCandleStick#getTime()}.
     * 
     * @param time
     *            see {@link DatedCandleStick#getTime()}
     * @return This builder
     * @see DatedCandleStick#getTime()
     */
    public MutableFullMarketData<T> setTime(final Instant time) {
        this.time = time;
        return this;
    }

    /**
     * @return see {@link CandleStick#getOpen()} if the open price is set and an empty {@link Optional} if not.
     * @see CandleStick#getOpen()
     */
    public Optional<Price> getOpen() {
        return Optional.ofNullable(open);
    }

    /**
     * See {@link CandleStick#getOpen()}.
     * 
     * @param open
     *            see {@link CandleStick#getOpen()}
     * @return This builder
     * @see CandleStick#getOpen()
     */
    public MutableFullMarketData<T> setOpen(final Price open) {
        this.open = open;
        return this;
    }

    /**
     * @param open
     *            see {@link Price#Price(double)} for the meaning of the expected double.
     * @return This builder
     * @see CandleStick#getOpen()
     */
    public MutableFullMarketData<T> setOpen(final double open) {
        this.open = new Price(open);
        return this;
    }

    /**
     * @return see {@link CandleStick#getHigh()} if the high price is set and an empty {@link Optional} if not.
     * @see CandleStick#getHigh()
     */
    public Optional<Price> getHigh() {
        return Optional.ofNullable(high);
    }

    /**
     * See {@link CandleStick#getHigh()}.
     * 
     * @param high
     *            see {@link CandleStick#getHigh()}
     * @return This builder
     * @see CandleStick#getHigh()
     */
    public MutableFullMarketData<T> setHigh(final Price high) {
        this.high = high;
        return this;
    }

    /**
     * @param high
     *            see {@link Price#Price(double)} for the meaning of the expected double.
     * @return This builder
     * @see CandleStick#getHigh()
     */
    public MutableFullMarketData<T> setHigh(final double high) {
        this.high = new Price(high);
        return this;
    }

    /**
     * @return see {@link CandleStick#getLow()} if the low price is set and an empty {@link Optional} if not.
     * @see CandleStick#getLow()
     */
    public Optional<Price> getLow() {
        return Optional.ofNullable(low);
    }

    /**
     * See {@link CandleStick#getLow()}.
     * 
     * @param low
     *            see {@link CandleStick#getLow()}
     * @return This builder
     * @see CandleStick#getLow()
     */
    public MutableFullMarketData<T> setLow(final Price low) {
        this.low = low;
        return this;
    }

    /**
     * @param low
     *            see {@link Price#Price(double)} for the meaning of the expected double.
     * @return This builder
     * @see CandleStick#getLow()
     */
    public MutableFullMarketData<T> setLow(final double low) {
        this.low = new Price(low);
        return this;
    }

    /**
     * @return see {@link CandleStick#getClose()} if the close price is set and an empty {@link Optional} if not.
     * @see CandleStick#getClose()
     */
    public Optional<Price> getClose() {
        return Optional.ofNullable(close);
    }

    /**
     * See {@link CandleStick#getClose()}.
     * 
     * @param close
     *            see {@link CandleStick#getClose()}
     * @return This builder
     * @see CandleStick#getClose()
     */
    public MutableFullMarketData<T> setClose(final Price close) {
        this.close = close;
        return this;
    }

    /**
     * @param close
     *            see {@link Price#Price(double)} for the meaning of the expected double.
     * @return This builder
     * @see CandleStick#getClose()
     */
    public MutableFullMarketData<T> setClose(final double close) {
        this.close = new Price(close);
        return this;
    }

    /**
     * @return see {@link FullMarketData#getSpread()} if the spread is set and an empty {@link Optional} if not.
     * @see FullMarketData#getSpread()
     */
    public Optional<Price> getSpread() {
        return Optional.ofNullable(spread);
    }

    /**
     * See {@link FullMarketData#getSpread()}.
     * 
     * @param spread
     *            see {@link FullMarketData#getSpread()}
     * @return This builder
     * @see FullMarketData#getSpread()
     */
    public MutableFullMarketData<T> setSpread(final Price spread) {
        this.spread = spread;
        return this;
    }

    /**
     * @param spread
     *            see {@link Price#Price(double)} for the meaning of the expected double.
     * @return This builder
     * @see FullMarketData#getSpread()
     */
    public MutableFullMarketData<T> setSpread(final double spread) {
        this.spread = new Price(spread);
        return this;
    }

    /**
     * @return see {@link FullMarketData#getVolume()} if the volume is set and an empty {@link Optional} if not.
     * @see FullMarketData#getVolume()
     */
    public Optional<Volume> getVolume() {
        return Optional.ofNullable(volume);
    }

    /**
     * See {@link FullMarketData#getVolume()}.
     * 
     * @param volume
     *            see {@link FullMarketData#getVolume()}
     * @return This builder
     * @see FullMarketData#getVolume()
     */
    public MutableFullMarketData<T> setVolume(final Volume volume) {
        this.volume = volume;
        return this;
    }

    /**
     * See {@link Volume#Volume(long, VolumeUnit)}.
     * 
     * @param volume
     *            see {@link Volume#Volume(long, VolumeUnit)}
     * @param unit
     *            see {@link Volume#Volume(long, VolumeUnit)}
     * @return This builder
     * @see FullMarketData#getVolume()
     */
    public MutableFullMarketData<T> setVolume(final long volume, final VolumeUnit unit) {
        this.volume = new Volume(volume, unit);
        return this;
    }

    /**
     * @return see FatCandleStick#getTickCount() if the tick count is set and an empty {@link Optional} if not.
     * @see FullMarketData#getTickCount()
     */
    public Optional<Long> getTickCount() {
        return Optional.ofNullable(tickCount);
    }

    /**
     * See {FatCandleStick#getTickCount()}.
     * 
     * @param tickCount
     *            see {FatCandleStick#getTickCount()}
     * @return This builder
     * @see FullMarketData#getTickCount()
     */
    public MutableFullMarketData<T> setTickCount(final long tickCount) {
        this.tickCount = tickCount;
        return this;
    }

    /**
     * Constructs an immutable {@link CandleStick} with the values of this instance.
     * 
     * <p>
     * The values open, high, low and close must be set before calling this method or else it will fail with an
     * exception.
     * </p>
     * 
     * @return The build candle Stick.
     * @throws IllegalStateException
     *             When not all required values where set.
     */
    public CandleStick<T> toImmutableCandleStick() {
        if (open == null || high == null || low == null || close == null) {
            throw new IllegalStateException(
                    "Failed to create a CandleStick as not all required values (open, high, low, close) "
                            + "where passed to this builder.");
        }
        return new CandleStick<T>(open, high, low, close);
    }

    /**
     * Constructs an immutable {@link DatedCandleStick} with the values of this instance.
     * 
     * <p>
     * The values time, open, high, low and close must be set before calling this method or else it will fail with an
     * exception.
     * </p>
     * 
     * @return The build candle Stick.
     * @throws IllegalStateException
     *             When not all required values where set.
     */
    public DatedCandleStick<T> toImmutableDatedCandleStick() {
        if (anyIsNull(time, open, high, low, close)) {
            throw new IllegalStateException(
                    "Failed to create a DatedCandleStick as not all required values (time, open, high, low, close) "
                            + "where passed to this builder.");
        }
        return new DatedCandleStick<T>(time, open, high, low, close);
    }

    /**
     * Constructs an immutable {@link FullMarketData} with the values of this instance.
     * 
     * <p>
     * The values time, open, high, low, close, spread, volume and tickCount must be set before calling this method or
     * else it will fail with an exception.
     * </p>
     * 
     * @return The build candle Stick.
     * @throws UnrecoverableProgrammingError
     *             When not all required values where set.
     */
    public FullMarketData<T> toImmutableFullMarketData() {
        if (anyIsNull(time, open, high, low, close, spread, volume, tickCount)) {
            throw new IllegalStateException(
                    "Failed to create a FatCandleStick as not all required values (time, open, high, low, close,"
                            + " spread, volume, tickCount) where passed to this builder.");
        }
        return new FullMarketData<T>(time, open, high, low, close, spread, volume, tickCount);
    }

    private boolean anyIsNull(final Object... candidates) {
        for (final Object candidate : candidates) {
            if (candidate == null) {
                return true;
            }
        }
        return false;
    }
}
