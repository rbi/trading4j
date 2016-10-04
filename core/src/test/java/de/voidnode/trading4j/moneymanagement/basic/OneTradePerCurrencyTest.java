package de.voidnode.trading4j.moneymanagement.basic;

import de.voidnode.trading4j.api.UnrecoverableProgrammingError;
import de.voidnode.trading4j.domain.ForexSymbol;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link OneTradePerCurrency} works as expected.
 * 
 * @author Raik Bieniek
 */
public class OneTradePerCurrencyTest {

    private final OneTradePerCurrency cut = new OneTradePerCurrency();

    /**
     * Only one trade per currency at a time is allowed.
     */
    @Test
    public void allowsOnlyOneTradePerCurrency() {
        assertThat(cut.isTradingAllowed(new ForexSymbol("EURUSD"))).isTrue();

        cut.blockCurrencies(new ForexSymbol("EURUSD"));

        assertThat(cut.isTradingAllowed(new ForexSymbol("EURUSD"))).isFalse();
        assertThat(cut.isTradingAllowed(new ForexSymbol("EURCHF"))).isFalse();
        assertThat(cut.isTradingAllowed(new ForexSymbol("USDCAD"))).isFalse();

        assertThat(cut.isTradingAllowed(new ForexSymbol("CADCHF"))).isTrue();
    }

    /**
     * Trading a currency is allowed again when it was freed up.
     */
    @Test
    public void allowsTradingACurrencyAgainWhenItWasFreedUp() {
        cut.blockCurrencies(new ForexSymbol("EURUSD"));
        cut.unblockCurrencies(new ForexSymbol("EURUSD"));

        assertThat(cut.isTradingAllowed(new ForexSymbol("EURUSD"))).isTrue();
        assertThat(cut.isTradingAllowed(new ForexSymbol("EURCHF"))).isTrue();
        assertThat(cut.isTradingAllowed(new ForexSymbol("USDCAD"))).isTrue();

    }

    /**
     * When blocking an already blocked currency a second time, the cut throws an exception.
     */
    @Test(expected = UnrecoverableProgrammingError.class)
    public void failsWhenBlockingAnAlreadyBlockedCurrency() {
        cut.blockCurrencies(new ForexSymbol("EURUSD"));
        cut.blockCurrencies(new ForexSymbol("EURCHF"));
    }

    /**
     * When a currency is unblocked that wasn't blocked, the cut trows an exception.
     */
    @Test(expected = UnrecoverableProgrammingError.class)
    public void failsWhenUnblockingAnUnblockedCurrency() {
        cut.unblockCurrencies(new ForexSymbol("EURCHF"));
    }
}
