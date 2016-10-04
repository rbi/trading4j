package de.voidnode.trading4j.testutils.assertions;

import de.voidnode.trading4j.api.Either;

import org.assertj.core.api.AbstractAssert;

/**
 * Assertions for {@link Either}s.
 *
 * @author Raik Bieniek
 * @param <LEFT>
 *            The type for the {@link Either#getLeft()} value.
 * @param <RIGHT>
 *            The type for the {@link Either#getRight()} value.
 */
public class EitherAssert<LEFT, RIGHT> extends AbstractAssert<EitherAssert<LEFT, RIGHT>, Either<LEFT, RIGHT>> {

    /**
     * Initializes the assertions.
     * 
     * @param actual
     *            The instance on which assertions should be made.
     */
    EitherAssert(final Either<LEFT, RIGHT> actual) {
        super(actual, EitherAssert.class);
    }

    /**
     * Expect actual to have the left value.
     */
    public void hasLeft() {
        isNotNull();
        if (!actual.hasLeft()) {
            failWithMessage("Expected an Either instance to have a left value but it had the right value. "
                    + "actual was: \"%s\"", actual);
        }
    }

    /**
     * Asserts that the left value is present at that it is equal to the given value.
     * 
     * @param left
     *            The value that the actual left value should be equal to.
     */
    public void hasLeftEqualTo(final LEFT left) {
        hasLeft();
        if (!actual.getLeft().equals(left)) {
            failWithMessage("Expected an Either instance to have a specific left value but it had another value. "
                    + "expected left value: \"%s\", actual: \"%s\"", left, actual);
        }
    }

    /**
     * Expect actual to have the right value.
     */
    public void hasRight() {
        if (!actual.hasRight()) {
            failWithMessage("Expected an Either instance to have a right value but it had the left value. "
                    + "actual was: \"%s\"", actual);
        }
    }

    /**
     * Asserts that the right value is present at that it is equal to the given value.
     * 
     * @param right
     *            The value that the actual right value should be equal to.
     */
    public void hasRightEqualTo(final RIGHT right) {
        hasRight();
        if (!actual.getRight().equals(right)) {
            failWithMessage("Expected an Either instance to have a specific right value but it had another value. "
                    + "expected right value: \"%s\", actual: \"%s\"", right, actual);
        }
    }
}
