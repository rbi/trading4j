package de.voidnode.trading4j.moneymanagement.basic;

import de.voidnode.trading4j.domain.ForexSymbol;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link FixedTradeCountAtATime} works as expected.
 * 
 * @author Raik Bieniek
 */
public class FixedTradeCountAtATimeTest {

    private static final ForexSymbol SOME_SYMBOL = new ForexSymbol("EURUSD");
    private static final ForexSymbol OTHER_SYMBOL = new ForexSymbol("AUDJPY");

    private final FixedTradeCountAtATime cut = new FixedTradeCountAtATime(2);

    /**
     * When the allowed active trade count is not reached yet, new trades are allowed.
     */
    @Test
    public void tradingIsAllowedWhenGlobalTradeCountMaximumIsNotReached() {
        assertThat(cut.isTradingAllowed(SOME_SYMBOL)).isTrue();
        assertThat(cut.isTradingAllowed(OTHER_SYMBOL)).isTrue();

        cut.blockCurrencies(SOME_SYMBOL);

        assertThat(cut.isTradingAllowed(SOME_SYMBOL)).isTrue();
        assertThat(cut.isTradingAllowed(OTHER_SYMBOL)).isTrue();
    }

    /**
     * New trades are not allowed when active trade limit is reached.
     */
    @Test
    public void tradingIsBlockedWhenGlobalTradeCountMaximumIsReached() {
        cut.blockCurrencies(SOME_SYMBOL);
        cut.blockCurrencies(SOME_SYMBOL);

        assertThat(cut.isTradingAllowed(SOME_SYMBOL)).isFalse();
        assertThat(cut.isTradingAllowed(OTHER_SYMBOL)).isFalse();
    }

    /**
     * When an active trade is closed at the global trade limit, trading is allowed again.
     */
    @Test
    public void tradingIsAllowedAgainWhenEnoughTradesAreClosed() {
        // Which symbols are used for blocking and unblocking is irrelevant.
        cut.blockCurrencies(SOME_SYMBOL);
        cut.blockCurrencies(SOME_SYMBOL);
        cut.unblockCurrencies(OTHER_SYMBOL);

        assertThat(cut.isTradingAllowed(SOME_SYMBOL)).isTrue();
        assertThat(cut.isTradingAllowed(OTHER_SYMBOL)).isTrue();
    }
}
