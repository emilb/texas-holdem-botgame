package se.cygni.texasholdem.communication.message.event;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.Player;

@IsATexasMessage
public class PlayerCheckedEvent extends TexasEvent {

    @Override
    public String toString() {

        return "PlayerCheckedEvent [player=" + player + "]";
    }

    private final Player player;

    @JsonCreator
    public PlayerCheckedEvent(@JsonProperty("player") final Player player) {

        this.player = player;
    }

    public Player getPlayer() {

        return player;
    }

}
