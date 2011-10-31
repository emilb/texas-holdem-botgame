package se.cygni.texasholdem.server.eventbus;

import se.cygni.texasholdem.game.BotPlayer;

public class PlayerQuitEvent {

    private final BotPlayer player;

    public PlayerQuitEvent(final BotPlayer player) {

        this.player = player;
    }

    public BotPlayer getPlayer() {

        return player;
    }

}
