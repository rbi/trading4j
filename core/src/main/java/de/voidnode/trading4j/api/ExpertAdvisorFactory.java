package de.voidnode.trading4j.api;

import java.util.Optional;

import de.voidnode.trading4j.domain.TimeFrame.M1;
import de.voidnode.trading4j.domain.environment.TradingEnvironmentInformation;
import de.voidnode.trading4j.domain.marketdata.FullMarketData;
import de.voidnode.trading4j.domain.orders.PendingOrder;
import de.voidnode.trading4j.expertadvisorfactory.DefaultExpertAdvisorFactory;

/**
 * Creates {@link AccountingExpertAdvisor}s identified by numbers.
 * 
 * <p>
 * If you need to implement this interface consider using {@link DefaultExpertAdvisorFactory} and implement the simpler
 * {@link BasicExpertAdvisorFactory} instead.
 * </p>
 * 
 * <p>
 * Initial data that is passed to the {@link AccountingExpertAdvisor} is usually historic data. This is done so that
 * {@link AccountingExpertAdvisor}s can initialize themselves before live data is received.
 * {@link TradingEnvironmentInformation#getNonHistoricTime()} can used to determine when received data should be
 * considered live data.
 * </p>
 * 
 * <p>
 * The prices of {@link FullMarketData} that is passed in is the raw data from the broker. It is the responsibility of
 * the implementor to handle special fees like markup or commissions (see
 * {@link TradingEnvironmentInformation#getSpecialFeesInformation()}.
 * </p>
 * 
 * @author Raik Bieniek
 */
public interface ExpertAdvisorFactory {

    /**
     * Creates a new instance of a well-known {@link ExpertAdvisor} denoted by its number.
     * 
     * @param expertAdvisorNumber
     *            The number of the {@link ExpertAdvisor} that should be created.
     * @param broker
     *            The broker where the {@link ExpertAdvisor} should send its orders to.
     * @param moneyManagement
     *            Used to manage the amount of money that is invested in each trade.
     * @param environment
     *            Information about the trading environment.
     * @return The {@link AccountingExpertAdvisor} if an {@link AccountingExpertAdvisor} is known for this number or an
     *         <code>empty</code> {@link Optional} if not.
     */
    Optional<AccountingExpertAdvisor<FullMarketData<M1>>> newExpertAdvisor(final int expertAdvisorNumber,
            final Broker<PendingOrder> broker, final MoneyManagement moneyManagement,
            final TradingEnvironmentInformation environment);
}
