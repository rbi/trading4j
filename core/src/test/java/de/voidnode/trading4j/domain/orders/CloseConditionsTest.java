package de.voidnode.trading4j.domain.orders;

import java.time.Instant;

import de.voidnode.trading4j.domain.monetary.Price;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link CloseConditions} works as expectde.
 * 
 * @author Raik Bieniek
 */
public class CloseConditionsTest {

    /**
     * {@link CloseConditions} can only be equal to other {@link CloseConditions} with exactly the same values.
     */
    @Test
    public void onlyCloseConditionsInstancesWithSameValuesAreEqual() {
        final CloseConditions cut = new CloseConditions(new Price(1.0), new Price(2.0), Instant.ofEpochMilli(3000));

        assertThat(cut).isEqualTo(new CloseConditions(new Price(1.0), new Price(2.0), Instant.ofEpochMilli(3000)));

        assertThat(cut).isNotEqualTo(new CloseConditions(new Price(5.0), new Price(3.0), Instant.ofEpochMilli(4000)));
        assertThat(cut).isNotEqualTo("Not a CloseConditionsObject");
        assertThat(cut).isNotEqualTo(null);
    }
}
