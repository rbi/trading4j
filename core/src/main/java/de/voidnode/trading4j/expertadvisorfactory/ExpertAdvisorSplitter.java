package de.voidnode.trading4j.expertadvisorfactory;

import de.voidnode.trading4j.api.AccountingExpertAdvisor;
import de.voidnode.trading4j.api.ExpertAdvisor;
import de.voidnode.trading4j.api.MoneyManagement;
import de.voidnode.trading4j.domain.TimeFrame.M1;
import de.voidnode.trading4j.domain.marketdata.FullMarketData;
import de.voidnode.trading4j.domain.monetary.Money;
import de.voidnode.trading4j.domain.monetary.Price;

/**
 * Splits the information an {@link AccountingExpertAdvisor} receives and transmits it to the instances that are
 * interested in them.
 * 
 * @author Raik Bieniek
 */
class ExpertAdvisorSplitter implements AccountingExpertAdvisor<FullMarketData<M1>> {

    private final ExpertAdvisor<FullMarketData<M1>> expertAdvisor;
    private final MoneyManagement moneyManagement;
    private final StrategyMoneyManagement<FullMarketData<M1>> strategyMoneyManagement;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param expertAdvisor
     *            The basic {@link ExpertAdvisor} that will receive new market data.
     * @param strategyMoneyManagement
     *            The {@link StrategyMoneyManagement} that will receive updates on the account currency to quote
     *            currency symbol updates.
     * @param moneyManagement
     *            The money management that will receive balance change events.
     */
    ExpertAdvisorSplitter(final ExpertAdvisor<FullMarketData<M1>> expertAdvisor,
            final StrategyMoneyManagement<FullMarketData<M1>> strategyMoneyManagement,
            final MoneyManagement moneyManagement) {
        this.expertAdvisor = expertAdvisor;
        this.strategyMoneyManagement = strategyMoneyManagement;
        this.moneyManagement = moneyManagement;
    }

    @Override
    public void newData(final FullMarketData<M1> marketData) {
        expertAdvisor.newData(marketData);
    }

    @Override
    public void balanceChanged(final Money money) {
        moneyManagement.updateBalance(money);
    }

    @Override
    public void accountCurrencyPriceChanged(final Price newPrice) {
        strategyMoneyManagement.updateAccountCurrencyExchangeRateChanged(newPrice);
    }
}
