package se.cygni.texasholdem.util;

import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.game.Player;

public class PlayerTypeConverter {

    public static Player fromBotPlayer(final BotPlayer bot) {

        return new Player(bot.getName(), bot.getChipAmount());
    }

}
