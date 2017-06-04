package de.voidnode.trading4j.indicators;

import java.util.Optional;

import static java.lang.Math.abs;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.Ratio;
import de.voidnode.trading4j.domain.marketdata.MarketData;
import de.voidnode.trading4j.domain.marketdata.WithOhlc;
import de.voidnode.trading4j.domain.monetary.Price;

import static de.voidnode.trading4j.domain.MarketDirection.DOWN;
import static de.voidnode.trading4j.domain.MarketDirection.UP;

/**
 * The parabolic stop and reverse (PSAR) indicator tries to detect a high probability for a switch of the direction of
 * an asset.
 * 
 * @author Raik Bieniek
 * @param <C>
 *            The concrete type of market data that is used as input.
 */
public class ParabolicStopAndReverse<C extends MarketData & WithOhlc> implements Indicator<Price, C> {

    private static final int SETUP_PERIODS = 60;

    private final double increasment;
    private final double maximalAcceleration;

    private int currentPeriode = 0;

    private MarketDirection trend;
    private double extremePoint;
    private double sar;
    private double accelerationFactor;

    /**
     * Initializes an instance with all required configuration.
     * 
     * @param accelerationFactorIncreasment
     *            The step size for speed increasing with which the price is moved towards the direction of the current
     *            market {@link Price}.
     * @param maximalAcceleration
     *            The maximal value for the acceleration factor.
     */
    public ParabolicStopAndReverse(final Ratio accelerationFactorIncreasment, final Ratio maximalAcceleration) {
        this.increasment = accelerationFactorIncreasment.asBasic();
        this.maximalAcceleration = maximalAcceleration.asBasic();
    }

    @Override
    public Optional<Price> indicate(final C marketPrice) {
        if (currentPeriode == 0) {
            this.trend = UP;
            this.extremePoint = marketPrice.getHigh().asDouble();
            this.sar = marketPrice.getLow().asDouble();
            this.accelerationFactor = increasment;
        }

        final double delta = abs(extremePoint - sar) * accelerationFactor;
        sar = trend == UP ? sar + delta : sar - delta;

        if (shouldTurn(marketPrice)) {
            turnSAR(marketPrice);
        } else {
            updateValues(marketPrice);
        }
        
        if (currentPeriode < SETUP_PERIODS) {
            currentPeriode++;
            return Optional.empty();
        } else {
            return Optional.of(new Price(sar));
        }
    }

    private boolean shouldTurn(final C marketPrice) {
        return trend == UP ? marketPrice.getLow().asDouble() <= sar : marketPrice.getHigh().asDouble() >= sar;
    }

    private void turnSAR(final C marketPrice) {
        accelerationFactor = increasment;
        trend = trend == UP ? DOWN : UP;
        sar = extremePoint;
        extremePoint = trend == UP ? marketPrice.getHigh().asDouble() : marketPrice.getLow().asDouble();
    }

    private void updateValues(final C marketPrice) {
        if (trend == UP) {
            final double high = marketPrice.getHigh().asDouble();
            if (high > extremePoint) {
                extremePoint = high;
                increaseAcceleration();
            }
        } else {
            final double low = marketPrice.getLow().asDouble();
            if (low < extremePoint) {
                extremePoint = low;
                increaseAcceleration();
            }
        }
    }

    private void increaseAcceleration() {
        final double newAccelerationFactor = accelerationFactor + increasment;
        if (newAccelerationFactor > maximalAcceleration) {
            accelerationFactor = maximalAcceleration;
        } else {
            accelerationFactor = newAccelerationFactor;
        }
    }
}
