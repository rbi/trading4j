package de.voidnode.trading4j.moneymanagement.basic;

import java.util.Currency;

import de.voidnode.trading4j.domain.Ratio;
import de.voidnode.trading4j.domain.monetary.Money;

import static de.voidnode.trading4j.domain.RatioUnit.PERCENT;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link RiskMoneyProvider} works as expected.
 * 
 * @author Raik Bieniek
 */
public class RiskMoneyProviderTest {

    private static final Currency EUR = Currency.getInstance("EUR");

    /**
     * The riskable money per trade is a fraction of the current balance.
     */
    @Test
    public void riskableMoneyIsAConfigurableFractionOfTheCompleteBalance() {
        assertThat(new RiskMoneyProvider(new Ratio(50, PERCENT)).calculateMoneyToRisk(new Money(400, 00, EUR)))
                .isEqualTo(new Money(200, 00, EUR));

        assertThat(new RiskMoneyProvider(new Ratio(20, PERCENT)).calculateMoneyToRisk(new Money(1000, 50, EUR)))
                .isEqualTo(new Money(200, 10, EUR));
    }

    /**
     * When a ratio less than 0 is passed in the constructor, the cut fails.
     */
    @Test(expected = IllegalArgumentException.class)
    public void failsWhenCutIsConstructedWithARatioLessThan0() {
        new RiskMoneyProvider(new Ratio(-10, PERCENT));
    }

    /**
     * When a ratio greater than 1 is passed in the constructor, the cut fails.
     */
    @Test(expected = IllegalArgumentException.class)
    public void failWhenCutIsConstructedWithRatioGreaterThan1() {
        new RiskMoneyProvider(new Ratio(101, PERCENT));
    }
}
