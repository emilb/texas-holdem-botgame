package se.cygni.texasholdem.communication.message.event;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.GamePlayer;

@IsATexasMessage
public class PlayerRaisedEvent extends TexasEvent {

    private final GamePlayer player;
    private final long raiseBet;

    @JsonCreator
    public PlayerRaisedEvent(
            @JsonProperty("player") final GamePlayer player,
            @JsonProperty("raiseBet") final long raiseBet) {

        this.player = player;
        this.raiseBet = raiseBet;
    }

    public GamePlayer getPlayer() {

        return player;
    }

    public long getRaiseBet() {

        return raiseBet;
    }

    @Override
    public String toString() {

        return "PlayerRaisedEvent [player=" + player + ", raiseBet=" + raiseBet + "]";
    }

}
