package de.voidnode.trading4j.testutils.assertions;

import de.voidnode.trading4j.api.Either;
import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.Ratio;
import de.voidnode.trading4j.domain.monetary.AccuratePrice;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * Custom assertions for trading classes.
 * 
 * @author Raik Bieniek
 */
public class Assertions extends org.assertj.core.api.Assertions {

    /**
     * Assertions for {@link MarketDirection}s.
     * 
     * @param actual
     *            The trend on which the assertions should be made.
     * @return The assertions
     */
    public static TrendAssert assertThat(final MarketDirection actual) {
        return new TrendAssert(actual);
    }

    /**
     * Assertions for {@link Price}s.
     * 
     * @param actual
     *            The {@link Price} on which the assertions should be made.
     * @return The assertions
     */
    public static PriceAssert assertThat(final Price actual) {
        return new PriceAssert(actual);
    }
    
    /**
     * Assertions for {@link AccuratePrice}s.
     * 
     * @param actual
     *            The {@link AccuratePrice} on which the assertions should be made.
     * @return The assertions
     */
    public static AccuratePriceAssert assertThat(final AccuratePrice actual) {
        return new AccuratePriceAssert(actual);
    }

    /**
     * Assertions for {@link Ratio}s.
     * 
     * @param actual
     *            The {@link Ratio} on which assertions should be made.
     * @return The assertions
     */
    public static RatioAssert assertThat(final Ratio actual) {
        return new RatioAssert(actual);
    }

    /**
     * Assertions for {@link Either}s.
     * 
     * @param actual
     *            The {@link Either} on which assertions should be made.
     * @param <LEFT>
     *            The type for the {@link Either#getLeft()} value.
     * @param <RIGHT>
     *            The type for the {@link Either#getRight()} value.
     * @return The utility to do assertions
     */
    public static <LEFT, RIGHT> EitherAssert<LEFT, RIGHT> assertThat(final Either<LEFT, RIGHT> actual) {
        return new EitherAssert<>(actual);
    }
}
