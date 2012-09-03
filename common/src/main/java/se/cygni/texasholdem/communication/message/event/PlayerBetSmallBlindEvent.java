package se.cygni.texasholdem.communication.message.event;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.GamePlayer;

@IsATexasMessage
public class PlayerBetSmallBlindEvent extends TexasEvent {

    private final GamePlayer player;
    private final long smallBlind;

    @JsonCreator
    public PlayerBetSmallBlindEvent(@JsonProperty("player") final GamePlayer player,
                                    @JsonProperty("smallBlind") final long smallBlind) {

        this.player = player;
        this.smallBlind = smallBlind;
    }

    public GamePlayer getPlayer() {

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
