package se.cygni.texasholdem.game.trainingplayers;

import se.cygni.texasholdem.client.ClientEventDispatcher;
import se.cygni.texasholdem.client.CurrentPlayState;
import se.cygni.texasholdem.communication.message.event.TexasEvent;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.player.Player;

public abstract class TrainingPlayer extends BotPlayer implements Player {

    ClientEventDispatcher eventDispatcher = new ClientEventDispatcher(this);
    ClientEventDispatcher currentPlayStateDispatcher;
    CurrentPlayState currentPlayState;

    public TrainingPlayer(String name, String sessionId, long chipAmount) {
        super(name, sessionId, chipAmount);
        currentPlayState = new CurrentPlayState(name);
        currentPlayStateDispatcher = new ClientEventDispatcher(currentPlayState.getPlayerImpl());
    }

    public TrainingPlayer(String name, String sessionId) {
        this(name, sessionId, 0);
    }

    public CurrentPlayState getCurrentPlayState() {
        return currentPlayState;
    }

    public void dispatchEvent(TexasEvent event) {
        currentPlayStateDispatcher.onEvent(event);
        eventDispatcher.onEvent(event);
    }
}
