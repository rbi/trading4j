package de.voidnode.trading4j.api;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

/**
 * Contains exactly one of two possible types of values.
 * 
 * <p>
 * If this is used to indicate either success or failure, it is convention to use {@link #getLeft()} for the failure
 * case and {@link #getRight()} for the success case.
 * </p>
 *
 * @author Raik Bieniek
 * @param <LEFT>
 *            The type for the {@link #getLeft()} value.
 * @param <RIGHT>
 *            The type for the {@link #getRight()} value.
 */
public final class Either<LEFT, RIGHT> {

    private final LEFT left;
    private final RIGHT right;

    /**
     * Constructs an instance where the left value is present.
     * 
     * @param left
     *            The left value.
     */
    private Either(final LEFT left) {
        this.left = left;
        this.right = null;
    }

    /**
     * Constructs an instance where the right value is present.
     * 
     * @param right
     *            The right value.
     * @param dummy
     *            Just a dummy value to get another constructor signature.
     */
    private Either(final RIGHT right, final boolean dummy) {
        this.left = null;
        this.right = right;
    }

    /**
     * Checks if the left value is present.
     * 
     * @return <code>true</code> if it is present and <code>false</code> if the right value is present instead.
     */
    public boolean hasLeft() {
        return left != null;
    }

    /**
     * Assumes that the left value is present and returns it.
     * 
     * @return The left value.
     * @throws NoSuchElementException
     *             If the left value isn't present because the right is.
     */
    public LEFT getLeft() throws NoSuchElementException {
        if (!hasLeft()) {
            throw new NoSuchElementException(
                    "Either was queried for the left element but the right element was present.");
        }
        return left;
    }

    /**
     * The left value if it is present.
     * 
     * @return The left value if it is present and an empty {@link Optional} if not.
     */
    public Optional<LEFT> getLeftOptional() {
        return Optional.ofNullable(left);
    }

    /**
     * Converts the left side to a new value.
     * 
     * @param mapper
     *            The mapping method.
     * @param <NEWLEFT>
     *            The new type for the left side.
     * @return A new instance with the converted right side.
     */
    @SuppressWarnings("unchecked")
    public <NEWLEFT> Either<NEWLEFT, RIGHT> mapLeft(final Function<LEFT, NEWLEFT> mapper) {
        if (hasRight()) {
            return (Either<NEWLEFT, RIGHT>) this;
        }
        return Either.withLeft(mapper.apply(left));
    }

    /**
     * Checks if the right value is present.
     * 
     * @return <code>true</code> if it is present and <code>false</code> if the left value is present instead.
     */
    public boolean hasRight() {
        return right != null;
    }

    /**
     * Assumes that the right value is present and returns it.
     * 
     * @return The right value.
     * @throws NoSuchElementException
     *             If the right value isn't present because the left is.
     */
    public RIGHT getRight() throws NoSuchElementException {
        if (!hasRight()) {
            throw new NoSuchElementException(
                    "Either was queried for the right element but the left element was present.");
        }
        return right;
    }

    /**
     * The right value if it is present.
     * 
     * @return The right value if it is present and an empty {@link Optional} if not.
     */
    public Optional<RIGHT> getRightOptional() {
        return Optional.ofNullable(right);
    }

    /**
     * Converts the right side to a new value.
     * 
     * @param mapper
     *            The mapping method.
     * @param <NEWRIGHT>
     *            The new type for the right side.
     * @return A new instance with the converted right side.
     */
    @SuppressWarnings("unchecked")
    public <NEWRIGHT> Either<LEFT, NEWRIGHT> mapRight(final Function<RIGHT, NEWRIGHT> mapper) {
        if (hasLeft()) {
            return (Either<LEFT, NEWRIGHT>) this;
        }
        return Either.withRight(mapper.apply(right));
    }

    @Override
    public String toString() {
        return hasLeft() ? "Either [with left = " + left + "]" : "Either [with right = " + right + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((left == null) ? 0 : left.hashCode());
        result = prime * result + ((right == null) ? 0 : right.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Either<?, ?> other = (Either<?, ?>) obj;
        if (left == null) {
            if (other.left != null) {
                return false;
            }
        } else if (!left.equals(other.left)) {
            return false;
        }
        if (right == null) {
            if (other.right != null) {
                return false;
            }
        } else if (!right.equals(other.right)) {
            return false;
        }
        return true;
    }

    /**
     * Constructs an instance where the left value is present.
     * 
     * @param left
     *            The left value
     * @param <LEFT>
     *            The type for the {@link #getLeft()} value.
     * @param <RIGHT>
     *            The type for the {@link #getRight()} value.
     * @return The constructed instance.
     */
    public static <LEFT, RIGHT> Either<LEFT, RIGHT> withLeft(final LEFT left) {
        return new Either<LEFT, RIGHT>(left);
    }

    /**
     * Constructs an instance where the right value is present.
     * 
     * @param right
     *            The right value
     * @param <LEFT>
     *            The type for the {@link #getLeft()} value.
     * @param <RIGHT>
     *            The type for the {@link #getRight()} value.
     * @return The constructed instance.
     */
    public static <LEFT, RIGHT> Either<LEFT, RIGHT> withRight(final RIGHT right) {
        return new Either<LEFT, RIGHT>(right, true);
    }
}
