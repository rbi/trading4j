package de.voidnode.trading4j.strategyexpertadvisor;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.Failed;
import de.voidnode.trading4j.api.OrderEventListener;
import de.voidnode.trading4j.api.OrderManagement;
import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;

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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Checks if {@link PendingOrderCreator} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class PendingOrderCreatorTest {

    private static final Price SOME_PRICE = new Price(1.0);

    @Mock
    private TradingStrategy<?> strategy;

    @Mock
    private Broker<BasicPendingOrder> broker;

    @InjectMocks
    private PendingOrderCreator cut;

    @Captor
    private ArgumentCaptor<BasicPendingOrder> sentPendingOrder;

    @Mock
    private OrderEventListener eventListener;

    @Mock
    private OrderManagement exemplaryOrderManagement;

    @Mock
    private Failed exemplaryFailure;

    /**
     * Sets up the behavior of the mocks.
     */
    @Before
    public void setUpMocks() {
        mockPrices(opt(SOME_PRICE), opt(SOME_PRICE), opt(SOME_PRICE));

        when(strategy.getEntryCondition()).thenReturn(LIMIT);
        when(broker.sendOrder(any(), any())).thenReturn(exemplaryOrderManagement);
    }

    /**
     * When any of the prices, the signal or the trend passed to the orderCreator is empty, no order should be send.
     */
    @Test
    public void shouldNotSendStopOrdersWhenTrendOrPricesAreEmpty() {
        when(strategy.getEntrySignal()).thenReturn(opt(UP));
        when(strategy.getTrend()).thenReturn(empty());
        mockPrices(opt(SOME_PRICE), opt(SOME_PRICE), opt(SOME_PRICE));
        assertThat(cut.checkMarketEntry(eventListener)).isEmpty();

        when(strategy.getEntrySignal()).thenReturn(empty());
        when(strategy.getTrend()).thenReturn(opt(UP));
        mockPrices(opt(SOME_PRICE), opt(SOME_PRICE), opt(SOME_PRICE));
        assertThat(cut.checkMarketEntry(eventListener)).isEmpty();

        mockSignalAndTrend(opt(UP));
        mockPrices(empty(), opt(SOME_PRICE), opt(SOME_PRICE));
        assertThat(cut.checkMarketEntry(eventListener)).isEmpty();

        mockSignalAndTrend(opt(UP));
        mockPrices(opt(SOME_PRICE), empty(), opt(SOME_PRICE));
        assertThat(cut.checkMarketEntry(eventListener)).isEmpty();

        mockSignalAndTrend(opt(UP));
        mockPrices(opt(SOME_PRICE), opt(SOME_PRICE), empty());
        assertThat(cut.checkMarketEntry(eventListener)).isEmpty();

        verifyNoMoreInteractions(broker);
    }

    /**
     * No order should be send if the trend is different from the entry signal.
     */
    @Test
    public void shouldNotSendOrdersWhenTrendDiffersFromSignal() {
        when(strategy.getEntrySignal()).thenReturn(opt(UP));
        when(strategy.getTrend()).thenReturn(opt(DOWN));

        assertThat(cut.checkMarketEntry(eventListener)).isEmpty();
    }

    /**
     * When the trend is up a buy order should be placed.
     */
    @Test
    public void buyOrdersAreSendWhenTheTrendIsUp() {
        mockSignalAndTrend(opt(UP));

        cut.checkMarketEntry(eventListener);

        verify(broker).sendOrder(this.sentPendingOrder.capture(), same(eventListener));
        final BasicPendingOrder orderSentToBroker = this.sentPendingOrder.getValue();
        assertThat(orderSentToBroker.getType()).isEqualTo(BUY);
    }

    /**
     * When the trend is down a sell order should be placed.
     */
    @Test
    public void sellOrdersAreSendWhenTheTrendIsDown() {
        mockSignalAndTrend(opt(DOWN));

        cut.checkMarketEntry(eventListener);

        verify(broker).sendOrder(this.sentPendingOrder.capture(), same(eventListener));
        final BasicPendingOrder orderSentToBroker = this.sentPendingOrder.getValue();
        assertThat(orderSentToBroker.getType()).isEqualTo(SELL);
    }

    /**
     * The cut uses the execution condition of the strategy.
     */
    @Test
    public void executionConditionOfTheStrategyIsUsed() {
        mockSignalAndTrend(opt(UP));

        when(strategy.getEntryCondition()).thenReturn(STOP);
        cut.checkMarketEntry(eventListener);

        when(strategy.getEntryCondition()).thenReturn(LIMIT);
        cut.checkMarketEntry(eventListener);

        verify(broker, times(2)).sendOrder(this.sentPendingOrder.capture(), same(eventListener));

        final List<BasicPendingOrder> orderSentToBroker = this.sentPendingOrder.getAllValues();
        assertThat(orderSentToBroker.get(0).getExecutionCondition()).isEqualTo(STOP);
        assertThat(orderSentToBroker.get(1).getExecutionCondition()).isEqualTo(LIMIT);
    }

    /**
     * When an order was placed, the order management from the broker is returned.
     */
    @Test
    public void orderManagementFromBrokerIsReturnedWhenAnOrderWasPlaced() {
        mockSignalAndTrend(opt(UP));

        final Optional<Order> order = cut.checkMarketEntry(eventListener);

        verify(broker).sendOrder(this.sentPendingOrder.capture(), same(eventListener));
        final BasicPendingOrder orderSentToBroker = this.sentPendingOrder.getValue();

        assertThat(order).isPresent().contains(new Order(orderSentToBroker, exemplaryOrderManagement));
    }

    /**
     * When the cut sends a pending order, the prices of the order should equal the prices passed to the cut.
     */
    @Test
    public void pendingOrderPricesShouldEqualThePricesPassedToTheCut() {
        mockSignalAndTrend(opt(UP));
        mockPrices(opt(new Price(2.0)), opt(new Price(3.0)), opt(new Price(4.0)));

        cut.checkMarketEntry(eventListener);

        verify(broker).sendOrder(this.sentPendingOrder.capture(), same(eventListener));
        final BasicPendingOrder orderSentToBroker = this.sentPendingOrder.getValue();

        assertThat(orderSentToBroker.getEntryPrice()).isEqualTo(new Price(2.0));
        assertThat(orderSentToBroker.getCloseConditions().getTakeProfit()).isEqualTo(new Price(3.0));
        assertThat(orderSentToBroker.getCloseConditions().getStopLoose()).isEqualTo(new Price(4.0));
    }

    /**
     * Sent orders will never have an expiration date.
     */
    @Test
    public void expirationDateIsUnset() {
        mockSignalAndTrend(opt(UP));
        cut.checkMarketEntry(eventListener);
        mockSignalAndTrend(opt(DOWN));
        cut.checkMarketEntry(eventListener);

        verify(broker, times(2)).sendOrder(this.sentPendingOrder.capture(), same(eventListener));
        final List<BasicPendingOrder> sentPendingOrders = this.sentPendingOrder.getAllValues();
        assertThat(sentPendingOrders.get(0).getCloseConditions().getExpirationDate().isPresent()).isFalse();
        assertThat(sentPendingOrders.get(1).getCloseConditions().getExpirationDate().isPresent()).isFalse();
    }

    private <T> Optional<T> opt(final T price) {
        return Optional.of(price);
    }

    private void mockSignalAndTrend(final Optional<MarketDirection> signal) {
        when(strategy.getEntrySignal()).thenReturn(signal);
        when(strategy.getTrend()).thenReturn(signal);
    }

    private void mockPrices(final Optional<Price> entryPrice, final Optional<Price> takeProfit,
            final Optional<Price> stopLoose) {
        when(strategy.getEntryPrice()).thenReturn(entryPrice);
        when(strategy.getTakeProfit()).thenReturn(takeProfit);
        when(strategy.getStopLoose()).thenReturn(stopLoose);
    }
}
