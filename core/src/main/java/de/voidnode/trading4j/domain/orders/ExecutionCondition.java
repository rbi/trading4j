package de.voidnode.trading4j.domain.orders;

import de.voidnode.trading4j.domain.monetary.Price;

/**
 * Conditions at which a {@link BasicPendingOrder} is executed.
 * 
 * @author Raik Bieniek
 */
public enum ExecutionCondition {

    /**
     * Executes a {@link BasicPendingOrder} directly at the current market {@link Price}.
     */
    DIRECT,

    /**
     * Execute a {@link BasicPendingOrder} when the market price reaches a given <em>worse</em> level compared to the
     * current market price.
     * 
     * <p>
     * What <em>worse</em> means depends on the {@link OrderType}. For {@link OrderType#BUY} <em>worse</em> level is a
     * lower {@link Price} and for {@link OrderType#SELL} a <em>worse</em> level is a higher {@link Price}.
     * </p>
     * Examples:
     * <ul>
     * <li>Given a current market price of 1.31, the entry price 1.30 and the {@link OrderType#BUY}. With a
     * {@link ExecutionCondition#LIMIT}, the order gets executes when the price reaches the <em>worse</em> level of
     * 1.30.</li>
     * <li>Given a current market price of 1.21, the entry price 1.22 and the {@link OrderType#SELL}. With a
     * {@link ExecutionCondition#LIMIT}, the order gets executes when the price reaches the <em>worse</em> level of
     * 1.22.</li>
     * </ul>
     */
    LIMIT,

    /**
     * Execute a {@link BasicPendingOrder} when the market price reaches a given <em>better</em> level compared to the
     * current market price.
     * 
     * <p>
     * What <em>better</em> means depends on the {@link OrderType}. For {@link OrderType#BUY} <em>better</em> level is a
     * higher {@link Price} and for {@link OrderType#SELL} a <em>better</em> level is a lower {@link Price}.
     * </p>
     * Examples:
     * <ul>
     * <li>Given a current market price of 1.31, the entry price 1.32 and the {@link OrderType#BUY}. With a
     * {@link ExecutionCondition#STOP}, the order gets executes when the price reaches the <em>better</em> level of
     * 1.32.</li>
     * <li>Given a current market price of 1.21, the entry price 1.20 and the {@link OrderType#SELL}. With a
     * {@link ExecutionCondition#STOP}, the order gets executes when the price reaches the <em>better</em> level of
     * 1.20.</li>
     * </ul>
     */
    STOP
}
