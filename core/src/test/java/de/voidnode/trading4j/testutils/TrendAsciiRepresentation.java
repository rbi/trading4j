package de.voidnode.trading4j.testutils;

/**
 * This interfaces documentation shows the visual representation of market data with ASCII symbols.
 * 
 * <h1 id="contents">Contents</h1>
 * <ol>
 * <li><a href="#candleStickChart">Candle Stick Chart</a></li>
 * <li><a href="#fractals">Fractals</a></li>
 * <li><a href="#trends">Trends</a></li>
 * </ol>
 * 
 * <h1 id="candleStickChart">Candle Stick Chart</h1>
 * <p>
 * A candle stick has the following representation.
 * </p>
 * 
 * <pre>
 *           |                        |
 *           +                        -
 *           +                        -
 *           |                        |
 *           |                        |
 * a bullish candle stick   a bearish candle stick
 * </pre>
 * 
 * <ul>
 * <li>The highest point of the top <code>|</code> symbol marks the high price of the candle.</li>
 * <li>The lowest point of the lowest <code>|</code> symbol marks the low price of the candle.</li>
 * <li>In a bullish candle stick the lowest point of the <code>+</code> symbols marks the opening price and the highest
 * point marks the closing price.</li>
 * <li>In a bearish candle stick the highest point of the <code>-</code> symbols marks the opening price and the lowest
 * point marks the closing price.</li>
 * </ul>
 * 
 * <p>
 * When the opening and the closing price do not matter they can be omitted.
 * </p>
 * 
 * <pre>
 *           |
 *           |
 *           |
 *           |
 *           |
 * a pseudo-candle stick with undefined opening and closing price.
 * </pre>
 * 
 * <p>
 * Multiple candle sticks separated by an empty column form a candle stick chart.
 * </p>
 * 
 * <pre>
 *           |    
 *           | |  
 *           | + - |
 * |         + + - -
 * | |       + | | -
 * - | |   | +   | |
 * - + - | + +     |
 * - + - | + |
 * | | - - +
 *   | | - +
 *     | | 
 *     |
 * </pre>
 * 
 * <h1 id="fractals">Fractals</h1>
 * <ul>
 * <li>Up-Fractals are visualized by a <code>^</code> symbol above a candle stick.</li>
 * <li>Down-Fractals are visualized by a <code>v</code> symbol below a candle stick.</li>
 * </ul>
 * 
 * <pre>
 *           ^
 *           |    
 *           | |  
 *           | + - |
 * |         + + - -
 * | |       + | | -
 * - | |   | +   | |
 * - + - | + +     |
 * - + - | + |
 * | | - - +
 *   | | - +
 *     | | 
 *     |
 *     v
 * </pre>
 * 
 * <h1 id="trends">Trands</h1>
 * <p>
 * Trends are visualized by a <code>+</code> symbol when they are up, a <code>-</code> symbol when they are down and a
 * <code>?</code> symbol when they are unknown. Each trend can optionally have an id that is explained in a legend below
 * the chart.
 * </p>
 * 
 * <pre>
 *              ^
 *              |    
 *              | |  
 *              | + - |
 *    |         + + - -
 *    | |       + | | -
 *    - | |   | +   | |
 *    - + - | + +     |
 *    - + - | + |
 *    | | - - +
 *      | | - +
 *        | | 
 *        |
 *        v
 * a: + - + - + - + - +
 * b: ? ? ? + + + + - -
 * 
 * a: dummy trend
 * b: some other trend
 * </pre>
 * 
 * @author Raik Bieniek
 */
public interface TrendAsciiRepresentation {

}
