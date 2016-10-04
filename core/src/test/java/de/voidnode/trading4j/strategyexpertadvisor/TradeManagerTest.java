package de.voidnode.trading4j.strategyexpertadvisor;

import java.util.Optional;

import de.voidnode.trading4j.api.Failed;
import de.voidnode.trading4j.api.OrderManagement;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;
import de.voidnode.trading4j.domain.orders.CloseConditions;
import de.voidnode.trading4j.domain.orders.MutableCloseConditions;
import de.voidnode.trading4j.domain.orders.MutablePendingOrder;

import static de.voidnode.trading4j.domain.orders.ExecutionCondition.STOP;
import static de.voidnode.trading4j.domain.orders.OrderType.BUY;
import static de.voidnode.trading4j.domain.orders.OrderType.SELL;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Checks if {@link TradeManager} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class TradeManagerTest {

    @Mock
    private TradingStrategy<?> valueCalculator;

    @Mock
    private OrderManagement orderManagement;

    @InjectMocks
    private TradeManager cut;

    @Mock
    private Failed exemplaryFailure;

    private final MutableCloseConditions exampleConditions = new MutableCloseConditions().setStopLoose(new Price(1.0))
            .setTakeProfit(new Price(1.0));
    private final MutablePendingOrder exampleOrder = new MutablePendingOrder().setExecutionCondition(STOP)
            .setEntryPrice(new Price(1.0)).setCloseConditions(exampleConditions);

    /**
     * Sets up the default behavior of the mocks.
     */
    @Before
    public void setUpMocks() {
        exampleOrder.setType(BUY);
        when(valueCalculator.getStopLoose()).thenReturn(Optional.of(new Price(1.0)));
        when(valueCalculator.getTakeProfit()).thenReturn(Optional.of(new Price(1.0)));

        when(orderManagement.changeCloseConditionsOfOrder(any())).thenReturn(Optional.empty());
    }

    /**
     * When the suggested stop loose limit for a buy order is higher, the stop loose limit should be raised.
     */
    @Test
    public void whenStopLooseForBuyOrderIsHigherTheStopLoseShouldBeChanged() {
        exampleOrder.setType(BUY);
        when(valueCalculator.getStopLoose()).thenReturn(Optional.of(new Price(2.0)));
        final BasicPendingOrder order = exampleOrder.toImmutableBasicPendingOrder();

        cut.manageTrade(new Order(order, orderManagement));

        verify(orderManagement).changeCloseConditionsOfOrder(new CloseConditions(new Price(1.0), new Price(2.0)));
    }

    /**
     * When the suggested stop loose limit for a buy order is lower, the stop loose limit should be kept untouched.
     */
    @Test
    public void whenStopLooseForBuyOrderIsLowerTheStopLoseShouldNotBeChanged() {
        exampleOrder.setType(BUY);
        when(valueCalculator.getStopLoose()).thenReturn(Optional.of(new Price(0.5)));
        final BasicPendingOrder order = exampleOrder.toImmutableBasicPendingOrder();

        cut.manageTrade(new Order(order, orderManagement));

        verifyNoMoreInteractions(orderManagement);
    }

    /**
     * When the suggested stop loose limit for a sell order is lower, the stop loose limit should be adapted.
     */
    @Test
    public void whenStopLooseForSellOrderIsLowerTheStopLoseShouldBeChanged() {
        exampleOrder.setType(SELL);
        when(valueCalculator.getStopLoose()).thenReturn(Optional.of(new Price(0.5)));
        final BasicPendingOrder order = exampleOrder.toImmutableBasicPendingOrder();

        cut.manageTrade(new Order(order, orderManagement));

        verify(orderManagement).changeCloseConditionsOfOrder(new CloseConditions(new Price(1.0), new Price(0.5)));
    }

    /**
     * When the suggested stop loose limit for a sell order is higher, the stop loose limit should be kept untouched.
     */
    @Test
    public void whenStopLooseForSellOrderIsHigherTheStopLoseShouldNotBeChanged() {
        exampleOrder.setType(SELL);
        when(valueCalculator.getStopLoose()).thenReturn(Optional.of(new Price(2.0)));
        final BasicPendingOrder order = exampleOrder.toImmutableBasicPendingOrder();

        cut.manageTrade(new Order(order, orderManagement));

        verifyNoMoreInteractions(orderManagement);
    }

    /**
     * When the take profit price in the strategy changed, the currently active trade should be adapted.
     */
    @Test
    public void whenTheTakeProfitChangedTheTradeShouldBeAdapted() {
        when(valueCalculator.getTakeProfit()).thenReturn(Optional.of(new Price(0.5)));
        final BasicPendingOrder order = exampleOrder.toImmutableBasicPendingOrder();

        cut.manageTrade(new Order(order, orderManagement));

        verify(orderManagement).changeCloseConditionsOfOrder(new CloseConditions(new Price(0.5), new Price(1.0)));
    }

    /**
     * When updating the {@link CloseConditions} of a trade succeed, the updated order should be returned.
     */
    @Test
    public void whenChangingTheCloseConditionsOfATradeSucceedTheOrderManagerShouldBeReturned() {
        exampleOrder.setType(BUY);
        when(valueCalculator.getStopLoose()).thenReturn(Optional.of(new Price(2.0)));
        final BasicPendingOrder order = exampleOrder.toImmutableBasicPendingOrder();

        final Optional<Order> returnedOrder = cut.manageTrade(new Order(order, orderManagement));

        final BasicPendingOrder expectedOrder = exampleOrder.setCloseConditions(
                exampleConditions.setStopLoose(new Price(2.0))).toImmutableBasicPendingOrder();
        assertThat(returnedOrder).contains(new Order(expectedOrder, orderManagement));
    }

    /**
     * When changing the {@link CloseConditions} of a trade failed, the trade should be closed.
     */
    @Test
    public void whenChangingTheCloseConditionsOfATradeFailedTheTradeShouldBeClosed() {
        when(orderManagement.changeCloseConditionsOfOrder(any())).thenReturn(Optional.of(exemplaryFailure));

        exampleOrder.setType(BUY);
        when(valueCalculator.getStopLoose()).thenReturn(Optional.of(new Price(2.0)));
        final BasicPendingOrder order = exampleOrder.toImmutableBasicPendingOrder();

        final Optional<Order> returnedOrder = cut.manageTrade(new Order(order, orderManagement));

        verify(orderManagement).changeCloseConditionsOfOrder(new CloseConditions(new Price(1.0), new Price(2.0)));
        verify(orderManagement).closeOrCancelOrder();
        assertThat(returnedOrder).isEmpty();
    }
}
