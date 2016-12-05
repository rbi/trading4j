package de.voidnode.trading4j.moneymanagement.standard;

import java.util.Currency;
import java.util.Optional;

import de.voidnode.trading4j.domain.ForexSymbol;
import de.voidnode.trading4j.domain.monetary.AccuratePrice;
import de.voidnode.trading4j.domain.monetary.Price;

import static de.voidnode.trading4j.testutils.assertions.Assertions.assertThat;

import org.assertj.core.data.Offset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

/**
 * Checks if {@link PipetteValueCalculator} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class PipetteValueCalculatorTest {

    private static final Price ARBITRARY_PRICE = new Price(1.53813);
    private static final Offset<Double> ALLOWED_OFFSET = Offset.offset(0.000000000001);

    @Mock
    private ExchangeRateStore exchangeRateStore;

    @InjectMocks
    private PipetteValueCalculator cut;

    /**
     * The value of one pipette is one pipette when the quote currency equals the trading account currency.
     */
    @Test
    public void pipetteValueIsOnePipetteWhenQuoteCurrencyEqualsAccountCurrency() {
        assertThat(cut.calculatePipetteValue(currency("USD"), new ForexSymbol("EURUSD"), ARBITRARY_PRICE))
                .isEqualTo(new AccuratePrice(0.00001), ALLOWED_OFFSET);

        assertThat(cut.calculatePipetteValue(currency("JPY"), new ForexSymbol("CHFJPY"), ARBITRARY_PRICE))
                .isEqualTo(new AccuratePrice(0.00001), ALLOWED_OFFSET);
    }

    /**
     * The value of one pipette is one pipette divided by the exchange rate when the base currency equals the trading
     * account currency.
     */
    @Test
    public void pipetteValueIsOnePipetteDividedByExchangeRateWhenBaseCurrencyEqualsAccountCurreny() {
        assertThat(cut.calculatePipetteValue(currency("EUR"), new ForexSymbol("EURUSD"), new Price(1.6)))
                .isEqualTo(new AccuratePrice(0.00000625), ALLOWED_OFFSET);

        assertThat(cut.calculatePipetteValue(currency("CHF"), new ForexSymbol("CHFJPY"), new Price(160.0)))
                .isEqualTo(new AccuratePrice(0.000000062500), ALLOWED_OFFSET);
    }

    /**
     * The value of one pipette is one pipette multiplied by the account currency exchange rate when the account
     * currency is the quote currency of that account currency exchange rate symbol.
     */
    @Test
    public void pipetteValueIsOnePipetteMultipliedByExchangeRateOfQuoteToAccountCurrencyWhenSymbolDoesNotContainAccountCurrency() {
        when(exchangeRateStore.getExchangeRate(Currency.getInstance("AUD"), Currency.getInstance("CAD"))).thenReturn(Optional.of(new AccuratePrice(1.8)));
        assertThat(cut.calculatePipetteValue(currency("CAD"), new ForexSymbol("EURAUD"), new Price(1.2))).isEqualTo(new AccuratePrice(0.000018), ALLOWED_OFFSET);

        when(exchangeRateStore.getExchangeRate(Currency.getInstance("USD"), Currency.getInstance("CHF"))).thenReturn(Optional.of(new AccuratePrice(1.358)));
        assertThat(cut.calculatePipetteValue(currency("CHF"), new ForexSymbol("GBPUSD"), new Price(1.9))).isEqualTo(new AccuratePrice(0.00001358), ALLOWED_OFFSET);
    }

    /**
     * Fails when the currency exchange rate does not contain the exchange rate for the required currencies.
     */
    @Test(expected = IllegalArgumentException.class)
    public void failsWhenExchangeRateStoreDoesNotContainRequieredExchangeRate() {
        when(exchangeRateStore.getExchangeRate(Currency.getInstance("AUD"), Currency.getInstance("CAD"))).thenReturn(Optional.empty());
        assertThat(cut.calculatePipetteValue(currency("CAD"), new ForexSymbol("EURAUD"), new Price(1.2))).isEqualTo(new AccuratePrice(0.000018), ALLOWED_OFFSET);
    }

    private Currency currency(final String currency) {
        return Currency.getInstance(currency);
    }
}
