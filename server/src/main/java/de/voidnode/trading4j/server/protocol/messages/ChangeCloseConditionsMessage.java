package de.voidnode.trading4j.server.protocol.messages;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.domain.orders.CloseConditions;
import de.voidnode.trading4j.domain.orders.PendingOrder;

/**
 * Request the remote {@link Broker} to change the {@link CloseConditions} of an opened pending order.
 * 
 * @author Raik Bieniek;
 */
public class ChangeCloseConditionsMessage implements Message {

    private final int id;
    private final CloseConditions conditions;

    /**
     * Initializes an instance with all its values.
     * 
     * @param id
     *            see {@link #getId()}
     * @param conditions
     *            see {@link #getNewCloseConditions()}
     */
    public ChangeCloseConditionsMessage(final int id, final CloseConditions conditions) {
        this.id = id;
        this.conditions = conditions;
    }

    /**
     * The id of the {@link PendingOrder} thats {@link CloseConditions} should be changed.
     * 
     * @return The id
     */
    public int getId() {
        return id;
    }

    /**
     * The new {@link CloseConditions} of the {@link PendingOrder}.
     * 
     * @return The new {@link CloseConditions}
     */
    public CloseConditions getNewCloseConditions() {
        return conditions;
    }

}
