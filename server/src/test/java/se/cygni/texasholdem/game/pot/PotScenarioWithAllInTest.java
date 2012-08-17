package se.cygni.texasholdem.game.pot;

import org.junit.Before;
import org.junit.Test;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.game.definitions.PlayState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class PotScenarioWithAllInTest {

    private Pot pot;
    private BotPlayer pA;
    private BotPlayer pB;
    private BotPlayer pC;
    private BotPlayer pD;

    @Before
    public void setUp() throws Exception {

        pA = new BotPlayer("A", "sessionA", 25);
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
    public void testWholePlayWithAllIns() {

        /**
         * PRE_FLOP
         *
         * - All players are in play but pA goes all in.
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

        pot.bet(pA, 20);
        pot.bet(pB, pot.getMinimumBetForPlayerToCall(pB));
        pot.bet(pC, pot.getMinimumBetForPlayerToCall(pC));
        pot.bet(pD, pot.getMinimumBetForPlayerToCall(pD) + 10);
        assertFalse(pot.isCurrentPlayStateBalanced());

        pot.bet(pB, pot.getMinimumBetForPlayerToCall(pB));
        pot.bet(pC, pot.getMinimumBetForPlayerToCall(pC));

        assertEquals(130L, pot.getTotalPotAmount());

        /**
         * FLOP
         *
         * - Assert that pA cannot place more bets - Assert that it is not
         * possible to change PlayState when current bets are unbalanced.
         */
        pot.nextPlayState();
        assertEquals(PlayState.FLOP, pot.getCurrentPlayState());

        try {
            pot.bet(pA, 100);
            fail("Player A could place bet even though funds are depleted");
        } catch (final Exception e) {
        }

        pot.bet(pB, 10L);
        pot.bet(pC, 20L);
        pot.bet(pD, 40L);

        assertEquals(200L, pot.getTotalPotAmount());

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

        pot.bet(pB, pot.getMinimumBetForPlayerToCall(pB));
        pot.bet(pC, pot.getMinimumBetForPlayerToCall(pC));
        pot.bet(pD, pot.getMinimumBetForPlayerToCall(pD));
        assertTrue(pot.isCurrentPlayStateBalanced());

        assertEquals(250L, pot.getTotalPotAmount());

        /**
         * TURN
         *
         * - All players check - assert the total bet amount for players
         */
        pot.nextPlayState();
        assertEquals(PlayState.TURN, pot.getCurrentPlayState());

        assertTrue(pot.isCurrentPlayStateBalanced());
        assertEquals(250L, pot.getTotalPotAmount());

        assertEquals(75L, pot.getTotalBetAmountForPlayer(pB));
        assertEquals(75L, pot.getTotalBetAmountForPlayer(pC));
        assertEquals(75L, pot.getTotalBetAmountForPlayer(pD));

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

        // Try to make bet for folded player pC and all in pA
        betButExpectError(pC, 50L);
        betButExpectError(pA, 150L);

        assertEquals(550L, pot.getTotalPotAmount());

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

        assertEquals(550L, pot.getTotalPotAmount());

        /**
         * PAYOUT - assert that players are paid their due
         *
         * First case rank: pB pD pA pC
         */
        final List<List<BotPlayer>> ranking = new ArrayList<List<BotPlayer>>();
        ranking.add(Arrays.asList(pB));
        ranking.add(Arrays.asList(pD));
        ranking.add(Arrays.asList(pA));
        ranking.add(Arrays.asList(pC));

        Map<BotPlayer, Long> payout = pot.calculatePayout(ranking);

        assertEquals(550, payout.get(pB).longValue());
        assertEquals(0, payout.get(pD).longValue());
        assertEquals(0, payout.get(pA).longValue());
        assertEquals(0, payout.get(pC).longValue());

        /**
         * PAYOUT - assert that players are paid their due
         *
         * Second case rank: pA pD pB pC
         */
        ranking.clear();
        ranking.add(Arrays.asList(pA));
        ranking.add(Arrays.asList(pD));
        ranking.add(Arrays.asList(pB));
        ranking.add(Arrays.asList(pC));

        payout = pot.calculatePayout(ranking);

        assertEquals(100, payout.get(pA).longValue());
        assertEquals(450, payout.get(pD).longValue());
        assertEquals(0, payout.get(pB).longValue());
        assertEquals(0, payout.get(pC).longValue());

        /**
         * PAYOUT - assert that players are paid their due
         *
         * Third case rank: (pA pB) pD pC
         */
        ranking.clear();
        ranking.add(Arrays.asList(pA, pB));
        ranking.add(Arrays.asList(pD));
        ranking.add(Arrays.asList(pC));

        payout = pot.calculatePayout(ranking);

        assertEquals(50, payout.get(pA).longValue());
        assertEquals(0, payout.get(pD).longValue());
        assertEquals(500, payout.get(pB).longValue());
        assertEquals(0, payout.get(pC).longValue());

        /**
         * PAYOUT - assert that players are paid their due
         *
         * Fourth case rank: pA (pB pD) pC
         */
        ranking.clear();
        ranking.add(Arrays.asList(pA));
        ranking.add(Arrays.asList(pB, pD));
        ranking.add(Arrays.asList(pC));

        payout = pot.calculatePayout(ranking);

        assertEquals(100, payout.get(pA).longValue());
        assertEquals(225, payout.get(pD).longValue());
        assertEquals(225, payout.get(pB).longValue());
        assertEquals(0, payout.get(pC).longValue());

        /**
         * PAYOUT - assert that players are paid their due
         *
         * Fifth case rank: pC (pB pD) pA Remember that Pc folded!
         */
        ranking.clear();
        ranking.add(Arrays.asList(pC));
        ranking.add(Arrays.asList(pB, pD));
        ranking.add(Arrays.asList(pA));

        payout = pot.calculatePayout(ranking);

        assertEquals(0, payout.get(pC).longValue());
        assertEquals(275, payout.get(pB).longValue());
        assertEquals(275, payout.get(pD).longValue());
        assertEquals(0, payout.get(pA).longValue());
    }

    private void betButExpectError(final BotPlayer p, final long amount) {

        try {
            pot.bet(p, amount);
            fail("Folder player: " + p + " was allowed to place a bet");
        } catch (final IllegalStateException e) {
            return;
        } catch (final IllegalArgumentException e) {
            return;
        } catch (final Exception e) {
            fail("Wrong exception thrown, expected IllegalStateException but got: "
                    + e.getClass().getSimpleName());
        }
    }
}
