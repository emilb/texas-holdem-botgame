package se.cygni.texasholdem.util;

import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.game.GamePlayer;

import java.util.ArrayList;
import java.util.List;

public class PlayerTypeConverter {

    private PlayerTypeConverter() {}

    public static List<GamePlayer> listOfBotPlayers(final List<BotPlayer> bots) {
        List<GamePlayer> players = new ArrayList<GamePlayer>(bots.size());
        for (BotPlayer bot : bots) {
            players.add(fromBotPlayer(bot));
        }

        return players;
    }

    public static GamePlayer fromBotPlayer(final BotPlayer bot) {

        return new GamePlayer(bot.getName(), bot.getChipAmount());
    }

}
