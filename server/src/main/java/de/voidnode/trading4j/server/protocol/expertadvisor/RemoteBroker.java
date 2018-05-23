package de.voidnode.trading4j.server.protocol.expertadvisor;

import java.util.Optional;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.Failed;
import de.voidnode.trading4j.api.OrderEventListener;
import de.voidnode.trading4j.api.OrderManagement;
import de.voidnode.trading4j.domain.orders.CloseConditions;
import de.voidnode.trading4j.domain.orders.PendingOrder;
import de.voidnode.trading4j.server.protocol.MessageBasedClientConnection;
import de.voidnode.trading4j.server.protocol.exceptions.CommunicationException;
import de.voidnode.trading4j.server.protocol.messages.ChangeCloseConditionsMessage;
import de.voidnode.trading4j.server.protocol.messages.CloseOrCancelPendingOrderMessage;
import de.voidnode.trading4j.server.protocol.messages.PlacePendingOrderMessage;
import de.voidnode.trading4j.server.protocol.messages.ResponseChangeCloseConditionsMessage;
import de.voidnode.trading4j.server.protocol.messages.ResponsePlacePendingOrderMessage;

/**
 * Converts local calls to {@link Broker} methods to messages and sends them to the remote {@link Broker}.
 * 
 * @author Raik Bieniek
 */
public class RemoteBroker implements Broker<PendingOrder> {

    private static final OrderManagement NO_OP_ORDER_MANAGEMENT = new NoOpOrderManagement();
    private final MessageBasedClientConnection clientConnection;
    private final PendingOrderMapper orderMapper;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param clientConnection
     *            The connection to the remote broker.
     * @param orderMapper
     *            Used to translate between {@link PendingOrder} objects and their ids.
     */
    public RemoteBroker(final MessageBasedClientConnection clientConnection, final PendingOrderMapper orderMapper) {
        this.clientConnection = clientConnection;
        this.orderMapper = orderMapper;
    }

    @Override
    public OrderManagement sendOrder(final PendingOrder order, final OrderEventListener eventListener) {
        try {
            clientConnection.sendMessage(new PlacePendingOrderMessage(order));
            final ResponsePlacePendingOrderMessage idMessage = clientConnection
                    .readMessage(ResponsePlacePendingOrderMessage.class);
            if (idMessage.isSuccess()) {
                orderMapper.put(idMessage.getId().get(), eventListener);
                return new RemoteOrderManagement(idMessage.getId().get());
            } else {
                eventListener.orderRejected(new MetaTraderFailure(idMessage.getErrorCode().get()));
                return NO_OP_ORDER_MANAGEMENT;
            }
        } catch (final CommunicationException e) {
            throw new LoopThroughCommunicationException(e);
        }
    }

    /**
     * Converts local order management calls to messages that are sent to be executed by a remote broker.
     */
    private class RemoteOrderManagement implements OrderManagement {

        private final int orderId;

        RemoteOrderManagement(final int orderId) {
            this.orderId = orderId;
        }

        @Override
        public void closeOrCancelOrder() {
            if (!orderMapper.has(orderId)) {
                throw new LoopThroughIllegalStateException(new IllegalStateException(
                        "The expert advisor tried to close or cancel an order that was already closed or canceld."));
            }
            try {
                orderMapper.remove(orderId);
                clientConnection.sendMessage(new CloseOrCancelPendingOrderMessage(orderId));
            } catch (CommunicationException e) {
                throw new LoopThroughCommunicationException(e);
            }
        }

        @Override
        public Optional<Failed> changeCloseConditionsOfOrder(final CloseConditions conditions) {
            if (!orderMapper.has(orderId)) {
                throw new LoopThroughIllegalStateException(
                        new IllegalStateException(
                                "The expert advisor tried to change the close conditions of an order that was already closed or canceled."));
            }
            try {
                clientConnection.sendMessage(new ChangeCloseConditionsMessage(orderId, conditions));
                final Optional<Integer> errorCode = clientConnection.readMessage(
                        ResponseChangeCloseConditionsMessage.class).getErrorCode();
                if (errorCode.isPresent()) {
                    return Optional.of(new MetaTraderFailure(errorCode.get()));
                }
                return Optional.empty();
            } catch (CommunicationException e) {
                throw new LoopThroughCommunicationException(e);
            }
        }
    }


    /**
     * An {@link OrderManagement} that does nothing on requests.
     */
    private static class NoOpOrderManagement implements OrderManagement {

        private static final Optional<Failed> NOT_SUPPORTED = Optional.of(new Failed("Changing close conditions"
                + " is not supported because the order has already been canceled."));

        @Override
        public void closeOrCancelOrder() {
        }

        @Override
        public Optional<Failed> changeCloseConditionsOfOrder(final CloseConditions conditions) {
            return NOT_SUPPORTED;
        }
    }
}
