package se.cygni.texasholdem.game.util;

import static se.cygni.texasholdem.test.util.PlayerTestUtil.*;

import org.junit.Assert;
import org.junit.Test;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.game.pot.Pot;

import java.util.List;

public class GameUtilTest {

    @Test
    public void testGetNextPlayerInPlay() throws Exception {

        List<BotPlayer> players = createRandomListOfPlayers(5);
        Pot pot = new Pot(players);

        Assert.assertEquals("Simple succession failed", players.get(1), GameUtil.getNextPlayerInPlay(players, players.get(0), pot));
        Assert.assertEquals("Roll-over succession failed", players.get(0), GameUtil.getNextPlayerInPlay(players, players.get(4), pot));

        players.get(3).setChipAmount(0);
        Assert.assertEquals("Failed to skip bankrupt player", players.get(4), GameUtil.getNextPlayerInPlay(players, players.get(2), pot));

    }

    @Test
    public void testGetOrderedListOfPlayersInPlay() throws Exception {

    }

    @Test
    public void testGetOrderedListOfPlayers() throws Exception {

    }

    @Test
    public void testGetActivePlayersWithChipsLeft() throws Exception {

    }

    @Test
    public void testGetNoofPlayersWithChipsLeft() throws Exception {

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
}
