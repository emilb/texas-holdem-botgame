package se.cygni.texasholdem.communication.message.event;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.GamePlayer;

@IsATexasMessage
public class PlayerQuitEvent extends TexasEvent {

    private final GamePlayer player;

    @JsonCreator
    public PlayerQuitEvent(@JsonProperty("player") final GamePlayer player) {

        this.player = player;
    }

    public GamePlayer getPlayer() {

        return player;
    }

    @Override
    public String toString() {

        return "PlayerQuitEvent [player=" + player + "]";
    }

}
