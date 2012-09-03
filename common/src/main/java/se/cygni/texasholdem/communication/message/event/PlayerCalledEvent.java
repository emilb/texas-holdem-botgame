package se.cygni.texasholdem.communication.message.event;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.GamePlayer;

@IsATexasMessage
public class PlayerCalledEvent extends TexasEvent {

    private final GamePlayer player;
    private final long callBet;

    @JsonCreator
    public PlayerCalledEvent(@JsonProperty("player") final GamePlayer player,
                             @JsonProperty("callBet") final long callBet) {

        this.player = player;
        this.callBet = callBet;
    }

    public GamePlayer getPlayer() {

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
