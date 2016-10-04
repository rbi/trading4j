package de.voidnode.trading4j.moneymanagement.basic;

import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.VolumeUnit;
import de.voidnode.trading4j.domain.monetary.AccuratePrice;
import de.voidnode.trading4j.domain.monetary.Money;
import de.voidnode.trading4j.domain.monetary.Price;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link VolumeCalculator} works as expected.
 * 
 * @author Raik Bieniek
 */
public class VolumeCalculatorTest {

    private final VolumeCalculator cut = new VolumeCalculator();

    /**
     * The volume for a trade is calculated with the following formula.
     * 
     * <p>
     * [Money to risk] / ([pips] * [pricePerPip])
     * </p>
     */
    @Test
    public void calculatesVolumeCorrectly() {
        final Volume volume1 = cut.calculateVolumeForTrade(new AccuratePrice(0.000005), new Price(200),
                new Money(100, 0, "EUR"));
        assertThat(volume1).isEqualTo(new Volume(100000, VolumeUnit.BASE));

        final Volume volume2 = cut.calculateVolumeForTrade(new AccuratePrice(0.000022), new Price(50),
                new Money(4000, 0, "USD"));
        assertThat(volume2).isEqualTo(new Volume(3636363, VolumeUnit.BASE));
    }
}
