package de.voidnode.trading4j.server;

import java.io.IOException;

import de.voidnode.trading4j.api.ExpertAdvisor;
import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.marketdata.CandleStick;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.domain.monetary.PriceUnit;
import de.voidnode.trading4j.domain.orders.PendingOrder;

import static de.voidnode.trading4j.domain.MarketDirection.DOWN;
import static de.voidnode.trading4j.domain.MarketDirection.UP;
import static de.voidnode.trading4j.domain.VolumeUnit.LOT;
import static de.voidnode.trading4j.testutils.assertions.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * A system test that creates a trading server as users of this library would do it and checks if all parts work well
 * together.
 * 
 * <p>
 * A test server is created by using the {@link TestServer} class. This test server provides some dummy indicators and
 * expert advisors. This test checks if they can be accessed over the network.
 * </p>
 * 
 * @author Raik Bieniek
 */
public class TradingServerAT {

    /**
     * A short time to wait in milliseconds.
     */
    private static final long SHORT_WAIT_TIME = 250;

    /**
     * The time to wait for the server to answer in milliseconds.
     */
    private static final int READ_TIMEOUT = 500;

    /**
     * An exemplary time of a candle stick.
     */
    private static final long DUMMY_TIME = 1L;

    /**
     * An exemplary price for an asset.
     */
    private static final Price DUMMY_MARKET_PRICE = new Price(1.0);

    /**
     * An exemplary spread price.
     */
    private static final Price DUMMY_SPREAD_PRICE = new Price(0.001);

    /**
     * An exemplary volume for an asset.
     */
    private static final Volume DUMMY_VOLUME = new Volume(1, LOT);

    /**
     * An exemplary amount of trades (ticks) that happened in a time frame.
     */
    private static final long DUMMY_TICK_COUNT = 50;

    /**
     * An exemplary margin fee.
     */
    private static final Price DUMMY_MARGIN = new Price(0);

    /**
     * An exemplary commission fee.
     */
    private static final Price DUMMY_COMMISSION = new Price(0);

    /**
     * An exemplary forex pair to trade.
     */
    private static final String DUMMY_FOREX_PAIR = "EURUSD";

    /**
     * An exemplary name of a broker.
     */
    private static final String DUMMY_BROKER_NAME = "Backtest";

    /**
     * An exemplary account number of a trading account.
     */
    private static final long DUMMY_ACCOUNT_NUMBER = 42;

    /**
     * An exemplary currency of the account.
     */
    private static final String DUMMY_ACCOUNT_CURRENCY = "EUR";

    /**
     * The number of the dummy indicator.
     */
    private static final int DUMMY_INDICATOR_NUMBER = 100;

    /**
     * An exemplary trading account balance.
     */
    private static final long DUMMY_BALANCE = 100000;

    /**
     * A number in the range of indicators that is not assigned to any known {@link Indicator}.
     */
    private static final int UNKNOWN_INDICATOR_NUMBER = 120;

    /**
     * The number for the {@link ExpertAdvisor} that sends a {@link PendingOrder} on every incoming {@link CandleStick}
     * .
     */
    private static final int ORDER_EVERY_ON_CANDLE_EA_NUMBER = 229;

    /**
     * A number in the range of expert advisors that is not assigned to any known {@link ExpertAdvisor}.
     */
    private static final int UNKNOWN_EXPERT_ADVISOR_NUMBER = 240;

    /**
     * The type that indicates that an expert advisor is requested.
     */
    private static final int EXPERT_ADVISOR_TYPE = 0;

    /**
     * The type that indicates that a trend indicator is requested.
     */
    private static final int TREND_INDICATOR_TYPE = 1;

    private TestServer server;

    /**
     * Starts the trading server that should be tested.
     */
    @Before
    public void startServer() {
        server = new TestServer();
    }

    /**
     * Stops the trading server after the tests.
     */
    @After
    public void stopServer() {
        server.stop();
    }

    /**
     * When a client connects and request the dummy trend {@link Indicator}, the server should send trends based on it.
     * 
     * @throws IOException
     *             Not expected in the test.
     */
    @Test
    public void shouldSendDummyIndicatorDataWhenRequested() throws IOException {
        final TradingClient client = new TradingClient(READ_TIMEOUT);

        client.sendTradingAlgorithmNumber(TREND_INDICATOR_TYPE, DUMMY_INDICATOR_NUMBER);

        client.sendCandle(DUMMY_TIME, DUMMY_MARKET_PRICE, DUMMY_MARKET_PRICE, DUMMY_MARKET_PRICE, DUMMY_MARKET_PRICE);
        MarketDirection trend = client.reciveTrend();
        assertThat(trend).is(UP);

        client.sendCandle(DUMMY_TIME, DUMMY_MARKET_PRICE, DUMMY_MARKET_PRICE, DUMMY_MARKET_PRICE, DUMMY_MARKET_PRICE);
        trend = client.reciveTrend();
        assertThat(trend).is(DOWN);

        client.sendCandle(DUMMY_TIME, DUMMY_MARKET_PRICE, DUMMY_MARKET_PRICE, DUMMY_MARKET_PRICE, DUMMY_MARKET_PRICE);
        trend = client.reciveTrend();
        assertThat(trend).is(UP);

        client.disconnect();
    }

    /**
     * When a client connects and requests the expert advisor that sends an order on every candle stick the server
     * should send pending orders on every candle stick.
     * 
     * @throws IOException
     *             Not expected in the test.
     */
    @Test
    public void shouldSendOrdersBasedOnOrderOnEveryCandleStickWhenRequested() throws IOException {
        final TradingClient client = new TradingClient(READ_TIMEOUT);

        // Initialization
        client.sendTradingAlgorithmNumber(EXPERT_ADVISOR_TYPE, ORDER_EVERY_ON_CANDLE_EA_NUMBER);
        client.sendTradingEnvironmentInfo(DUMMY_BROKER_NAME, DUMMY_ACCOUNT_NUMBER, DUMMY_ACCOUNT_CURRENCY,
                DUMMY_FOREX_PAIR, DUMMY_FOREX_PAIR, DUMMY_MARGIN, DUMMY_COMMISSION, DUMMY_TIME, DUMMY_VOLUME,
                DUMMY_VOLUME, DUMMY_VOLUME);

        client.sendAccountCurrencyPriceChanged(DUMMY_MARKET_PRICE);
        client.reciveEventHandlingFinished();
        // first candle -> first order
        client.sendFatCandle(DUMMY_TIME, DUMMY_MARKET_PRICE, DUMMY_MARKET_PRICE, DUMMY_MARKET_PRICE, DUMMY_MARKET_PRICE,
                DUMMY_SPREAD_PRICE, DUMMY_VOLUME, DUMMY_TICK_COUNT);
        final PendingOrder order1 = client.recivePendingOrder();
        client.sendResponsePendingOrder(1);
        client.reciveEventHandlingFinished();

        // second candle but no order because money management prevents multiple orders for same currencies
        client.sendFatCandle(DUMMY_TIME, DUMMY_MARKET_PRICE, DUMMY_MARKET_PRICE, DUMMY_MARKET_PRICE, DUMMY_MARKET_PRICE,
                DUMMY_SPREAD_PRICE, DUMMY_VOLUME, DUMMY_TICK_COUNT);
        client.reciveEventHandlingFinished();

        // close first order -> money management accepts orders again
        client.sendPendingOrderExecuted(1, DUMMY_TIME, DUMMY_MARKET_PRICE);
        client.sendBalanceChanged(DUMMY_BALANCE);
        client.reciveEventHandlingFinished();
        client.reciveEventHandlingFinished();

        client.sendPendingOrderClosed(1, DUMMY_TIME, DUMMY_MARKET_PRICE);
        client.reciveEventHandlingFinished();

        // third candle -> second order
        client.sendFatCandle(DUMMY_TIME, DUMMY_MARKET_PRICE, DUMMY_MARKET_PRICE, DUMMY_MARKET_PRICE, DUMMY_MARKET_PRICE,
                DUMMY_SPREAD_PRICE, DUMMY_VOLUME, DUMMY_TICK_COUNT);
        client.recivePendingOrder();
        client.sendResponsePendingOrder(2);
        client.reciveEventHandlingFinished();

        client.disconnect();

        // The prices should be in plausible ranges
        final Price closePlusOneCent = DUMMY_MARKET_PRICE.plus(1, PriceUnit.MINOR);
        final Price closeMinusOneCent = DUMMY_MARKET_PRICE.minus(1, PriceUnit.MINOR);

        // some plausibility checks for the first order
        assertThat(order1.getCloseConditions().getTakeProfit()).isLessThan(closePlusOneCent)
                .isGreaterThan(closeMinusOneCent);
        assertThat(order1.getCloseConditions().getStopLoose()).isLessThan(closePlusOneCent)
                .isGreaterThan(closeMinusOneCent);
        assertThat(order1.getEntryPrice()).isLessThan(closePlusOneCent).isGreaterThan(closeMinusOneCent);
    }

    /**
     * When a client connects and requests an unknown indicator the server should terminate the connection.
     * 
     * <p>
     * Indicators id range from 0 to 127
     * </p>
     * 
     * @throws IOException
     *             Not expected in the test.
     */
    @Test
    public void shouldCloseTheConnectionWhenAnUnknownIndicatorWasRequested() throws IOException {
        final TradingClient client = new TradingClient(READ_TIMEOUT);

        client.sendTradingAlgorithmNumber(TREND_INDICATOR_TYPE, UNKNOWN_INDICATOR_NUMBER);
        waitShortly();
        client.serverShouldHaveTerminatedTheConnection();
    }

    /**
     * When a client connects and requests an unknown expert advisor the server should terminate the connection.
     * 
     * <p>
     * Indicators id range from 128 to 255
     * </p>
     * 
     * @throws IOException
     *             Not expected in the test.
     */
    @Test
    public void shouldCloseTheConnectionWhenAnUnknownExpertAdvisorWasRequested() throws IOException {
        final TradingClient client = new TradingClient(READ_TIMEOUT);

        client.sendTradingAlgorithmNumber(EXPERT_ADVISOR_TYPE, UNKNOWN_EXPERT_ADVISOR_NUMBER);
        client.sendTradingEnvironmentInfo(DUMMY_BROKER_NAME, DUMMY_ACCOUNT_NUMBER, DUMMY_ACCOUNT_CURRENCY,
                DUMMY_FOREX_PAIR, DUMMY_FOREX_PAIR, DUMMY_MARGIN, DUMMY_COMMISSION, DUMMY_TIME, DUMMY_VOLUME,
                DUMMY_VOLUME, DUMMY_VOLUME);

        waitShortly();
        client.serverShouldHaveTerminatedTheConnection();
    }

    private void waitShortly() {
        try {
            Thread.sleep(SHORT_WAIT_TIME);
        } catch (final InterruptedException e) {
            throw new RuntimeException("Could not wait for " + SHORT_WAIT_TIME + " ms.", e);
        }
    }
}
