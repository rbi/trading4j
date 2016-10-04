package de.voidnode.trading4j.domain.monetary;

import java.util.Currency;

import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.Ratio;
import de.voidnode.trading4j.domain.Volume;

import static de.voidnode.trading4j.domain.RatioUnit.PERCENT;
import static de.voidnode.trading4j.domain.RatioUnit.PERMILLE;
import static de.voidnode.trading4j.domain.VolumeUnit.LOT;
import static de.voidnode.trading4j.domain.VolumeUnit.NANO_LOT;
import static de.voidnode.trading4j.domain.monetary.PriceUnit.MAJOR;
import static de.voidnode.trading4j.domain.monetary.PriceUnit.MINOR;
import static de.voidnode.trading4j.domain.monetary.PriceUnit.PIP;
import static de.voidnode.trading4j.domain.monetary.PriceUnit.PIPETTE;
import static de.voidnode.trading4j.testutils.assertions.Assertions.assertThat;

import org.assertj.core.data.Offset;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link Price} works as expected.
 * 
 * @author Raik Bieniek
 */
public class PriceTest {

    private static final Offset<Double> ACCURACY = Offset.offset(0.000000001);

    /**
     * If a {@link Double} is used as input it should be mathematically correctly rounded to the 5th decimal place.
     */
    @Test
    public void doubleUsedAsInputShouldBeRoundedCorrectly() {
        assertThat(new Price(1.0).asPipette()).isEqualTo(100000);

        assertThat(new Price(1.12345958).asPipette()).isEqualTo(112346);
        assertThat(new Price(1.12345001).asPipette()).isEqualTo(112345);

        assertThat(new Price(1.12345001).asDouble()).isEqualTo(1.12345000, ACCURACY);
        assertThat(new Price(1.12345958).asDouble()).isEqualTo(1.12346000, ACCURACY);
    }

    /**
     * If a {@link Long} is used as input it should be interpreted as value in pipettes.
     */
    @Test
    public void longsUsedAsInputShouldBeInterpretedAsPipette() {
        assertThat(new Price(123456).asPipette()).isEqualTo(123456);
        assertThat(new Price(1234567).asPipette()).isEqualTo(1234567);

        assertThat(new Price(123456).asDouble()).isEqualTo(1.23456000, ACCURACY);
        assertThat(new Price(1234567).asDouble()).isEqualTo(12.3456700, ACCURACY);
    }

    /**
     * A price can be constructed by passing a value and a unit to it.
     */
    @Test
    public void priceCanBeConstructedByPassingAValueAndAUnit() {
        assertThat(new Price(12345, PIPETTE).asPipette()).isEqualTo(12345);
        assertThat(new Price(12345, PIP).asPipette()).isEqualTo(123450);
        assertThat(new Price(12345, MINOR).asPipette()).isEqualTo(12345000);
        assertThat(new Price(12345, MAJOR).asPipette()).isEqualTo(1234500000);

        assertThat(new Price(-12345, PIP).asPipette()).isEqualTo(-123450);
        assertThat(new Price(0, MINOR).asPipette()).isEqualTo(0);
    }

    /**
     * A price should only equal other (non-<code>null</code>) instances of {@link Price}s thats value is the same.
     */
    @Test
    public void shouldEqualOtherPricesThatsValueIsTheSame() {
        assertThat(new Price(123456)).isEqualTo(new Price(1.23456));

        assertThat(new Price(123456)).isNotEqualTo(new Price(1.3));
        assertThat(new Price(1.0)).isNotEqualTo(null);
        assertThat(new Price(1)).isNotEqualTo(1);
        assertThat(new Price(1.0)).isNotEqualTo("a string");
    }

    /**
     * The {@link Price#isGreaterThan(Price)} and {@link Price#isLessThan(Price)} methods should work as obviously
     * expected.
     */
    @Test
    public void lessThanAndGreaterThanShouldWorkAsIntuitivlyExpected() {
        assertThat(new Price(1.0).isGreaterThan(new Price(0.9))).isTrue();
        assertThat(new Price(1.0).isGreaterThan(new Price(1.1))).isFalse();
        assertThat(new Price(1000).isGreaterThan(new Price(1000))).isFalse();
        assertThat(new Price(Long.MAX_VALUE).isGreaterThan(new Price(Long.MIN_VALUE))).isTrue();
        assertThat(new Price(Long.MIN_VALUE).isGreaterThan(new Price(Long.MAX_VALUE))).isFalse();

        assertThat(new Price(1.0).isLessThan(new Price(1.1))).isTrue();
        assertThat(new Price(1.0).isLessThan(new Price(0.9))).isFalse();
        assertThat(new Price(1000).isLessThan(new Price(1000))).isFalse();
        assertThat(new Price(Long.MAX_VALUE).isLessThan(new Price(Long.MIN_VALUE))).isFalse();
        assertThat(new Price(Long.MIN_VALUE).isLessThan(new Price(Long.MAX_VALUE))).isTrue();
    }

    /**
     * The {@link Price#isStrongerThan(Price, MarketDirection)} and {@link Price#isWeakerThan(Price, MarketDirection)} methods should
     * consider the direction correctly.
     */
    @Test
    public void strongerThanAndWeakerThanShouldConsiderTheDirectionCorrectly() {
        assertThat(new Price(10).isStrongerThan(new Price(5), MarketDirection.UP)).isTrue();
        assertThat(new Price(10).isStrongerThan(new Price(5), MarketDirection.DOWN)).isFalse();
        assertThat(new Price(10).isStrongerThan(new Price(10), MarketDirection.UP)).isFalse();
        assertThat(new Price(10).isStrongerThan(new Price(10), MarketDirection.DOWN)).isFalse();
        assertThat(new Price(Long.MAX_VALUE).isStrongerThan(new Price(Long.MIN_VALUE), MarketDirection.UP)).isTrue();

        assertThat(new Price(10).isWeakerThan(new Price(5), MarketDirection.UP)).isFalse();
        assertThat(new Price(10).isWeakerThan(new Price(5), MarketDirection.DOWN)).isTrue();
        assertThat(new Price(10).isWeakerThan(new Price(10), MarketDirection.UP)).isFalse();
        assertThat(new Price(10).isWeakerThan(new Price(10), MarketDirection.DOWN)).isFalse();
        assertThat(new Price(Long.MAX_VALUE).isWeakerThan(new Price(Long.MIN_VALUE), MarketDirection.UP)).isFalse();
    }

    /**
     * The {@link Price#compareTo(Price)} method should work as obviously expected.
     */
    @Test
    public void theComparatorShouldWorkAsIntuitivlyExpected() {
        assertThat(new Price(1.0).compareTo(new Price(3.0))).isLessThan(0);
        assertThat(new Price(6.58).compareTo(new Price(6.58))).isEqualTo(0);
        assertThat(new Price(3.69).compareTo(new Price(2.20))).isGreaterThan(0);

        assertThat(new Price(3.69).compareTo(new Price(-6.48))).isGreaterThan(0);

        assertThat(new Price(Long.MAX_VALUE - 1).compareTo(new Price(Long.MIN_VALUE + 1))).isGreaterThan(0);
    }

    /**
     * Adding two prices should work correctly.
     */
    @Test
    public void addingOtherPricesShouldWork() {
        assertThat(new Price(50).plus(new Price(1.0))).isEqualTo(new Price(1.0005));
        assertThat(new Price(2).plus(new Price(60).plus(new Price(500)))).isEqualTo(new Price(562));

        assertThat(new Price(1.5).plus(new Price(0))).isEqualTo(new Price(1.5));
        assertThat(new Price(0.2).plus(new Price(-0.4))).isEqualTo(new Price(-0.2));
        assertThat(new Price(-50).plus(new Price(-60))).isEqualTo(new Price(-110));
    }

    /**
     * Adding raw values should work correctly, honoring the provided {@link PriceUnit}.
     */
    @Test
    public void addingRawValuesShouldWork() {
        assertThat(new Price(1.0).plus(2, MAJOR)).isEqualTo(new Price(3.0));
        assertThat(new Price(1.0).plus(26, MINOR)).isEqualTo(new Price(1.26));
        assertThat(new Price(1.0).plus(59, PIP)).isEqualTo(new Price(1.0059));
        assertThat(new Price(1.0).plus(7, PIPETTE)).isEqualTo(new Price(1.00007));

        assertThat(new Price(1.0).plus(81359, PIP)).isEqualTo(new Price(9.1359));
        assertThat(new Price(1.0).plus(0, MINOR)).isEqualTo(new Price(1.0));
        assertThat(new Price(-20.0).plus(105, MINOR)).isEqualTo(new Price(-18.95));
    }

    /**
     * Adding fractions of {@link Price} given by {@link Ratio}s should work.
     */
    @Test
    public void addingRatiosShouldWork() {
        assertThat(new Price(10000).plus(10, PERCENT)).isEqualTo(new Price(11000));
        assertThat(new Price(18680).plus(new Ratio(53, PERMILLE))).isEqualTo(new Price(19670));

        assertThat(new Price(1).plus(50, PERCENT)).isEqualTo(new Price(2));
        assertThat(new Price(1).plus(49, PERCENT)).isEqualTo(new Price(1));

        assertThat(new Price(2928).plus(0, PERCENT)).isEqualTo(new Price(2928));
        assertThat(new Price(-100).plus(10, PERCENT)).isEqualTo(new Price(-110));
    }

    /**
     * Subtracting two prices should work correctly.
     */
    @Test
    public void subtractingOtherPricesShouldWork() {
        assertThat(new Price(1.0).minus(new Price(50))).isEqualTo(new Price(0.9995));
        assertThat(new Price(2.0).minus(new Price(60)).minus(new Price(500))).isEqualTo(new Price(199440));

        assertThat(new Price(1.5).minus(new Price(0))).isEqualTo(new Price(1.5));
        assertThat(new Price(-0.2).minus(new Price(-0.4))).isEqualTo(new Price(0.2));
        assertThat(new Price(-50).minus(new Price(60))).isEqualTo(new Price(-110));
    }

    /**
     * Subtracting raw values should work correctly, honoring the provided {@link PriceUnit}.
     */
    @Test
    public void subtractingRawValuesShould() {
        assertThat(new Price(10.0).minus(6, MAJOR)).isEqualTo(new Price(4.0));
        assertThat(new Price(10.0).minus(48, MINOR)).isEqualTo(new Price(9.52));
        assertThat(new Price(10.12345).minus(34, PIP)).isEqualTo(new Price(10.12005));
        assertThat(new Price(10.00005).minus(6, PIPETTE)).isEqualTo(new Price(9.99999));

        assertThat(new Price(10.0).minus(58162, PIP)).isEqualTo(new Price(4.1838));
        assertThat(new Price(8.68).minus(0, PIPETTE)).isEqualTo(new Price(8.68));
        assertThat(new Price(10.0).minus(20, MAJOR)).isEqualTo(new Price(-10.0));
    }

    /**
     * Subtracting fractions of {@link Price} given by {@link Ratio}s should work.
     */
    @Test
    public void subtractingRatiosShouldWork() {
        assertThat(new Price(10000).minus(10, PERCENT)).isEqualTo(new Price(9000));
        assertThat(new Price(18680).minus(new Ratio(53, PERMILLE))).isEqualTo(new Price(17690));

        assertThat(new Price(1).minus(50, PERCENT)).isEqualTo(new Price(1));
        assertThat(new Price(1).minus(51, PERCENT)).isEqualTo(new Price(0));

        assertThat(new Price(2928).minus(0, PERCENT)).isEqualTo(new Price(2928));
        assertThat(new Price(-100).minus(10, PERCENT)).isEqualTo(new Price(-90));
    }

    /**
     * The inverse of the price should be calculated correctly.
     */
    @Test
    public void inverseShouldBeCalculatedCorrectly() {
        assertThat(new Price(2581).inverse()).isEqualTo(new Price(-2581));
        assertThat(new Price(-528921).inverse()).isEqualTo(new Price(528921));
        assertThat(new Price(0).inverse()).isEqualTo(new Price(0));
    }

    /**
     * The absolute {@link Price} should be calculated correctly.
     */
    @Test
    public void absolutePriceShoudBeCalculatedCorrectly() {
        assertThat(new Price(2581).absolute()).isEqualTo(new Price(2581));
        assertThat(new Price(-528921).absolute()).isEqualTo(new Price(528921));
        assertThat(new Price(0).absolute()).isEqualTo(new Price(0));
    }

    /**
     * Multiplying a {@link Price} with a {@link Ratio} should work.
     * 
     * <p>
     * The results are rounded mathematically correctly.
     * </p>
     */
    @Test
    public void mutliplyingWithRatiosShouldWork() {
        // simple case
        assertThat(new Price(10).multiply(new Ratio(50, PERCENT))).isEqualTo(new Price(5));

        // rounding
        assertThat(new Price(10).multiply(new Ratio(1.0 / 3.0))).isEqualTo(new Price(3));
        assertThat(new Price(10).multiply(new Ratio(2.0 / 3.0))).isEqualTo(new Price(7));

        // special cases
        assertThat(new Price(-10).multiply(new Ratio(50, PERCENT))).isEqualTo(new Price(-5));
        assertThat(new Price(5018).multiply(new Ratio(0, PERCENT))).isEqualTo(new Price(0));
    }

    /**
     * Multiplying a {@link Price} with a {@link Volume} should result in a {@link Money} instance.
     */
    @Test
    public void multiplyingWithMoneyShouldWork() {
        final Currency eur = Currency.getInstance("EUR");
        final Currency usd = Currency.getInstance("USD");

        assertThat(new Price(53059).multiply(new Volume(10, LOT), eur)).isEqualTo(new Money(530590, 0, eur));
        assertThat(new Price(-5800).multiply(new Volume(9, NANO_LOT), usd)).isEqualTo(new Money(-52, 20, usd));

        assertThat(new Price(5878).multiply(new Volume(1, NANO_LOT), eur)).isEqualTo(new Money(5, 87, eur));

        assertThat(new Price(1000).multiply(new Volume(0, LOT), eur)).isEqualTo(new Money(0, eur));
        assertThat(new Price(0).multiply(new Volume(10, LOT), usd)).isEqualTo(new Money(0, usd));
    }

    /**
     * Dividing a price by another price should result in a Ratio.
     */
    @Test
    public void dividingByAnotherPriceResultsInARatio() {
        assertThat(new Price(300).divide(new Price(600))).isApproximatelyEqualTo(new Ratio(0.5), ACCURACY);
        assertThat(new Price(5).divide(new Price(5))).isApproximatelyEqualTo(new Ratio(1.0), ACCURACY);
        assertThat(new Price(513).divide(new Price(0))).isApproximatelyEqualTo(new Ratio(Double.POSITIVE_INFINITY),
                ACCURACY);
    }

    /**
     * A price can check if it is between two other prices.
     */
    @Test
    public void canCalculateIfPriceIsBetweenOtherPrices() {
        assertThat(new Price(10).isBetweenInclusive(new Price(5), new Price(15))).isTrue();
        assertThat(new Price(10).isBetweenInclusive(new Price(15), new Price(5))).isTrue();
        assertThat(new Price(10).isBetweenInclusive(new Price(150), new Price(50))).isFalse();

        assertThat(new Price(10).isBetweenInclusive(new Price(11), new Price(10))).isTrue();
        assertThat(new Price(10).isBetweenInclusive(new Price(10), new Price(11))).isTrue();
        assertThat(new Price(10).isBetweenInclusive(new Price(10), new Price(10))).isTrue();
        assertThat(new Price(0).isBetweenInclusive(new Price(-1), new Price(1))).isTrue();
        assertThat(new Price(-21).isBetweenInclusive(new Price(-1), new Price(-30))).isTrue();
        assertThat(new Price(-21).isBetweenInclusive(new Price(-31), new Price(-30))).isFalse();
    }

    /**
     * The {@link Price#toString()} produces a {@link String} with 5 decimal places and a - sign when it is negative.
     */
    @Test
    public void toStringHas5DecimalPlacesAndASignWhenNegative() {
        assertThat(new Price(158636).toString()).isEqualTo("1.58636");
        assertThat(new Price(951).toString()).isEqualTo("0.00951");
        assertThat(new Price(95100).toString()).isEqualTo("0.95100");
        assertThat(new Price(-10).toString()).isEqualTo("-0.00010");
        assertThat(new Price(-995183).toString()).isEqualTo("-9.95183");
        assertThat(new Price(0).toString()).isEqualTo("0.00000");
    }

    /**
     * The {@link Price#toStringWithSign()} method always adds a sign even if the price is positive.
     */
    @Test
    public void toStringWithSignAlwaysAddsASignEvenWhenPriceIsPositive() {
        assertThat(new Price(158636).toStringWithSign()).isEqualTo("+1.58636");
        assertThat(new Price(951).toStringWithSign()).isEqualTo("+0.00951");
        assertThat(new Price(95100).toStringWithSign()).isEqualTo("+0.95100");
        assertThat(new Price(-10).toStringWithSign()).isEqualTo("-0.00010");
        assertThat(new Price(-995183).toStringWithSign()).isEqualTo("-9.95183");
        assertThat(new Price(0).toStringWithSign()).isEqualTo("+0.00000");
    }
}