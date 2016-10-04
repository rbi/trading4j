package de.voidnode.trading4j.functionality.broker;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.Either;
import de.voidnode.trading4j.api.Failed;
import de.voidnode.trading4j.api.MarketDataListener;
import de.voidnode.trading4j.api.OrderEventListener;
import de.voidnode.trading4j.api.OrderFilter;
import de.voidnode.trading4j.api.OrderManagement;
import de.voidnode.trading4j.api.UnrecoverableProgrammingError;
import de.voidnode.trading4j.domain.marketdata.MarketData;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;

/**
 * Prevent orders to an other broker in improper market situations or when they fail certain criteria.
 * 
 * <p>
 * What improper market situations are or what fail criteria for an order are is decided by {@link OrderFilter}s passed
 * in the constructor.
 * </p>
 *
 * @author Raik Bieniek
 * @param <C>
 *            The market data the {@link OrderFilter}s expect as input.
 */
public class OrderFilteringBroker<C extends MarketData<?>> implements Broker<BasicPendingOrder>, MarketDataListener<C> {

    private final OrderFilter<C>[] orderFilters;
    private final Broker<BasicPendingOrder> broker;

    private boolean receivedData;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param broker
     *            The broker that should be used to execute orders when trading isn't blocked.
     * @param orderFilters
     *            All {@link OrderFilter}s that should block orders when they indicate improper market situations or
     *            improper orders. These situations are indicated by returning an instance of {@link Failed} which
     *            should describe the error in an human readable format in their {@link Failed#toString()} method.
     */
    @SafeVarargs
    public OrderFilteringBroker(final Broker<BasicPendingOrder> broker, final OrderFilter<C>... orderFilters) {
        this.broker = broker;
        this.orderFilters = orderFilters;

    }

    @Override
    public Either<Failed, OrderManagement> sendOrder(final BasicPendingOrder order,
            final OrderEventListener eventListener) {
        if (!receivedData) {
            throw new UnrecoverableProgrammingError(
                    "To decide if an order is blocked, the market data stream needs to be recieved but no market data was received yet.");
        }

        final List<Failed> blocks = stream(orderFilters).map(guard -> guard.filterOrder(order))
                .filter(Optional::isPresent).map(Optional::get).collect(toList());
        final Optional<Failed> directlyBlocked = blocks.isEmpty() ? Optional.empty()
                : Optional.of(new TradingBlocked(blocks));
        if (directlyBlocked.isPresent()) {
            return Either.withLeft(directlyBlocked.get());
        }

        return broker.sendOrder(order, eventListener);
    }

    @Override
    public void newData(final C candleStick) {
        receivedData = true;
        stream(orderFilters).forEach(guard -> guard.updateMarketData(candleStick));
    }

    /**
     * Indicates that an order has been blocked because of improper market conditions.
     */
    private static final class TradingBlocked extends Failed {

        /**
         * Initializes an instance with all its dependencies.
         * 
         * @param reasons
         *            The reasons describing why the current market situation is improper.
         */
        TradingBlocked(final List<Failed> reasons) {
            super(concatReasons(reasons));
        }

        private static String concatReasons(final List<Failed> reasons) {
            final StringBuilder builder = new StringBuilder(
                    "Trading has been blocked because of improper market conditions. ");
            builder.append("The concrete reasons where:");
            for (final Failed failed : reasons) {
                builder.append("\n* ");
                builder.append(failed);
            }
            return builder.toString();
        }
    }
}
