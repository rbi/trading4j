package de.voidnode.trading4j.server.reporting.implementations;

import static java.util.Arrays.asList;

import de.voidnode.trading4j.domain.trades.CompletedTrade;
import de.voidnode.trading4j.server.reporting.AdmininstratorNotifier;
import de.voidnode.trading4j.server.reporting.DeveloperNotifier;
import de.voidnode.trading4j.server.reporting.TraderNotifier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Checks if {@link CombiningNotifier} works as expected.
 * 
 * @author Raik Bieniek
 */
@RunWith(MockitoJUnitRunner.class)
public class CombiningNotifierTest {

    @Mock
    private AdmininstratorNotifier admin1;

    @Mock
    private AdmininstratorNotifier admin2;

    @Mock
    private DeveloperNotifier dev1;

    @Mock
    private DeveloperNotifier dev2;

    @Mock
    private TraderNotifier trader1;

    @Mock
    private TraderNotifier trader2;

    private CombiningNotifier cut;

    @Mock
    private CompletedTrade someTrade;
    private final Throwable someException = new RuntimeException("some exception");
    private final String someMessage = "some message";

    /**
     * Sets up the cut.
     */
    @Before
    public void setUpCut() {
        cut = new CombiningNotifier(asList(trader1, trader2), asList(admin1, admin2), asList(dev1, dev2));
    }

    /**
     * The cut distributes trader events to all traders.
     */
    @Test
    public void traderEventsAreDistributedToAllTraders() {
        cut.tradeCompleted(someTrade);

        verify(trader1).tradeCompleted(someTrade);
        verify(trader2).tradeCompleted(someTrade);
        verifyNoMoreInteractions(trader1, trader2, admin1, admin2, dev1, dev2);
    }

    /**
     * The cut distributes administrator events to all administrators.
     */
    @Test
    public void administratorEventsAreDistributedToAllAdministrators() {
        cut.unrecoverableError(someMessage, someException);
        cut.unexpectedEvent(someMessage, someException);
        cut.unexpectedEvent(someMessage);
        cut.informalEvent(someMessage);
        
        verify(admin1).unrecoverableError(someMessage, someException);
        verify(admin1).unexpectedEvent(someMessage, someException);
        verify(admin1).unexpectedEvent(someMessage);
        verify(admin1).informalEvent(someMessage);
        verify(admin2).unrecoverableError(someMessage, someException);
        verify(admin2).unexpectedEvent(someMessage, someException);
        verify(admin2).unexpectedEvent(someMessage);
        verify(admin2).informalEvent(someMessage);
        verifyNoMoreInteractions(trader1, trader2, admin1, admin2, dev1, dev2);  
    }

    /**
     * The cut distributes developer events to all developers.
     */
    @Test
    public void developerEventsAreDistributedToAllDevelopers() {
        cut.unrecoverableProgrammingError(someMessage, someException);
                
        verify(dev1).unrecoverableProgrammingError(someMessage, someException);
        verify(dev2).unrecoverableProgrammingError(someMessage, someException);
        verifyNoMoreInteractions(trader1, trader2, admin1, admin2, dev1, dev2);
    }

    /**
     * It is possible that for some events no listeners are registered.
     */
    @Test
    public void listenersCanBeEmpty() {
        cut = new CombiningNotifier(asList(), asList(), asList(dev1));
        
        cut.tradeCompleted(someTrade);
        cut.unexpectedEvent(someMessage);
        
        verifyNoMoreInteractions(trader1, trader2, admin1, admin2, dev1, dev2);
    }
}
