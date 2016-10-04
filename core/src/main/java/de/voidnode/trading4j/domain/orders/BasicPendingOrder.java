package de.voidnode.trading4j.domain.orders;

import de.voidnode.trading4j.domain.monetary.Price;

/**
 * An abstract order that defines the bounds of the order but does not contain a concrete volume to trade.
 * 
 * @author Raik Bieniek
 */
public class BasicPendingOrder {

    private final OrderType type;
    private final ExecutionCondition executionCondition;
    private final Price entryPrice;
    private final CloseConditions closeConditions;

    /**
     * Constructs an instance with all its data.
     * 
     * @param type
     *            see {@link #getType()}
     * @param executionCondition
     *            see {@link #getExecutionCondition()}
     * @param entryPrice
     *            see {@link #getEntryPrice()}
     * @param closeConditions
     *            see {@link #getCloseConditions()}
     */
    public BasicPendingOrder(final OrderType type, final ExecutionCondition executionCondition, final Price entryPrice,
            final CloseConditions closeConditions) {
        this.type = type;
        this.executionCondition = executionCondition;
        this.entryPrice = entryPrice;
        this.closeConditions = closeConditions;
    }

    /**
     * The type of the order.
     * 
     * @return The type
     */
    public OrderType getType() {
        return type;
    }

    /**
     * The condition under which the order should be executed.
     * 
     * <p>
     * This specifies how the {@link #getEntryPrice()} should be interpreted.
     * </p>
     * 
     * @return The condition
     */
    public ExecutionCondition getExecutionCondition() {
        return executionCondition;
    }

    /**
     * The market price at which the order should be executed.
     * 
     * <p>
     * This is interpreted as the ask or the bid price depending of the {@link #getType()}.
     * </p>
     * 
     * @return The entry price
     */
    public Price getEntryPrice() {
        return entryPrice;
    }

    /**
     * The conditions at which the order should be automatically closed or canceled when met.
     * 
     * @return The close conditions
     */
    public CloseConditions getCloseConditions() {
        return closeConditions;
    }

    @Override
    public String toString() {
        return "BasicPendingOrder [type=" + type + ", executionCondition=" + executionCondition + ", entryPrice="
                + entryPrice + ", closeConditions=" + closeConditions + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((closeConditions == null) ? 0 : closeConditions.hashCode());
        result = prime * result + ((entryPrice == null) ? 0 : entryPrice.hashCode());
        result = prime * result + ((executionCondition == null) ? 0 : executionCondition.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    // CHECKSTYLE:OFF generated code
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof BasicPendingOrder)) {
            return false;
        }
        final BasicPendingOrder other = (BasicPendingOrder) obj;
        if (closeConditions == null) {
            if (other.closeConditions != null) {
                return false;
            }
        } else if (!closeConditions.equals(other.closeConditions)) {
            return false;
        }
        if (entryPrice == null) {
            if (other.entryPrice != null) {
                return false;
            }
        } else if (!entryPrice.equals(other.entryPrice)) {
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
    // CHECKSTYLE:ON
}
