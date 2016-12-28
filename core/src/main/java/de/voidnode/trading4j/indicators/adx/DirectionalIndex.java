package de.voidnode.trading4j.indicators.adx;

import java.util.Optional;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.Ratio;
import de.voidnode.trading4j.domain.marketdata.MarketData;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * Calculates the directional index for one market direction (+DI or -DI).
 * 
 * @author Raik Bieniek
 * @param <C>
 *            The concrete type of {@link MarketData} that is used as input.
 */
class DirectionalIndex<C extends MarketData<?>> implements Indicator<Ratio, C> {

    private static final Ratio ZERO = new Ratio(0);

    private final Indicator<Price, C> directionalMovement;
    private final Indicator<Price, C> trueRange;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param directionalMovement
     *            The positive or negative directional movement (+DM or -DM).
     * @param trueRange
     *            The true range.
     */
    DirectionalIndex(final Indicator<Price, C> directionalMovement, final Indicator<Price, C> trueRange) {
        this.directionalMovement = directionalMovement;
        this.trueRange = trueRange;
    }

    @Override
    public Optional<Ratio> indicate(final C marketPrice) {
        final Optional<Price> optDm = directionalMovement.indicate(marketPrice);
        final Optional<Price> optTr = trueRange.indicate(marketPrice);

        return optDm.flatMap(dm -> optTr.map(tr -> tr.asPipette() == 0 ? ZERO : new Ratio(dm.divide(tr).asBasic())));
    }

}
