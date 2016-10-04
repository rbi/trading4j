package de.voidnode.trading4j.domain.trades;

import java.time.Instant;

import static java.util.Arrays.asList;

import de.voidnode.trading4j.domain.ForexSymbol;
import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.monetary.Money;
import de.voidnode.trading4j.domain.monetary.Price;

import static de.voidnode.trading4j.domain.VolumeUnit.LOT;
import static de.voidnode.trading4j.domain.VolumeUnit.MICRO_LOT;
import static de.voidnode.trading4j.domain.VolumeUnit.MINI_LOT;
import static de.voidnode.trading4j.domain.orders.ExecutionCondition.LIMIT;
import static de.voidnode.trading4j.domain.orders.OrderType.BUY;
import static de.voidnode.trading4j.domain.orders.OrderType.SELL;
import static de.voidnode.trading4j.domain.trades.TradeEventType.PENDING_ORDER_OPENED;
import static de.voidnode.trading4j.domain.trades.TradeEventType.PENDING_ORDER_PLACED;
import static de.voidnode.trading4j.domain.trades.TradeEventType.TRADE_CLOSED;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link CompletedTrade} works as expected.
 * 
 * @author Raik Bieniek
 */
public class CompletedTradeTest {

    private static final Instant SOME_TIME = Instant.ofEpochMilli(59801);

    private static final TradeEvent SOME_PENDING_ORDER_PLACED = new TradeEvent(PENDING_ORDER_PLACED, SOME_TIME,
            "placed by EA", new Price(10), new Price(5), new Price(5));
    private static final TradeEvent SOME_PENDING_ORDER_OPENED = new TradeEvent(PENDING_ORDER_OPENED, SOME_TIME,
            "entry price reached", new Price(10), new Price(15), new Price(5));
    private static final TradeEvent SOME_ORDER_CLOSED = new TradeEvent(TRADE_CLOSED, SOME_TIME, "take profit reached",
            new Price(15), new Price(15), new Price(5));
    private static final TradeEvent OTHER_ORDER_CLOSED = new TradeEvent(TRADE_CLOSED, SOME_TIME, "take profit reached",
            new Price(9621), new Price(15), new Price(5));

    private static final ForexSymbol EURUSD = new ForexSymbol("EURUSD");
    private static final ForexSymbol USDJPY = new ForexSymbol("USDJPY");

    /**
     * The cut can calculates the absolute profit correctly.
     * 
     * <p>
     * The absolute profit is calculated by multiplying the relative profit with the traded volume. It is measured in
     * the quote currency of the traded symbol.
     * </p>
     * 
     */
    @Test
    public void absoluteProfitIsCalculatedCorrectly() {
        final CompletedTrade win = new CompletedTrade(BUY, LIMIT, EURUSD, new Volume(10, LOT), new Price(10),
                asList(SOME_PENDING_ORDER_OPENED, SOME_ORDER_CLOSED));
        assertThat(win.getAbsoluteProfit().get()).isEqualTo(new Money(50, 00, "USD"));

        final CompletedTrade win2 = new CompletedTrade(BUY, LIMIT, EURUSD, new Volume(3, MINI_LOT), new Price(10),
                asList(SOME_PENDING_ORDER_OPENED, OTHER_ORDER_CLOSED));
        assertThat(win2.getAbsoluteProfit().get()).isEqualTo(new Money(2883, 30, "USD"));

        final CompletedTrade loose = new CompletedTrade(SELL, LIMIT, USDJPY, new Volume(6, MICRO_LOT), new Price(10),
                asList(SOME_PENDING_ORDER_OPENED, SOME_ORDER_CLOSED));
        assertThat(loose.getAbsoluteProfit().get()).isEqualTo(new Money(-30, "JPY"));

        final CompletedTrade notClosed = new CompletedTrade(BUY, LIMIT, EURUSD, new Volume(10, LOT), new Price(10),
                asList(SOME_PENDING_ORDER_PLACED, SOME_PENDING_ORDER_OPENED));
        assertThat(notClosed.getAbsoluteProfit()).isEmpty();
    }

    /**
     * A {@link CompletedTrade} is only equal to other {@link CompletedTrade}s with the same values.
     */
    @Test
    public void equalsOnlyOtherCompleteTradesWithSameValues() {
        final CompletedTrade cut = new CompletedTrade(BUY, LIMIT, EURUSD, new Volume(10, LOT), new Price(10),
                asList(SOME_PENDING_ORDER_PLACED, OTHER_ORDER_CLOSED));

        assertThat(cut).isEqualTo(new CompletedTrade(BUY, LIMIT, EURUSD, new Volume(10, LOT), new Price(10),
                asList(SOME_PENDING_ORDER_PLACED, OTHER_ORDER_CLOSED)));

        assertThat(cut).isNotEqualTo(new CompletedTrade(BUY, LIMIT, USDJPY, new Volume(10, LOT), new Price(10),
                asList(SOME_PENDING_ORDER_PLACED, OTHER_ORDER_CLOSED)));
        assertThat(cut).isNotEqualTo(new CompletedTrade(BUY, LIMIT, EURUSD, new Volume(5, LOT), new Price(10),
                asList(SOME_PENDING_ORDER_PLACED, OTHER_ORDER_CLOSED)));
        assertThat(cut).isNotEqualTo(new CompletedTrade(BUY, LIMIT, EURUSD, new Volume(10, LOT), new Price(5280),
                asList(SOME_PENDING_ORDER_PLACED, OTHER_ORDER_CLOSED)));
        assertThat(cut).isNotEqualTo(new CompletedTrade(SELL, LIMIT, EURUSD, new Volume(10, LOT), new Price(10),
                asList(SOME_PENDING_ORDER_PLACED, OTHER_ORDER_CLOSED)));
        assertThat(cut).isNotEqualTo(new CompletedTrade(BUY, LIMIT, EURUSD, new Volume(10, LOT), new Price(10),
                asList(SOME_PENDING_ORDER_PLACED)));
        assertThat(cut).isNotEqualTo(
                new BasicCompletedTrade(BUY, LIMIT, asList(SOME_PENDING_ORDER_PLACED, OTHER_ORDER_CLOSED)));
        assertThat(cut).isNotEqualTo(null);
        assertThat(cut).isNotEqualTo("not a trade");
    }
}
