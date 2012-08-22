package se.cygni.texasholdem.game.util;

import junit.framework.TestCase;
import org.junit.Test;
import se.cygni.texasholdem.game.BestHand;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.definitions.PokerHand;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static se.cygni.texasholdem.test.util.TestUtil.createPokerHandUtil;
import static se.cygni.texasholdem.test.util.TestUtil.getCard;
import static se.cygni.texasholdem.test.util.TestUtil.getCards;

public class PokerHandRankUtilTest {

    @Test
    public void testGetPlayerRankings() throws Exception {
        BotPlayer player1 = new BotPlayer("player1", "jkjk");
        player1.receiveCard(getCard("Th"));
        player1.receiveCard(getCard("Ks"));

        BotPlayer player2 = new BotPlayer("player2", "vfv");
        player2.receiveCard(getCard("Js"));
        player2.receiveCard(getCard("7d"));

        List<BotPlayer> players = new ArrayList<BotPlayer>();
        players.add(player1);
        players.add(player2);

        PokerHandRankUtil pokerHandRankUtil = new PokerHandRankUtil(getCards("Kd", "Qh", "Jc", "Qs", "Tc"), players);

        List<List<BotPlayer>> playerRankings = pokerHandRankUtil.getPlayerRankings();

        assertEquals(2, playerRankings.size());
        assertEquals(player1,  playerRankings.get(0).get(0));
        assertEquals(-1, pokerHandRankUtil.getBestHand(player1).compareTo(pokerHandRankUtil.getBestHand(player2)));
    }
}
