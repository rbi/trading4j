package de.voidnode.trading4j.server;

import java.net.Socket;
import java.util.Optional;

import de.voidnode.trading4j.api.BasicExpertAdvisorFactory;
import de.voidnode.trading4j.api.Broker;
import de.voidnode.trading4j.api.ExpertAdvisor;
import de.voidnode.trading4j.api.Indicator;
import de.voidnode.trading4j.api.TrendIndicatorFactory;
import de.voidnode.trading4j.domain.MarketDirection;
import de.voidnode.trading4j.domain.environment.TradingEnvironmentInformation;
import de.voidnode.trading4j.domain.marketdata.impl.DatedCandleStick;
import de.voidnode.trading4j.domain.marketdata.impl.FullMarketData;
import de.voidnode.trading4j.domain.orders.BasicPendingOrder;
import de.voidnode.trading4j.domain.timeframe.M1;
import de.voidnode.trading4j.server.oio.OioServer;

/**
 * A server that is build using the trading4j library.
 * 
 * <p>
 * This server is used for testing trading4j.
 * </p>
 * 
 * @author Raik Bieniek
 */
class TestServer {

    private Thread serverThread;

    /**
     * Starts the server.
     */
    TestServer() {
        start();
    }

    private void start() {
        final TradingServerBuilder builder = new TradingServerBuilder();
        builder.trendIndicators(new TestIndicators());
        builder.expertAdvisors(new TestExpertAdvisors());
        final OioServer server = builder.build();

        serverThread = new Thread(() -> server.start());
        serverThread.start();

        waitForStartupFinished();
    }

    /**
     * Stops the Server.
     */
    public void stop() {
        serverThread.interrupt();
    }

    private void waitForStartupFinished() {
        for (int i = 0; i < 10; i++) {
            waitShortly();
            if (isServerReady()) {
                return;
            }
        }
        stop();
        throw new RuntimeException("The trading server did not finish startup after 1 second.");
    }

    private boolean isServerReady() {
        try (Socket s = new Socket("localhost", 6474)) {
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    private void waitShortly() {
        try {
            Thread.sleep(100);
        } catch (final InterruptedException e) {
            throw new RuntimeException("Could not wait for " + 100 + " ms.", e);
        }
    }

    /**
     * Test indicators that are tried to be accessed over the network from the {@link TradingServerAT}.
     */
    private static class TestIndicators implements TrendIndicatorFactory {

        @Override
        public Optional<Indicator<MarketDirection, DatedCandleStick<M1>>> newIndicatorByNumber(
                final int indicatorNumber) {

            if (indicatorNumber == 100) {
                return Optional.of(new DummyIndicator<>());
            }
            return Optional.empty();
        }
    }

    /**
     * Test expert advisors that are tried to be accessed over the network from the {@link TradingServerAT}.
     */
    private static class TestExpertAdvisors implements BasicExpertAdvisorFactory {

        @Override
        public Optional<ExpertAdvisor<FullMarketData<M1>>> newExpertAdvisor(final int expertAdvisorNumber,
                final Broker<BasicPendingOrder> broker, final TradingEnvironmentInformation environment) {
            if (expertAdvisorNumber == 229) {
                return Optional.of(new OrderOnEveryTickExpertAdvisor<>(broker));
            }
            return Optional.empty();
        }

    }

}
