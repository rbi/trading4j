package de.voidnode.trading4j.server.protocol.expertadvisor;

import de.voidnode.trading4j.api.OrderEventListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link PendingOrderMapper} works as expected.
 *
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class PendingOrderMapperTest {

    private static final int INITIAL_PENDING_ORDER_ID = 42;
    private static final int OTHER_PENDING_ORDER_ID = 5;

    private PendingOrderMapper cut = new PendingOrderMapper();

    @Mock
    private OrderEventListener initialOrderEventListener;

    @Mock
    private OrderEventListener otherOrderEventListener;

    /**
     * Sets up the initial state of the {@link PendingOrderMapper}.
     */
    @Before
    public void setUpCut() {
        cut.put(INITIAL_PENDING_ORDER_ID, initialOrderEventListener);
    }

    /**
     * The cut should store {@link OrderEventListener} and map them to the ids of orders.
     */
    @Test
    public void shouldStoreOrderEventListenersAndMakeThemQueriableByTheirOrderId() {
        cut.put(OTHER_PENDING_ORDER_ID, otherOrderEventListener);

        assertThat(cut.get(INITIAL_PENDING_ORDER_ID)).isSameAs(initialOrderEventListener);
        assertThat(cut.get(OTHER_PENDING_ORDER_ID)).isSameAs(otherOrderEventListener);
    }

    /**
     * When the cut is queried for the {@link OrderEventListener} of an unknown order id, an exception is thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void queriingForUnknownPendingOrderIdsShouldResultInAnException() {
        // otherPendingOrder is not put to the map this time

        cut.get(OTHER_PENDING_ORDER_ID);
    }

    /**
     * The cut allows to be checked for a {@link OrderEventListener} with a given id to be present.
     */
    @Test
    public void canCheckIfOrderWithIdIsKnown() {
        assertThat(cut.has(INITIAL_PENDING_ORDER_ID)).isTrue();
        assertThat(cut.has(OTHER_PENDING_ORDER_ID)).isFalse();
    }

    /**
     * It should be possible to remove order id to {@link OrderEventListener} mappings from the cut.
     */
    @Test
    public void shouldBeAbleToRemoveMappings() {
        cut.remove(INITIAL_PENDING_ORDER_ID);

        assertThat(cut.has(INITIAL_PENDING_ORDER_ID)).isFalse();
    }

    /**
     * When an {@link OrderEventListener} with an unknown id is removed from the cut, an exception should be thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void removingAnUnknownIdShouldResultInAnException() {
        cut.remove(OTHER_PENDING_ORDER_ID);
    }
}
