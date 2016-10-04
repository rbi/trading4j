package de.voidnode.trading4j.expertadvisorfactory;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;

import de.voidnode.trading4j.api.OrderFilter;
import de.voidnode.trading4j.domain.TimeFrame.M1;
import de.voidnode.trading4j.domain.marketdata.DatedCandleStick;
import de.voidnode.trading4j.domain.marketdata.MutableFullMarketData;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Checks if {@link HistoricDataOrderFilter} works as expected.
 * 
 * @author Raik Bieniek
 */
public class HistoricDataOrderFilterTest {

    private final MutableFullMarketData<M1> testMarketData = new MutableFullMarketData<M1>().setOpen(1.0).setHigh(1.0)
            .setLow(1.0).setClose(1.0);
    private final Instant firstNonHistoricData = Instant.now();

    /**
     * Trading is blocked as long as historic market data is received.
     */
    @Test
    public void blocksTradingForHistoricMarketData() {
        final OrderFilter<DatedCandleStick<M1>> cut = new HistoricDataOrderFilter<>(firstNonHistoricData);
        testMarketData.setTime(firstNonHistoricData.minus(20, HOURS));
        
        cut.updateMarketData(testMarketData.toImmutableDatedCandleStick());
        assertThat(cut.filterOrder(mock(BasicPendingOrder.class))).isPresent();

        testMarketData.setTime(firstNonHistoricData.minusNanos(1));
        cut.updateMarketData(testMarketData.toImmutableDatedCandleStick());
        assertThat(cut.filterOrder(mock(BasicPendingOrder.class))).isPresent();
    }

    /**
     * Trading is allowed as soon as first live market data is received.
     */
    @Test
    public void allowsTradeAsSoonAsFirstNonHistoricMarketDataIsReceived() {
        OrderFilter<DatedCandleStick<M1>> cut = new HistoricDataOrderFilter<>(firstNonHistoricData);
        
        testMarketData.setTime(firstNonHistoricData);
        cut.updateMarketData(testMarketData.toImmutableDatedCandleStick());
        assertThat(cut.filterOrder(mock(BasicPendingOrder.class))).isEmpty();

        cut = new HistoricDataOrderFilter<>(firstNonHistoricData);
        testMarketData.setTime(firstNonHistoricData.plus(20, HOURS));
        cut.updateMarketData(testMarketData.toImmutableDatedCandleStick());
        assertThat(cut.filterOrder(mock(BasicPendingOrder.class))).isEmpty();

        testMarketData.setTime(firstNonHistoricData.plus(10, MINUTES));
        cut.updateMarketData(testMarketData.toImmutableDatedCandleStick());
        assertThat(cut.filterOrder(mock(BasicPendingOrder.class))).isEmpty();
    }
}
