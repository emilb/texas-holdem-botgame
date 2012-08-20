package se.cygni.texasholdem.table;

import com.google.common.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.cygni.texasholdem.communication.message.event.PlayIsStartedEvent;
import se.cygni.texasholdem.communication.message.event.TexasEvent;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.server.eventbus.EventWrapper;
import se.cygni.texasholdem.server.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GameRoundTest {

    @Mock
    private SessionManager sessionManager;

    @Mock
    private EventBus eventBus;

    private BotPlayer pA;
    private BotPlayer pB;
    private BotPlayer pC;
    private BotPlayer pD;
    private List<BotPlayer> players;

    private Table table;

    @Before
    public void setup() throws Exception {

        pA = new BotPlayer("A", "sessionA", 25);
        pB = new BotPlayer("B", "sessionB", 1000);
        pC = new BotPlayer("C", "sessionC", 1000);
        pD = new BotPlayer("D", "sessionD", 1000);

        players = new ArrayList<BotPlayer>();
        players.add(pA);
        players.add(pB);
        players.add(pC);
        players.add(pD);
    }

    @Test
    public void testCorrectPlayerAssignmentSimple() {

        final GameRound round = new GameRound(players, pA, 5, 10, 10, 3, eventBus,
                sessionManager);

        assertEquals(pA, round.getDealerPlayer());
        assertEquals(pB, round.getSmallBlindPlayer());
        assertEquals(pC, round.getBigBlindPlayer());
    }

    @Test
    public void testCorrectPlayerAssignmentWithTwoPlayers() {

        final GameRound round = new GameRound(players.subList(0, 2), pA, 5, 10,
                10, 3, eventBus,
                sessionManager);

        assertEquals(pA, round.getDealerPlayer());
        assertEquals(pB, round.getSmallBlindPlayer());
        assertEquals(pA, round.getBigBlindPlayer());
    }

    @Test
    public void testCorrectPlayerAssignmentTurn() {

        final GameRound round = new GameRound(players, pC, 5, 10, 10, 3, eventBus,
                sessionManager);

        assertEquals(pC, round.getDealerPlayer());
        assertEquals(pD, round.getSmallBlindPlayer());
        assertEquals(pA, round.getBigBlindPlayer());
    }

    // @Test
    public void testBasicPlay() {

        final ArgumentCaptor<EventWrapper> argument = ArgumentCaptor
                .forClass(EventWrapper.class);

        final GameRound round = new GameRound(players, pA, 5, 10, 10, 3, eventBus,
                sessionManager);

        round.playGameRound();

        verify(eventBus, times(19)).post(argument.capture());
        // assertTrue(argument.getAllValues().get(0).getEvent() instanceof
        // PlayIsStartedEvent);
        assertEventOfClassAndToCorrectRecipients(PlayIsStartedEvent.class,
                argument.getAllValues().get(0), players);
        // is(instanceOf(EventWrapper.class))
    }

    private void assertEventOfClassAndToCorrectRecipients(
            final Class<? extends TexasEvent> eventClass,
            final EventWrapper eventWrapper,
            final List<BotPlayer> players) {

        assertTrue(eventWrapper.getEvent().getClass() == eventClass);

    }
}
