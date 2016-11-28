package de.voidnode.trading4j.moneymanagement.basic;

import java.util.Currency;

import de.voidnode.trading4j.domain.ForexSymbol;
import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.VolumeUnit;
import de.voidnode.trading4j.domain.monetary.AccuratePrice;
import de.voidnode.trading4j.domain.monetary.Money;
import de.voidnode.trading4j.domain.monetary.Price;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Checks if {@link DefaultMoneyManagement} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultMoneyManagementTest {

    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Currency USD = Currency.getInstance("USD");
    private static final ForexSymbol SYMBOL_EURUSD = new ForexSymbol(EUR, USD);

    private static final Price SOME_PRICE = new Price(1.0);
    private static final Volume SOME_STEP_SIZE = new Volume(1, VolumeUnit.BASE);

    @Mock
    private OneTradePerCurrency currencyBlocker;

    @Mock
    private RiskMoneyProvider moneyProvider;

    @Mock
    private PipetteValueCalculator pipetteValueCalculator;

    @Mock
    private VolumeCalculator volumeCalculator;

    @Mock
    private VolumeStepSizeRounder volumeStepSizeRounder;

    @Mock
    private ExchangeRateStore exchangeRateStore;

    @InjectMocks
    private DefaultMoneyManagement cut;

    /**
     * Sets up the default behavior for the mocks.
     */
    @Before
    public void setUpMocks() {
        when(currencyBlocker.isTradingAllowed(any())).thenReturn(true);
        when(volumeCalculator.calculateVolumeForTrade(any(), any(), any())).thenReturn(new Volume(1, VolumeUnit.LOT));
    }

    /**
     * The cut provides {@link Volume} when trading isn't blocked for the {@link ForexSymbol}.
     */
    @Test
    public void providesVolumeWhenTradingIsntBlocked() {
        when(currencyBlocker.isTradingAllowed(new ForexSymbol("EURUSD"))).thenReturn(true);

        assertThat(cut.requestVolume(SYMBOL_EURUSD, SOME_PRICE, SOME_PRICE, SOME_STEP_SIZE)).isPresent();

        verify(currencyBlocker).isTradingAllowed(new ForexSymbol("EURUSD"));
        verify(currencyBlocker).blockCurrencies(new ForexSymbol("EURUSD"));
        verifyNoMoreInteractions(currencyBlocker);
    }

    /**
     * The cut provides no {@link Volume} when trading is blocked for the {@link ForexSymbol}.
     */
    @Test
    public void notProvidingVolumeWhenTradingIsBlocked() {
        when(currencyBlocker.isTradingAllowed(new ForexSymbol("EURUSD"))).thenReturn(false);

        assertThat(cut.requestVolume(SYMBOL_EURUSD, SOME_PRICE, SOME_PRICE, SOME_STEP_SIZE)).isEmpty();

        verify(currencyBlocker).isTradingAllowed(new ForexSymbol("EURUSD"));
        verifyNoMoreInteractions(currencyBlocker);
    }

    /**
     * When lent volume is returned, trading for the current {@link ForexSymbol} is allowed again.
     */
    @Test
    public void unblocksCurrenciesWhenVolumeIsReturned() {
        cut.requestVolume(SYMBOL_EURUSD, SOME_PRICE, SOME_PRICE, SOME_STEP_SIZE).get().releaseVolume();

        verify(currencyBlocker).unblockCurrencies(SYMBOL_EURUSD);
    }

    /**
     * The cut passes the correct values to the calculating classes.
     */
    @Test
    public void passesCorrectValuesToCalculationClases() {
        // return correct output values only when correct input values are supplied.
        when(moneyProvider.calculateMoneyToRisk(new Money(1234, 0, "JPY"))).thenReturn(new Money(123, 4, "EUR"));
        when(pipetteValueCalculator.calculatePipetteValue(currency("JPY"), new ForexSymbol("CHFJPY"), new Price(123)))
                .thenReturn(new AccuratePrice(42.0));
        when(volumeCalculator.calculateVolumeForTrade(new AccuratePrice(42.0), new Price(4231),
                new Money(123, 4, "EUR"))).thenReturn(new Volume(5318, VolumeUnit.MICRO_LOT));
        when(volumeStepSizeRounder.round(new Volume(5318, VolumeUnit.MICRO_LOT), new Volume(128, VolumeUnit.MICRO_LOT)))
                .thenReturn(new Volume(5085, VolumeUnit.MICRO_LOT));

        cut.updateBalance(new Money(1234, 0, currency("JPY")));

        assertThat(cut.requestVolume(new ForexSymbol("CHFJPY"), new Price(123), new Price(4231),
                new Volume(128, VolumeUnit.MICRO_LOT)).get().getVolume())
                        .isEqualTo(new Volume(5085, VolumeUnit.MICRO_LOT));
    }

    /**
     * The cut passes exchange rates to the exchange rate store.
     */
    @Test
    public void passesExchangeRateToStore() {
        cut.updateExchangeRate(SYMBOL_EURUSD, SOME_PRICE);

        verify(exchangeRateStore).updateExchangeRate(SYMBOL_EURUSD, SOME_PRICE);
    }

    private Currency currency(final String currency) {
        return Currency.getInstance(currency);
    }
}
