package de.voidnode.trading4j.functionality.expertadvisor;

import static java.util.Arrays.asList;

import de.voidnode.trading4j.api.ExpertAdvisor;
import de.voidnode.trading4j.domain.marketdata.MarketData;
import de.voidnode.trading4j.domain.marketdata.impl.BasicMarketData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.inOrder;

/**
 * Checks if {@link MarketDataDistributor} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class MarketDataDistributorTest {

    private final MarketData candle1 = new BasicMarketData(1.0);
    private final MarketData candle2 = new BasicMarketData(2.0);

    @Mock
    private ExpertAdvisor<MarketData> input1;
    @Mock
    private ExpertAdvisor<MarketData> input2;

    private MarketDataDistributor<MarketData> cut;

    /**
     * Sets up the class under test.
     */
    @Before
    public void setUpCutAndTests() {
        cut = new MarketDataDistributor<>(asList(input1, input2));
    }

    /**
     * The cut passes received {@link MarketData}s to all {@link ExpertAdvisor}s passed in the constructor in the order
     * they where passed in the constructor.
     */
    @Test
    public void distributesCandleSticksToTheExpertAdvisorsInTheOrderPassedInTheConstructor() {
        cut.newData(candle1);
        cut.newData(candle2);

        final InOrder inOrder = inOrder(input1, input2);
        inOrder.verify(input1).newData(candle1);
        inOrder.verify(input2).newData(candle1);
        inOrder.verify(input1).newData(candle2);
        inOrder.verify(input2).newData(candle2);
        inOrder.verifyNoMoreInteractions();
    }
}
