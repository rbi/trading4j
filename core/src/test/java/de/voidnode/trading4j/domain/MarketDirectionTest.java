package de.voidnode.trading4j.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link MarketDirection} works as expected.
 * 
 * @author Raik Bieniek
 */
public class MarketDirectionTest {

    /**
     * The trend can be inverted correctly.
     */
    @Test
    public void canBeInverted() {
        assertThat(MarketDirection.UP.inverted()).isEqualTo(MarketDirection.DOWN);
        assertThat(MarketDirection.DOWN.inverted()).isEqualTo(MarketDirection.UP);
    }
}
