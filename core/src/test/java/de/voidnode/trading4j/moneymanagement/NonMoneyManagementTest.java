package de.voidnode.trading4j.moneymanagement;

import de.voidnode.trading4j.api.MoneyManagement;
import de.voidnode.trading4j.domain.ForexSymbol;
import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.monetary.Price;

import static de.voidnode.trading4j.domain.VolumeUnit.LOT;
import static de.voidnode.trading4j.domain.VolumeUnit.MINI_LOT;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link NonMoneyManagement} works as expected.
 * 
 * @author Raik Bieniek
 */
public class NonMoneyManagementTest {

    private static final Price SOME_PRICE = new Price(1.0);
    private static final Volume SOME_STEP_SIZE = new Volume(1, MINI_LOT);

    /**
     * The cut always returns the volume returned in the constructor.
     */
    @Test
    public void allwaysReturnsConfiguredVolume() {
        final MoneyManagement cut1 = new NonMoneyManagement(new Volume(42, LOT));
        final MoneyManagement cut2 = new NonMoneyManagement(new Volume(58, MINI_LOT));

        assertThat(
                cut1.requestVolume(new ForexSymbol("EURUSD"), SOME_PRICE, SOME_PRICE, SOME_STEP_SIZE).get().getVolume())
                        .isEqualTo(new Volume(42, LOT));

        assertThat(
                cut2.requestVolume(new ForexSymbol("CHFJPY"), SOME_PRICE, SOME_PRICE, SOME_STEP_SIZE).get().getVolume())
                        .isEqualTo(new Volume(58, MINI_LOT));

        // releasing volume does nothing including not crashing
        cut1.requestVolume(new ForexSymbol("USDCHF"), SOME_PRICE, SOME_PRICE, SOME_STEP_SIZE).get().releaseVolume();
    }

}
