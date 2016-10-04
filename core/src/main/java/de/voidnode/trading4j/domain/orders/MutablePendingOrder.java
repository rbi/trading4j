package de.voidnode.trading4j.domain.orders;

import de.voidnode.trading4j.api.UnrecoverableProgrammingError;
import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.VolumeUnit;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * A mutable version of a {@link PendingOrder}.
 * 
 * @author Raik Bieniek
 */
public class MutablePendingOrder {

    private Volume volume;
    private OrderType type;
    private ExecutionCondition executionCondition;
    private Price entryPrice;
    private CloseConditions closeConditions;

    /**
     * Creates an instance with empty values.
     */
    public MutablePendingOrder() {

    }

    /**
     * A copy-constructor that copies the values for the new instance from an existing order.
     * 
     * @param order
     *            The order to copy the values from.
     */
    public MutablePendingOrder(final PendingOrder order) {
        this.volume = order.getVolume();
        this.type = order.getType();
        this.executionCondition = order.getExecutionCondition();
        this.entryPrice = order.getEntryPrice();
        this.closeConditions = order.getCloseConditions();
    }

    /**
     * A copy-constructor that copies the values of a {@link BasicPendingOrder} for the new instance from an existing
     * order.
     * 
     * @param order
     *            The order to copy the values from.
     */
    public MutablePendingOrder(final BasicPendingOrder order) {
        this.type = order.getType();
        this.executionCondition = order.getExecutionCondition();
        this.entryPrice = order.getEntryPrice();
        this.closeConditions = order.getCloseConditions();
    }

    /**
     * See {@link PendingOrder#getVolume()}.
     * 
     * @return see {@link PendingOrder#getVolume()}
     * @see PendingOrder#getVolume()
     */
    public Volume getVolume() {
        return volume;
    }

    /**
     * @param volume
     *            the new volume
     * @return this builder for fluent a API.
     * @see PendingOrder#getVolume()
     */
    public MutablePendingOrder setVolume(final Volume volume) {
        this.volume = volume;
        return this;
    }

    /**
     * A shortcut to set the volume by its raw values.
     * 
     * @param amount
     *            The raw amount for the volume.
     * @param unit
     *            The {@link VolumeUnit} as which the <code>amount</code> is to be interpreted.
     * @return this builder for fluent a API.
     * @see Volume#Volume(long, VolumeUnit)
     * @see PendingOrder#getVolume()
     */
    public MutablePendingOrder setVolume(final int amount, final VolumeUnit unit) {
        this.volume = new Volume(amount, unit);
        return this;
    }

    /**
     * See {@link BasicPendingOrder#getType()}.
     * 
     * @return see {@link BasicPendingOrder#getType()}
     * @see BasicPendingOrder#getType()
     */
    public OrderType getType() {
        return type;
    }

    /**
     * @param type
     *            the new type
     * @return this builder for fluent a API.
     * @see BasicPendingOrder#getType()
     */
    public MutablePendingOrder setType(final OrderType type) {
        this.type = type;
        return this;
    }

    /**
     * See {@link BasicPendingOrder#getExecutionCondition()}.
     * 
     * @return see {@link BasicPendingOrder#getExecutionCondition()}
     * @see BasicPendingOrder#getExecutionCondition()
     */
    public ExecutionCondition getExecutionCondition() {
        return executionCondition;
    }

    /**
     * @param executionCondition
     *            the new execution condition
     * @return this builder for fluent a API.
     * @see BasicPendingOrder#getExecutionCondition()
     */
    public MutablePendingOrder setExecutionCondition(final ExecutionCondition executionCondition) {
        this.executionCondition = executionCondition;
        return this;
    }

    /**
     * See {@link BasicPendingOrder#getCloseConditions()}.
     * 
     * @return see {@link BasicPendingOrder#getCloseConditions()}
     * @see BasicPendingOrder#getCloseConditions()
     */
    public CloseConditions getCloseConditions() {
        return closeConditions;
    }

    /**
     * A shortcut that build a {@link CloseConditions} object for this instance from a {@link MutableCloseConditions}.
     * 
     * @param conditions
     *            The new {@link CloseConditions}
     * @return this builder for fluent a API.
     * @see BasicPendingOrder#getCloseConditions()
     */
    public MutablePendingOrder setCloseConditions(final MutableCloseConditions conditions) {
        this.closeConditions = conditions.toImmutable();
        return this;
    }

    /**
     * @param conditions
     *            The new {@link CloseConditions}
     * @return this builder for fluent a API.
     * @see BasicPendingOrder#getCloseConditions()
     */
    public MutablePendingOrder setCloseConditions(final CloseConditions conditions) {
        this.closeConditions = conditions;
        return this;
    }

    /**
     * See {@link BasicPendingOrder#getEntryPrice()}.
     * 
     * @return see {@link BasicPendingOrder#getEntryPrice()}
     * @see BasicPendingOrder#getEntryPrice()
     */
    public Price getEntryPrice() {
        return entryPrice;
    }

    /**
     * @param entryPrice
     *            the new entry price
     * @return this builder for fluent a API.
     * @see BasicPendingOrder#getEntryPrice()
     */
    public MutablePendingOrder setEntryPrice(final Price entryPrice) {
        this.entryPrice = entryPrice;
        return this;
    }

    /**
     * Constructs an immutable {@link PendingOrder} with the values of this instance.
     * 
     * <p>
     * The required values volume, type, executionCondition, entryPrice, takeProfit and stopLoose must be set before
     * calling this method or else it will fail with an exception.
     * </p>
     * 
     * @return The build {@link PendingOrder}.
     * @throws UnrecoverableProgrammingError
     *             When not all required values where set.
     */
    public PendingOrder toImmutablePendingOrder() throws UnrecoverableProgrammingError {
        if (volume == null) {
            failWithMissing("volume");
        }
        if (type == null) {
            failWithMissing("type");
        }
        if (executionCondition == null) {
            failWithMissing("executionCondition");
        }
        if (entryPrice == null) {
            failWithMissing("entryPrice");
        }
        if (closeConditions == null) {
            failWithMissing("closeConditions");
        }
        return new PendingOrder(volume, type, executionCondition, entryPrice, closeConditions);
    }

    /**
     * Constructs an immutable {@link BasicPendingOrder} with the values of this instance.
     * 
     * <p>
     * The required values type, executionCondition, entryPrice, takeProfit and stopLoose must be set before calling
     * this method or else it will fail with an exception.
     * </p>
     * 
     * @return The build {@link BasicPendingOrder}.
     * @throws UnrecoverableProgrammingError
     *             When not all required values where set.
     */
    public BasicPendingOrder toImmutableBasicPendingOrder() {
        if (type == null) {
            failWithMissing("type");
        }
        if (executionCondition == null) {
            failWithMissing("executionCondition");
        }
        if (entryPrice == null) {
            failWithMissing("entryPrice");
        }
        if (closeConditions == null) {
            failWithMissing("closeConditions");
        }
        return new BasicPendingOrder(type, executionCondition, entryPrice, closeConditions);
    }

    private void failWithMissing(final String missingField) {
        throw new UnrecoverableProgrammingError("Failed to create a pending order as the required field " + missingField
                + " was not passed to this builder.");
    }
}
