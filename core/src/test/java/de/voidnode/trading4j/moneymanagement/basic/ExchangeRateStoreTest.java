package de.voidnode.trading4j.moneymanagement.basic;

import java.util.Currency;
import java.util.Optional;

import de.voidnode.trading4j.domain.ForexSymbol;
import de.voidnode.trading4j.domain.monetary.AccuratePrice;
import de.voidnode.trading4j.domain.monetary.Price;

import static de.voidnode.trading4j.testutils.assertions.Assertions.assertThat;

import org.assertj.core.data.Offset;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link ExchangeRateStore} work as expected.
 * 
 * @author Raik Bieniek
 */
public class ExchangeRateStoreTest {

    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Currency AUD = Currency.getInstance("AUD");
    private static final Currency USD = Currency.getInstance("USD");

    private static final Price SOME_EXCHANGE_RATE = new Price(1.0);
    private static final Offset<Double> ALLOWED_OFFSET = Offset.offset(0.0000001);

    private ExchangeRateStore cut = new ExchangeRateStore();

    /**
     * When no appropriate exchange rate was stored, an empty {@link Optional} is returned when the exchange rate is
     * requested.
     */
    @Test
    public void emptyOptionalWhenNoAppropriateExchangeRateWasStored() {
        cut.updateExchangeRate(new ForexSymbol(EUR, AUD), SOME_EXCHANGE_RATE);

        assertThat(cut.getExchangeRate(EUR, USD)).isEmpty();
    }

    /**
     * When the requested exchange rate was stored, it is returned when it is requested.
     */
    @Test
    public void exchangeRateIsReturnedWhenItWasStoredBefore() {
        cut.updateExchangeRate(new ForexSymbol(EUR, USD), new Price(1.6));

        assertThat(cut.getExchangeRate(EUR, USD).get()).isEqualTo(new AccuratePrice(1.6), ALLOWED_OFFSET);
        assertThat(cut.getExchangeRate(USD, EUR).get()).isEqualTo(new AccuratePrice(0.625), ALLOWED_OFFSET);
    }
}
