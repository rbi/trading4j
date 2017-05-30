package de.voidnode.trading4j.indicators;

import java.util.Optional;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.Ratio;
import de.voidnode.trading4j.domain.marketdata.CandleStick;
import de.voidnode.trading4j.domain.marketdata.MarketData;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.timeframe.TimeFrame;
import de.voidnode.trading4j.functionality.smoothers.ExponentialMovingAveragePrice;

/**
 * The relative strength index (RSI) compares past gains and losses of an asset to detect overbought and oversold
 * conditions.
 * 
 * <p>
 * If the RSI is less than 30%, the asset is oversold. If it is higher that 70% the asset is overbought.
 * </p>
 * 
 * <p>
 * The RSI is calculated the following way:
 * </p>
 * 
 * <pre>
 *              100
 * RSI = 100 - ------
 *             1 + RS
 * </pre>
 * 
 * <p>
 * Where <code>RS</code> is defined the following way:
 * </p>
 * 
 * <pre>
 * RS = average gain/average loose
 * </pre>
 * 
 * <p>
 * Gain and loose are calculated comparing the <code>marketData(t)</code> and the <code>marketData(t-1)</code>.
 * </p>
 * <ul>
 * <li>When the {@link Price} of <code>marketData(t)</code> was higher than the {@link Price}
 * <code>marketData(t-1)</code>, than the <code>gain</code> for this period is
 * <code>marketData(t) - marketData(t-1)</code> and the <code>loose</code> for this period is <code>0</code></li>
 * <li>When the {@link Price} of <code>marketData(t)</code> was lower than the {@link Price}
 * <code>marketData(t-1)</code>, than the <code>gain</code> for this period is <code>0</code> and the <code>loose</code>
 * for this period is <code>marketData(t - 1) - marketData(t)</code></li>
 * <li>When the {@link Price} of <code>marketData(t)</code> is equal to the {@link Price} <code>marketData(t-1)</code>,
 * than the <code>gain</code> for this period is <code>0</code> and the <code>loose</code> for this period is also
 * <code>0</code></li>
 * </ul>
 * 
 * @author Raik Bieniek
 * @param <MP>
 *            The type of {@link MarketData} that the indicator should be using for the calculations.
 * @param <TF>
 *            The time frame of the passed {@link CandleStick} which will also be the output time frame.
 */
public class RelativeStrengthIndex<MP extends MarketData<?>, TF extends TimeFrame> implements Indicator<Ratio, MP> {

    private final Indicator<Price, MarketData<TF>> upMa;
    private final Indicator<Price, MarketData<TF>> downMa;

    private final MarketData<TF> noChange = new MarketData<>(new Price(0));
    private Price lastPrice;

    /**
     * Initializes an instance by instantiating all its dependencies itself.
     * 
     * <p>
     * With this constructors {@link ExponentialMovingAveragePrice}s are used.
     * </p>
     * 
     * @param aggregationCount
     *            The amount of candles that should be used for each moving average.
     */
    public RelativeStrengthIndex(final int aggregationCount) {
        final MovingAverageIndicatorFactory factory = new MovingAverageIndicatorFactory();
        this.upMa = factory.createExponentialMovingAverage(aggregationCount);
        this.downMa = factory.createExponentialMovingAverage(aggregationCount);
    }

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param upMa
     *            The moving average that should be used for candles with upward changes.
     * @param downMa
     *            The moving average that should be used for candles with downward changes.
     */
    public RelativeStrengthIndex(final Indicator<Price, MarketData<TF>> upMa,
            final Indicator<Price, MarketData<TF>> downMa) {
        this.upMa = upMa;
        this.downMa = downMa;
    }

    @Override
    public Optional<Ratio> indicate(final MP price) {
        final Price lastPrice = this.lastPrice;
        final Price currentPrice = price.getClose();
        this.lastPrice = price.getClose();

        if (lastPrice == null) {
            return Optional.empty();
        }

        if (lastPrice.equals(currentPrice)) {
            return rsi(noChange, noChange);
        }

        if (lastPrice.isLessThan(currentPrice)) {
            return rsi(new MarketData<>(currentPrice.minus(lastPrice)), noChange);
        } else {
            return rsi(noChange, new MarketData<>(lastPrice.minus(currentPrice)));
        }
    }

    private Optional<Ratio> rsi(final MarketData<TF> upChange, final MarketData<TF> downChange) {
        final Optional<Price> upMa = this.upMa.indicate(upChange);
        final Optional<Price> downMa = this.downMa.indicate(downChange);

        if (!upMa.isPresent() || !downMa.isPresent()) {
            return Optional.empty();
        }

        final double rs = (double) upMa.get().asPipette() / (double) downMa.get().asPipette();
        final double rsi = 1 - (1 / (1 + rs));
        return Optional.of(new Ratio(rsi));
    }
}
