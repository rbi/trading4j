package de.voidnode.trading4j.domain.orders;

import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.monetary.Price;

import static de.voidnode.trading4j.domain.VolumeUnit.LOT;
import static de.voidnode.trading4j.domain.orders.ExecutionCondition.LIMIT;
import static de.voidnode.trading4j.domain.orders.ExecutionCondition.STOP;
import static de.voidnode.trading4j.domain.orders.OrderType.BUY;
import static de.voidnode.trading4j.domain.orders.OrderType.SELL;
import static de.voidnode.trading4j.testutils.assertions.Assertions.assertThat;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link MutablePendingOrder} works as expected.
 * 
 * @author Raik Bieniek
 */
public class MutablePendingOrderTest {

    /**
     * The {@link MutablePendingOrder} class should build correct {@link PendingOrder} with the values passed to it.
     */
    @Test
    public void pendingOrdersAreBuildWithPassedValues() {
        final CloseConditions closeConditions = new CloseConditions(new Price(1.3), new Price(1.4));
        final MutablePendingOrder mutabalePendingOrder = new MutablePendingOrder().setVolume(new Volume(2, LOT))
                .setType(SELL).setExecutionCondition(LIMIT).setEntryPrice(new Price(1.2))
                .setCloseConditions(closeConditions);

        final PendingOrder order = mutabalePendingOrder.toImmutablePendingOrder();

        assertThat(order.getVolume()).isEqualTo(new Volume(2, LOT));
        assertThat(order.getType()).isEqualTo(SELL);
        assertThat(order.getExecutionCondition()).isEqualTo(LIMIT);
        assertThat(order.getEntryPrice()).isEqualTo(new Price(1.2));
        assertThat(order.getCloseConditions()).isEqualTo(closeConditions);
    }

    /**
     * The copy constructor should copy the values of an original {@link PendingOrder} for the new instance.
     */
    @Test
    public void pendingOrderCopyConstructorShoudCopyValuesFromOtherPendingOrdersCorrectly() {
        final PendingOrder orig = new PendingOrder(new Volume(4, LOT), BUY, STOP, new Price(5.0), new CloseConditions(
                new Price(6.0), new Price(7.0)));

        final MutablePendingOrder cut = new MutablePendingOrder(orig);
        assertThat(cut.getVolume()).isEqualTo(new Volume(4, LOT));
        assertThat(cut.getType()).isEqualTo(BUY);
        assertThat(cut.getExecutionCondition()).isEqualTo(STOP);
        assertThat(cut.getEntryPrice()).isEqualTo(new Price(5.0));
        assertThat(cut.getCloseConditions()).isEqualTo(new CloseConditions(new Price(6.0), new Price(7.0)));
    }

    /**
     * When a {@link PendingOrder} should be build but a mandatory field was not set, the {@link MutablePendingOrder}
     * instance should fail.
     */
    @Test
    public void pendingOrderBuildingshouldFailWhenAMandatoryFieldWasNotSet() {
        final Volume dummyLot = new Volume(1, LOT);
        final Price dummyPrice = new Price(1.0);
        final CloseConditions closeConditions = new CloseConditions(new Price(1.0), new Price(1.0));
        int exceptionsCaught = 0;

        try {
            new MutablePendingOrder().setType(SELL).setExecutionCondition(LIMIT).setEntryPrice(dummyPrice)
                    .setCloseConditions(closeConditions).toImmutablePendingOrder();
        } catch (final IllegalStateException e) {
            exceptionsCaught++;
        }
        try {
            new MutablePendingOrder().setVolume(dummyLot).setExecutionCondition(LIMIT).setEntryPrice(dummyPrice)
                    .setCloseConditions(closeConditions).toImmutablePendingOrder();
        } catch (final IllegalStateException e) {
            exceptionsCaught++;
        }
        try {
            new MutablePendingOrder().setVolume(dummyLot).setType(SELL).setEntryPrice(dummyPrice)
                    .setCloseConditions(closeConditions).toImmutablePendingOrder();
        } catch (final IllegalStateException e) {
            exceptionsCaught++;
        }
        try {
            new MutablePendingOrder().setVolume(dummyLot).setType(SELL).setExecutionCondition(LIMIT)
                    .setCloseConditions(closeConditions).toImmutablePendingOrder();
        } catch (final IllegalStateException e) {
            exceptionsCaught++;
        }
        try {
            new MutablePendingOrder().setVolume(dummyLot).setType(SELL).setExecutionCondition(LIMIT)
                    .setEntryPrice(dummyPrice).toImmutablePendingOrder();
        } catch (final IllegalStateException e) {
            exceptionsCaught++;
        }

        assertThat(exceptionsCaught).as("Expected the immutable conversion to fail 5 times but it failed %d times.",
                exceptionsCaught).isEqualTo(5);
    }

    /**
     * The {@link MutablePendingOrder} class should build correct {@link BasicPendingOrder} with the values passed to
     * it.
     */
    @Test
    public void basicPendingOrdersAreBuildWithPassedValues() {
        final CloseConditions closeConditions = new CloseConditions(new Price(1.3), new Price(1.4));
        final MutablePendingOrder mutabalePendingOrder = new MutablePendingOrder().setType(SELL)
                .setExecutionCondition(LIMIT).setEntryPrice(new Price(1.2)).setCloseConditions(closeConditions);

        final BasicPendingOrder order = mutabalePendingOrder.toImmutableBasicPendingOrder();

        assertThat(order.getType()).isEqualTo(SELL);
        assertThat(order.getExecutionCondition()).isEqualTo(LIMIT);
        assertThat(order.getEntryPrice()).isEqualTo(new Price(1.2));
        assertThat(order.getCloseConditions()).isEqualTo(closeConditions);
    }

    /**
     * The copy constructor should copy the values of an original {@link BasicPendingOrder} for the new instance.
     */
    @Test
    public void basicPendingOrderCopyConstructorShoudCopyValuesFromOtherPendingOrdersCorrectly() {
        final BasicPendingOrder orig = new BasicPendingOrder(BUY, STOP, new Price(5.0), new CloseConditions(new Price(
                6.0), new Price(7.0)));

        final MutablePendingOrder cut = new MutablePendingOrder(orig);
        assertThat(cut.getType()).isEqualTo(BUY);
        assertThat(cut.getExecutionCondition()).isEqualTo(STOP);
        assertThat(cut.getEntryPrice()).isEqualTo(new Price(5.0));
        assertThat(cut.getCloseConditions()).isEqualTo(new CloseConditions(new Price(6.0), new Price(7.0)));
    }

    /**
     * When a {@link BasicPendingOrder} should be build but a mandatory field was not set, the
     * {@link MutablePendingOrder} instance should fail.
     */
    @Test
    public void basicPendingOrderBuildingshouldFailWhenAMandatoryFieldWasNotSet() {
        final Price dummyPrice = new Price(1.0);
        final CloseConditions closeConditions = new CloseConditions(new Price(1.0), new Price(1.0));
        int exceptionsCaught = 0;

        try {
            new MutablePendingOrder().setExecutionCondition(LIMIT).setEntryPrice(dummyPrice)
                    .setCloseConditions(closeConditions).toImmutableBasicPendingOrder();
        } catch (final IllegalStateException e) {
            exceptionsCaught++;
        }
        try {
            new MutablePendingOrder().setType(SELL).setEntryPrice(dummyPrice).setCloseConditions(closeConditions)
                    .toImmutableBasicPendingOrder();
        } catch (final IllegalStateException e) {
            exceptionsCaught++;
        }
        try {
            new MutablePendingOrder().setType(SELL).setExecutionCondition(LIMIT).setCloseConditions(closeConditions)
                    .toImmutableBasicPendingOrder();
        } catch (final IllegalStateException e) {
            exceptionsCaught++;
        }
        try {
            new MutablePendingOrder().setType(SELL).setExecutionCondition(LIMIT).setEntryPrice(dummyPrice)
                    .toImmutableBasicPendingOrder();
        } catch (final IllegalStateException e) {
            exceptionsCaught++;
        }

        assertThat(exceptionsCaught).as("Expected the immutable conversion to fail 4 times but it failed %d times.",
                exceptionsCaught).isEqualTo(4);
    }
}
