package se.cygni.texasholdem.game.pot;

import org.junit.Before;
import org.junit.Test;
import se.cygni.texasholdem.game.BotPlayer;

import static org.junit.Assert.assertEquals;

public class PotSplitterTest {

    private PotSplitter splitter;
    private BotPlayer pA;
    private BotPlayer pB;
    private BotPlayer pC;
    private BotPlayer pD;

    @Before
    public void init() {

        splitter = new PotSplitter();

        pA = new BotPlayer("A", "sessionA", 25);
        pB = new BotPlayer("B", "sessionB", 1000);
        pC = new BotPlayer("C", "sessionC", 1000);
        pD = new BotPlayer("D", "sessionD", 1000);
    }

    @Test
    public void testOnePlayer() {

        splitter.addMaxWinAmountForPlayer(pA, 100);

        assertEquals(100, splitter.getWinAmountFor(pA));
    }

    @Test
    public void testTwoPlayersSameWinAmount() {

        splitter.addMaxWinAmountForPlayer(pA, 100);
        splitter.addMaxWinAmountForPlayer(pB, 100);

        assertEquals(50, splitter.getWinAmountFor(pA));
        assertEquals(50, splitter.getWinAmountFor(pB));
    }

    @Test
    public void testFourPlayersSameWinAmount() {

        splitter.addMaxWinAmountForPlayer(pA, 100);
        splitter.addMaxWinAmountForPlayer(pB, 100);
        splitter.addMaxWinAmountForPlayer(pC, 100);
        splitter.addMaxWinAmountForPlayer(pD, 100);

        assertEquals(25, splitter.getWinAmountFor(pA));
        assertEquals(25, splitter.getWinAmountFor(pB));
        assertEquals(25, splitter.getWinAmountFor(pC));
        assertEquals(25, splitter.getWinAmountFor(pD));
    }

    @Test
    public void testTwoPlayersDifferentWinAmount() {

        splitter.addMaxWinAmountForPlayer(pA, 100);
        splitter.addMaxWinAmountForPlayer(pB, 550);

        assertEquals(50, splitter.getWinAmountFor(pA));
        assertEquals(500, splitter.getWinAmountFor(pB));
    }

    @Test
    public void testFourPlayersDifferentWinAmount() {

        splitter.addMaxWinAmountForPlayer(pA, 100);
        splitter.addMaxWinAmountForPlayer(pB, 500);
        splitter.addMaxWinAmountForPlayer(pC, 400);
        splitter.addMaxWinAmountForPlayer(pD, 300);

        assertEquals(25, splitter.getWinAmountFor(pA));
        assertEquals(242, splitter.getWinAmountFor(pB));
        assertEquals(142, splitter.getWinAmountFor(pC));
        assertEquals(91, splitter.getWinAmountFor(pD));
    }
}
