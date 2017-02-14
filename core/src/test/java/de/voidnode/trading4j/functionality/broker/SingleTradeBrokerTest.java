package de.voidnode.trading4j.functionality.broker;

import java.time.Instant;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.Failed;
import de.voidnode.trading4j.api.OrderEventListener;
import de.voidnode.trading4j.api.OrderManagement;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;
import de.voidnode.trading4j.domain.orders.CloseConditions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Checks if {@link SingleTradeBroker} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class SingleTradeBrokerTest {

    @Mock
    private Broker<BasicPendingOrder> broker;

    @InjectMocks
    private SingleTradeBroker cut;

    @Mock
    private OrderEventListener someEventListener;

    @Mock
    private BasicPendingOrder someOrder;

    @Mock
    private OrderManagement someOrderManagement;

    @Mock
    private Failed someFailed;

    @Mock
    private CloseConditions someCloseConditions;

    @Captor
    private ArgumentCaptor<OrderEventListener> receivedOrderEventListener;

    /**
     * Sets up the mocks.
     */
    @Before
    public void setUpMocks() {
        when(broker.sendOrder(any(), any())).thenReturn(someOrderManagement);
    }

    /**
     * A second order that is placed while the first order is still active will fail.
     */
    @Test
    public void secondPlacedOrderWillFail() {
        cut.sendOrder(someOrder, someEventListener);
        verify(someEventListener, never()).orderRejected(any());

        cut.sendOrder(someOrder, someEventListener);
        verify(someEventListener).orderRejected(any());
    }

    /**
     * If a placed order is closed, new orders can be opened.
     */
    @Test
    public void closedFirstOrderAllowsFurtherOrders() {
        cut.sendOrder(someOrder, someEventListener);
        verify(broker).sendOrder(eq(someOrder), receivedOrderEventListener.capture());
        receivedOrderEventListener.getValue().orderClosed(Instant.EPOCH.plusMillis(1234), new Price(42));

        reset(broker, someEventListener);

        cut.sendOrder(someOrder, someEventListener);
        verify(broker).sendOrder(eq(someOrder), any());
        verifyNoMoreInteractions(someEventListener);
    }

    /**
     * If a order placed first is canceled, new orders can be opened.
     */
    @Test
    public void canceledFirstOrderAllowsFurtherOrders() {
        final OrderManagement firstOrder = cut.sendOrder(someOrder, someEventListener);
        firstOrder.closeOrCancelOrder();

        cut.sendOrder(someOrder, someEventListener);
        verifyNoMoreInteractions(someEventListener);
    }

    /**
     * If placing a pending order at the wrapped broker failed, placing orders is not blocked.
     */
    @Test
    public void failedPendingOrdersDoNotBlockTrading() {
        when(broker.sendOrder(any(), any())).thenAnswer(invocation -> {
            final OrderEventListener orderEventListener = (OrderEventListener) invocation.getArguments()[1];
            orderEventListener.orderRejected(new Failed("simulated error"));
            return someOrderManagement;
        });

        cut.sendOrder(someOrder, someEventListener);
        verify(broker).sendOrder(any(), any());
        verify(someEventListener).orderRejected(any());

        reset(broker);

        cut.sendOrder(someOrder, someEventListener);
        verify(broker).sendOrder(any(), any());
    }

    /**
     * The cut passes order events through to the user provided {@link OrderEventListener}.
     */
    @Test
    public void orderEventsArePassedThrough() {
        // check order event listener
        cut.sendOrder(someOrder, someEventListener);
        verify(broker).sendOrder(eq(someOrder), receivedOrderEventListener.capture());

        receivedOrderEventListener.getValue().orderOpened(Instant.EPOCH.plusMillis(823), new Price(1337));
        verify(someEventListener).orderOpened(Instant.EPOCH.plusMillis(823), new Price(1337));

        receivedOrderEventListener.getValue().orderClosed(Instant.EPOCH.plusMillis(1234), new Price(42));
        verify(someEventListener).orderClosed(Instant.EPOCH.plusMillis(1234), new Price(42));

        // check order manager
        final OrderManagement sendOrder = cut.sendOrder(someOrder, someEventListener);
        sendOrder.changeCloseConditionsOfOrder(someCloseConditions);
        verify(someOrderManagement).changeCloseConditionsOfOrder(someCloseConditions);

        sendOrder.closeOrCancelOrder();
        verify(someOrderManagement).closeOrCancelOrder();
    }
}
