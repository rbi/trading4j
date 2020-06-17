package de.voidnode.trading4j.domain.marketdata;

import de.voidnode.trading4j.domain.monetary.Price;

/**
 * Describes time depended information about a security like its price.
 * 
 * <p>
 * There are many information about a security that is time depended. The most common one is its {@link #getClose()
 * price} which is part of this basic information. Other information are not needed in all cases and are therefore
 * available via separate interfaces which extend this basic one. Examples for these interfaces are {@link WithOhlc} or
 * {@link WithVolume}. With this separation it is possible to fine-grained define the data needed for an algorithm. E.g
 * an indicator that needs a stream of price, volume and spread for the security could be defined like this.
 * </p>
 * 
 * <pre>
 * class MyIndicator&lt;M extends MarketData &amp; WithSpread &amp; WithVolume&gt; implements Indicator&lt;MarketDirection, M&gt; {
 *     Optional&lt;MarketDirection&gt; indicate(M md) {
 *         return doSomeThing(md.getClose(), md.getSpread(), md.getVolume());
 *     }
 * }
 * </pre>
 * 
 * <p>
 * Code that needs to create {@link MarketData} instances with different amount of information can use the
 * implementation in the <code>impl</code> sub-package.
 * </p>
 * 
 * <p>
 * Usually {@link MarketData} for a security is used as a stream. Each element contains the aggregated information for a
 * given time frame. The length of these time frames is usually expected to be the same for each element in the stream.
 * </p>
 * 
 * @author Raik Bieniek
 */
public interface MarketData {

    /**
     * The value at the end of the time period this market data aggregates.
     * 
     * @return the closing price
     */
    Price getClose();
}
