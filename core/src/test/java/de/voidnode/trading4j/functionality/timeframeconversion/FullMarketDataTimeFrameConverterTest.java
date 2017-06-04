package de.voidnode.trading4j.functionality.timeframeconversion;

import java.time.Instant;

import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.marketdata.impl.DatedCandleStick;
import de.voidnode.trading4j.domain.marketdata.impl.FullMarketData;
import de.voidnode.trading4j.domain.marketdata.impl.MutableFullMarketData;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.timeframe.TimeFrame;

import static de.voidnode.trading4j.domain.VolumeUnit.LOT;
import static de.voidnode.trading4j.domain.VolumeUnit.MICRO_LOT;
import static de.voidnode.trading4j.domain.VolumeUnit.MINI_LOT;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Checks that {@link FullMarketDataTimeFrameConverter} aggregates additional fields correctly.
 * 
 * <p>
 * This test relays on the functionality of the base class {@link TimeFrameConverter} without testing it. The
 * functionallity of the base class is tested in {@link TimeFrameConveterTest}.
 * </p>
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class FullMarketDataTimeFrameConverterTest {

    private final MutableFullMarketData<TimeFrame> baseData = new MutableFullMarketData<>().setOpen(1.0).setHigh(1.0)
            .setLow(1.0).setClose(1.0);

    private final Instant firstTime = Instant.EPOCH.plusSeconds(600);

    @Mock
    private TimeFrame inputTimeFrame;

    @Mock
    private TimeFrame outputTimeFrame;

    private FullMarketDataTimeFrameConverter<FullMarketData<TimeFrame>, TimeFrame, TimeFrame> cut;

    /**
     * Sets up the class under test.
     */
    @Before
    public void setUpCut() {
        when(inputTimeFrame.instantOfNextFrame(firstTime.minusSeconds(60))).thenReturn(firstTime);
        when(inputTimeFrame.instantOfNextFrame(firstTime)).thenReturn(firstTime.plusSeconds(60));
        when(inputTimeFrame.instantOfNextFrame(firstTime.plusSeconds(60))).thenReturn(firstTime.plusSeconds(120));
        when(inputTimeFrame.instantOfNextFrame(firstTime.plusSeconds(120))).thenReturn(firstTime.plusSeconds(180));
        when(inputTimeFrame.instantOfNextFrame(firstTime.plusSeconds(180))).thenReturn(firstTime.plusSeconds(240));
        when(inputTimeFrame.instantOfNextFrame(firstTime.plusSeconds(240))).thenReturn(firstTime.plusSeconds(300));

        when(outputTimeFrame.areInSameTimeFrame(firstTime.minusSeconds(60), firstTime)).thenReturn(false);
        when(outputTimeFrame.areInSameTimeFrame(firstTime, firstTime.plusSeconds(60))).thenReturn(true);
        when(outputTimeFrame.areInSameTimeFrame(firstTime.plusSeconds(60), firstTime.plusSeconds(120)))
                .thenReturn(false);
        when(outputTimeFrame.areInSameTimeFrame(firstTime.plusSeconds(120), firstTime.plusSeconds(180)))
                .thenReturn(true);
        when(outputTimeFrame.areInSameTimeFrame(firstTime.plusSeconds(180), firstTime.plusSeconds(240)))
                .thenReturn(true);
        when(outputTimeFrame.areInSameTimeFrame(firstTime.plusSeconds(240), firstTime.plusSeconds(300)))
                .thenReturn(false);

        cut = new FullMarketDataTimeFrameConverter<>(inputTimeFrame, outputTimeFrame);
    }

    /**
     * The cut should aggregate additional fields of a {@link FullMarketData} that are not present in a
     * {@link DatedCandleStick} correctly.
     */
    @Test
    public void aggregatesAdditionalFieldsCorrectly() {
        final FullMarketData<TimeFrame> notAggregated = baseData.setTime(firstTime.minusSeconds(60)).setVolume(23, LOT)
                .setTickCount(3000).setSpread(new Price(89510)).toImmutableFullMarketData();

        final FullMarketData<TimeFrame> firstAggregate1 = baseData.setTime(firstTime).setVolume(11, MINI_LOT)
                .setTickCount(12).setSpread(new Price(10)).toImmutableFullMarketData();
        final FullMarketData<TimeFrame> firstAggregate2 = baseData.setTime(firstTime.plusSeconds(60))
                .setVolume(53, MINI_LOT).setTickCount(82).setSpread(new Price(20)).toImmutableFullMarketData();

        cut.aggregate(notAggregated);
        cut.aggregate(firstAggregate1);
        final FullMarketData<TimeFrame> aggregated1 = cut.aggregate(firstAggregate2).get();

        // Volume and tick count are added up but the average spread is build.
        assertThat(aggregated1.getVolume()).isEqualTo(new Volume(64, MINI_LOT));
        assertThat(aggregated1.getTickCount()).isEqualTo(94);
        assertThat(aggregated1.getSpread()).isEqualTo(new Price(15));

        // Check that the second aggregated stick does not carry over any values of the first.

        final FullMarketData<TimeFrame> secondAggregate1 = baseData.setTime(firstTime.plusSeconds(120))
                .setVolume(28, MICRO_LOT).setTickCount(29).setSpread(new Price(15)).toImmutableFullMarketData();
        final FullMarketData<TimeFrame> secondAggregate2 = baseData.setTime(firstTime.plusSeconds(180))
                .setVolume(32, MICRO_LOT).setTickCount(82).setSpread(new Price(92)).toImmutableFullMarketData();
        final FullMarketData<TimeFrame> secondAggregate3 = baseData.setTime(firstTime.plusSeconds(240))
                .setVolume(67, MICRO_LOT).setTickCount(210).setSpread(new Price(26)).toImmutableFullMarketData();

        cut.aggregate(secondAggregate1);
        cut.aggregate(secondAggregate2);
        final FullMarketData<TimeFrame> aggregated2 = cut.aggregate(secondAggregate3).get();

        assertThat(aggregated2.getVolume()).isEqualTo(new Volume(127, MICRO_LOT));
        assertThat(aggregated2.getTickCount()).isEqualTo(321);
        assertThat(aggregated2.getSpread()).isEqualTo(new Price(44));
    }
}
