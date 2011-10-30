package se.cygni.texasholdem.server.eventbus;

import se.cygni.texasholdem.game.BotPlayer;

public class NewPlayerEvent {

    private final BotPlayer player;

    public NewPlayerEvent(final BotPlayer player) {

        this.player = player;
    }

    public BotPlayer getPlayer() {

        return player;
    }

}
