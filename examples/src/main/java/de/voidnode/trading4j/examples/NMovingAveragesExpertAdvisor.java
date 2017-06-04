package de.voidnode.trading4j.examples;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.marketdata.MarketData;
import de.voidnode.trading4j.domain.marketdata.WithOhlc;
import de.voidnode.trading4j.domain.marketdata.WithSpread;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.monetary.PriceUnit;
import de.voidnode.trading4j.domain.orders.ExecutionCondition;
import de.voidnode.trading4j.strategyexpertadvisor.TradingStrategy;

/**
 * A trading strategy based on multiple moving averages indicating a trend in the same direction.
 * 
 * @author Raik Bieniek
 *
 * @param <C>
 *            The type of {@link MarketData}s that is used as input.
 */
class NMovingAveragesExpertAdvisor<C extends MarketData & WithOhlc & WithSpread> implements TradingStrategy<C> {

    private static final Optional<Price> NO_TAKE_PROFIT = Optional.of(new Price(0));
    private static final Price ENTRY_PRICE_DISTANCE = new Price(15, PriceUnit.PIPETTE);

    private final Indicator<Price, C> slow;
    private final List<Indicator<Price, C>> fastMovingAverages;

    private Optional<MarketDirection> trend = Optional.empty();
    private Optional<MarketDirection> signal = Optional.empty();
    private Optional<Price> lastSlow = Optional.empty();
    private Optional<Price> entry;
    private Price spread;
    private MaAlignment oldAlignment;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param slow
     *            A slow moving average for the long-term trend.
     * @param fastMovingAverages
     *            Faster moving averages that are used to detect a signal. They must be ordered from the slowest to the
     *            fastest. At least one fast moving average must be passed.
     */
    @SafeVarargs
    NMovingAveragesExpertAdvisor(final Indicator<Price, C> slow, final Indicator<Price, C>... fastMovingAverages) {
        if (fastMovingAverages.length == 0) {
            throw new IllegalArgumentException(
                    "No fast moving average was passed in the constructor. At least on is requiered.");
        }
        this.fastMovingAverages = asList(fastMovingAverages);
        this.slow = slow;
    }

    @Override
    public void update(final C candle) {
        final List<Optional<Price>> nextFast = fastMovingAverages.stream().map(i -> i.indicate(candle))
                .collect(toList());
        final Optional<Price> nextSlow = slow.indicate(candle);
        this.spread = candle.getSpread();

        nextSlow.ifPresent(p -> {
            if (p.isGreaterThan(candle.getHigh())) {
                trend = Optional.of(MarketDirection.DOWN);
            } else if (p.isLessThan(candle.getLow())) {
                trend = Optional.of(MarketDirection.UP);
            } else {
                trend = Optional.empty();
            }
        });

        detectSignalAndAllignment(nextFast, nextSlow);
        entry = Optional.of(candle.getClose());
        lastSlow = nextSlow;
    }

    private void detectSignalAndAllignment(final List<Optional<Price>> nextFast, final Optional<Price> nextSlow) {
        final boolean allPresent = allPresent(nextFast, nextSlow);
        final MaAlignment alignment;
        if (allPresent && orderedDescending(nextSlow, nextFast)) {
            alignment = MaAlignment.DOWN;
        } else if (allPresent && orderedAscending(nextSlow, nextFast)) {
            alignment = MaAlignment.UP;
        } else {
            alignment = MaAlignment.UNALIGNED;
        }

        if (alignment == MaAlignment.UP && oldAlignment != MaAlignment.UP) {
            signal = Optional.of(MarketDirection.UP);
        } else if (alignment == MaAlignment.DOWN && oldAlignment != MaAlignment.DOWN) {
            signal = Optional.of(MarketDirection.DOWN);
        } else {
            signal = Optional.empty();
        }

        oldAlignment = alignment;
    }

    @Override
    public Optional<MarketDirection> getEntrySignal() {
        return signal;
    }

    @Override
    public Optional<MarketDirection> getTrend() {
        return trend;
    }

    @Override
    public Optional<Price> getEntryPrice() {
        return signal.flatMap(sig -> entry.map(price -> sig == MarketDirection.UP
                ? price.plus(spread).plus(ENTRY_PRICE_DISTANCE) : price.minus(ENTRY_PRICE_DISTANCE)));
    }

    @Override
    public Optional<Price> getTakeProfit() {
        return NO_TAKE_PROFIT;
    }

    @Override
    public Optional<Price> getStopLoose() {
        return trend.flatMap(trend -> lastSlow.map(sl -> trend == MarketDirection.UP ? sl : sl.plus(spread)));
    }

    @Override
    public ExecutionCondition getEntryCondition() {
        return ExecutionCondition.STOP;
    }

    private boolean orderedDescending(final Optional<Price> first, final List<Optional<Price>> rest) {
        Price lastPrice = first.get();
        for (final Optional<Price> price : rest) {
            if (lastPrice.isLessThan(price.get())) {
                return false;
            }
            lastPrice = price.get();
        }
        return true;
    }

    private boolean orderedAscending(final Optional<Price> first, final List<Optional<Price>> rest) {
        Price lastPrice = first.get();
        for (final Optional<Price> price : rest) {
            if (lastPrice.isGreaterThan(price.get())) {
                return false;
            }
            lastPrice = price.get();
        }
        return true;
    }

    private boolean allPresent(final List<Optional<Price>> prices, final Optional<Price> extraPrice) {
        if (!extraPrice.isPresent()) {
            return false;
        }
        for (final Optional<Price> price : prices) {
            if (!price.isPresent()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Describes the alignment of the moving averages.
     */
    private enum MaAlignment {
        UP, DOWN, UNALIGNED
    }
}
