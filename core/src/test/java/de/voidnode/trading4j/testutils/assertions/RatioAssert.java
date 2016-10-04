package de.voidnode.trading4j.testutils.assertions;

import de.voidnode.trading4j.domain.Ratio;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.data.Offset;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Assertions for {@link Ratio}s.
 * 
 * @author Raik Bieniek
 */
public class RatioAssert extends AbstractAssert<RatioAssert, Ratio> {

    /**
     * Initializes the assertions with the object to do assertions on.
     * 
     * @param actual
     *            The object that on which assertions should be done.
     */
    protected RatioAssert(final Ratio actual) {
        super(actual, RatioAssert.class);
    }

    /**
     * Expect <code>actual</code> to be approximately equal to another ratio within a given range.
     * 
     * @param ratio
     *            The value that <code>actual</code> should be approximately equal to.
     * @param allowedOffset
     *            The difference between <code>actual</code> and <code>ratio</code> that is allowed.
     * @return <code>this</code> for assertion chaining
     */
    public RatioAssert isApproximatelyEqualTo(final Ratio ratio, final Offset<Double> allowedOffset) {
        isNotNull();

        assertThat(actual.asBasic())
                .overridingErrorMessage(
                        "Expected the ratio %s to be approximately equal to the ratio %s within an offset of %s but they weren't. ",
                        actual, ratio, allowedOffset).isEqualTo(ratio.asBasic(), allowedOffset);
        return this;
    }

}
