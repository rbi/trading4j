package de.voidnode.trading4j.domain.orders;

import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.VolumeUnit;
import de.voidnode.trading4j.domain.monetary.Price;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link PendingOrder} works as expected.
 * 
 * @author Raik Bieniek
 */
public class PendingOrderTest {

    /**
     * {@link PendingOrder} are equal to other instances that have the same values.
     */

    /**
     * A {@link BasicPendingOrder} order is equal only to other instances of {@link BasicPendingOrder}s or sub-classes
     * that have exactly the same values.
     */
    @Test
    public void equalsOtherPendingOrdersWithSameValues() {
        final PendingOrder order1 = new PendingOrder(new Volume(41, VolumeUnit.LOT), OrderType.BUY,
                ExecutionCondition.LIMIT, new Price(42), new CloseConditions(new Price(43), new Price(44)));
        final PendingOrder order2 = new PendingOrder(new Volume(41, VolumeUnit.LOT), OrderType.BUY,
                ExecutionCondition.LIMIT, new Price(42), new CloseConditions(new Price(43), new Price(44)));
        final ExemplarySubClass subClass = new ExemplarySubClass(new Volume(41, VolumeUnit.LOT), OrderType.BUY,
                ExecutionCondition.LIMIT, new Price(42), new CloseConditions(new Price(43), new Price(44)));
        final PendingOrder differentVolume =  new PendingOrder(new Volume(5, VolumeUnit.LOT), OrderType.BUY,
                ExecutionCondition.LIMIT, new Price(42), new CloseConditions(new Price(43), new Price(44)));
        final PendingOrder differentBaseValues =  new PendingOrder(new Volume(41, VolumeUnit.LOT), OrderType.BUY,
                ExecutionCondition.LIMIT, new Price(42), new CloseConditions(new Price(43), new Price(40)));


        assertThat(order1).isEqualTo(order1);
        assertThat(order1).isEqualTo(order2);
        assertThat(order1).isEqualTo(subClass);

        assertThat(order1).isNotEqualTo(differentVolume);
        assertThat(order1).isNotEqualTo(differentBaseValues);
        assertThat(order1).isNotEqualTo(null);
        assertThat(order1).isNotEqualTo("Not an order");
    }

    /**
     * An exemplary sub-class of a {@link PendingOrder} that is used to check equality.
     */
    private static final class ExemplarySubClass extends PendingOrder {

        // just to differ from a plain PendingOrder
        @SuppressWarnings("unused")
        private final int someValue = 42;

        ExemplarySubClass(final Volume volume, final OrderType type,
                final ExecutionCondition executionCondition, final Price entryPrice,
                final CloseConditions closeConditions) {
            super(volume, type, executionCondition, entryPrice, closeConditions);
        }
    }
}
