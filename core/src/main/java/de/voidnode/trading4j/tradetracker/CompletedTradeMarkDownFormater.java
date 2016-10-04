package de.voidnode.trading4j.tradetracker;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static java.util.stream.IntStream.range;

import de.voidnode.trading4j.domain.monetary.Money;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.trades.CompletedTrade;
import de.voidnode.trading4j.domain.trades.TradeEvent;

/**
 * Formats {@link CompletedTrade}s as <a href="http://pandoc.org/README.html#pandocs-markdown">Pandoc markdown</a>.
 * 
 * @author Raik Bieniek
 */
public class CompletedTradeMarkDownFormater {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSSSS");

    private final ZoneId timeZone;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param timeZone
     *            The time zone that should be used for dates.
     */
    public CompletedTradeMarkDownFormater(final ZoneId timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * Formats a completed trade as Pandoc markdown.
     * 
     * @param trade
     *            The trade to format.
     * @return The Pandoc markdown formated completed trade.
     */
    public String format(final CompletedTrade trade) {
        final StringBuilder builder = new StringBuilder();
        addHeadLine(trade, builder);
        addOverview(trade, builder);
        addEvents(trade, builder);
        return builder.toString();
    }

    private void addHeadLine(final CompletedTrade trade, final StringBuilder builder) {
        builder.append("# Trade ");
        builder.append(trade.getType());
        builder.append(" ");
        builder.append(trade.getSymbol());
        builder.append(" ");
        builder.append(trade.getVolume());
        builder.append(" ");
        builder.append(toStringWithSign(trade.getRelativeProfit()));
        builder.append("\n\n");
    }

    private void addOverview(final CompletedTrade trade, final StringBuilder builder) {
        final String absoluteProfit = moneyToStringWithSign(trade.getAbsoluteProfit());
        builder.append("## Overview\n\n");
        builder.append("--------------- ");
        appendDivider(absoluteProfit.length(), builder);
        builder.append("\nType            ");
        builder.append(trade.getType());
        builder.append("\nCondition       ");
        builder.append(trade.getExecutionCondition());
        builder.append("\nSymbol          ");
        builder.append(trade.getSymbol());
        builder.append("\nVolume          ");
        builder.append(trade.getVolume());
        builder.append("\nProfit Relative ");
        builder.append(toStringWithSign(trade.getRelativeProfit()));
        builder.append("\nProfit Absolute ");
        builder.append(absoluteProfit);
        builder.append("\nSpread          ");
        builder.append(trade.getSpread());
        builder.append("\n--------------- ");
        appendDivider(absoluteProfit.length(), builder);
        builder.append("\n\n");

    }

    private void addEvents(final CompletedTrade trade, final StringBuilder builder) {
        builder.append("## Events\n\n");
        builder.append("Time                      Type                         Price       T/P       S/L Reason\n");
        builder.append("------------------------- ------------------------ --------- --------- --------- ");
        appendDividerForLongestReason(trade, builder);
        builder.append("\n");
        for (TradeEvent event : trade.getEvents()) {
            appendEvent(event, builder);
        }
    }

    private void appendDividerForLongestReason(final CompletedTrade trade, final StringBuilder builder) {
        // add as least as many dividers as are needed for the heading "Reason"
        int longest = 6;
        for (final TradeEvent event : trade.getEvents()) {
            final int current = event.getReason().length();
            if (current > longest) {
                longest = current;
            }
        }
        appendDivider(longest, builder);
    }

    private void appendDivider(final int count, final StringBuilder builder) {
        range(0, count).forEach(i -> builder.append("-"));
    }

    private void appendEvent(final TradeEvent event, final StringBuilder builder) {
        builder.append(event.getTime().atZone(timeZone).format(FORMATTER));
        builder.append(" ");
        appendWithPadding(event.getType().toString(), 24, false, builder);
        builder.append(" ");

        if (event.getPrice().isPresent()) {
            appendPriceWithPadding(event.getPrice().get(), builder);
            builder.append(" ");
        } else {
            builder.append("          ");
        }
        if (event.getCloseConditions().isPresent()) {
            appendPriceWithPadding(event.getCloseConditions().get().getTakeProfit(), builder);
            builder.append(" ");
            appendPriceWithPadding(event.getCloseConditions().get().getStopLoose(), builder);
            builder.append(" ");
        } else {
            builder.append("                    ");
        }
        builder.append(event.getReason());
        builder.append("\n");
    }

    private void appendPriceWithPadding(final Price price, final StringBuilder builder) {
        appendWithPadding(price.toString(), 9, true, builder);
    }

    private void appendWithPadding(final String toAppend, final int count, final boolean padLeft,
            final StringBuilder builder) {
        if (!padLeft) {
            builder.append(toAppend);
        }
        range(0, count - toAppend.length()).forEach(i -> builder.append(" "));
        if (padLeft) {
            builder.append(toAppend);
        }
    }

    private String toStringWithSign(final Optional<Price> price) {
        return price.map(p -> p.toStringWithSign()).orElse("canceled");
    }


    private String moneyToStringWithSign(final Optional<Money> money) {
        return money.map(m -> m.toStringWithSign()).orElse("canceled");
    }
}
