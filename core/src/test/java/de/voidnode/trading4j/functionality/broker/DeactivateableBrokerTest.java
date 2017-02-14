package de.voidnode.trading4j.functionality.broker;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.OrderEventListener;
import de.voidnode.trading4j.api.OrderManagement;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;

import static de.voidnode.trading4j.testutils.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Checks if {@link DeactivateableBroker} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class DeactivateableBrokerTest {

    @Mock
    private Broker<BasicPendingOrder> broker;

    @InjectMocks
    private DeactivateableBroker cut;

    @Mock
    private OrderEventListener someEventListener;

    @Mock
    private BasicPendingOrder someOrder;

    @Mock
    private OrderManagement someOrderManagement;

    /**
     * Sets up the mocks.
     */
    @Before
    public void setUpMocks() {
        when(broker.sendOrder(someOrder, someEventListener)).thenReturn(someOrderManagement);
    }

    /**
     * When the cut is created trading is deactivated.
     */
    @Test
    public void atStartTradingIsDeactivated() {
        cut.sendOrder(someOrder, someEventListener);

        verify(someEventListener).orderRejected(any());
        verifyNoMoreInteractions(broker);
    }

    /**
     * When trading is activated the cut passes through all orders to the wrapped broker.
     */
    @Test
    public void passesThroughOrdersWhenTradingIsActivated() {
        cut.activate();
        cut.sendOrder(someOrder, someEventListener);

        verify(someEventListener, never()).orderRejected(any());
        verify(broker).sendOrder(someOrder, someEventListener);
    }

    /**
     * When trading is deactivated again all new orders will fail.
     */
    @Test
    public void failsAllOrdersWhenTradingIsDeactivated() {
        cut.activate();
        cut.deactivate();
        cut.sendOrder(someOrder, someEventListener);

        verify(someEventListener).orderRejected(any());
        verifyNoMoreInteractions(broker);
    }
}
