package se.cygni.texasholdem.communication.message.event;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.Player;

@IsATexasMessage
public class PlayerRaisedEvent extends TexasEvent {

    private final Player player;
    private final long callBet;
    private final long raiseBet;

    @JsonCreator
    public PlayerRaisedEvent(
            @JsonProperty("player") final Player player,
            @JsonProperty("callBet") final long callBet,
            @JsonProperty("raiseBet") final long raiseBet) {

        this.player = player;
        this.callBet = callBet;
        this.raiseBet = raiseBet;
    }

    public Player getPlayer() {

        return player;
    }

    public long getCallBet() {

        return callBet;
    }

    public long getRaiseBet() {

        return raiseBet;
    }

    @Override
    public String toString() {

        return "PlayerRaisedEvent [player=" + player + ", callBet=" + callBet
                + ", raiseBet=" + raiseBet + "]";
    }

}
