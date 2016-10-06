package de.voidnode.trading4j.server.protocol.expertadvisor;

import java.util.Optional;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.Either;
import de.voidnode.trading4j.api.ExpertAdvisor;
import de.voidnode.trading4j.api.Failed;
import de.voidnode.trading4j.api.OrderEventListener;
import de.voidnode.trading4j.api.OrderManagement;
import de.voidnode.trading4j.domain.VolumeUnit;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.CloseConditions;
import de.voidnode.trading4j.domain.orders.ExecutionCondition;
import de.voidnode.trading4j.domain.orders.MutableCloseConditions;
import de.voidnode.trading4j.domain.orders.MutablePendingOrder;
import de.voidnode.trading4j.domain.orders.OrderType;
import de.voidnode.trading4j.domain.orders.PendingOrder;
import de.voidnode.trading4j.server.protocol.CommunicationException;
import de.voidnode.trading4j.server.protocol.messages.ChangeCloseConditionsMessage;
import de.voidnode.trading4j.server.protocol.messages.CloseOrCancelPendingOrderMessage;
import de.voidnode.trading4j.server.protocol.messages.Message;
import de.voidnode.trading4j.server.protocol.messages.MessageBasedClientConnection;
import de.voidnode.trading4j.server.protocol.messages.PlacePendingOrderMessage;
import de.voidnode.trading4j.server.protocol.messages.ResponseChangeCloseConditionsMessage;
import de.voidnode.trading4j.server.protocol.messages.ResponsePlacePendingOrderMessage;

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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Checks if {@link RemoteBroker} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class RemoteBrokerTest {
    private static final int EXAMPLE_PENDING_ORDER_ID = 42;

    private static final PendingOrder EXAMPLE_PENDING_ORDER = new MutablePendingOrder()
            .setType(OrderType.BUY)
            .setExecutionCondition(ExecutionCondition.STOP)
            .setVolume(1, VolumeUnit.LOT)
            .setEntryPrice(new Price(1.0))
            .setCloseConditions(new MutableCloseConditions().setTakeProfit(new Price(2.0)).setStopLoose(new Price(3.0)))
            .toImmutablePendingOrder();

    private static final CloseConditions EXAMPLE_NEW_CLOSE_CONDITIONS = new MutableCloseConditions()
            .setTakeProfit(new Price(4.0)).setStopLoose(new Price(5.0)).toImmutable();

    @Mock
    private PendingOrderMapper pendingOrderMapper;

    @Mock
    private MessageBasedClientConnection client;

    @Mock
    private OrderEventListener exampleOrderEventListener;

    @InjectMocks
    private RemoteBroker cut;

    @Captor
    private ArgumentCaptor<Message> sentMessage;

    @Captor
    private ArgumentCaptor<PendingOrder> updatedPendingOrderCaptor;

    /**
     * Sets up the default behavior of the mocks.
     * 
     * @throws CommunicationException
     *             not expected to leave the test.
     */
    @Before
    public void setUpMocks() throws CommunicationException {
        when(client.readMessage(ResponsePlacePendingOrderMessage.class)).thenReturn(
                new ResponsePlacePendingOrderMessage(true, 42));
        when(pendingOrderMapper.has(EXAMPLE_PENDING_ORDER_ID)).thenReturn(true);
    }

    // /////////////////////////
    // / place pending order ///
    // /////////////////////////

    /**
     * When the {@link ExpertAdvisor} creates a {@link PendingOrder} the protocol should send it to the remote broker.
     * 
     * @throws CommunicationException
     *             not expected to leave the test.
     */
    @Test
    public void shouldSendPendingOrdersCorrectly() throws CommunicationException {
        final Either<Failed, OrderManagement> result = cut.sendOrder(EXAMPLE_PENDING_ORDER, exampleOrderEventListener);

        // sent message
        verify(client).sendMessage(sentMessage.capture());
        assertThat(sentMessage.getValue()).isInstanceOf(PlacePendingOrderMessage.class);
        final PlacePendingOrderMessage sentMessage = (PlacePendingOrderMessage) this.sentMessage.getValue();
        assertThat(sentMessage.getPendingOrder()).isEqualTo(EXAMPLE_PENDING_ORDER);

        assertThat(result).hasRight();
    }

    /**
     * When an {@link PendingOrder} is send to a remote {@link Broker}, the remote broker sends back the id of the
     * {@link PendingOrder}. This id should be registered in the {@link PendingOrderMapper} with the
     * {@link OrderEventListener} that was received.
     * 
     * @throws CommunicationException
     *             not expected to leave the test.
     */
    @Test
    public void shouldRegisterPendingOrderIdWithTheReceivedOrderEventListenerWhenSendingAPendingOrder()
            throws CommunicationException {
        cut.sendOrder(EXAMPLE_PENDING_ORDER, exampleOrderEventListener);

        verify(pendingOrderMapper).put(EXAMPLE_PENDING_ORDER_ID, exampleOrderEventListener);
    }

    /**
     * When the expert advisor tried to send a pending order but the remote broker failed to place this order, the
     * expert advisor should be notified.
     * 
     * @throws CommunicationException
     *             not expected to leave the test.
     */
    @Test
    public void shouldInformTheExpertAdvisorWhenSendingAPendingOrderFailed() throws CommunicationException {
        when(client.readMessage(ResponsePlacePendingOrderMessage.class)).thenReturn(
                new ResponsePlacePendingOrderMessage(false, 50));

        final Either<Failed, OrderManagement> result = cut.sendOrder(EXAMPLE_PENDING_ORDER, exampleOrderEventListener);

        assertThat(result).hasLeftEqualTo(new MetaTraderFailure(50));
    }

    // ///////////////////////////////////
    // / close or cancel pending order ///
    // ///////////////////////////////////

    /**
     * When the {@link ExpertAdvisor} requests the closing of a {@link PendingOrder} it should be send correctly to the
     * remote {@link Broker}.
     * 
     * @throws CommunicationException
     *             not expected to leave the test.
     */
    @Test
    public void shouldCloseOrCancelPendingOrdersCorrectly() throws CommunicationException {
        final OrderManagement orderManagement = cut.sendOrder(EXAMPLE_PENDING_ORDER, exampleOrderEventListener)
                .getRight();
        orderManagement.closeOrCancelOrder();

        // Called for the send message and the close message.
        verify(client, times(2)).sendMessage(sentMessage.capture());
        assertThat(sentMessage.getAllValues().get(1)).isInstanceOf(CloseOrCancelPendingOrderMessage.class);
        final CloseOrCancelPendingOrderMessage closeOrCancelMessage = (CloseOrCancelPendingOrderMessage) sentMessage
                .getAllValues().get(1);
        assertThat(closeOrCancelMessage.getId()).isEqualTo(EXAMPLE_PENDING_ORDER_ID);
    }

    /**
     * When the user closes or cancels a {@link PendingOrder} it should be removed from the {@link PendingOrder} to id
     * mapping to prevent memory leaks.
     * 
     * @throws CommunicationException
     *             not expected to leave the test.
     */
    @Test
    public void aClosedOrCanceledPendingOrderShouldBeRemovedFromTheMapping() throws CommunicationException {
        final OrderManagement orderManagement = cut.sendOrder(EXAMPLE_PENDING_ORDER, exampleOrderEventListener)
                .getRight();
        orderManagement.closeOrCancelOrder();

        verify(pendingOrderMapper).remove(EXAMPLE_PENDING_ORDER_ID);
    }

    /**
     * When the {@link ExpertAdvisor} tries to close or cancel an already closed or canceled order, an
     * {@link LoopThroughIllegalStateException} should be thrown.
     * 
     * @throws CommunicationException
     *             not expected to leave the test.
     */
    @Test(expected = LoopThroughIllegalStateException.class)
    public void closeOrCancelAnAlreadyClosedOrCanceledOrderShouldFail() throws CommunicationException {
        when(pendingOrderMapper.has(EXAMPLE_PENDING_ORDER_ID)).thenReturn(false);

        final OrderManagement orderManagement = cut.sendOrder(EXAMPLE_PENDING_ORDER, exampleOrderEventListener)
                .getRight();
        orderManagement.closeOrCancelOrder();
    }

    // //////////////////////////////////////////////
    // / Change close conditions of pending order ///
    // //////////////////////////////////////////////

    /**
     * When the user changes the {@link CloseConditions} of an order, the cut should send this request to the remote
     * broker correctly.
     * 
     * @throws CommunicationException
     *             not expected to leave the test.
     */
    @Test
    public void shouldSentCloseConditionChangesOfPendingOrdersCorrectly() throws CommunicationException {
        when(client.readMessage(ResponseChangeCloseConditionsMessage.class)).thenReturn(
                new ResponseChangeCloseConditionsMessage());

        final OrderManagement orderManagement = cut.sendOrder(EXAMPLE_PENDING_ORDER, exampleOrderEventListener)
                .getRight();
        final Optional<Failed> result = orderManagement.changeCloseConditionsOfOrder(EXAMPLE_NEW_CLOSE_CONDITIONS);

        // Called for the send message and the close message.
        verify(client, times(2)).sendMessage(sentMessage.capture());
        assertThat(sentMessage.getAllValues().get(1)).isInstanceOf(ChangeCloseConditionsMessage.class);
        final ChangeCloseConditionsMessage changeCloseConditionsMessage = (ChangeCloseConditionsMessage) sentMessage
                .getAllValues().get(1);
        assertThat(changeCloseConditionsMessage.getId()).isEqualTo(EXAMPLE_PENDING_ORDER_ID);
        assertThat(changeCloseConditionsMessage.getNewCloseConditions()).isEqualTo(EXAMPLE_NEW_CLOSE_CONDITIONS);

        assertThat(result).isEmpty();
    }

    /**
     * When changing the {@link CloseConditions} failed on the remote side, the {@link ExpertAdvisor} should be informed
     * correctly.
     * 
     * @throws CommunicationException
     *             not expected to leave the test.
     */
    @Test
    public void whenChangingCloseConditionsFailedTheExpertAdvisorShouldBeInformed() throws CommunicationException {
        when(client.readMessage(ResponseChangeCloseConditionsMessage.class)).thenReturn(
                new ResponseChangeCloseConditionsMessage(50));

        final OrderManagement orderManagement = cut.sendOrder(EXAMPLE_PENDING_ORDER, exampleOrderEventListener)
                .getRight();
        final Optional<Failed> result = orderManagement.changeCloseConditionsOfOrder(EXAMPLE_NEW_CLOSE_CONDITIONS);

        assertThat(result).isPresent().contains(new MetaTraderFailure(50));
    }

    /**
     * When the {@link ExpertAdvisor} tries to change the {@link CloseConditions} of an order that was already closed or
     * canceled placed, an {@link LoopThroughIllegalStateException} should be thrown.
     * 
     * @throws CommunicationException
     *             not expected to leave the test.
     */
    @Test(expected = LoopThroughIllegalStateException.class)
    public void changeCloseConditionsOfAnUnknownPendingOrderShouldFail() throws CommunicationException {
        when(pendingOrderMapper.has(EXAMPLE_PENDING_ORDER_ID)).thenReturn(false);

        final OrderManagement orderManagement = cut.sendOrder(EXAMPLE_PENDING_ORDER, exampleOrderEventListener)
                .getRight();

        orderManagement.changeCloseConditionsOfOrder(EXAMPLE_NEW_CLOSE_CONDITIONS);
    }
}
