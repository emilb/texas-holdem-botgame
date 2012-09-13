package se.cygni.texasholdem.test.util;

import se.cygni.texasholdem.game.BotPlayer;

import java.util.ArrayList;
import java.util.List;

public class PlayerTestUtil {

    public static List<BotPlayer> createRandomListOfPlayers(int length) {
        List<BotPlayer> players = new ArrayList<BotPlayer>();
        for (int i = 0; i < length; i++) {
            BotPlayer player = new BotPlayer("player" + i, "jkjk" + i);
            player.setChipAmount(10000);
            players.add(player);
        }

        return players;
    }
}
