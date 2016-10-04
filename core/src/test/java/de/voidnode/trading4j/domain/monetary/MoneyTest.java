package de.voidnode.trading4j.domain.monetary;

import java.util.Currency;

import de.voidnode.trading4j.api.UnrecoverableProgrammingError;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link Money} works as expected.
 * 
 * @author Raik Bieniek
 */
public class MoneyTest {

    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Currency USD = Currency.getInstance("USD");

    /**
     * Money instances can be constructed by passing a value for the major and minor part.
     */
    @Test
    public void constructableFromMajorMinorRawValues() {
        final Money money1 = new Money(1234, 56, EUR);
        assertThat(money1.asRawValue()).isEqualTo(123456L);

        final Money money2 = new Money(-8530, 31, USD);
        assertThat(money2.asRawValue()).isEqualTo(-853031L);
        assertThat(money2.getCurrency()).isEqualTo(USD);

        final Money money3 = new Money(0, 35, "JPY");
        assertThat(money3.asRawValue()).isEqualTo(35L);
        assertThat(money3.getCurrency()).isEqualTo(Currency.getInstance("JPY"));
    }

    /**
     * Constructing a money instance with a minor value greater 99 will fail.
     */
    @Test(expected = UnrecoverableProgrammingError.class)
    public void constructingWithAMinorValueGreater99WillFail() {
        new Money(54, 123, EUR);
    }

    /**
     * Constructing a money instance with a minor value less than 0 will fail.
     */
    @Test(expected = UnrecoverableProgrammingError.class)
    public void constructingWithAMinorValueLessThan0WillFail() {
        new Money(42, -4, USD);
    }

    /**
     * Money instances can be constructed by passing the raw value.
     */
    @Test
    public void constructableFromRawValue() {
        assertThat(new Money(4286, EUR).asRawValue()).isEqualTo(4286);

        final Money money1 = new Money(0, USD);
        assertThat(money1.asRawValue()).isEqualTo(0);
        assertThat(money1.getCurrency()).isEqualTo(USD);

        final Money money2 = new Money(-958125, "CHF");
        assertThat(money2.asRawValue()).isEqualTo(-958125);
        assertThat(money2.getCurrency()).isEqualTo(Currency.getInstance("CHF"));
    }

    /**
     * A {@link Money} instance is only equal to other {@link Money} instances with the same raw value.
     */
    @Test
    public void equalsOnlyOtherMoneyInstancesWithSameValue() {
        assertThat(new Money(58, EUR)).isEqualTo(new Money(0, 58, EUR));
        assertThat(new Money(-921, 59, USD)).isEqualTo(new Money(-92159, USD));
        assertThat(new Money(0, 0, "CHF")).isEqualTo(new Money(0, Currency.getInstance("CHF")));

        assertThat(new Money(42, EUR)).isNotEqualTo(new Money(821, EUR));
        assertThat(new Money(42, EUR)).isNotEqualTo(new Money(42, USD));
        assertThat(new Money(42, EUR)).isNotEqualTo("Not money");
        assertThat(new Money(42, EUR)).isNotEqualTo(null);
    }

    /**
     * The {@link Money#toString()} method returns the money with two decimal places, the currency and a negative sign
     * when negative.
     */
    @Test
    public void toStringWorksAsExpected() {
        assertThat(new Money(58, 92, EUR).toString()).isEqualTo("58.92 EUR");
        assertThat(new Money(-60, USD).toString()).isEqualTo("-0.60 USD");
        assertThat(new Money(0, 0, "CHF").toString()).isEqualTo("0.00 CHF");
    }

    /**
     * The {@link Money#toStringWithSign()} works as the {@link Money#toString()} method but always adds a sign, even
     * when the {@link Money} is positive.
     */
    @Test
    public void toStringWithSignAllwaysAddsASign() {
        assertThat(new Money(58, 92, EUR).toStringWithSign()).isEqualTo("+58.92 EUR");
        assertThat(new Money(-60, USD).toStringWithSign()).isEqualTo("-0.60 USD");
        assertThat(new Money(0, 0, "CHF").toStringWithSign()).isEqualTo("+0.00 CHF");

    }
}
