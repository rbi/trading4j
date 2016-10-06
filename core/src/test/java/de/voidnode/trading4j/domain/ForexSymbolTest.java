package de.voidnode.trading4j.domain;

import java.util.Currency;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link ForexSymbol} works as expected.
 * 
 * @author Raik Bieniek
 */
public class ForexSymbolTest {

    /**
     * A {@link ForexSymbol} can be constructed from two known currencies.
     */
    @Test
    public void constructableForTwoKnownCurrencies() {
        final ForexSymbol currency1 = new ForexSymbol("EURUSD");
        assertThat(currency1.getBaseCurrency()).isEqualTo(Currency.getInstance("EUR"));
        assertThat(currency1.getQuoteCurrency()).isEqualTo(Currency.getInstance("USD"));

        final ForexSymbol currency2 = new ForexSymbol("AUDCAD");
        assertThat(currency2.getBaseCurrency()).isEqualTo(Currency.getInstance("AUD"));
        assertThat(currency2.getQuoteCurrency()).isEqualTo(Currency.getInstance("CAD"));
    }

    /**
     * When one of the currencies of the symbol name passed in the constructor is unknown, construction fails.
     */
    @Test(expected = RuntimeException.class)
    public void failsConstructionWhenCurrenciesAreUnknown() {
        new ForexSymbol("UNKXYZ");
    }

    /**
     * When the symbol name has less than 6 letters construction fails.
     */
    @Test(expected = IllegalArgumentException.class)
    public void failsConstructionWhenSymbolHasLessThan6Letters() {
        new ForexSymbol("EURUS");
    }

    /**
     * When the symbol name has more than 6 letters construction fails.
     */
    @Test(expected = IllegalArgumentException.class)
    public void failsConstructionWhenSymbolHasMoreThan6Letters() {
        new ForexSymbol("EURUSDD");
    }

    /**
     * {@link ForexSymbol}s are equal only to other {@link ForexSymbol} with the same currencies in the same order.
     */
    @Test
    public void equalsOnlyOtherForexSymbols() {
        assertThat(new ForexSymbol("EURUSD")).isEqualTo(new ForexSymbol("EURUSD"));

        assertThat(new ForexSymbol("EURUSD")).isNotEqualTo(new ForexSymbol("AUDCAD"));
        assertThat(new ForexSymbol("EURUSD")).isNotEqualTo(new ForexSymbol("USDEUR"));
        assertThat(new ForexSymbol("EURUSD")).isNotEqualTo("not a forex symbol");
        assertThat(new ForexSymbol("EURUSD")).isNotEqualTo(null);
    }
}
