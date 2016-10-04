package de.voidnode.trading4j.moneymanagement.basic;

import java.util.Currency;

import de.voidnode.trading4j.domain.ForexSymbol;
import de.voidnode.trading4j.domain.monetary.AccuratePrice;
import de.voidnode.trading4j.domain.monetary.Price;

import static de.voidnode.trading4j.testutils.assertions.Assertions.assertThat;

import org.assertj.core.data.Offset;
import org.junit.Test;

/**
 * Checks if {@link PipetteValueCalculator} works as expected.
 * 
 * @author Raik Bieniek
 */
public class PipetteValueCalculatorTest {

    private static final Price ARBITRARY_PRICE = new Price(1.53813);
    private static final Offset<Double> ALLOWED_OFFSET = Offset.offset(0.000000000001);
    private final PipetteValueCalculator cut = new PipetteValueCalculator();

    /**
     * The value of one pipette is one pipette when the quote currency equals the trading account currency.
     */
    @Test
    public void pipetteValueIsOnePipetteWhenQuoteCurrencyEqualsAccountCurrency() {
        assertThat(cut.calculatePipetteValue(currency("USD"), new ForexSymbol("EURUSD"), ARBITRARY_PRICE, new ForexSymbol("EURUSD"),
                ARBITRARY_PRICE)).isEqualTo(new AccuratePrice(0.00001), ALLOWED_OFFSET);

        assertThat(cut.calculatePipetteValue(currency("JPY"), new ForexSymbol("CHFJPY"), ARBITRARY_PRICE, new ForexSymbol("CHFJPY"),
                ARBITRARY_PRICE)).isEqualTo(new AccuratePrice(0.00001), ALLOWED_OFFSET);
    }

    /**
     * The value of one pipette is one pipette divided by the exchange rate when the base currency equals the trading
     * account currency.
     */
    @Test
    public void pipetteValueIsOnePipetteDividedByExchangeRateWhenBaseCurrencyEqualsAccountCurreny() {
        assertThat(cut.calculatePipetteValue(currency("EUR"), new ForexSymbol("EURUSD"), new Price(1.6), new ForexSymbol("EURUSD"),
                new Price(1.6))).isEqualTo(new AccuratePrice(0.00000625), ALLOWED_OFFSET);

        assertThat(cut.calculatePipetteValue(currency("CHF"), new ForexSymbol("CHFJPY"), new Price(160.0), new ForexSymbol("CHFJPY"),
                new Price(160.0))).isEqualTo(new AccuratePrice(0.000000062500), ALLOWED_OFFSET);
    }

    /**
     * The value of one pipette is one pipette multiplied by the account currency exchange rate when the account
     * currency is the quote currency of that account currency exchange rate symbol.
     */
    @Test
    public void pipetteValueIsOnePipetteMultipliedByAccountCurrencyExchangeRateWhenAccountCurrencySymbolHasAccountCurrencyAsQuoteCurrency() {
        assertThat(cut.calculatePipetteValue(currency("CAD"), new ForexSymbol("EURAUD"), new Price(1.2), new ForexSymbol("AUDCAD"),
                new Price(1.8))).isEqualTo(new AccuratePrice(0.000018), ALLOWED_OFFSET);
        
        assertThat(cut.calculatePipetteValue(currency("CHF"), new ForexSymbol("GBPUSD"), new Price(1.9), new ForexSymbol("USDCHF"),
                new Price(1.358))).isEqualTo(new AccuratePrice(0.00001358), ALLOWED_OFFSET);
    }

    /**
     * The value of one pipette is one pipette divided by the account currency exchange rate when the account currency
     * is the base currency of that account currency exchange rate symbol.
     */
    @Test
    public void pipetteValueIsOnePipetteDividedByAccountCurrencyExchangeRateWhenAccountCurrencySymbolHasAccountCurrencyAsBaseCurrency() {
        assertThat(cut.calculatePipetteValue(currency("USD"), new ForexSymbol("GBPCHF"), new Price(1.41), new ForexSymbol("USDCHF"),
                new Price(1.25))).isEqualTo(new AccuratePrice(0.000008), ALLOWED_OFFSET);
        
        assertThat(cut.calculatePipetteValue(currency("CAD"), new ForexSymbol("NZDJPY"), new Price(1.325), new ForexSymbol("CADJPY"),
                new Price(1.63))).isEqualTo(new AccuratePrice(0.0000061349693), ALLOWED_OFFSET);

    }

    /**
     * Fails when the account currency exchange rate does not contain the account currency.
     */
    @Test(expected = IllegalArgumentException.class)
    public void failsWhenAccountCurrencyExchangeRateSymbolDoesNotContainAccountCurrency() {
        cut.calculatePipetteValue(currency("CHF"), new ForexSymbol("EURUSD"), ARBITRARY_PRICE, new ForexSymbol("USDCAD"),
                ARBITRARY_PRICE);
    }

    /**
     * Fails when the account currency exchange rate does not contain the qoute currency of the traded symbol.
     */
    @Test(expected = IllegalArgumentException.class)
    public void failsWhenAccountCurrencyExchangeRateSymbolDoesNotContainTradeSymbolQuoteCurrency() {
        cut.calculatePipetteValue(currency("CHF"), new ForexSymbol("EURUSD"), ARBITRARY_PRICE, new ForexSymbol("CHFJPY"),
                ARBITRARY_PRICE);
    }

    private Currency currency(final String currency) {
        return Currency.getInstance(currency);
    }
}
