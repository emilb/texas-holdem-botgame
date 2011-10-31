package se.cygni.texasholdem.communication.message.event;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.Player;

@IsATexasMessage
public class PlayerCalledEvent extends TexasEvent {

    private final Player player;
    private final long callBet;

    @JsonCreator
    public PlayerCalledEvent(@JsonProperty("player") final Player player,
            @JsonProperty("callBet") final long callBet) {

        this.player = player;
        this.callBet = callBet;
    }

    public Player getPlayer() {

        return player;
    }

    public long getCallBet() {

        return callBet;
    }

    @Override
    public String toString() {

        return "PlayerCalledEvent [player=" + player + ", callBet=" + callBet
                + "]";
    }
}
