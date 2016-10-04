package de.voidnode.trading4j.domain.orders;

import de.voidnode.trading4j.domain.monetary.Price;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link BasicPendingOrder} works as expected.
 * 
 * @author Raik Bieniek
 */
public class BasicPendingOrderTest {

    /**
     * A {@link BasicPendingOrder} order is equal only to other instances of {@link BasicPendingOrder}s or sub-classes
     * that have exactly the same values.
     */
    @Test
    public void equalsOtherBasicPendingOrdersWithSameValues() {
        final BasicPendingOrder order1 = new BasicPendingOrder(OrderType.BUY, ExecutionCondition.LIMIT, new Price(42),
                new CloseConditions(new Price(43), new Price(44)));
        final BasicPendingOrder order2 = new BasicPendingOrder(OrderType.BUY, ExecutionCondition.LIMIT, new Price(42),
                new CloseConditions(new Price(43), new Price(44)));
        final ExemplarySubClass subClass = new ExemplarySubClass(OrderType.BUY, ExecutionCondition.LIMIT,
                new Price(42), new CloseConditions(new Price(43), new Price(44)));
        final BasicPendingOrder otherOrder = new BasicPendingOrder(OrderType.BUY, ExecutionCondition.STOP,
                new Price(42), new CloseConditions(new Price(43), new Price(40)));

        assertThat(order1).isEqualTo(order1);
        assertThat(order1).isEqualTo(order2);
        assertThat(order1).isEqualTo(subClass);

        assertThat(order1).isNotEqualTo(otherOrder);
        assertThat(order1).isNotEqualTo(null);
        assertThat(order1).isNotEqualTo("Not an order");
    }

    /**
     * An exemplary sub-class of a {@link BasicPendingOrder} that is used to check equality.
     */
    private static final class ExemplarySubClass extends BasicPendingOrder {

        // just to differ from a plain BasicPendingOrder
        @SuppressWarnings("unused")
        private final int someValue = 42;

        ExemplarySubClass(final OrderType type, final ExecutionCondition executionCondition,
                final Price entryPrice, final CloseConditions closeConditions) {
            super(type, executionCondition, entryPrice, closeConditions);
        }
    }
}
