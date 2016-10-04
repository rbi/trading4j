package de.voidnode.trading4j.domain.orders;

import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * An order to trade a certain amount of one currency for another.
 *
 * <p>
 * A pending order consists of
 * </p>
 * <ul>
 * <li>the amount of a currency to buy or sell ({@link #getVolume()})</li>
 * <li>the execution condition and the type of the order ({@link #getType()}, {@link #getExecutionCondition()},
 * {@link #getEntryPrice()})</li>
 * <li>the condition at which the order should be closed ({@link #getCloseConditions()})</li>
 * </ul>
 * 
 * @author Raik Bieniek
 */
public class PendingOrder extends BasicPendingOrder {

    private final Volume volume;

    /**
     * Initializes the pending order.
     * 
     * @param volume
     *            see {@link #getVolume()}
     * @param type
     *            see {@link #getType()}
     * @param executionCondition
     *            see {@link #getExecutionCondition()}
     * @param entryPrice
     *            see {@link #getEntryPrice()}
     * @param closeConditions
     *            see {@link #getCloseConditions()}
     */
    public PendingOrder(final Volume volume, final OrderType type, final ExecutionCondition executionCondition,
            final Price entryPrice, final CloseConditions closeConditions) {
        super(type, executionCondition, entryPrice, closeConditions);
        this.volume = volume;
    }

    /**
     * The amount of a currency to buy or sell.
     * 
     * @return The volume
     */
    public Volume getVolume() {
        return volume;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((volume == null) ? 0 : volume.hashCode());
        return result;
    }

    /**
     * {@link PendingOrder}s are only equal to other {@link PendingOrder}s or sub-classes thats fields are equal.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof PendingOrder)) {
            return false;
        }
        final PendingOrder other = (PendingOrder) obj;
        if (volume == null) {
            if (other.volume != null) {
                return false;
            }
        } else if (!volume.equals(other.volume)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PendingOrder [volume=" + volume + ", getType()=" + getType() + ", getExecutionCondition()="
                + getExecutionCondition() + ", getEntryPrice()=" + getEntryPrice() + ", getCloseConditions()="
                + getCloseConditions() + "]";
    }
}
