package se.cygni.texasholdem.game.trainingplayers;

import se.cygni.texasholdem.client.ClientEventDispatcher;
import se.cygni.texasholdem.communication.message.event.TexasEvent;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.player.Player;

public abstract class TrainingPlayer extends BotPlayer implements Player {

    ClientEventDispatcher eventDispatcher = new ClientEventDispatcher(this);

    public TrainingPlayer(String name, String sessionId, long chipAmount) {
        super(name, sessionId, chipAmount);
    }

    public TrainingPlayer(String name, String sessionId) {
        super(name, sessionId);
    }

    public void dispatchEvent(TexasEvent event) {
        eventDispatcher.onEvent(event);
    }
}
