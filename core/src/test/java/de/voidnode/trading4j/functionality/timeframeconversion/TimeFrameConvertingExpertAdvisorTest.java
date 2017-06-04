package de.voidnode.trading4j.functionality.timeframeconversion;

import java.util.Optional;

import de.voidnode.trading4j.api.ExpertAdvisor;
import de.voidnode.trading4j.domain.marketdata.impl.DatedCandleStick;
import de.voidnode.trading4j.domain.timeframe.M1;
import de.voidnode.trading4j.domain.timeframe.M15;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Checks if {@link TimeFrameConvertingExpertAdvisor} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class TimeFrameConvertingExpertAdvisorTest {

    @Mock
    private ExpertAdvisor<DatedCandleStick<M15>> inputExpertAdvisor;

    @Mock
    private TimeFrameConverter<DatedCandleStick<M1>, DatedCandleStick<M15>, M1, M15> converter;

    @InjectMocks
    private TimeFrameConvertingExpertAdvisor<DatedCandleStick<M1>, DatedCandleStick<M15>, M1, M15> cut;

    @Mock
    private DatedCandleStick<M1> someInput1;

    @Mock
    private DatedCandleStick<M1> someInput2;

    @Mock
    private DatedCandleStick<M1> someInput3;

    @Mock
    private DatedCandleStick<M15> someOutput1;

    @Mock
    private DatedCandleStick<M15> someOutput2;

    /**
     * Set up default behavior of the mocks.
     */
    @Before
    public void setUpMocks() {
        when(converter.aggregate(any())).thenReturn(Optional.empty());
    }

    /**
     * The cut passes all market data to the {@link TimeFrameConverter}.
     */
    @Test
    public void passesAllInputMarketDataToTimeFrameConverter() {
        cut.newData(someInput1);
        cut.newData(someInput2);

        verify(converter).aggregate(someInput1);
        verify(converter).aggregate(someInput2);
    }

    /**
     * The cut passes completely aggregated market data to the original {@link ExpertAdvisor}.
     */
    @Test
    public void passesOnlyCompletlyAggregateMarketDataToOriginalExpertAdvisor() {
        when(converter.aggregate(someInput1)).thenReturn(Optional.of(someOutput1));
        when(converter.aggregate(someInput3)).thenReturn(Optional.of(someOutput2));

        cut.newData(someInput1);
        cut.newData(someInput2);
        cut.newData(someInput3);

        verify(inputExpertAdvisor).newData(someOutput1);
        verify(inputExpertAdvisor).newData(someOutput2);
    }
}
