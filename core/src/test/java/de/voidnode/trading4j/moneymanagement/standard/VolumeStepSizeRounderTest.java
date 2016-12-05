package de.voidnode.trading4j.moneymanagement.standard;

import de.voidnode.trading4j.domain.Volume;

import static de.voidnode.trading4j.domain.VolumeUnit.BASE;
import static de.voidnode.trading4j.domain.VolumeUnit.LOT;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link VolumeStepSizeRounder} works as expected.
 * 
 * @author Raik Bieniek
 */
public class VolumeStepSizeRounderTest {

    private final VolumeStepSizeRounder cut = new VolumeStepSizeRounder();

    /**
     * {@link Volume}s are always rounded down to match a given step size.
     */
    @Test
    public void roundsVolumeDownToNextStepSize() {
        assertThat(cut.round(new Volume(32, BASE), new Volume(10, BASE))).isEqualTo(new Volume(30, BASE));
        assertThat(cut.round(new Volume(37, BASE), new Volume(10, BASE))).isEqualTo(new Volume(30, BASE));
        assertThat(cut.round(new Volume(2, BASE), new Volume(10, BASE))).isEqualTo(new Volume(0, BASE));
        assertThat(cut.round(new Volume(403, LOT), new Volume(15, LOT))).isEqualTo(new Volume(390, LOT));
    }
}
