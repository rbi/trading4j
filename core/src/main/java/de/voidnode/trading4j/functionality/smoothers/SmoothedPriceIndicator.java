package de.voidnode.trading4j.functionality.smoothers;

import java.util.Optional;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.marketdata.MarketData;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * Provides the smoothed close {@link Price} of {@link MarketData} passed as input.
 * 
 * @author Raik Bieniek
 * @param <MP>
 *            The concrete type of {@link MarketData} that is passed as input.
 */
public class SmoothedPriceIndicator<MP extends MarketData<?>> implements Indicator<Price, MP> {

    private final Smoother<Price> priceSmoother;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param priceSmoother
     *            The {@link Price} smoother that should be used.
     */
    public SmoothedPriceIndicator(final Smoother<Price> priceSmoother) {
        this.priceSmoother = priceSmoother;
    }

    @Override
    public Optional<Price> indicate(final MP marketPrice) {
        return priceSmoother.smooth(marketPrice.getClose());
    }

}
