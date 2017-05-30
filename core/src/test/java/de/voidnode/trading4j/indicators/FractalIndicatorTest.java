package de.voidnode.trading4j.indicators;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.marketdata.CandleStick;
import de.voidnode.trading4j.domain.timeframe.M1;

import static de.voidnode.trading4j.testutils.CandleStickStreams.candleStickStream;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests if {@link FractalIndicator} works as expected.
 * 
 * <p>
 * For a description of the ASCII charts see {@link de.voidnode.trading4j.testutils.TrendAsciiRepresentation}
 * </p>
 * 
 * @author Raik Bieniek
 */
@SuppressWarnings("unchecked")
public class FractalIndicatorTest {

    private static final double SOME_PRICE = 1.0;
    private static final Optional<MarketDirection> UP = Optional.of(MarketDirection.UP);
    private static final Optional<MarketDirection> DOWN = Optional.of(MarketDirection.DOWN);
    private static final Optional<MarketDirection> UNKNOWN = Optional.empty();

    private final Indicator<MarketDirection, CandleStick<M1>> upFractals = new FractalIndicator<>(MarketDirection.UP);
    private final Indicator<MarketDirection, CandleStick<M1>> downFractals = new FractalIndicator<>(MarketDirection.DOWN);

    ///////////////////
    /// Up Fractals ///
    ///////////////////

    /**
     * The following constellation has a single up fractal at position 3/5.
     * 
     * <pre>
     *     ^
     *     |
     *     | |
     *   | | | |
     * | |   | |
     * | |   |
     * | |
     *   |
     * </pre>
     */
    @Test
    public void upFractalPositiveCase1() {
        final Stream<CandleStick<M1>> candleData = candleStickStream(new double[][] {
                // open, high, low, close
                { 1.095, 1.11, 1.09, 1.096 }, // 0
                { 1.096, 1.12, 1.08, 1.115 }, // 1
                { 1.115, 1.14, 1.12, 1.127 }, // 2
                { 1.127, 1.13, 1.11, 1.113 }, // 3
                { 1.113, 1.12, 1.10, 1.103 }, // 4
        });

        final List<Optional<MarketDirection>> fractals = candleData.map((stick) -> upFractals.indicate(stick))
                .collect(Collectors.toList());
        assertThat(fractals).containsExactly(UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UP);
    }

    /**
     * The following constellation has two up fractals at position 3/9 and 7/9.
     * 
     * <pre>
     *             ^
     *     ^       |
     *     |       | |
     *     | |   | | | |
     *   | | | | |   | |
     * | |   | |       |
     * | |   |
     * | |
     *   |
     * </pre>
     */
    @Test
    public void upFractalPositiveCase2() {
        final Stream<CandleStick<M1>> candleData = candleStickStream(new double[][] {
                // open, high, low, close
                { 1.095, 1.11, 1.09, 1.096 }, // 0
                { 1.096, 1.12, 1.08, 1.115 }, // 1
                { 1.115, 1.14, 1.12, 1.127 }, // 2
                { 1.127, 1.13, 1.11, 1.113 }, // 3
                { 1.113, 1.12, 1.10, 1.103 }, // 4
                { 1.103, 1.13, 1.11, 1.126 }, // 5
                { 1.126, 1.15, 1.12, 1.133 }, // 6
                { 1.133, 1.14, 1.11, 1.117 }, // 7
                { 1.117, 1.13, 1.10, 1.124 }, // 8
        });

        final List<Optional<MarketDirection>> fractals = candleData.map((stick) -> upFractals.indicate(stick))
                .collect(Collectors.toList());
        assertThat(fractals).containsExactly(UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UP, UNKNOWN, UNKNOWN, UNKNOWN, UP);
    }

    /**
     * The following constellation has a single up fractal at position 3/5.
     * 
     * <pre>
     *     ^
     *     |
     *     |   |
     * |   | | |
     * | |   | |
     * | |   |
     * | |
     *   |
     * </pre>
     */
    @Test
    public void upFractalPositiveCase3() {
        final Stream<CandleStick<M1>> candleData = candleStickStream(new double[][] {
                // open, high, low, close
                { 1.095, 1.12, 1.09, 1.096 }, // 0
                { 1.096, 1.11, 1.08, 1.115 }, // 1
                { 1.115, 1.14, 1.12, 1.127 }, // 2
                { 1.127, 1.12, 1.11, 1.113 }, // 3
                { 1.113, 1.13, 1.10, 1.103 }, // 4
        });

        final List<Optional<MarketDirection>> fractals = candleData.map((stick) -> upFractals.indicate(stick))
                .collect(Collectors.toList());
        assertThat(fractals).containsExactly(UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UP);
    }

    /**
     * The following constellation has no up fractals.
     * 
     * <pre>
     * |   | |
     * | | | | |
     * | |   | |
     * | |   |
     * | |
     *   |
     * </pre>
     */
    @Test
    public void upFractalNegativeCase1() {
        final Stream<CandleStick<M1>> candleData = candleStickStream(new double[][] {
                // open, high, low, close
                { 1.095, 1.13, 1.09, 1.096 }, // 0
                { 1.096, 1.12, 1.08, 1.115 }, // 1
                { 1.115, 1.13, 1.12, 1.127 }, // 2
                { 1.127, 1.13, 1.11, 1.113 }, // 3
                { 1.113, 1.12, 1.10, 1.103 }, // 4
        });

        final List<Optional<MarketDirection>> fractals = candleData.map((stick) -> upFractals.indicate(stick))
                .collect(Collectors.toList());
        assertThat(fractals).containsExactly(UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN);
    }

    /////////////////////
    /// Down Fractals ///
    /////////////////////

    /**
     * The following constellation has a single down fractal at position 3/5.
     * 
     * <pre>
     *       |
     *   |   | |
     * | |   | |
     * | |   |
     * | | | |
     *   | |
     *     |
     *     v
     * </pre>
     */
    @Test
    public void downFractalsPositiveCase1() {
        final Stream<CandleStick<M1>> candleData = candleStickStream(new double[][] {
                // open, high, low, close
                { 1.094, 1.10, 1.08, 1.096 }, // 0
                { 1.096, 1.11, 1.07, 1.081 }, // 1
                { 1.081, 1.08, 1.06, 1.080 }, // 2
                { 1.080, 1.12, 1.08, 1.107 }, // 3
                { 1.107, 1.11, 1.10, 1.108 }, // 4
        });

        final List<Optional<MarketDirection>> fractals = candleData.map((stick) -> downFractals.indicate(stick))
                .collect(Collectors.toList());
        assertThat(fractals).containsExactly(UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, DOWN);
    }

    /**
     * The following constellation has two down fractals at position 3/9 and 7/9.
     * 
     * <pre>
     *       |
     *   |   | |   |
     * | |   | | | |
     * | |   |   | |     |
     * | | | |   | |     |
     *   | |       | | |
     *     |         | |
     *     v         |
     *               v
     * </pre>
     */
    @Test
    public void downFractalsPositiveCase2() {
        final Stream<CandleStick<M1>> candleData = candleStickStream(new double[][] {
                // open, high, low, close
                { 1.094, 1.10, 1.08, 1.096 }, // 0
                { 1.096, 1.11, 1.07, 1.081 }, // 1
                { 1.081, 1.08, 1.06, 1.080 }, // 2
                { 1.080, 1.12, 1.08, 1.107 }, // 3
                { 1.107, 1.11, 1.10, 1.108 }, // 4
                { 1.108, 1.10, 1.08, 1.095 }, // 5
                { 1.095, 1.11, 1.07, 1.070 }, // 6
                { 1.070, 1.07, 1.05, 1.064 }, // 7
                { 1.064, 1.07, 1.06, 1.069 }, // 8
                { 1.081, 1.09, 1.08, 1.082 }, // 9
        });

        final List<Optional<MarketDirection>> fractals = candleData.map((stick) -> downFractals.indicate(stick))
                .collect(Collectors.toList());
        assertThat(fractals).containsExactly(UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, DOWN, UNKNOWN, UNKNOWN, UNKNOWN,
                UNKNOWN, DOWN);
    }

    /**
     * The following constellation has a single down fractal at position 3/5.
     * 
     * <pre>
     *       |
     *   |   | |
     * | |   | |
     * | |     |
     * | | |   |
     * |   |
     *     |
     *     v
     * </pre>
     */
    @Test
    public void downFractalsPositiveCase3() {
        final Stream<CandleStick<M1>> candleData = candleStickStream(new double[][] {
                // open, high, low, close
                { SOME_PRICE, SOME_PRICE, 1.07, SOME_PRICE }, // 0
                { SOME_PRICE, SOME_PRICE, 1.08, SOME_PRICE }, // 1
                { SOME_PRICE, SOME_PRICE, 1.06, SOME_PRICE }, // 2
                { SOME_PRICE, SOME_PRICE, 1.10, SOME_PRICE }, // 3
                { SOME_PRICE, SOME_PRICE, 1.08, SOME_PRICE }, // 4
        });

        final List<Optional<MarketDirection>> fractals = candleData.map((stick) -> downFractals.indicate(stick))
                .collect(Collectors.toList());
        assertThat(fractals).containsExactly(UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, DOWN);
    }

    /**
     * The following constellation has no no down fractals.
     * 
     * <pre>
     *       |
     *   |   | |
     * | |   | |
     * | |   | |
     * | | | | |
     *   | |   |
     * </pre>
     */
    @Test
    public void downFractalsNegativeCase1() {
        final Stream<CandleStick<M1>> candleData = candleStickStream(new double[][] {
                // open, high, low, close
                { 1.094, 1.10, 1.08, 1.096 }, // 0
                { 1.096, 1.11, 1.07, 1.081 }, // 1
                { 1.081, 1.08, 1.07, 1.080 }, // 2
                { 1.080, 1.12, 1.08, 1.107 }, // 3
                { 1.107, 1.11, 1.07, 1.108 }, // 4
        });

        final List<Optional<MarketDirection>> fractals = candleData.map((stick) -> downFractals.indicate(stick))
                .collect(Collectors.toList());
        assertThat(fractals).containsExactly(UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN);
    }
}
