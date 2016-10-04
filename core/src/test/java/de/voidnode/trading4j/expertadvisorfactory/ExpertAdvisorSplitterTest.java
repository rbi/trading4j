package de.voidnode.trading4j.expertadvisorfactory;

import java.util.Currency;

import de.voidnode.trading4j.api.ExpertAdvisor;
import de.voidnode.trading4j.api.MoneyManagement;
import de.voidnode.trading4j.domain.TimeFrame.M1;
import de.voidnode.trading4j.domain.marketdata.FullMarketData;
import de.voidnode.trading4j.domain.monetary.Money;
import de.voidnode.trading4j.domain.monetary.Price;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

/**
 * Checks that {@link ExpertAdvisorSplitter} works as expected.
 * 
 * @author Raik Bieniek;
 */
@RunWith(MockitoJUnitRunner.class)
public class ExpertAdvisorSplitterTest {

    private static final Currency SOME_CURRENCY = Currency.getInstance("CHF");

    @Mock
    private ExpertAdvisor<FullMarketData<M1>> expertAdvisor;

    @Mock
    private MoneyManagement moneyManagement;

    @Mock
    private StrategyMoneyManagement<FullMarketData<M1>> strategyMoneyManagement;

    private ExpertAdvisorSplitter cut;

    @Mock
    private FullMarketData<M1> someMarketData;

    /**
     * Creates the class under test.
     */
    @Before
    public void createCut() {
        cut = new ExpertAdvisorSplitter(expertAdvisor, strategyMoneyManagement, moneyManagement);
    }

    /**
     * New market data is passed to the basic expert advisor.
     */
    @Test
    public void newMarketDataIsPassedToExpertAdvisor() {
        cut.newData(someMarketData);

        verify(expertAdvisor).newData(someMarketData);
    }

    /**
     * New account balance is passed to the money management.
     */
    @Test
    public void newAccountBalanceIsPassedToMoneyManagement() {
        cut.balanceChanged(new Money(1234, 56, SOME_CURRENCY));

        verify(moneyManagement).updateBalance(new Money(1234, 56, SOME_CURRENCY));
    }

    /**
     * The events for account currency exchange rate updates are passed to the strategy money management.
     */
    @Test
    public void accountCurrencyPriceChangedEventsAreSendToTheStrategyMoneyManagement() {
        cut.accountCurrencyPriceChanged(new Price(42));

        verify(strategyMoneyManagement).updateAccountCurrencyExchangeRateChanged(new Price(42));
    }

}
