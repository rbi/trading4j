package de.voidnode.trading4j.strategyexpertadvisor;

import java.util.Optional;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.Failed;
import de.voidnode.trading4j.api.OrderEventListener;
import de.voidnode.trading4j.api.OrderManagement;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;
import de.voidnode.trading4j.domain.orders.MutableCloseConditions;
import de.voidnode.trading4j.domain.orders.MutablePendingOrder;

import static de.voidnode.trading4j.domain.MarketDirection.DOWN;
import static de.voidnode.trading4j.domain.MarketDirection.UP;
import static de.voidnode.trading4j.domain.orders.ExecutionCondition.LIMIT;
import static de.voidnode.trading4j.domain.orders.ExecutionCondition.STOP;
import static de.voidnode.trading4j.domain.orders.OrderType.BUY;
import static de.voidnode.trading4j.domain.orders.OrderType.SELL;
import static de.voidnode.trading4j.testutils.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Checks if {@link PendingOrderManager} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class PendingOrderManagerTest {

    private static final BasicPendingOrder EXEMPLARY_BUY_ORDER = new MutablePendingOrder()
            .setType(BUY)
            .setEntryPrice(new Price(1.0))
            .setExecutionCondition(STOP)
            .setCloseConditions(new MutableCloseConditions().setStopLoose(new Price(1.0)).setTakeProfit(new Price(1.0)))
            .toImmutableBasicPendingOrder();

    private static final BasicPendingOrder EXEMPLARY_SELL_ORDER = new MutablePendingOrder()
            .setType(SELL)
            .setEntryPrice(new Price(2.0))
            .setExecutionCondition(LIMIT)
            .setCloseConditions(new MutableCloseConditions().setStopLoose(new Price(2.0)).setTakeProfit(new Price(2.0)))
            .toImmutableBasicPendingOrder();

    @Mock
    private TradingStrategy<?> strategy;

    @Mock
    private Broker<BasicPendingOrder> broker;

    @Mock
    private OrderEventListener eventListener;

    @InjectMocks
    private PendingOrderManager cut;

    @Captor
    private ArgumentCaptor<BasicPendingOrder> sentPendingOrder;

    @Mock
    private OrderManagement exemplaryOrderManagementBuy;

    @Mock
    private OrderManagement exemplaryOrderManagementSell;

    @Mock
    private OrderManagement exemplaryOrderManagementFromBroker;

    @Mock
    private Failed exemplaryFailure;

    private Order exemplaryBuyOrder;

    private Order exemplarySellOrder;

    /**
     * Sets up the default behavior of the mocks which may be overridden by individual tests.
     */
    @Before
    public void setUpDefaultBehaviorOfMocks() {
        when(strategy.getTrend()).thenReturn(Optional.of(UP));
        when(strategy.getEntryPrice()).thenReturn(Optional.of(new Price(1.0)));
        when(strategy.getStopLoose()).thenReturn(Optional.of(new Price(1.0)));
        when(strategy.getTakeProfit()).thenReturn(Optional.of(new Price(1.0)));
        when(broker.sendOrder(any(), any())).thenReturn(exemplaryOrderManagementFromBroker);
    }

    /**
     * Sets up the test data.
     */
    @Before
    public void setupTestData() {
        exemplaryBuyOrder = new Order(EXEMPLARY_BUY_ORDER, exemplaryOrderManagementBuy);
        exemplarySellOrder = new Order(EXEMPLARY_SELL_ORDER, exemplaryOrderManagementSell);
    }

    /**
     * When no prices changes, the pending order should be left untouched.
     */
    @Test
    public void doesNotChangeOrderWhenPricesDontChange() {
        final Optional<Order> manageBuyOrder = cut.manageOrder(exemplaryBuyOrder, eventListener);
        assertThat(manageBuyOrder).contains(exemplaryBuyOrder);

        verifyNoMoreInteractions(broker);
    }

    /**
     * Cancels pending buy orders when the trend is down.
     */
    @Test
    public void abortsPendingBuyOrdersWhenTrendIsDown() {
        when(strategy.getTrend()).thenReturn(Optional.of(DOWN));
        when(strategy.getEntryPrice()).thenReturn(Optional.of(new Price(2.0)));
        when(strategy.getStopLoose()).thenReturn(Optional.of(new Price(2.0)));

        final Optional<Order> managedSellOrder = cut.manageOrder(exemplarySellOrder, eventListener);
        final Optional<Order> manageBuyOrder = cut.manageOrder(exemplaryBuyOrder, eventListener);

        assertThat(managedSellOrder).contains(exemplarySellOrder);
        assertThat(manageBuyOrder).isEmpty();
        verify(exemplaryOrderManagementBuy).closeOrCancelOrder();
        verifyNoMoreInteractions(broker);
    }

    /**
     * Cancels pending sell orders when the trend is up.
     */
    @Test
    public void abortsPendingSellOrdersWhenTrendIsUp() {
        when(strategy.getTrend()).thenReturn(Optional.of(UP));

        final Optional<Order> managedSellOrder = cut.manageOrder(exemplarySellOrder, eventListener);
        final Optional<Order> manageBuyOrder = cut.manageOrder(exemplaryBuyOrder, eventListener);

        assertThat(managedSellOrder).isEmpty();
        assertThat(manageBuyOrder).contains(exemplaryBuyOrder);
        verify(exemplaryOrderManagementSell).closeOrCancelOrder();
        verifyNoMoreInteractions(broker);
    }

    /**
     * When the current best entry {@link Price} for {@link BasicPendingOrder}s changes, the {@link BasicPendingOrder}
     * should be replaced.
     */
    @Test
    public void whenEntryPriceChangesPendingOrderShouldBeReplaced() {
        when(strategy.getEntryPrice()).thenReturn(Optional.of(new Price(3.0)));

        final Optional<Order> manageBuyOrder = cut.manageOrder(exemplaryBuyOrder, eventListener);
        verify(exemplaryOrderManagementBuy).closeOrCancelOrder();
        verify(broker).sendOrder(this.sentPendingOrder.capture(), same(eventListener));
        final BasicPendingOrder newOrder = this.sentPendingOrder.getValue();

        assertThat(manageBuyOrder).contains(new Order(newOrder, exemplaryOrderManagementFromBroker));
        assertThat(newOrder.getType()).isEqualTo(BUY);
        assertThat(newOrder.getExecutionCondition()).isEqualTo(STOP);
        assertThat(newOrder.getEntryPrice()).isEqualTo(new Price(3.0));
        assertThat(newOrder.getCloseConditions().getStopLoose()).isEqualTo(new Price(1.0));
        assertThat(newOrder.getCloseConditions().getTakeProfit()).isEqualTo(new Price(1.0));
    }

    /**
     * When the current best stop loose {@link Price} for {@link BasicPendingOrder}s changes, the
     * {@link BasicPendingOrder} should be replaced.
     */
    @Test
    public void whenStopLoosePriceChangesPendingOrderShouldBeReplaced() {
        when(strategy.getStopLoose()).thenReturn(Optional.of(new Price(3.0)));

        final Optional<Order> manageBuyOrder = cut.manageOrder(exemplaryBuyOrder, eventListener);
        verify(exemplaryOrderManagementBuy).closeOrCancelOrder();
        verify(broker).sendOrder(this.sentPendingOrder.capture(), same(eventListener));
        final BasicPendingOrder newOrder = this.sentPendingOrder.getValue();

        assertThat(manageBuyOrder).contains(new Order(newOrder, exemplaryOrderManagementFromBroker));
        assertThat(newOrder.getType()).isEqualTo(BUY);
        assertThat(newOrder.getExecutionCondition()).isEqualTo(STOP);
        assertThat(newOrder.getEntryPrice()).isEqualTo(new Price(1.0));
        assertThat(newOrder.getCloseConditions().getStopLoose()).isEqualTo(new Price(3.0));
        assertThat(newOrder.getCloseConditions().getTakeProfit()).isEqualTo(new Price(1.0));
    }

    /**
     * When the current pending order should be changed but some of the required prices are missing, the current pending
     * order should be aborted.
     */
    @Test
    public void whenPendingOrderShouldBeReplacedButValuesAreMissingTheCurrentPendingOrderIsAborted() {
        when(strategy.getEntryPrice()).thenReturn(Optional.of(new Price(2.0)));
        // when(strategy.getStopLoose()).thenReturn(Optional.of(new Price(2.0)));
        when(strategy.getTakeProfit()).thenReturn(Optional.empty());

        final Optional<Order> managedOrder = cut.manageOrder(exemplaryBuyOrder, eventListener);
        assertThat(managedOrder).isEmpty();

        verify(exemplaryOrderManagementBuy).closeOrCancelOrder();
        verifyNoMoreInteractions(broker);
    }
}
