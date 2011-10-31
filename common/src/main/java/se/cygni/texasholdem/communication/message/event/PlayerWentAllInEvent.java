package se.cygni.texasholdem.communication.message.event;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.Player;

@IsATexasMessage
public class PlayerWentAllInEvent extends TexasEvent {

    private final Player player;
    private final long allInAmount;

    @JsonCreator
    public PlayerWentAllInEvent(
            @JsonProperty("player") final Player player,
            @JsonProperty("allInAmount") final long allInAmount) {

        this.player = player;
        this.allInAmount = allInAmount;
    }

    public Player getPlayer() {

        return player;
    }

    public long getAllInAmount() {

        return allInAmount;
    }

    @Override
    public String toString() {

        return "PlayerWentAllInEvent [player=" + player + ", allInAmount="
                + allInAmount + "]";
    }

}
