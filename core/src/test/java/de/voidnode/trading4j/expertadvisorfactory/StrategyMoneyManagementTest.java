package de.voidnode.trading4j.expertadvisorfactory;

import java.time.Instant;
import java.util.Optional;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.ExpertAdvisor;
import de.voidnode.trading4j.api.Failed;
import de.voidnode.trading4j.api.MoneyManagement;
import de.voidnode.trading4j.api.OrderEventListener;
import de.voidnode.trading4j.api.OrderManagement;
import de.voidnode.trading4j.api.UsedVolumeManagement;
import de.voidnode.trading4j.domain.ForexSymbol;
import de.voidnode.trading4j.domain.TimeFrame.M1;
import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.marketdata.MarketData;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;
import de.voidnode.trading4j.domain.orders.MutableCloseConditions;
import de.voidnode.trading4j.domain.orders.MutablePendingOrder;
import de.voidnode.trading4j.domain.orders.PendingOrder;

import static de.voidnode.trading4j.domain.VolumeUnit.LOT;
import static de.voidnode.trading4j.domain.orders.ExecutionCondition.STOP;
import static de.voidnode.trading4j.domain.orders.OrderType.SELL;
import static de.voidnode.trading4j.testutils.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Checks if {@link StrategyMoneyManagement} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class StrategyMoneyManagementTest {

    private static final ForexSymbol SOME_FOREX_SYMBOL = new ForexSymbol("EURUSD");
    private static final Volume SOME_VOLUME = new Volume(42, LOT);
    private static final Price SOME_PRICE = new Price(53);
    private static final Volume SOME_STEP_SIZE = new Volume(82, LOT);

    @Mock
    private Broker<PendingOrder> broker;

    @Mock
    private MoneyManagement moneyManagement;

    private StrategyMoneyManagement<MarketData<M1>> cut;

    private final BasicPendingOrder someOrder = new MutablePendingOrder().setType(SELL).setExecutionCondition(STOP)
            .setEntryPrice(new Price(25))
            .setCloseConditions(new MutableCloseConditions().setTakeProfit(new Price(10)).setStopLoose(new Price(30)))
            .toImmutableBasicPendingOrder();

    @Mock
    private OrderEventListener someEventListener;

    @Mock
    private OrderManagement someOrderManagement;

    @Mock
    private UsedVolumeManagement someUsedVolumeManagement;

    @Mock
    private Failed someFailed;

    @Captor
    private ArgumentCaptor<OrderEventListener> eventListenerCaptor;

    /**
     * Sets up the cut and the mocks.
     */
    @Before
    public void setUpCutAndMock() {
        when(someUsedVolumeManagement.getVolume()).thenReturn(SOME_VOLUME);
        when(moneyManagement.requestVolume(any(), any(), any(), any()))
                .thenReturn(Optional.of(someUsedVolumeManagement));
        when(broker.sendOrder(any(), any())).thenReturn(someOrderManagement);

        cut = new StrategyMoneyManagement<>(broker, moneyManagement, SOME_FOREX_SYMBOL, SOME_STEP_SIZE);
        cut.newData(new MarketData<>(SOME_PRICE));
    }

    /**
     * When requesting a {@link Volume}, all required data is passed.
     */
    @Test
    public void sendAllNecessaryDataWhenRequestingMoney() {
        cut.sendOrder(someOrder, someEventListener);

        final Price expectedLooseOnStopLoose = new Price(5); // absolute value of (entry price - stop loose)

        verify(moneyManagement).requestVolume(SOME_FOREX_SYMBOL, SOME_PRICE, expectedLooseOnStopLoose, SOME_STEP_SIZE);
    }

    /**
     * When requesting a {@link Volume} succeed, a {@link PendingOrder} is placed at the {@link Broker}.
     */
    @Test
    public void placesPendingOrderAtBrokerWhenVolumeRequestSucceed() {
        cut.sendOrder(someOrder, someEventListener);

        verify(broker).sendOrder(
                eq(new MutablePendingOrder(someOrder).setVolume(SOME_VOLUME).toImmutablePendingOrder()),
                any(OrderEventListener.class));
        verifyNoMoreInteractions(someEventListener);
    }

    /**
     * When requesting a {@link Volume} failed, a {@link Failed} instance is returned.
     */
    @Test
    public void returnsFailedWhenVolumeRequstFailed() {
        when(moneyManagement.requestVolume(any(), any(), any(), any())).thenReturn(Optional.empty());

        cut.sendOrder(someOrder, someEventListener);

        verify(someEventListener).orderRejected(any());
        verifyNoMoreInteractions(broker);
    }

    /**
     * When the order has failed to being sent to the broker, the volume is returned.
     */
    @Test
    public void returnsVolumeWhenSendingOrderHasFailed() {
        when(broker.sendOrder(any(), any())).thenAnswer(invocation -> {
            final OrderEventListener orderEventListener = (OrderEventListener) invocation.getArguments()[1];
            orderEventListener.orderRejected(new Failed("simulated error"));
            return someOrderManagement;
        });

        cut.sendOrder(someOrder, someEventListener);

        verify(broker).sendOrder(any(), any());
        verify(someEventListener).orderRejected(any());
        verify(someUsedVolumeManagement).releaseVolume();
    }

    /**
     * When the broker closed the order, the lent {@link Volume}s is returned to money management.
     */
    @Test
    public void returnsVolumeWhenOrderIsClosed() {
        final OrderManagement orderManagement = cut.sendOrder(someOrder, someEventListener);

        orderManagement.closeOrCancelOrder();

        verify(someUsedVolumeManagement).releaseVolume();
        verify(someOrderManagement).closeOrCancelOrder();
    }

    /**
     * When the {@link ExpertAdvisor} cancels the order, the lent {@link Volume}s is returned to money management.
     */
    @Test
    public void returnsVolumeWhenOrderIsCanceled() {
        final Price somePrice = new Price(42);
        final Instant someTime = Instant.EPOCH.plusMillis(5821);

        cut.sendOrder(someOrder, someEventListener);
        verify(broker).sendOrder(any(), eventListenerCaptor.capture());
        eventListenerCaptor.getValue().orderClosed(someTime, somePrice);

        verify(someUsedVolumeManagement).releaseVolume();
        verify(someEventListener).orderClosed(someTime, somePrice);
    }
}
