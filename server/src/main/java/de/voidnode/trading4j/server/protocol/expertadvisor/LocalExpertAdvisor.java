package de.voidnode.trading4j.server.protocol.expertadvisor;

import java.util.Currency;

import de.voidnode.trading4j.api.AccountingExpertAdvisor;
import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.ExpertAdvisor;
import de.voidnode.trading4j.api.OrderEventListener;
import de.voidnode.trading4j.domain.TimeFrame.M1;
import de.voidnode.trading4j.domain.marketdata.FullMarketData;
import de.voidnode.trading4j.domain.monetary.Money;
import de.voidnode.trading4j.domain.orders.PendingOrder;
import de.voidnode.trading4j.server.protocol.ProtocolException;
import de.voidnode.trading4j.server.protocol.messages.AccountCurrencyExchangeRateChangedMessage;
import de.voidnode.trading4j.server.protocol.messages.BalanceChangedMessage;
import de.voidnode.trading4j.server.protocol.messages.Message;
import de.voidnode.trading4j.server.protocol.messages.NewMarketDataExtendedMessage;
import de.voidnode.trading4j.server.protocol.messages.PendingOrderConditionalyClosedMessage;
import de.voidnode.trading4j.server.protocol.messages.PendingOrderConditionalyExecutedMessage;

/**
 * Converts incoming {@link Message}s from the remote {@link Broker} to method calls to the local {@link ExpertAdvisor}.
 * 
 * @author Raik Bieniek
 */
public class LocalExpertAdvisor {

    private final AccountingExpertAdvisor<FullMarketData<M1>> expertAdvisor;
    private final PendingOrderMapper orderMapper;
    private final Currency balanceCurrency;

    /**
     * Initializes the message to method call converter with all its dependencies.
     * 
     * @param expertAdvisor
     *            The expert advisor to send incoming messages to.
     * @param orderMapper
     *            Used to translate between {@link PendingOrder} objects and their ids.
     * @param balanceCurrency
     *            The currency that the balance of the trading account is kept in.
     */
    public LocalExpertAdvisor(final AccountingExpertAdvisor<FullMarketData<M1>> expertAdvisor,
            final PendingOrderMapper orderMapper, final Currency balanceCurrency) {
        this.expertAdvisor = expertAdvisor;
        this.orderMapper = orderMapper;
        this.balanceCurrency = balanceCurrency;
    }

    /**
     * Converts an incoming message from the remote {@link Broker} to method calls for the local {@link ExpertAdvisor}.
     * 
     * @param message
     *            The received message.
     * @throws ProtocolException
     *             If the message could not be handled appropriately.
     */
    public void handleMessage(final Message message) throws ProtocolException {
        if (message instanceof NewMarketDataExtendedMessage) {
            handle((NewMarketDataExtendedMessage) message);
        } else if (message instanceof AccountCurrencyExchangeRateChangedMessage) {
            handle((AccountCurrencyExchangeRateChangedMessage) message);
        } else if (message instanceof PendingOrderConditionalyExecutedMessage) {
            handle((PendingOrderConditionalyExecutedMessage) message);
        } else if (message instanceof PendingOrderConditionalyClosedMessage) {
            handle((PendingOrderConditionalyClosedMessage) message);
        } else if (message instanceof BalanceChangedMessage) {
            handle((BalanceChangedMessage) message);
        } else {
            throw new ProtocolException("Received a message of type '" + message.getClass().getSimpleName()
                    + "' which was not expected for the expert advisor protocol.");
        }
    }

    private void handle(final NewMarketDataExtendedMessage message) {
        expertAdvisor.newData(message.getCandleStick());
    }

    private void handle(final BalanceChangedMessage message) {
        expertAdvisor.balanceChanged(new Money(message.getNewBalance(), balanceCurrency));
    }

    private void handle(final AccountCurrencyExchangeRateChangedMessage message) {
        expertAdvisor.accountCurrencyPriceChanged(message.getNewRate());
    }

    private void handle(final PendingOrderConditionalyExecutedMessage message) throws ProtocolException {
        if (!orderMapper.has(message.getOrderId())) {
            throw new ProtocolException("Received a message that the order with the id " + message.getOrderId()
                    + " was executed but no pending order with this id was placed by this expert advisor.");
        }
        final OrderEventListener listener = orderMapper.get(message.getOrderId());
        listener.orderOpened(message.getTime(), message.getPrice());
    }

    private void handle(final PendingOrderConditionalyClosedMessage message) throws ProtocolException {
        if (!orderMapper.has(message.getOrderId())) {
            throw new ProtocolException("Received a message that the order with the id " + message.getOrderId()
                    + " was closed but no pending order with this id was placed by this expert advisor or it already was closed.");
        }
        final OrderEventListener listener = orderMapper.get(message.getOrderId());
        orderMapper.remove(message.getOrderId());
        listener.orderClosed(message.getTime(), message.getPrice());
    }
}
