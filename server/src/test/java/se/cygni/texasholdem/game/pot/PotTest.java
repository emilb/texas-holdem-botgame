package se.cygni.texasholdem.game.pot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.game.definitions.PlayState;

public class PotTest {

    private Pot pot;
    private BotPlayer pA;
    private BotPlayer pB;
    private BotPlayer pC;
    private BotPlayer pD;

    @Before
    public void setUp() throws Exception {

        pA = new BotPlayer("A", "sessionA", 1000);
        pB = new BotPlayer("B", "sessionB", 1000);
        pC = new BotPlayer("C", "sessionC", 1000);
        pD = new BotPlayer("D", "sessionD", 1000);

        final List<BotPlayer> players = new ArrayList<BotPlayer>();
        players.add(pA);
        players.add(pB);
        players.add(pC);
        players.add(pD);

        pot = new Pot(players);
    }

    @Test
    public void testMinimumBetForPlayerToCall() {

        pot.bet(pA, 5L);
        pot.bet(pB, 10L);
        pot.bet(pC, 10L);
        pot.bet(pD, 20L);

        assertEquals(15L, pot.getMinimumBetForPlayerToCall(pA));
        assertEquals(10L, pot.getMinimumBetForPlayerToCall(pB));
        assertEquals(10L, pot.getMinimumBetForPlayerToCall(pC));
        assertEquals(0L, pot.getMinimumBetForPlayerToCall(pD));
    }

    @Test
    public void testBet() {

        pot.bet(pA, 5L);
        pot.bet(pB, 10L);
        pot.bet(pC, 10L);
        pot.bet(pD, 20L);
        pot.bet(pA, 15L);
        pot.bet(pB, 10L);

        assertEquals(20L, pot.getTotalBetAmountForPlayer(pA));
        assertEquals(20L, pot.getTotalBetAmountForPlayer(pB));
        assertEquals(10L, pot.getTotalBetAmountForPlayer(pC));
        assertEquals(20L, pot.getTotalBetAmountForPlayer(pD));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeBet() {

        pot.bet(pA, 5L);
        pot.bet(pB, 10L);
        pot.bet(pC, 10L);
        pot.bet(pD, 20L);
        pot.bet(pA, 15L);
        pot.bet(pB, -10L);
    }

    @Test(expected = IllegalStateException.class)
    public void testBetWhenPlayerHasFolded() {

        pot.bet(pA, 5L);
        pot.bet(pB, 10L);
        pot.bet(pC, 10L);
        pot.bet(pD, 20L);
        pot.bet(pA, 15L);
        pot.fold(pB);
        pot.bet(pB, 10L);
    }

    @Test
    public void testFold() {

        pot.fold(pD);

        assertFalse(pot.hasFolded(pA));
        assertFalse(pot.hasFolded(pB));
        assertFalse(pot.hasFolded(pC));
        assertTrue(pot.hasFolded(pD));
    }

    @Test(expected = IllegalStateException.class)
    public void testNextPlayStateBeyondLast() {

        pot.nextPlayState(); // to FLOP
        pot.nextPlayState(); // to TURN
        pot.nextPlayState(); // to RIVER
        pot.nextPlayState(); // to SHOWDOWN
        pot.nextPlayState(); // ERROR
    }

    @Test
    public void testNextPlayState() {

        assertEquals(PlayState.PRE_FLOP, pot.getCurrentPlayState());
        assertEquals(PlayState.FLOP, pot.nextPlayState());
        assertEquals(PlayState.FLOP, pot.getCurrentPlayState());
        assertEquals(PlayState.TURN, pot.nextPlayState());
        assertEquals(PlayState.TURN, pot.getCurrentPlayState());
        assertEquals(PlayState.RIVER, pot.nextPlayState());
        assertEquals(PlayState.RIVER, pot.getCurrentPlayState());
        assertEquals(PlayState.SHOWDOWN, pot.nextPlayState());
        assertEquals(PlayState.SHOWDOWN, pot.getCurrentPlayState());
    }

    @Test
    public void testGetCurrentPlayState() {

        assertEquals(PlayState.PRE_FLOP, pot.getCurrentPlayState());
    }

    @Test
    public void testGetTotalPotSize() {

        pot.bet(pA, 5L);
        pot.bet(pB, 10L);
        pot.bet(pC, 10L);
        pot.bet(pD, 20L);
        pot.bet(pA, 15L);
        pot.bet(pB, 10L);

        assertEquals(70L, pot.getTotalPotAmount());
    }

    @Test
    public void testIsCurrentPlayStateBalanced() {

        pot.bet(pA, 5L);
        pot.bet(pB, 10L);
        pot.bet(pC, 10L);
        pot.bet(pD, 20L);
        pot.bet(pA, 15L);
        pot.bet(pB, 10L);

        assertFalse(pot.isCurrentPlayStateBalanced());

        pot.bet(pC, 10L);
        assertTrue(pot.isCurrentPlayStateBalanced());
    }

    @Test
    public void testIsCurrentPlayStateBalancedWithPlayerFolded() {

        pot.bet(pA, 5L);
        pot.bet(pB, 10L);
        pot.bet(pC, 10L);
        pot.bet(pD, 20L);
        pot.bet(pA, 15L);
        pot.bet(pB, 10L);
        pot.fold(pC);

        assertTrue(pot.isCurrentPlayStateBalanced());
    }
}
