package de.voidnode.trading4j.domain.orders;

import java.time.Instant;
import java.util.Optional;

import de.voidnode.trading4j.api.UnrecoverableProgrammingError;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * A mutable version of {@link CloseConditions} which can be used as builder for {@link CloseConditions}.
 * 
 * @author Raik Bieniek
 */
public class MutableCloseConditions {

    private Price takeProfit;
    private Price stopLoose;
    private Instant expirationDate;

    /**
     * Creates an instance with empty values.
     */
    public MutableCloseConditions() {

    }

    /**
     * A copy-constructor that copies the values for the new instance from an existing order.
     * 
     * @param closeConditions
     *            The {@link CloseConditions} to copy the values from.
     */
    public MutableCloseConditions(final CloseConditions closeConditions) {
        this.takeProfit = closeConditions.getTakeProfit();
        this.stopLoose = closeConditions.getStopLoose();
        closeConditions.getExpirationDate().ifPresent(date -> expirationDate = date);
    }

    /**
     * See {@link CloseConditions#getTakeProfit()}.
     * 
     * @return see {@link CloseConditions#getTakeProfit()}
     * @see CloseConditions#getTakeProfit()
     */
    public Price getTakeProfit() {
        return takeProfit;
    }

    /**
     * @param takeProfit
     *            the new take profit value
     * @return this builder for fluent a API.
     * @see CloseConditions#getTakeProfit()
     */
    public MutableCloseConditions setTakeProfit(final Price takeProfit) {
        this.takeProfit = takeProfit;
        return this;
    }

    /**
     * See {@link CloseConditions#getStopLoose()}.
     * 
     * @return see {@link CloseConditions#getStopLoose()}
     * @see CloseConditions#getStopLoose()
     */
    public Price getStopLoose() {
        return stopLoose;
    }

    /**
     * @param stopLoose
     *            the new stop loose value.
     * @return see {@link CloseConditions#getStopLoose()}
     * @see CloseConditions#getStopLoose()
     */
    public MutableCloseConditions setStopLoose(final Price stopLoose) {
        this.stopLoose = stopLoose;
        return this;
    }

    /**
     * See {@link CloseConditions#getExpirationDate()}.
     * 
     * @return see {@link CloseConditions#getExpirationDate()}
     * @see CloseConditions#getExpirationDate()
     */
    public Optional<Instant> getExpirationDate() {
        return Optional.ofNullable(expirationDate);
    }

    /**
     * @param expirationDate
     *            the new expiration date or <code>null</code> to unset it.
     * @return see {@link CloseConditions#getExpirationDate()}
     * @see CloseConditions#getExpirationDate()
     */
    public MutableCloseConditions setExpirationDate(final Instant expirationDate) {
        this.expirationDate = expirationDate;
        return this;
    }

    /**
     * Constructs an immutable {@link CloseConditions} instance with the values of this instance.
     * 
     * <p>
     * The required values takeProfit and stopLoose must be set before calling this method or else it will fail with an
     * exception. Setting an expiration date is optional. If it was not set an immutable {@link CloseConditions} without
     * an expiration date will be build.
     * </p>
     * 
     * @return The build {@link CloseConditions}.
     * @throws UnrecoverableProgrammingError
     *             When not all required values where set.
     */
    public CloseConditions toImmutable() throws UnrecoverableProgrammingError {
        if (takeProfit == null) {
            failWithMissing("takeProfit");
        }
        if (stopLoose == null) {
            failWithMissing("stopLoose");
        }
        if (expirationDate == null) {
            return new CloseConditions(takeProfit, stopLoose);
        } else {
            return new CloseConditions(takeProfit, stopLoose, expirationDate);
        }
    }

    private void failWithMissing(final String missingField) {
        throw new UnrecoverableProgrammingError("Failed to create a close conditions instance as the required field "
                + missingField + " was not passed to this builder.");
    }
}
