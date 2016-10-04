package de.voidnode.trading4j.domain.environment;

import de.voidnode.trading4j.domain.VolumeUnit;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * Information about special kinds of fees that the broker charges besides the spread.
 * 
 * @author Raik Bieniek
 */
public class SpecialFeesInformation {

    private final Price markup;
    private final Price commission;

    /**
     * Initializes a new instance with constant data.
     * 
     * @param markup
     *            see {@link #getMarkup()}
     * @param commission
     *            see {@link #getCommission()}
     */
    public SpecialFeesInformation(final Price markup, final Price commission) {
        this.markup = markup;
        this.commission = commission;
    }

    /**
     * The markup fee the broker charges.
     * 
     * <p>
     * If this is 0 than the broker doesn't charge any markup fee.
     * </p>
     * 
     * @return The markup fee
     */
    public Price getMarkup() {
        return this.markup;
    }

    /**
     * The overall commission the broker charges per {@link VolumeUnit#BASE} that is used in a trade.
     * 
     * <p>
     * This value must be the combined commission for opening and closing a trade.
     * </p>
     * 
     * @return The commission
     */
    public Price getCommission() {
        return commission;
    }

    @Override
    public String toString() {
        return "SpecialFeesInformation [markup=" + markup + ", commission=" + commission + "]";
    }
}
