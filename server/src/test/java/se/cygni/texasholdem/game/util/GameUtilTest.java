package se.cygni.texasholdem.game.util;

import org.junit.Assert;
import org.junit.Test;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.game.pot.Pot;

import java.util.ArrayList;
import java.util.List;

import static se.cygni.texasholdem.test.util.PlayerTestUtil.createRandomListOfPlayers;

public class GameUtilTest {

    @Test
    public void testGetNextPlayerInPlay() throws Exception {

        List<BotPlayer> players = createRandomListOfPlayers(5);
        Pot pot = new Pot(players);

        Assert.assertEquals("Simple succession failed",
                players.get(1), GameUtil.getNextPlayerInPlay(players, players.get(0), pot));

        Assert.assertEquals("Roll-over succession failed",
                players.get(0), GameUtil.getNextPlayerInPlay(players, players.get(4), pot));

        players.get(3).setChipAmount(0);
        Assert.assertEquals("Failed to skip bankrupt player",
                players.get(4), GameUtil.getNextPlayerInPlay(players, players.get(2), pot));

    }

    @Test
    public void testGetOrderedListOfPlayersInPlay() throws Exception {

    }

    @Test
    public void testGetActivePlayersWithChipsLeft() throws Exception {

        List<BotPlayer> players = createRandomListOfPlayers(5);

        assertEqualList(players, GameUtil.getActivePlayersWithChipsLeft(players));

        players.get(0).setChipAmount(0);
        List<BotPlayer> expectedList = new ArrayList<BotPlayer>(players);
        expectedList.remove(0);
        assertEqualList(expectedList, GameUtil.getActivePlayersWithChipsLeft(players));

        for (BotPlayer p : players) {
            p.setChipAmount(0);
        }
        assertEqualList(new ArrayList<BotPlayer>(), GameUtil.getActivePlayersWithChipsLeft(players));

    }

    @Test
    public void testGetNoofPlayersWithChipsLeft() throws Exception {

        List<BotPlayer> players = createRandomListOfPlayers(5);

        Assert.assertEquals("Did not detect all players with chips left",
                5, GameUtil.getNoofPlayersWithChipsLeft(players));

        players.get(0).setChipAmount(0);
        Assert.assertEquals("Did not detect that one player is bankrupt",
                4, GameUtil.getNoofPlayersWithChipsLeft(players));

        for (BotPlayer p : players) {
            p.setChipAmount(0);
        }
        Assert.assertEquals("Did not detect that all players are bankrupt",
                0, GameUtil.getNoofPlayersWithChipsLeft(players));
    }

    @Test
    public void testGetBustedPlayers() throws Exception {

    }

    @Test
    public void testPlayerHasChips() throws Exception {

    }

    @Test
    public void testClearAllCards() throws Exception {

    }

    @Test
    public void testIsThereAWinner() throws Exception {

    }

    @Test
    public void testIsActionValid() throws Exception {

    }

    private void assertEqualList(List<?> first, List<?> second) {
        Assert.assertEquals("Lists not of same length",
                first.size(), second.size());

        for (Object o : first) {
            Assert.assertTrue("Missing value: " + o,  second.contains(o));
        }
    }
}
