package de.voidnode.trading4j.domain.orders;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.MINUTES;

import de.voidnode.trading4j.domain.monetary.Price;

import static de.voidnode.trading4j.testutils.assertions.Assertions.assertThat;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link MutableCloseConditions} works as expected.
 * 
 * @author Raik Bieniek
 */
public class MutableCloseConditionsTest {

    /**
     * The cut should build correct {@link CloseConditions} based on the values passed.
     */
    @Test
    public void shoudBuildCorrectCloseConditionsWithThePassedValues() {
        final MutableCloseConditions mutableCloseConditions = new MutableCloseConditions()
                .setTakeProfit(new Price(1.3)).setStopLoose(new Price(1.4));

        final CloseConditions conditionsWithoutDate = mutableCloseConditions.toImmutable();

        assertThat(conditionsWithoutDate.getTakeProfit()).isEqualTo(new Price(1.3));
        assertThat(conditionsWithoutDate.getStopLoose()).isEqualTo(new Price(1.4));
        assertThat(conditionsWithoutDate.getExpirationDate().isPresent()).isFalse();

        final Instant dummyDate = Instant.EPOCH.plus(5, MINUTES);
        mutableCloseConditions.setExpirationDate(dummyDate);
        final CloseConditions orderWithDate = mutableCloseConditions.toImmutable();

        assertThat(orderWithDate.getTakeProfit()).isEqualTo(new Price(1.3));
        assertThat(orderWithDate.getStopLoose()).isEqualTo(new Price(1.4));
        assertThat(orderWithDate.getExpirationDate()).contains(dummyDate);
    }

    /**
     * When a {@link CloseConditions} instance should be build but a mandatory field was not set, the
     * {@link MutableCloseConditions} instance should fail.
     */
    @Test
    public void shouldFailWhenAMandatoryFieldWasNotSet() {
        int exceptionsCaught = 0;
        try {
            new MutableCloseConditions().setTakeProfit(new Price(1.0)).toImmutable();
        } catch (final IllegalStateException e) {
            exceptionsCaught++;
        }
        try {
            new MutableCloseConditions().setStopLoose(new Price(1.0)).toImmutable();
        } catch (final IllegalStateException e) {
            exceptionsCaught++;
        }

        assertThat(exceptionsCaught).as("Expected the immutable conversion to fail 2 times but it failed %d times.",
                exceptionsCaught).isEqualTo(2);
    }

    /**
     * The copy constructor should copy the values of an original {@link CloseConditions} for the new instance.
     */
    @Test
    public void copyConstructorShoudCopyValuesFromOtherCloseConditionsCorrectly() {
        final CloseConditions orig1 = new CloseConditions(new Price(1.0), new Price(2.0));
        final CloseConditions orig2 = new CloseConditions(new Price(3.0), new Price(4.0), Instant.ofEpochSecond(50));

        final MutableCloseConditions copy1 = new MutableCloseConditions(orig1);
        final MutableCloseConditions copy2 = new MutableCloseConditions(orig2);

        assertThat(copy1.getTakeProfit()).isEqualTo(new Price(1.0));
        assertThat(copy1.getStopLoose()).isEqualTo(new Price(2.0));
        assertThat(copy1.getExpirationDate()).isEmpty();

        assertThat(copy2.getTakeProfit()).isEqualTo(new Price(3.0));
        assertThat(copy2.getStopLoose()).isEqualTo(new Price(4.0));
        assertThat(copy2.getExpirationDate()).isPresent().contains(Instant.ofEpochSecond(50));
    }
}
