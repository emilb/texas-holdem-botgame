package se.cygni.texasholdem.game.pot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.game.definitions.PlayState;

public class PotScenarioTest {

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
    public void testWholePlay() {

        /**
         * PRE_FLOP
         * 
         * - All players are in play.
         */
        pot.bet(pA, 5L);
        pot.bet(pB, 10L);
        pot.bet(pC, 10L);
        pot.bet(pD, 20L);

        assertEquals(15L, pot.getMinimumBetForPlayerToCall(pA));
        assertEquals(10L, pot.getMinimumBetForPlayerToCall(pB));
        assertEquals(10L, pot.getMinimumBetForPlayerToCall(pC));
        assertEquals(0L, pot.getMinimumBetForPlayerToCall(pD));
        assertFalse(pot.isCurrentPlayStateBalanced());

        pot.bet(pA, pot.getMinimumBetForPlayerToCall(pA));
        pot.bet(pB, pot.getMinimumBetForPlayerToCall(pB));
        pot.bet(pC, pot.getMinimumBetForPlayerToCall(pC));
        pot.bet(pD, pot.getMinimumBetForPlayerToCall(pD));
        assertTrue(pot.isCurrentPlayStateBalanced());

        assertEquals(80L, pot.getTotalPotAmount());

        /**
         * FLOP
         * 
         * - pA decides to fold. - Assert that it is not possible to change
         * PlayState when current bets are unbalanced. - Assert that a folded
         * player cannot place bet.
         */
        pot.nextPlayState();
        assertEquals(PlayState.FLOP, pot.getCurrentPlayState());

        pot.fold(pA);
        assertTrue(pot.hasFolded(pA));
        pot.bet(pB, 10L);
        pot.bet(pC, 20L);
        pot.bet(pD, 40L);

        assertEquals(150L, pot.getTotalPotAmount());

        // Try to switch state in unbalanced state
        try {
            pot.nextPlayState();
            fail("Could enter next state with unbalanced play state");
        } catch (final Exception e) {
        }

        assertEquals(0L, pot.getMinimumBetForPlayerToCall(pA));
        assertEquals(30L, pot.getMinimumBetForPlayerToCall(pB));
        assertEquals(20L, pot.getMinimumBetForPlayerToCall(pC));
        assertEquals(0L, pot.getMinimumBetForPlayerToCall(pD));

        // Try to make bet for folded player pA
        betButExpectError(pA, pot.getMinimumBetForPlayerToCall(pA));

        pot.bet(pB, pot.getMinimumBetForPlayerToCall(pB));
        pot.bet(pC, pot.getMinimumBetForPlayerToCall(pC));
        pot.bet(pD, pot.getMinimumBetForPlayerToCall(pD));
        assertTrue(pot.isCurrentPlayStateBalanced());

        assertEquals(200L, pot.getTotalPotAmount());

        /**
         * TURN
         * 
         * - All players check - assert the total bet amount for players
         */
        pot.nextPlayState();
        assertEquals(PlayState.TURN, pot.getCurrentPlayState());

        assertTrue(pot.isCurrentPlayStateBalanced());
        assertEquals(200L, pot.getTotalPotAmount());

        assertEquals(20L, pot.getTotalBetAmountForPlayer(pA));
        assertEquals(60L, pot.getTotalBetAmountForPlayer(pB));
        assertEquals(60L, pot.getTotalBetAmountForPlayer(pC));
        assertEquals(60L, pot.getTotalBetAmountForPlayer(pD));

        /**
         * RIVER
         * 
         * - pC folds - pB and pD battles but both are in play to SHOWDOWN -
         * assert that pA and pC cannot place more bets -
         */
        pot.nextPlayState();
        assertEquals(PlayState.RIVER, pot.getCurrentPlayState());

        pot.bet(pB, 120L);
        pot.fold(pC);
        pot.bet(pD, 150L);
        pot.bet(pB, 30L);

        // Try to make bet for folded player pA and pC
        betButExpectError(pA, 150L);
        betButExpectError(pC, 50L);

        assertEquals(500L, pot.getTotalPotAmount());

        /**
         * SHOWDOWN
         * 
         * - assert that no bets can be placed - assert that it is not possible
         * to change to next state
         */
        pot.nextPlayState();
        assertEquals(PlayState.SHOWDOWN, pot.getCurrentPlayState());

        betButExpectError(pB, 20L);
        betButExpectError(pA, 35L);

        // Try to switch state when in SHOWDOWN
        try {
            pot.nextPlayState();
            fail("Could enter next state when in last state");
        } catch (final Exception e) {
        }

        assertEquals(500L, pot.getTotalPotAmount());
    }

    private void betButExpectError(final BotPlayer p, final long amount) {

        try {
            pot.bet(p, amount);
            fail("Folder player: " + p + " was allowed to place a bet");
        } catch (final IllegalStateException e) {
            return;
        } catch (final Exception e) {
            fail("Wrong exception thrown, expected IllegalStateException but got: "
                    + e.getClass().getSimpleName());
        }
    }
}
