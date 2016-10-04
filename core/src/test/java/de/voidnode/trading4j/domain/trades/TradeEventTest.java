package de.voidnode.trading4j.domain.trades;

import java.time.Instant;

import de.voidnode.trading4j.domain.monetary.Price;

import static de.voidnode.trading4j.domain.trades.TradeEventType.CLOSE_CONDITIONS_CHANGED;
import static de.voidnode.trading4j.domain.trades.TradeEventType.PENDING_ORDER_CANCELD;
import static de.voidnode.trading4j.domain.trades.TradeEventType.PENDING_ORDER_OPENED;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link TradeEvent} works as expected.
 * 
 * @author Raik Bieniek
 */
public class TradeEventTest {

    private static final Instant SOME_TIME = Instant.EPOCH.plusMillis(5000);
    private static final Instant OTHER_TIME = Instant.EPOCH.plusMillis(1000);

    /**
     * The cut can be constructed without a {@link Price} and without close limits if they are irrelevant for the event.
     */
    @Test
    public void canBeConstructedWithoutAPriceAndWithoutLimits() {
        assertThat(new TradeEvent(CLOSE_CONDITIONS_CHANGED, SOME_TIME, "some reason", new Price(1), new Price(2))
                .getPrice()).isEmpty();
        assertThat(new TradeEvent(PENDING_ORDER_CANCELD, SOME_TIME, "some reason").getCloseConditions()).isEmpty();
        assertThat(new TradeEvent(PENDING_ORDER_OPENED, SOME_TIME, "some reason", new Price(10), new Price(20),
                new Price(30)).getCloseConditions().get().getTakeProfit()).isEqualTo(new Price(20));
    }

    /**
     * A {@link TradeEvent} is only equal to other trade events that have the same values.
     */
    @Test
    public void equalsOnlyOtherTradeEventsWithSameValues() {
        final TradeEvent cut = new TradeEvent(PENDING_ORDER_OPENED, SOME_TIME, "some reason", new Price(1),
                new Price(2), new Price(3));

        assertThat(cut).isEqualTo(new TradeEvent(PENDING_ORDER_OPENED, SOME_TIME, "some reason", new Price(1),
                new Price(2), new Price(3)));

        assertThat(cut).isNotEqualTo(new TradeEvent(PENDING_ORDER_OPENED, SOME_TIME, "some reason", new Price(1),
                new Price(2), new Price(4)));
        assertThat(cut).isNotEqualTo(new TradeEvent(PENDING_ORDER_OPENED, OTHER_TIME, "some reason", new Price(1),
                new Price(2), new Price(3)));
        assertThat(cut).isNotEqualTo(new TradeEvent(PENDING_ORDER_OPENED, SOME_TIME, "other reason", new Price(1),
                new Price(2), new Price(3)));
        assertThat(cut).isNotEqualTo(new TradeEvent(PENDING_ORDER_CANCELD, SOME_TIME, "some reason", new Price(1),
                new Price(2), new Price(3)));

        assertThat(cut).isNotEqualTo(null);
        assertThat(cut).isNotEqualTo("not a trade event");
    }
}
