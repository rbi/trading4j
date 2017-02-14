package de.voidnode.trading4j.expertadvisorfactory;

import java.time.Instant;
import java.util.Optional;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.Failed;
import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.api.MarketDataListener;
import de.voidnode.trading4j.api.OrderEventListener;
import de.voidnode.trading4j.api.OrderManagement;
import de.voidnode.trading4j.api.UsedVolumeManagement;
import de.voidnode.trading4j.api.VolumeLender;
import de.voidnode.trading4j.domain.ForexSymbol;
import de.voidnode.trading4j.domain.TimeFrame.M1;
import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.marketdata.MarketData;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;
import de.voidnode.trading4j.domain.orders.CloseConditions;
import de.voidnode.trading4j.domain.orders.MutablePendingOrder;
import de.voidnode.trading4j.domain.orders.PendingOrder;

/**
 * Adds the {@link Volume} to trade to a {@link BasicPendingOrder} from a trading strategy by requesting it from the
 * money management.
 *
 * @author Raik Bieniek
 * @param <C> The market the improper-market-situation-{@link Indicator} expect as input.
 */
class StrategyMoneyManagement<C extends MarketData<M1>> implements Broker<BasicPendingOrder>, MarketDataListener<C> {

    private static final OrderManagement NO_OP_ORDER_MANAGEMENT = new NoOpOrderManagement();
    private static final Failed NO_VOLUME = new Failed(
            "Could not place the pending order at the broker because the money management did not provide money.");

    private final Broker<PendingOrder> broker;
    private final VolumeLender volumeLender;
    private final ForexSymbol forexSymbol;
    private final Volume allowedStepSize;

    private Optional<Price> lastMarketData = Optional.empty();

    /**
     * Initializes an instance with all its dependencies.
     *
     * @param broker          The {@link Broker} that should be used to trade concrete orders.
     * @param volumeLender    Used to request {@link Volume}s for trades.
     * @param forexSymbol     The symbol that is traded.
     * @param allowedStepSize The allowed step size for volumes.
     */
    StrategyMoneyManagement(final Broker<PendingOrder> broker, final VolumeLender volumeLender,
                            final ForexSymbol forexSymbol, final Volume allowedStepSize) {
        this.broker = broker;
        this.volumeLender = volumeLender;
        this.forexSymbol = forexSymbol;
        this.allowedStepSize = allowedStepSize;
    }

    @Override
    public OrderManagement sendOrder(final BasicPendingOrder order,
                                     final OrderEventListener eventListener) {
        final Price lastPrice = lastMarketData.orElseThrow(() -> new IllegalStateException(
                "An order was send before the current value of the traded symbol was passed to this instance."));
        final Price difference = new Price(
                Math.abs(order.getEntryPrice().asPipette() - order.getCloseConditions().getStopLoose().asPipette()));
        final Optional<UsedVolumeManagement> volumeManagement = volumeLender.requestVolume(forexSymbol, lastPrice,
                difference, allowedStepSize);

        if (volumeManagement.isPresent()) {
            final VolumeReturner returner = new VolumeReturner(volumeManagement.get(), eventListener);
            final OrderManagement origMgmnt = broker.sendOrder(new MutablePendingOrder(order)
                    .setVolume(volumeManagement.get().getVolume()).toImmutablePendingOrder(), returner);
            // register the original order management at the volume returner
            returner.setOrderManagement(origMgmnt);
            return returner;
        } else {
            eventListener.orderRejected(NO_VOLUME);
            return NO_OP_ORDER_MANAGEMENT;
        }
    }

    @Override
    public void newData(final C marketData) {
        this.lastMarketData = Optional.of(marketData.getClose());
    }

    /**
     * Returns {@link Volume} to the money management when the order is closed.
     */
    private static final class VolumeReturner implements OrderManagement, OrderEventListener {

        private final OrderEventListener origEventListener;
        private final UsedVolumeManagement volumeManagement;
        private OrderManagement origOrderManagement;

        VolumeReturner(final UsedVolumeManagement volumeManagement, final OrderEventListener origEventListener) {
            this.volumeManagement = volumeManagement;
            this.origEventListener = origEventListener;
        }

        public void setOrderManagement(final OrderManagement origOrderManagement) {
            this.origOrderManagement = origOrderManagement;
        }

        @Override
        public void orderRejected(final Failed failure) {
            volumeManagement.releaseVolume();
            origEventListener.orderRejected(failure);
        }

        @Override
        public void orderOpened(final Instant time, final Price price) {
            origEventListener.orderOpened(time, price);

        }

        @Override
        public void orderClosed(final Instant time, final Price price) {
            volumeManagement.releaseVolume();
            origEventListener.orderClosed(time, price);
        }

        @Override
        public void closeOrCancelOrder() {
            origOrderManagement.closeOrCancelOrder();
            volumeManagement.releaseVolume();

        }

        @Override
        public Optional<Failed> changeCloseConditionsOfOrder(final CloseConditions conditions) {
            return origOrderManagement.changeCloseConditionsOfOrder(conditions);
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
