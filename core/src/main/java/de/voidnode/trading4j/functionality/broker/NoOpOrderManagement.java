package de.voidnode.trading4j.functionality.broker;

import java.util.Optional;

import de.voidnode.trading4j.api.Failed;
import de.voidnode.trading4j.api.OrderManagement;
import de.voidnode.trading4j.domain.orders.CloseConditions;

/**
 * An {@link OrderManagement} that does nothing on requests.
 */
class NoOpOrderManagement implements OrderManagement {

    private static final Optional<Failed> NOT_SUPPORTED = Optional.of(new Failed("Changing close conditions"
            + " is not supported because the order has already been canceled."));

    @Override
    public void closeOrCancelOrder() {

    }

    @Override
    public Optional<Failed> changeCloseConditionsOfOrder(final CloseConditions conditions) {
        return NOT_SUPPORTED;
    }
}