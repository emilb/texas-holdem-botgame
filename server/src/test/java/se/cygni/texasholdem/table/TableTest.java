package se.cygni.texasholdem.table;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import se.cygni.texasholdem.game.BotPlayer;

import com.google.common.eventbus.EventBus;

public class TableTest {

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

        table = new Table(new GamePlan(), new EventBus());
        table.addPlayer(pA);
        table.addPlayer(pB);
        table.addPlayer(pC);
        table.addPlayer(pD);
    }

    @Test
    public void testShiftRolesForPlayers() {

        table.shiftRolesForPlayers();
        assertEquals(pA, table.getDealerPlayer());
        assertEquals(pB, table.getSmallBlindPlayer());
        assertEquals(pC, table.getBigBlindPlayer());

        table.shiftRolesForPlayers();
        assertEquals(pB, table.getDealerPlayer());
        assertEquals(pC, table.getSmallBlindPlayer());
        assertEquals(pD, table.getBigBlindPlayer());

        table.shiftRolesForPlayers();
        assertEquals(pC, table.getDealerPlayer());
        assertEquals(pD, table.getSmallBlindPlayer());
        assertEquals(pA, table.getBigBlindPlayer());

    }

    @Test
    public void testShiftRolesForPlayersWhenOnePlayerIsOut() {

        table.shiftRolesForPlayers();
        assertEquals(pA, table.getDealerPlayer());
        assertEquals(pB, table.getSmallBlindPlayer());
        assertEquals(pC, table.getBigBlindPlayer());

        // pC lost everything
        pC.getChips(1000);

        table.shiftRolesForPlayers();
        assertEquals(pB, table.getDealerPlayer());
        assertEquals(pD, table.getSmallBlindPlayer());
        assertEquals(pA, table.getBigBlindPlayer());

        table.shiftRolesForPlayers();
        assertEquals(pD, table.getDealerPlayer());
        assertEquals(pA, table.getSmallBlindPlayer());
        assertEquals(pB, table.getBigBlindPlayer());

    }

    @Test
    public void testShiftRolesForPlayersWhenOnlyTwoPlayers() {

        table = new Table(new GamePlan(), new EventBus());
        table.addPlayer(pA);
        table.addPlayer(pB);

        table.shiftRolesForPlayers();
        assertEquals(pA, table.getDealerPlayer());
        assertEquals(pB, table.getSmallBlindPlayer());
        assertEquals(pA, table.getBigBlindPlayer());

        table.shiftRolesForPlayers();
        assertEquals(pB, table.getDealerPlayer());
        assertEquals(pA, table.getSmallBlindPlayer());
        assertEquals(pB, table.getBigBlindPlayer());
    }

    @Test
    public void testIsThereAWinner() {

        Assert.assertFalse(table.isThereAWinner());

        pA.getChips(25);
        Assert.assertFalse(table.isThereAWinner());

        pB.getChips(1000);
        Assert.assertFalse(table.isThereAWinner());

        pC.getChips(1000);
        Assert.assertTrue(table.isThereAWinner());
    }
}
