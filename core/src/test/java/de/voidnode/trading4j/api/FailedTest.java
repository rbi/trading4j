package de.voidnode.trading4j.api;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link Failed} works as expected.
 * 
 * @author Raik Bieniek
 */
public class FailedTest {

    /**
     * A {@link Failed} instance is only equal to other {@link Failed} instances with the same reason.
     */
    @Test
    public void equalsOnlyOtherFailedInstancesWhithTheSameReason() {
        assertThat(new Failed("some reason")).isEqualTo(new Failed("some reason"));

        assertThat(new Failed("some reason")).isNotEqualTo(new Failed("other reason"));
        assertThat(new Failed("some reason")).isNotEqualTo("not a failed");
        assertThat(new Failed("some reason")).isNotEqualTo(null);
    }

    /**
     * The {@link Failed#toString()} method just returns the reason.
     */
    @Test
    public void toStringEqualsTheReason() {
        assertThat(new Failed("some reason").toString()).isEqualTo("some reason");
    }
}
