package de.voidnode.trading4j.functionality.broker;

import java.time.Instant;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.Either;
import de.voidnode.trading4j.api.Failed;
import de.voidnode.trading4j.api.OrderEventListener;
import de.voidnode.trading4j.api.OrderManagement;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;
import de.voidnode.trading4j.domain.orders.CloseConditions;

import static de.voidnode.trading4j.testutils.assertions.Assertions.assertThat;

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
import static org.mockito.Mockito.verify;
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
        when(broker.sendOrder(any(), any())).thenReturn(Either.withRight(someOrderManagement));
    }

    /**
     * A second order that is placed while the first order is still active will fail.
     */
    @Test
    public void secondPlacedOrderWillFail() {
        final Either<Failed, OrderManagement> firstOrder = cut.sendOrder(someOrder, someEventListener);
        assertThat(firstOrder).hasRight();

        final Either<Failed, OrderManagement> secondOrder = cut.sendOrder(someOrder, someEventListener);
        assertThat(secondOrder).hasLeft();
    }

    /**
     * If a order placed first is closed, new orders can be opened.
     */
    @Test
    public void closedFirstOrderAllowsFurtherOrders() {
        cut.sendOrder(someOrder, someEventListener);
        verify(broker).sendOrder(eq(someOrder), receivedOrderEventListener.capture());
        receivedOrderEventListener.getValue().orderClosed(Instant.EPOCH.plusMillis(1234), new Price(42));

        final Either<Failed, OrderManagement> secondOrder = cut.sendOrder(someOrder, someEventListener);
        assertThat(secondOrder).hasRight();
    }

    /**
     * If a order placed first is canceled, new orders can be opened.
     */
    @Test
    public void canceledFirstOrderAllowsFurtherOrders() {
        final Either<Failed, OrderManagement> firstOrder = cut.sendOrder(someOrder, someEventListener);
        firstOrder.getRight().closeOrCancelOrder();

        final Either<Failed, OrderManagement> secondOrder = cut.sendOrder(someOrder, someEventListener);
        assertThat(secondOrder).hasRight();
    }

    /**
     * If placing a pending order at the wrapped broker failed, placing orders is not blocked.
     */
    @Test
    public void failedPendingOrdersDoNotBlockTrading() {
        when(broker.sendOrder(any(), any())).thenReturn(Either.withLeft(someFailed));

        final Either<Failed, OrderManagement> firstOrder = cut.sendOrder(someOrder, someEventListener);
        assertThat(firstOrder).hasLeftEqualTo(someFailed);

        when(broker.sendOrder(any(), any())).thenReturn(Either.withRight(someOrderManagement));
        final Either<Failed, OrderManagement> secondOrder = cut.sendOrder(someOrder, someEventListener);
        assertThat(secondOrder).hasRight();
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
        final Either<Failed, OrderManagement> sendOrder = cut.sendOrder(someOrder, someEventListener);
        sendOrder.getRight().changeCloseConditionsOfOrder(someCloseConditions);
        verify(someOrderManagement).changeCloseConditionsOfOrder(someCloseConditions);

        sendOrder.getRight().closeOrCancelOrder();
        verify(someOrderManagement).closeOrCancelOrder();
    }
}
