package de.voidnode.trading4j.tradetracker;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static java.time.ZoneOffset.UTC;
import static java.util.Arrays.asList;

import de.voidnode.trading4j.domain.ForexSymbol;
import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.trades.CompletedTrade;
import de.voidnode.trading4j.domain.trades.TradeEvent;

import static de.voidnode.trading4j.domain.VolumeUnit.MINI_LOT;
import static de.voidnode.trading4j.domain.orders.ExecutionCondition.LIMIT;
import static de.voidnode.trading4j.domain.orders.OrderType.BUY;
import static de.voidnode.trading4j.domain.trades.TradeEventType.CLOSE_CONDITIONS_CHANGED;
import static de.voidnode.trading4j.domain.trades.TradeEventType.PENDING_ORDER_OPENED;
import static de.voidnode.trading4j.domain.trades.TradeEventType.PENDING_ORDER_PLACED;
import static de.voidnode.trading4j.domain.trades.TradeEventType.TRADE_CLOSED;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link CompletedTradeMarkDownFormater} works as expected.
 * 
 * @author Raik Bieniek
 */
public class CompletedTradeMarkDownFormaterIT {

    private final CompletedTradeMarkDownFormater cut = new CompletedTradeMarkDownFormater(ZoneOffset.UTC);

    /**
     * Checks that a completed trade is formated as expected.
     * 
     * @throws Exception
     *             When reading the should file fails which is not expected.
     */
    @Test
    public void case1() throws Exception {
        final LocalDate baseTime = LocalDate.of(2015, 9, 13);

        final TradeEvent placeEvent = new TradeEvent(PENDING_ORDER_PLACED,
                baseTime.atTime(11, 14, 53, 539210000).toInstant(UTC), "Short reason", new Price(15868105),
                new Price(174000), new Price(169015));
        final TradeEvent openEvent = new TradeEvent(PENDING_ORDER_OPENED,
                baseTime.atTime(11, 16, 10, 5210000).toInstant(UTC),
                "long long long long long long long long long reason", new Price(169615));
        final TradeEvent changeEvent = new TradeEvent(CLOSE_CONDITIONS_CHANGED,
                baseTime.atTime(11, 19, 26, 693920000).toInstant(UTC), "some reason", new Price(15868105),
                new Price(15868105));
        final TradeEvent closeEvent = new TradeEvent(TRADE_CLOSED, baseTime.atTime(11, 52, 03, 49240000).toInstant(UTC),
                "other reason", new Price(175140));

        final CompletedTrade trade = new CompletedTrade(BUY, LIMIT, new ForexSymbol("EURUSD"),
                new Volume(123, MINI_LOT), new Price(183), asList(placeEvent, openEvent, changeEvent, closeEvent));

        final String is = cut.format(trade);
        final String should = readFile("/example-completed-trade1.md");
        assertThat(is).isEqualTo(should);
    }

    private String readFile(final String file) throws URISyntaxException, IOException {
        final URI resource = CompletedTradeMarkDownFormaterIT.class.getResource(file).toURI();
        return new String(Files.readAllBytes(Paths.get(resource)));
    }
}
