package de.voidnode.trading4j.domain.orders;

import java.time.Instant;
import java.util.Optional;

import de.voidnode.trading4j.domain.monetary.Price;

/**
 * Conditions that close an order automatically when they have been met.
 * 
 * @author Raik Bieniek
 */
public class CloseConditions {
    private final Price takeProfit;
    private final Price stopLoose;
    private final Optional<Instant> expirationDate;

    /**
     * Initializes an instance with an expiration date.
     * 
     * @param takeProfit
     *            see {@link #getTakeProfit()}
     * @param stopLoose
     *            see {@link #getStopLoose()}
     * @param expirationDate
     *            see {@link #expirationDate}
     */
    public CloseConditions(final Price takeProfit, final Price stopLoose, final Instant expirationDate) {
        this.takeProfit = takeProfit;
        this.stopLoose = stopLoose;
        this.expirationDate = Optional.of(expirationDate);
    }

    /**
     * Initializes an instance without an expiration date.
     * 
     * @param takeProfit
     *            see {@link #getTakeProfit()}
     * @param stopLoose
     *            see {@link #getStopLoose()}
     */
    public CloseConditions(final Price takeProfit, final Price stopLoose) {
        this.takeProfit = takeProfit;
        this.stopLoose = stopLoose;
        this.expirationDate = Optional.empty();
    }

    /**
     * The market price at which an active order should be automatically closed when the market price develops into the
     * positive direction.
     * 
     * <p>
     * This is interpreted as the ask or the bid price depending of {@link BasicPendingOrder#getType()}.
     * </p>
     * 
     * @return The stop loose limit.
     */
    public Price getTakeProfit() {
        return takeProfit;
    }

    /**
     * The market price at which an active order should be automatically closed when the market price develops into the
     * negative direction.
     * 
     * <p>
     * This is interpreted as the ask or the bid price depending of {@link BasicPendingOrder#getType()}.
     * </p>
     * 
     * @return The stop loose limit.
     */
    public Price getStopLoose() {
        return stopLoose;
    }

    /**
     * The date at which a pending order that was not activated yet should automatically be canceled.
     * 
     * <p>
     * If no such date is set, the order will not be canceled automatically based on a given time.
     * </p>
     * 
     * @return The expiration date or an {@link Optional#empty()} if no expiration date is set.
     */
    public Optional<Instant> getExpirationDate() {
        return expirationDate;
    }

    // CHECKSTYLE:OFF eclipse generated
    @Override
    public boolean equals(final Object obj) {
        // CHECKSTYLE:ON
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CloseConditions other = (CloseConditions) obj;
        if (expirationDate == null) {
            if (other.expirationDate != null) {
                return false;
            }
        } else if (!expirationDate.equals(other.expirationDate)) {
            return false;
        }
        if (stopLoose == null) {
            if (other.stopLoose != null) {
                return false;
            }
        } else if (!stopLoose.equals(other.stopLoose)) {
            return false;
        }
        if (takeProfit == null) {
            if (other.takeProfit != null) {
                return false;
            }
        } else if (!takeProfit.equals(other.takeProfit)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expirationDate == null) ? 0 : expirationDate.hashCode());
        result = prime * result + ((stopLoose == null) ? 0 : stopLoose.hashCode());
        result = prime * result + ((takeProfit == null) ? 0 : takeProfit.hashCode());
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        builder.append("CloseConditions [take profit=");
        builder.append(takeProfit);

        builder.append(", stop loose=");
        builder.append(stopLoose);

        expirationDate.ifPresent((e) -> {
            builder.append(", expirationDate=");
            builder.append(e);
        });

        builder.append("]");
        return builder.toString();
    }
}
