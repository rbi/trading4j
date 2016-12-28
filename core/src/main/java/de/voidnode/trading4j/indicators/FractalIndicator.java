package de.voidnode.trading4j.indicators;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.marketdata.CandleStick;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * Calculates fractals for a single market direction.
 * 
 * <p>
 * If both, up and down fractals are required, two instances of this class are needed.
 * </p>
 * 
 * <p>
 * An up fractal for a candle stick <code>n</code> exists when the following is <code>true</code>:
 * </p>
 * 
 * <ul>
 * <li><code>high(n-2) &lt; high(n)</code></li>
 * <li><code>low(n-1) &lt; high(n)</code></li>
 * <li><code>high(n) &gt; high(n+1)</code></li>
 * <li><code>high(n) &gt; high(n+2)</code></li>
 * </ul>
 *
 * <p>
 * A down fractal for a candle stick <code>n</code> exists when the following is <code>true</code>:
 * </p>
 * 
 * <ul>
 * <li><code>low(n-2) &gt; low(n)</code></li>
 * <li><code>low(n-1) &gt; low(n)</code></li>
 * <li><code>low(n) &lt; low(n+1)</code></li>
 * <li><code>low(n) &lt; low(n+2)</code></li>
 * </ul>
 *
 * @author Raik Bieniek
 * @param <C>
 *            The type of {@link CandleStick} that the trend should base on.
 */
public class FractalIndicator<C extends CandleStick<?>> implements Indicator<MarketDirection, C> {

    private final MarketDirection direction;
    private final List<C> sticks = new LinkedList<>();

    /**
     * Initializes an instance with all required data.
     * 
     * @param direction
     *            The market direction for which fractals should be calculated.
     */
    public FractalIndicator(final MarketDirection direction) {
        this.direction = direction;

    }

    @Override
    public Optional<MarketDirection> indicate(final C candle) {
        sticks.add(candle);
        if (sticks.size() < 5) {
            return Optional.empty();
        }
        final Price[] high = new Price[] { getBest(0), getBest(1), getBest(2), getBest(3), getBest(4) };
        sticks.remove(0);
        final boolean isUpFractal = high[0].isWeakerThan(high[2], direction) && high[1].isWeakerThan(high[2], direction)
                && high[2].isStrongerThan(high[3], direction) && high[2].isStrongerThan(high[4], direction);
        return isUpFractal ? Optional.of(direction) : Optional.empty();
    }

    private Price getBest(final int index) {
        return sticks.get(index).getStrongest(direction);
    }
}
