package de.voidnode.trading4j.api;

import java.util.NoSuchElementException;
import java.util.Optional;

import static de.voidnode.trading4j.testutils.assertions.Assertions.assertThat;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link Either} works as expected.
 * 
 * @author Raik Bieniek
 */
public class EitherTest {

    /**
     * The left value is returned when the left value is present.
     */
    @Test
    public void returnsLeftWhenLeftIsPresent() {
        assertThat(Either.withLeft("some left").getLeft()).isEqualTo("some left");
    }

    /**
     * The right value is returned when the right value is present.
     */
    @Test
    public void returnsRightWhenRightIsPresent() {
        assertThat(Either.withRight("some right").getRight()).isEqualTo("some right");
    }

    /**
     * When right is queried while left is present an exception is thrown.
     */
    @Test(expected = NoSuchElementException.class)
    public void queriingForRightWhenLeftIsPresentThrowsAnException() {
        Either.withLeft("some left").getRight();
    }

    /**
     * When left is queried while right is present an exception is thrown.
     */
    @Test(expected = NoSuchElementException.class)
    public void queriingForLeftWhenRightIsPresentThrowsAnException() {
        Either.withRight("some left").getLeft();
    }

    /**
     * Can return an {@link Optional} with the left value.
     */
    @Test
    public void optionalyReturnsLeft() {
        assertThat(Either.withLeft("some left").getLeftOptional()).contains("some left");
        assertThat(Either.withLeft("some left").getRightOptional()).isEmpty();
    }

    /**
     * Can return an {@link Optional} with the right value.
     */
    @Test
    public void optionalyReturnsRight() {
        assertThat(Either.withRight("some right").getLeftOptional()).isEmpty();
        assertThat(Either.withRight("some right").getRightOptional()).contains("some right");
    }

    /**
     * The left value can be mapped to a new value.
     */
    @Test
    public void mappingOfTheLeftValueIsPossible() {
        final Either<String, Long> withLeft = Either.withLeft("orig left");
        final Either<String, Long> withRight = Either.withRight(57L);

        assertThat(withLeft.mapLeft((value) -> value.length()).getLeft()).isEqualTo(9);
        assertThat(withRight.mapLeft((value) -> value.length()).getRight()).isEqualTo(57L);
    }

    /**
     * The right value can be mapped to a new value.
     */
    @Test
    public void mappingOfTheRightValueIsPossible() {
        final Either<Long, String> withLeft = Either.withLeft(57L);
        final Either<Long, String> withRight = Either.withRight("orig right");

        assertThat(withLeft.mapRight((value) -> value.length()).getLeft()).isEqualTo(57L);
        assertThat(withRight.mapRight((value) -> value.length()).getRight()).isEqualTo(10);
    }

    /**
     * The cut can be checked for having the left or right value present.
     */
    @Test
    public void canBeCheckedForHavingLeftOrRightPresent() {
        final Either<Integer, String> withLeft = Either.withLeft(42);
        final Either<Integer, String> withRight = Either.withRight("hello world");

        assertThat(withLeft.hasLeft()).isTrue();
        assertThat(withLeft.hasRight()).isFalse();

        assertThat(withRight.hasLeft()).isFalse();
        assertThat(withRight.hasRight()).isTrue();
    }

    /**
     * An {@link Either} equals other {@link Either} that have an equal value at the same side.
     */
    @Test
    public void equalsOtherEithersWithEqualValueOnSameSide() {
        assertThat(Either.withLeft("some value")).isEqualTo(Either.withLeft("some value"));
        assertThat(Either.withRight("some value")).isEqualTo(Either.withRight("some value"));

        assertThat(Either.withLeft("some value")).isNotEqualTo(Either.withRight("some value"));
        assertThat(Either.withRight("some value")).isNotEqualTo(Either.withLeft("some value"));
        assertThat(Either.withLeft("some value")).isNotEqualTo(Either.withRight(42));
        assertThat(Either.withRight("some value")).isNotEqualTo(Either.withRight(null));

    }
}
