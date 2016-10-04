package de.voidnode.trading4j.domain.trades;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

import de.voidnode.trading4j.domain.Ratio;
import de.voidnode.trading4j.domain.monetary.Price;

import static de.voidnode.trading4j.domain.RatioUnit.PERCENT;
import static de.voidnode.trading4j.domain.orders.ExecutionCondition.LIMIT;
import static de.voidnode.trading4j.domain.orders.OrderType.BUY;
import static de.voidnode.trading4j.domain.orders.OrderType.SELL;
import static de.voidnode.trading4j.domain.trades.TradeEventType.PENDING_ORDER_OPENED;
import static de.voidnode.trading4j.domain.trades.TradeEventType.PENDING_ORDER_PLACED;
import static de.voidnode.trading4j.domain.trades.TradeEventType.TRADE_CLOSED;
import static de.voidnode.trading4j.testutils.assertions.Assertions.assertThat;

import org.assertj.core.data.Offset;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link BasicCompletedTrade} works as expected.
 *
 * @author Raik Bieniek
 */
public class BasicCompletedTradeTest {

    private static final Offset<Double> ALLOWED_OFFSET = Offset.offset(0.000001);

    private static final Instant SOME_TIME = Instant.ofEpochMilli(59801);

    private static final TradeEvent SOME_PENDING_ORDER_PLACED = new TradeEvent(PENDING_ORDER_PLACED, SOME_TIME,
            "placed by EA", new Price(10), new Price(5), new Price(5));
    private static final TradeEvent OTHER_PENDING_ORDER_PLACED = new TradeEvent(PENDING_ORDER_PLACED, SOME_TIME,
            "placed by EA", new Price(20), new Price(5), new Price(5));
    private static final TradeEvent SOME_PENDING_ORDER_OPENED = new TradeEvent(PENDING_ORDER_OPENED, SOME_TIME,
            "entry price reached", new Price(10), new Price(15), new Price(5));
    private static final TradeEvent SOME_ORDER_CLOSED = new TradeEvent(TRADE_CLOSED, SOME_TIME, "take profit reached",
            new Price(15), new Price(15), new Price(5));
    private static final TradeEvent OTHER_ORDER_CLOSED = new TradeEvent(TRADE_CLOSED, SOME_TIME, "take profit reached",
            new Price(5), new Price(15), new Price(5));

    /**
     * A {@link BasicCompletedTrade} instance is only equal to other {@link BasicCompletedTrade} instances with exactly
     * the same values.
     */
    @Test
    public void equalsOnlyOtherInstancesWithSameValues() {
        final BasicCompletedTrade cut = new BasicCompletedTrade(BUY, LIMIT,
                asList(SOME_PENDING_ORDER_PLACED, SOME_PENDING_ORDER_OPENED, SOME_ORDER_CLOSED));
        assertThat(cut).isEqualTo(new BasicCompletedTrade(BUY, LIMIT,
                asList(SOME_PENDING_ORDER_PLACED, SOME_PENDING_ORDER_OPENED, SOME_ORDER_CLOSED)));

        assertThat(cut).isNotEqualTo(new BasicCompletedTrade(BUY, LIMIT,
                asList(OTHER_PENDING_ORDER_PLACED, SOME_PENDING_ORDER_OPENED, SOME_ORDER_CLOSED)));
        assertThat(cut).isNotEqualTo(
                new BasicCompletedTrade(BUY, LIMIT, asList(SOME_PENDING_ORDER_PLACED, SOME_PENDING_ORDER_OPENED)));
        assertThat(cut).isNotEqualTo(new BasicCompletedTrade(SELL, LIMIT,
                asList(SOME_PENDING_ORDER_PLACED, SOME_PENDING_ORDER_OPENED, SOME_ORDER_CLOSED)));

        assertThat(cut).isNotEqualTo("not a basic completed trade");
        assertThat(cut).isNotEqualTo(null);
    }
    
    
    /**
     * The relative profit is calculated correctly.
     */
    @Test
    public void relativeProfitIsCalculatedCorrectlyWhenTradeWasOpened() {
        assertThat(new BasicCompletedTrade(BUY, LIMIT, asList(SOME_PENDING_ORDER_OPENED, SOME_ORDER_CLOSED))
                .getRelativeProfit().get()).isEqualTo(new Price(5));
        assertThat(new BasicCompletedTrade(BUY, LIMIT, asList(SOME_PENDING_ORDER_OPENED, OTHER_ORDER_CLOSED))
                .getRelativeProfit().get()).isEqualTo(new Price(-5));
        
        assertThat(new BasicCompletedTrade(SELL, LIMIT, asList(SOME_PENDING_ORDER_OPENED, SOME_ORDER_CLOSED))
                .getRelativeProfit().get()).isEqualTo(new Price(-5));
        assertThat(new BasicCompletedTrade(SELL, LIMIT, asList(SOME_PENDING_ORDER_OPENED, OTHER_ORDER_CLOSED))
                .getRelativeProfit().get()).isEqualTo(new Price(5));
    }
    
    /**
     * When the trade was never opened or closed, the relative profit is empty.
     */
    @Test
    public void relativeProfitIsEmptyWhenTradeWasNeverOpenedOrClosed() {
        assertThat(new BasicCompletedTrade(BUY, LIMIT, asList(SOME_PENDING_ORDER_OPENED)).getRelativeProfit()).isEmpty();
        assertThat(new BasicCompletedTrade(BUY, LIMIT, asList(SOME_ORDER_CLOSED)).getRelativeProfit()).isEmpty();
    }

    /**
     * The rate of return is calculated correctly.
     */
    @Test
    public void rateOfReturnIsCalculatedCorrectlyWhenTradeWasOpened() {
        assertThat(new BasicCompletedTrade(BUY, LIMIT, asList(SOME_PENDING_ORDER_OPENED, SOME_ORDER_CLOSED))
                .getRateOfReturn().get()).isApproximatelyEqualTo(new Ratio(150, PERCENT), ALLOWED_OFFSET);
        assertThat(new BasicCompletedTrade(BUY, LIMIT, asList(SOME_PENDING_ORDER_OPENED, OTHER_ORDER_CLOSED))
                .getRateOfReturn().get()).isApproximatelyEqualTo(new Ratio(50, PERCENT), ALLOWED_OFFSET);
        
        assertThat(new BasicCompletedTrade(SELL, LIMIT, asList(SOME_PENDING_ORDER_OPENED, SOME_ORDER_CLOSED))
                .getRateOfReturn().get()).isApproximatelyEqualTo(new Ratio(50, PERCENT), ALLOWED_OFFSET);
        assertThat(new BasicCompletedTrade(SELL, LIMIT, asList(SOME_PENDING_ORDER_OPENED, OTHER_ORDER_CLOSED))
                .getRateOfReturn().get()).isApproximatelyEqualTo(new Ratio(150, PERCENT), ALLOWED_OFFSET);
    }

    /**
     * When the trade was never opened or closed, the rate of return is empty.
     */
    @Test
    public void rateOfReturnIsEmptyWhenTradeWasNeverOpenedOrClosed() {
        assertThat(new BasicCompletedTrade(BUY, LIMIT, asList(SOME_PENDING_ORDER_OPENED)).getRateOfReturn()).isEmpty();
        assertThat(new BasicCompletedTrade(BUY, LIMIT, asList(SOME_ORDER_CLOSED)).getRateOfReturn()).isEmpty();
    }
    
    /**
     * When the original {@link List} of {@link TradeEvent}s that was passed in the constructor changes this does not
     * interfere with the {@link List} of {@link TradeEvent}s in the cut.
     */
    @Test
    public void changesOnOriginalEventListDoNotInterfereWithCut() {
        final List<TradeEvent> origList = new ArrayList<>();
        origList.add(SOME_PENDING_ORDER_OPENED);
        final BasicCompletedTrade cut = new BasicCompletedTrade(BUY, LIMIT, origList);

        origList.add(SOME_ORDER_CLOSED);

        assertThat(cut.getEvents()).containsExactly(SOME_PENDING_ORDER_OPENED);
    }

    /**
     * When operations that would change the {@link List} of {@link TradeEvent}s are called on the {@link List}, an
     * exception is thrown.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void callingMutableOperationsOnTheListOfTradeEventsResultsInAnException() {
        new BasicCompletedTrade(BUY, LIMIT, new ArrayList<>()).getEvents().add(OTHER_ORDER_CLOSED);
    }
}
