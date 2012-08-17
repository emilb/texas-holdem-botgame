package se.cygni.texasholdem.communication.message.event;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.Player;

@IsATexasMessage
public class PlayerQuitEvent extends TexasEvent {

    private final Player player;

    @JsonCreator
    public PlayerQuitEvent(@JsonProperty("player") final Player player) {

        this.player = player;
    }

    public Player getPlayer() {

        return player;
    }

    @Override
    public String toString() {

        return "PlayerQuitEvent [player=" + player + "]";
    }

}
