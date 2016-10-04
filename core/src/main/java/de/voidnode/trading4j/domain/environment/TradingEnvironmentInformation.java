package de.voidnode.trading4j.domain.environment;

import java.time.Instant;
import java.util.Currency;

import de.voidnode.trading4j.domain.ForexSymbol;
import de.voidnode.trading4j.domain.marketdata.FullMarketData;

/**
 * Various trading environment information.
 * 
 * @author Raik Bieniek
 */
public class TradingEnvironmentInformation {

    private final AccountInformation accountInformation;
    private final ForexSymbol tradeSymbol;
    private final ForexSymbol accountSymbol;
    private final SpecialFeesInformation specialFeesInformation;
    private final Instant nonHistoricTime;
    private final VolumeConstraints volumeConstraints;

    /**
     * Initializes an instance.
     *
     * @param accountInformation
     *            see {@link #getAccountInformation()}
     * @param tradeSymbol
     *            see {@link #getTradeSymbol()}
     * @param accountSymbol
     *            see {@link #getAccountSymbol()}
     * @param volumeConstraints
     *            see {@link #getVolumeConstraints()}
     * @param specialFeesInformation
     *            see {@link #getSpecialFeesInformation()}
     * @param nonHistoricTime
     *            see {@link #getNonHistoricTime()}
     */
    public TradingEnvironmentInformation(final AccountInformation accountInformation, final ForexSymbol tradeSymbol,
            final ForexSymbol accountSymbol, final SpecialFeesInformation specialFeesInformation,
            final Instant nonHistoricTime, final VolumeConstraints volumeConstraints) {
        this.accountInformation = accountInformation;
        this.tradeSymbol = tradeSymbol;
        this.accountSymbol = accountSymbol;
        this.specialFeesInformation = specialFeesInformation;
        this.volumeConstraints = volumeConstraints;
        this.nonHistoricTime = nonHistoricTime;
    }

    /**
     * Information about the account at the broker.
     * 
     * @return Information about the account.
     */
    public AccountInformation getAccountInformation() {
        return accountInformation;
    }

    /**
     * The forex pair that should be traded by the expert advisor.
     * 
     * @return The symbol of the traded forex pair.
     */
    public ForexSymbol getTradeSymbol() {
        return tradeSymbol;
    }

    /**
     * The forex pair that exchanges the account currency to the quote {@link Currency} of the {@link #getTradeSymbol()}
     * .
     * 
     * <p>
     * If the account currency is one of the currencies of the {@link #getTradeSymbol()}, than this value is equal to
     * {@link #getTradeSymbol()}.
     * </p>
     * 
     * @return The forex pair
     */
    public ForexSymbol getAccountSymbol() {
        return accountSymbol;
    }

    /**
     * Information about special kinds of fees that the broker charges besides the spread.
     * 
     * @return The information.
     */
    public SpecialFeesInformation getSpecialFeesInformation() {
        return specialFeesInformation;
    }

    /**
     * The first time at which sent market data should be treated as live data instead of historic data.
     * 
     * <p>
     * Initially historic market data is sent so that the expert advisor can initialize its internal state. This time is
     * the time of the first {@link FullMarketData} that is not historic but the current live data.
     * </p>
     * 
     * @return The time of the first non-historic {@link FullMarketData}.
     */
    public Instant getNonHistoricTime() {
        return nonHistoricTime;
    }

    /**
     * Constraints on the volume that can be set in orders.
     * 
     * @return The constraints
     */
    public VolumeConstraints getVolumeConstraints() {
        return volumeConstraints;
    }

    @Override
    public String toString() {
        return "TradingEnvironmentInformation [accountInformation=" + accountInformation + ", tradeSymbol="
                + tradeSymbol + ", accountSymbol=" + accountSymbol + ", specialFeesInformation="
                + specialFeesInformation + ", nonHistoricTime=" + nonHistoricTime + ", volumeConstraints="
                + volumeConstraints + "]";
    }

}
