package de.voidnode.trading4j.strategyexpertadvisor;

import de.voidnode.trading4j.api.OrderManagement;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;

/**
 * Contains the {@link BasicPendingOrder} with the current values of an order and the {@link OrderManagement} for
 * managing the order.
 * 
 * @author Raik Bieniek
 */
class Order {

    private final BasicPendingOrder pendingOrder;
    private final OrderManagement orderManagement;

    /**
     * Creates an instance with all required values.
     * 
     * @param pendingOrder
     *            see {@link #getPendingOrder()}.
     * @param orderManagement
     *            see {@link #getOrderManagement()}
     */
    Order(final BasicPendingOrder pendingOrder, final OrderManagement orderManagement) {
        this.pendingOrder = pendingOrder;
        this.orderManagement = orderManagement;
    }

    /**
     * The current values of the order.
     * 
     * @return The current values
     */
    public BasicPendingOrder getPendingOrder() {
        return pendingOrder;
    }

    /**
     * Used to manage the order.
     * 
     * @return The order manager.
     */
    public OrderManagement getOrderManagement() {
        return orderManagement;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((orderManagement == null) ? 0 : orderManagement.hashCode());
        result = prime * result + ((pendingOrder == null) ? 0 : pendingOrder.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Order)) {
            return false;
        }
        final Order other = (Order) obj;
        if (orderManagement == null) {
            if (other.orderManagement != null) {
                return false;
            }
        } else if (!orderManagement.equals(other.orderManagement)) {
            return false;
        }
        if (pendingOrder == null) {
            if (other.pendingOrder != null) {
                return false;
            }
        } else if (!pendingOrder.equals(other.pendingOrder)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Order [pendingOrder=" + pendingOrder + ", orderManagement=" + orderManagement + "]";
    }
}
