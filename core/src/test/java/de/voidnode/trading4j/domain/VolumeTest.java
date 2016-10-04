package de.voidnode.trading4j.domain;

import static de.voidnode.trading4j.domain.VolumeUnit.BASE;
import static de.voidnode.trading4j.domain.VolumeUnit.LOT;
import static de.voidnode.trading4j.domain.VolumeUnit.MICRO_LOT;
import static de.voidnode.trading4j.domain.VolumeUnit.MINI_LOT;
import static de.voidnode.trading4j.domain.VolumeUnit.NANO_LOT;

import org.assertj.core.data.Offset;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link Volume} works as expected.
 * 
 * @author Raik Bieniek
 */
public class VolumeTest {

    private static final Offset<Double> ACCURACY = Offset.offset(0.000000001);

    /**
     * Raw volume values that are used as input should be transformed correctly based on their {@link VolumeUnit}.
     */
    @Test
    public void shouldTransformRawInputValuesCorrectlyBasedOnTheirUnits() {
        assertThat(new Volume(25, BASE).asAbsolute()).isEqualTo(25);
        assertThat(new Volume(3, NANO_LOT).asAbsolute()).isEqualTo(300);
        assertThat(new Volume(8, MICRO_LOT).asAbsolute()).isEqualTo(8000);
        assertThat(new Volume(2, MINI_LOT).asAbsolute()).isEqualTo(20000);
        assertThat(new Volume(9, LOT).asAbsolute()).isEqualTo(900000);

        assertThat(new Volume(581, NANO_LOT).asAbsolute()).isEqualTo(58100);
        assertThat(new Volume(-7, MICRO_LOT).asAbsolute()).isEqualTo(-7000);
        assertThat(new Volume(0, LOT).asAbsolute()).isEqualTo(0);
    }

    /**
     * {@link Volume#asLot()} should convert correctly to {@link Double} using {@link VolumeUnit#LOT} as unit.
     */
    @Test
    public void shouldConvertCorrectlyToDouble() {
        assertThat(new Volume(56, MINI_LOT).asLot()).isEqualTo(5.6, ACCURACY);
        assertThat(new Volume(7123456, BASE).asLot()).isEqualTo(71.23456, ACCURACY);

        assertThat(new Volume(-6, MICRO_LOT).asLot()).isEqualTo(-0.06, ACCURACY);
    }

    /**
     * Should only equal other {@link Volume}s thats {@link Volume#asAbsolute()} is the same.
     */
    @Test
    public void shouldOnlyEqualOtherVolumesWithSameAbsoluteValue() {
        assertThat(new Volume(20, MINI_LOT)).isEqualTo(new Volume(20, MINI_LOT));
        assertThat(new Volume(200, NANO_LOT)).isEqualTo(new Volume(20, MICRO_LOT));

        assertThat(new Volume(123, MICRO_LOT)).isNotEqualTo(new Volume(456, MICRO_LOT));
        assertThat(new Volume(5, BASE)).isNotEqualTo(5);
        assertThat(new Volume(58, NANO_LOT)).isNotEqualTo("5800");
        assertThat(new Volume(31, MINI_LOT)).isNotEqualTo(null);
    }
}
