package se.cygni.texasholdem.util;

import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.game.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerTypeConverter {

    public static List<Player> listOfBotPlayers(final List<BotPlayer> bots) {
        List<Player> players = new ArrayList<Player>(bots.size());
        for (BotPlayer bot : bots) {
            players.add(fromBotPlayer(bot));
        }

        return players;
    }

    public static Player fromBotPlayer(final BotPlayer bot) {

        return new Player(bot.getName(), bot.getChipAmount());
    }

}
