package de.voidnode.trading4j.functionality.broker;

import java.util.Optional;

import static java.util.Optional.empty;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.Failed;
import de.voidnode.trading4j.api.OrderEventListener;
import de.voidnode.trading4j.api.OrderFilter;
import de.voidnode.trading4j.api.OrderManagement;
import de.voidnode.trading4j.domain.marketdata.MarketData;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;
import de.voidnode.trading4j.domain.timeframe.M1;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Checks if {@link OrderFilteringBroker} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderFilteringBrokerTest {

    @Mock
    private Broker<BasicPendingOrder> broker;

    @Mock
    private OrderFilter<MarketData<M1>> indicator1;

    @Mock
    private OrderFilter<MarketData<M1>> indicator2;

    private OrderFilteringBroker<MarketData<M1>> cut;

    @Mock
    private BasicPendingOrder someOrder;

    @Mock
    private OrderEventListener someEventListener;

    @Mock
    private OrderManagement someOrderManagement;

    @Mock
    private Failed someFailed;

    private final MarketData<M1> someMarketPrice = new MarketData<>(10);
    private final MarketData<M1> someOtherMarketPrice = new MarketData<>(20);

    /**
     * Initializes the class under test.
     */
    @Before
    public void setUpCut() {
        when(indicator1.filterOrder(any())).thenReturn(failure());
        when(indicator2.filterOrder(any())).thenReturn(failure());

        when(broker.sendOrder(someOrder, someEventListener)).thenReturn(someOrderManagement);
        cut = new OrderFilteringBroker<>(broker, indicator1, indicator2);
    }

    /**
     * A trade is allowed after first {@link MarketData} was received when no {@link OrderFilter} blocks trading.
     */
    @Test
    public void allowsTradingAfterMarketDataWasReceivedWhenNoTradeGuardBlocksTrades() {
        when(indicator1.filterOrder(someOrder)).thenReturn(empty());
        when(indicator2.filterOrder(someOrder)).thenReturn(empty());

        cut.newData(someMarketPrice);

        assertThat(cut.sendOrder(someOrder, someEventListener)).isNotNull();
        verify(broker).sendOrder(someOrder, someEventListener);
        verifyNoMoreInteractions(someEventListener);
    }

    /**
     * When any {@link OrderFilter} blocks a specific trade then the cut blocks the trade too.
     */
    @Test
    public void blocksTradesWhenAnyTradeGuardBlocksTheTrade() {
        when(indicator1.filterOrder(someOrder)).thenReturn(empty()).thenReturn(failure());
        when(indicator2.filterOrder(someOrder)).thenReturn(failure()).thenReturn(empty());

        cut.newData(someMarketPrice);
        cut.sendOrder(someOrder, someEventListener);
        cut.sendOrder(someOrder, someEventListener);

        verify(someEventListener, times(2)).orderRejected(any());
        verifyNoMoreInteractions(broker);
    }

    /**
     * All market data that the cut receives is distributed to all trade guards.
     */
    @Test
    public void updatesTradeGuardsWithAllMarketData() {
        cut.newData(someMarketPrice);
        verify(indicator1).updateMarketData(someMarketPrice);
        verify(indicator2).updateMarketData(someMarketPrice);

        cut.newData(someOtherMarketPrice);
        verify(indicator1).updateMarketData(someOtherMarketPrice);
        verify(indicator2).updateMarketData(someOtherMarketPrice);
    }

    /**
     * When the cut receives a {@link BasicPendingOrder} before any {@link MarketData} was passed to it, an exception is
     * thrown.
     */
    @Test(expected = IllegalStateException.class)
    public void failsWhenTradeWasReceivedBeforeFirstMarketData() {
        when(indicator1.filterOrder(any())).thenReturn(empty());
        when(indicator2.filterOrder(any())).thenReturn(empty());

        cut.sendOrder(someOrder, someEventListener);
    }

    private Optional<Failed> failure() {
        return Optional.of(someFailed);
    }
}
