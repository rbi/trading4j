package de.voidnode.trading4j.domain.trades;

import java.time.Instant;
import java.util.Optional;

import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.CloseConditions;

/**
 * Describes a single event in a trade.
 * 
 * @author Raik Bieniek
 */
public class TradeEvent {

    private final TradeEventType type;
    private final Instant time;
    private final String reason;
    private final Optional<Price> price;
    private final Optional<CloseConditions> closeConditions;

    /**
     * Creates a new event with a price and close conditions.
     * 
     * @param type
     *            see {@link #getType()}
     * @param time
     *            see {@link #getTime()}
     * @param reason
     *            see {@link #getReason()}
     * @param price
     *            see {@link #getPrice()}
     * @param closeConditions
     *            see {@link #getCloseConditions()}
     */
    public TradeEvent(final TradeEventType type, final Instant time, final String reason, final Price price,
            final CloseConditions closeConditions) {
        this.type = type;
        this.time = time;
        this.reason = reason;
        this.price = Optional.ofNullable(price);
        this.closeConditions = Optional.ofNullable(closeConditions);
    }

    /**
     * Creates a new event with a price and close conditions.
     * 
     * @param type
     *            see {@link #getType()}
     * @param time
     *            see {@link #getTime()}
     * @param reason
     *            see {@link #getReason()}
     * @param price
     *            see {@link #getPrice()}
     * @param takeProfit
     *            see {@link #getCloseConditions()}
     * @param stopLoose
     *            see {@link #getCloseConditions()}
     */
    public TradeEvent(final TradeEventType type, final Instant time, final String reason, final Price price,
            final Price takeProfit, final Price stopLoose) {
        this(type, time, reason, price, new CloseConditions(takeProfit, stopLoose));
    }

    /**
     * Creates a new event with a price but without close conditions.
     * 
     * @param type
     *            see {@link #getType()}
     * @param time
     *            see {@link #getTime()}
     * @param reason
     *            see {@link #getReason()}
     * @param price
     *            see {@link #getPrice()}
     */
    public TradeEvent(final TradeEventType type, final Instant time, final String reason, final Price price) {
        this(type, time, reason, price, (CloseConditions) null);
    }

    /**
     * Creates a new event with a price but without and close conditions.
     * 
     * @param type
     *            see {@link #getType()}
     * @param time
     *            see {@link #getTime()}
     * @param reason
     *            see {@link #getReason()}
     * @param closeConditions
     *            see {@link #getCloseConditions()}
     */
    public TradeEvent(final TradeEventType type, final Instant time, final String reason,
            final CloseConditions closeConditions) {
        this(type, time, reason, null, closeConditions);
    }

    /**
     * Creates a new event with a price but without and close conditions.
     * 
     * @param type
     *            see {@link #getType()}
     * @param time
     *            see {@link #getTime()}
     * @param reason
     *            see {@link #getReason()}
     * @param takeProfit
     *            see {@link #getCloseConditions()}
     * @param stopLoose
     *            see {@link #getCloseConditions()}
     */
    public TradeEvent(final TradeEventType type, final Instant time, final String reason, final Price takeProfit,
            final Price stopLoose) {
        this(type, time, reason, null, new CloseConditions(takeProfit, stopLoose));
    }

    /**
     * Creates an event without a price and without and close conditions.
     * 
     * @param type
     *            see {@link #getType()}
     * @param time
     *            see {@link #getTime()}
     * @param reason
     *            see {@link #getReason()}
     */
    public TradeEvent(final TradeEventType type, final Instant time, final String reason) {
        this(type, time, reason, null, (CloseConditions) null);
    }

    /**
     * The type of this event.
     * 
     * @return The type
     */
    public TradeEventType getType() {
        return type;
    }

    /**
     * The time at which this event happened.
     * 
     * @return The time
     */
    public Instant getTime() {
        return time;
    }

    /**
     * A human readable phrase describing why this event happened.
     * 
     * @return The reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * The market {@link Price} of the asset that was active for this event.
     * 
     * <p>
     * The meaning of this value depends on the {@link #getType()} of this event.
     * </p>
     * <ul>
     * <li>If type is {@link TradeEventType#PENDING_ORDER_PLACED} than this is the entry price.</li>
     * <li>If type is {@link TradeEventType#PENDING_ORDER_OPENED} than this is the price at which the order was opened.
     * </li>
     * <li>If type is {@link TradeEventType#TRADE_CLOSED} than this is the price at which the order was closed.</li>
     * <li>For other types this will be {@link Optional#empty()}.
     * </ul>
     * 
     * @return The assets {@link Price} if it is relevant for this event or an empty {@link Optional} if not.
     */
    public Optional<Price> getPrice() {
        return price;
    }

    /**
     * The new {@link CloseConditions} of the pending order or trade that was active after this event.
     * 
     * @return The new {@link CloseConditions} if this event changed them and an empty {@link Optional} if it didn't.
     */
    public Optional<CloseConditions> getCloseConditions() {
        return closeConditions;
    }

    /**
     * A {@link TradeEvent} is only equal to other {@link TradeEvent} that have exactly the same values.
     */
    // CHECKSTYLE:OFF generated code
    @Override
    public boolean equals(final Object obj) {
        // CHECKSTYLE:ON
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TradeEvent)) {
            return false;
        }
        final TradeEvent other = (TradeEvent) obj;
        if (closeConditions == null) {
            if (other.closeConditions != null) {
                return false;
            }
        } else if (!closeConditions.equals(other.closeConditions)) {
            return false;
        }
        if (price == null) {
            if (other.price != null) {
                return false;
            }
        } else if (!price.equals(other.price)) {
            return false;
        }
        if (reason == null) {
            if (other.reason != null) {
                return false;
            }
        } else if (!reason.equals(other.reason)) {
            return false;
        }
        if (time == null) {
            if (other.time != null) {
                return false;
            }
        } else if (!time.equals(other.time)) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((closeConditions == null) ? 0 : closeConditions.hashCode());
        result = prime * result + ((price == null) ? 0 : price.hashCode());
        result = prime * result + ((reason == null) ? 0 : reason.hashCode());
        result = prime * result + ((time == null) ? 0 : time.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "TradeEvent [type=" + type + ", time=" + time + ", reason=" + reason + ", price=" + price
                + ", close conditions=" + closeConditions + "]";
    }
}
