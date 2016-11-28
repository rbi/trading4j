package de.voidnode.trading4j.expertadvisorfactory;

import java.util.Optional;

import static java.util.Optional.empty;

import de.voidnode.trading4j.api.MoneyManagement;
import de.voidnode.trading4j.api.TraderNotifier;
import de.voidnode.trading4j.api.UsedVolumeManagement;
import de.voidnode.trading4j.domain.ForexSymbol;
import de.voidnode.trading4j.domain.Volume;
import de.voidnode.trading4j.domain.VolumeUnit;
import de.voidnode.trading4j.domain.monetary.Money;
import de.voidnode.trading4j.domain.monetary.Price;
import de.voidnode.trading4j.moneymanagement.SharedMoneyManagement;
import de.voidnode.trading4j.moneymanagement.SharedMoneyManagement.ReleasableMoneyManagement;

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
 * Checks if {@link SharedMoneyManagement} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class SharedMoneyManagementTest {

    private static final ForexSymbol SOME_SYMBOL = new ForexSymbol("EURUSD");
    private static final ForexSymbol OTHER_SYMBOL = new ForexSymbol("AUDCAD");
    private static final Volume SOME_VOLUME = new Volume(5, VolumeUnit.LOT);
    private static final Price SOME_PRICE = new Price(10);
    private static final Price OTHER_PRICE = new Price(2532);
    private static final Money SOME_MONEY = new Money(281, "EUR");

    @Mock
    private MoneyManagement moneyManagement;

    @Mock
    private TraderNotifier trader;

    @InjectMocks
    private SharedMoneyManagement cut;

    @Mock
    private UsedVolumeManagement someVolumeManagement;

    @Mock
    private UsedVolumeManagement otherVolumeManagement;

    /**
     * Sets the default behavior for the mocks.
     */
    @Before
    public void wiresUpMocks() {
        when(moneyManagement.requestVolume(SOME_SYMBOL, SOME_PRICE, SOME_PRICE, SOME_VOLUME))
                .thenReturn(opt(someVolumeManagement)).thenReturn(opt(otherVolumeManagement));
    }

    /**
     * When requested, the cut returns lent {@link Volume} to the original {@link MoneyManagement}.
     */
    @Test
    public void returnsLentVolumeWhenRequested() {
        final ReleasableMoneyManagement connection1 = cut.newConnection();
        final ReleasableMoneyManagement connection2 = cut.newConnection();
        connection1.requestVolume(SOME_SYMBOL, SOME_PRICE, SOME_PRICE, SOME_VOLUME);
        connection2.requestVolume(SOME_SYMBOL, SOME_PRICE, SOME_PRICE, SOME_VOLUME);

        connection1.realeaseAllAquieredVolume();

        verify(someVolumeManagement).releaseVolume();
        verifyNoMoreInteractions(otherVolumeManagement);
    }

    /**
     * The cut returns multiple requested {@link Volume}s when necessary.
     */
    @Test
    public void returnsMultipleVolumeWhenNecessary() {
        final ReleasableMoneyManagement connection = cut.newConnection();
        connection.requestVolume(SOME_SYMBOL, SOME_PRICE, SOME_PRICE, SOME_VOLUME);
        connection.requestVolume(SOME_SYMBOL, SOME_PRICE, SOME_PRICE, SOME_VOLUME);

        connection.realeaseAllAquieredVolume();

        verify(someVolumeManagement).releaseVolume();
        verify(otherVolumeManagement).releaseVolume();
    }

    /**
     * When volume is returned the trader is informed.
     */
    @Test
    public void logsUnexpectedEventWhenVolumeIsReturned() {
        final ReleasableMoneyManagement connection = cut.newConnection();
        connection.requestVolume(SOME_SYMBOL, SOME_PRICE, SOME_PRICE, SOME_VOLUME);

        connection.realeaseAllAquieredVolume();

        verify(trader).unexpectedEvent(any(String.class));
    }

    /**
     * When no volume needs to be returned, nothing needs to be logged.
     */
    @Test
    public void nothingIsLoggedWhenNoVolumeIsReturned() {
        when(moneyManagement.requestVolume(SOME_SYMBOL, SOME_PRICE, SOME_PRICE, SOME_VOLUME))
                .thenReturn(opt(someVolumeManagement)).thenReturn(empty()).thenReturn(opt(otherVolumeManagement));

        final ReleasableMoneyManagement connection1 = cut.newConnection();
        final ReleasableMoneyManagement connection2 = cut.newConnection();
        // volume was lent but the expert advisor returned it
        connection1.requestVolume(SOME_SYMBOL, SOME_PRICE, SOME_PRICE, SOME_VOLUME).get().releaseVolume();
        // no volume provided by the original instance
        connection1.requestVolume(SOME_SYMBOL, SOME_PRICE, SOME_PRICE, SOME_VOLUME);
        // volume was lent by another expert advisor
        connection2.requestVolume(SOME_SYMBOL, SOME_PRICE, SOME_PRICE, SOME_VOLUME);

        connection1.realeaseAllAquieredVolume();
        verifyNoMoreInteractions(trader);
    }

    /**
     * Events for the {@link MoneyManagement} are passed through to the original {@link MoneyManagement}.
     */
    @Test
    public void passesThroughMoneyManagementEvents() {
        when(someVolumeManagement.getVolume()).thenReturn(SOME_VOLUME);

        final ReleasableMoneyManagement connection = cut.newConnection();

        final UsedVolumeManagement usedVolumeManagement = connection
                .requestVolume(SOME_SYMBOL, SOME_PRICE, SOME_PRICE, SOME_VOLUME).get();
        assertThat(usedVolumeManagement.getVolume()).isEqualTo(SOME_VOLUME);

        usedVolumeManagement.releaseVolume();
        verify(someVolumeManagement).releaseVolume();
       
        connection.updateBalance(SOME_MONEY);
        verify(moneyManagement).updateBalance(SOME_MONEY);
        
        connection.updateExchangeRate(OTHER_SYMBOL, OTHER_PRICE);
        verify(moneyManagement).updateExchangeRate(OTHER_SYMBOL, OTHER_PRICE);
    }

    private Optional<UsedVolumeManagement> opt(final UsedVolumeManagement volumeManagement) {
        return Optional.of(volumeManagement);
    }
}
