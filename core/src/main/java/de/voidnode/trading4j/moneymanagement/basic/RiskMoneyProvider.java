package de.voidnode.trading4j.moneymanagement.basic;

import de.voidnode.trading4j.api.UnrecoverableProgrammingError;
import de.voidnode.trading4j.domain.Ratio;
import de.voidnode.trading4j.domain.monetary.Money;

/**
 * Calculates the money that should be risk for a single trade.
 * 
 * @author Raik Bieniek
 */
class RiskMoneyProvider {

    private final Ratio balanceToRiskRatio;

    /**
     * Initializes an instance with all requiered configuration.
     * 
     * @param balanceToRiskRatio
     *            The ratio of the available balance that should be risk per trade.
     * @throws UnrecoverableProgrammingError
     *             When <code>balanceToRiskRatio</code> is less than 0 or greater than 1.
     */
    RiskMoneyProvider(final Ratio balanceToRiskRatio) throws UnrecoverableProgrammingError {
        if (balanceToRiskRatio.asBasic() < 0 || balanceToRiskRatio.asBasic() > 1) {
            throw new UnrecoverableProgrammingError(new IllegalArgumentException(
                    "The ratio of balance to risk must between 0% and 100% but was: " + balanceToRiskRatio + "."));
        }
        this.balanceToRiskRatio = balanceToRiskRatio;
    }

    /**
     * Calculates the {@link Money} that can be risk for a single trade for a given balance.
     * 
     * @param balance
     *            The absolute balance that is currently available.
     * @return The money that can be risk for a single trade.
     */
    public Money calculateMoneyToRisk(final Money balance) {
        return new Money((long) (balance.asRawValue() * balanceToRiskRatio.asBasic()), balance.getCurrency());
    }
}
