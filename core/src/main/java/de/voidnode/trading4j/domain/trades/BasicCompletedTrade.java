package de.voidnode.trading4j.domain.trades;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import de.voidnode.trading4j.domain.Ratio;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.ExecutionCondition;
import de.voidnode.trading4j.domain.orders.OrderType;

import static de.voidnode.trading4j.domain.orders.OrderType.BUY;
import static de.voidnode.trading4j.domain.trades.TradeEventType.PENDING_ORDER_OPENED;
import static de.voidnode.trading4j.domain.trades.TradeEventType.TRADE_CLOSED;

/**
 * Describes a complete trade cycle of an asset from the market entry to the market exit.
 * 
 * @author Raik Bieniek
 */
public class BasicCompletedTrade {

    private final OrderType type;
    private final ExecutionCondition executionCondition;
    private final List<TradeEvent> events;

    /**
     * Initializes an instance with all its data.
     *
     * @param type
     *            see {@link #getType()}
     * @param executionCondition
     *            see {@link #getExecutionCondition()}
     * @param events
     *            see {@link #getEvents()}, a copy of the list will be created so that it is safe to change the list
     *            passed here afterwards.
     */
    public BasicCompletedTrade(final OrderType type, final ExecutionCondition executionCondition,
            final List<TradeEvent> events) {
        this.type = type;
        this.executionCondition = executionCondition;
        final List<TradeEvent> copy = new ArrayList<>(events.size());
        for (final TradeEvent event : events) {
            copy.add(event);
        }
        this.events = Collections.unmodifiableList(copy);
    }

    /**
     * The type of this trade (BUY or SELL).
     * 
     * @return The type
     */
    public OrderType getType() {
        return type;
    }

    /**
     * The condition on which the pending order should be executed.
     * 
     * @return The condition
     */
    public ExecutionCondition getExecutionCondition() {
        return executionCondition;
    }

    /**
     * All events that happened during this trade.
     * 
     * @return A read only {@link List} of the trade events. Calling mutating operations on this list will result in
     *         exceptions to be thrown.
     */
    public List<TradeEvent> getEvents() {
        return events;
    }

    /**
     * Calculates the relative profit of this trade.
     * 
     * <p>
     * The relative profit is the difference between the open and the close {@link Price}. This is interpreted as win or
     * loose (positive or negative {@link Price}) depending on whether this was a BUY or SELL trade.
     * </p>
     * 
     * @return The relative profit or an empty {@link Optional} when this trade was never opened or closed.
     */
    public Optional<Price> getRelativeProfit() {
        return getExitPrice().flatMap(exit -> getEntryPrice().map(entry -> exit.minus(entry)))
                .map(diff -> type == BUY ? diff : diff.inverse());
    }

    /**
     * Calculates the rate of return for this trade.
     * 
     * @return The rate of return as relative value. A value smaller than 100% means looses and a value greater than
     *         100% means profits. If this value is empty, than the order wasn't opened and therefore no money was
     *         invested.
     */
    public Optional<Ratio> getRateOfReturn() {
        return getExitPrice().flatMap(exit -> getEntryPrice().map(entry -> exit.asDouble() / entry.asDouble())
                .map(ratio -> type == BUY ? new Ratio(ratio) : new Ratio(2.0 - ratio)));
    }

    private Optional<Price> getEntryPrice() {
        return events.stream().filter(ev -> ev.getType().equals(PENDING_ORDER_OPENED)).findFirst()
                .flatMap(ev -> ev.getPrice());
    }

    private Optional<Price> getExitPrice() {
        return events.stream().filter(ev -> ev.getType().equals(TRADE_CLOSED)).findFirst().flatMap(ev -> ev.getPrice());
    }

    /**
     * {@link BasicCompletedTrade}s are only equal to other {@link BasicCompletedTrade}s thats values are exactly the
     * same.
     * 
     * @param obj
     *            The other instance to compare this one to.
     * @return <code>true</code> If both are equal and <code>false</code> if not.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof BasicCompletedTrade)) {
            return false;
        }
        final BasicCompletedTrade other = (BasicCompletedTrade) obj;
        if (events == null) {
            if (other.events != null) {
                return false;
            }
        } else if (!events.equals(other.events)) {
            return false;
        }
        if (executionCondition != other.executionCondition) {
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
        result = prime * result + ((events == null) ? 0 : events.hashCode());
        result = prime * result + ((executionCondition == null) ? 0 : executionCondition.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "BasicCompletedTrade [type=" + type + ", execution condition=" + executionCondition + ", events="
                + events + "]";
    }
}
