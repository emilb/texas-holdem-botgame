package se.cygni.texasholdem.communication.message.event;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.Player;

@IsATexasMessage
public class PlayerBetSmallBlindEvent extends TexasEvent {

    private final Player player;
    private final long smallBlind;

    @JsonCreator
    public PlayerBetSmallBlindEvent(@JsonProperty("player") final Player player,
                                    @JsonProperty("smallBlind") final long smallBlind) {

        this.player = player;
        this.smallBlind = smallBlind;
    }

    public Player getPlayer() {

        return player;
    }

    public long getSmallBlind() {

        return smallBlind;
    }

    @Override
    public String toString() {

        return "PlayerCalledEvent [player=" + player + ", smallBlind=" + smallBlind
                + "]";
    }
}
