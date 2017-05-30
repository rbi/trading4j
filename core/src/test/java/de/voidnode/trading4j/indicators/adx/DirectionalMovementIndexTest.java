package de.voidnode.trading4j.indicators.adx;

import java.util.Optional;

import static java.util.Optional.empty;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.Ratio;
import de.voidnode.trading4j.domain.marketdata.CandleStick;
import de.voidnode.trading4j.domain.timeframe.M1;

import static de.voidnode.trading4j.testutils.assertions.Assertions.assertThat;

import org.assertj.core.data.Offset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Checks if the {@link DirectionalMovementIndex} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class DirectionalMovementIndexTest {

    private static final Optional<Ratio> SOME_RATIO = optRatio(10);

    private static final CandleStick<M1> SOME_CANDLE = new CandleStick<>(10, 10, 10, 10);

    private static final Offset<Double> ALLOWED_OFFSET = Offset.offset(0.00001);

    @Mock
    private Indicator<Ratio, CandleStick<M1>> averagePlusDi;

    @Mock
    private Indicator<Ratio, CandleStick<M1>> averageMinusDi;

    private DirectionalMovementIndex<CandleStick<M1>> cut;

    /**
     * Instantiates the class under test.
     */
    @Before
    public void setUpCut() {
        cut = new DirectionalMovementIndex<>(averagePlusDi, averageMinusDi);
    }

    /**
     * As long as at least one input is empty, the indicator is empty.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void returnsEmptyAsLongAsOneInputIsEmpty() {
        when(averagePlusDi.indicate(any())).thenReturn(empty(), SOME_RATIO, SOME_RATIO);
        when(averageMinusDi.indicate(any())).thenReturn(SOME_RATIO, empty(), SOME_RATIO);

        assertThat(cut.indicate(SOME_CANDLE)).isEmpty();
        assertThat(cut.indicate(SOME_CANDLE)).isEmpty();
        assertThat(cut.indicate(SOME_CANDLE)).isPresent();
    }

    /**
     * The {@link CandleStick} that is passed as input is passed through to the DI indicators.
     */
    @Test
    public void candlesArePassedThroughToTheIndicators() {
        when(averagePlusDi.indicate(any())).thenReturn(empty());
        when(averageMinusDi.indicate(any())).thenReturn(empty());

        cut.indicate(SOME_CANDLE);

        verify(averagePlusDi).indicate(SOME_CANDLE);
        verify(averageMinusDi).indicate(SOME_CANDLE);
    }

    /**
     * The cut calculates the DMI correctly based on the input values.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void calculatesDMICorrectly() {
        when(averagePlusDi.indicate(any())).thenReturn(optRatio(0.5), optRatio(0.568), optRatio(0.921), optRatio(0.201));
        when(averageMinusDi.indicate(any())).thenReturn(optRatio(0.4), optRatio(0.958), optRatio(0.484), optRatio(0.69));

        assertThat(cut.indicate(SOME_CANDLE).get()).isApproximatelyEqualTo(new Ratio(0.111111111), ALLOWED_OFFSET);
        assertThat(cut.indicate(SOME_CANDLE).get()).isApproximatelyEqualTo(new Ratio(0.255570118), ALLOWED_OFFSET);
        assertThat(cut.indicate(SOME_CANDLE).get()).isApproximatelyEqualTo(new Ratio(0.3110320285), ALLOWED_OFFSET);
        assertThat(cut.indicate(SOME_CANDLE).get()).isApproximatelyEqualTo(new Ratio(0.54882155), ALLOWED_OFFSET);
    }

    private static Optional<Ratio> optRatio(final double ratio) {
        return Optional.of(new Ratio(ratio));
    }
}
