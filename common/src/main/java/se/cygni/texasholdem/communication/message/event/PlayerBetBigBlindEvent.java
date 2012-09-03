package se.cygni.texasholdem.communication.message.event;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.GamePlayer;

@IsATexasMessage
public class PlayerBetBigBlindEvent extends TexasEvent {

    private final GamePlayer player;
    private final long bigBlind;

    @JsonCreator
    public PlayerBetBigBlindEvent(@JsonProperty("player") final GamePlayer player,
                                  @JsonProperty("bigBlind") final long smallBlind) {

        this.player = player;
        this.bigBlind = smallBlind;
    }

    public GamePlayer getPlayer() {

        return player;
    }

    public long getBigBlind() {

        return bigBlind;
    }

    @Override
    public String toString() {

        return "PlayerCalledEvent [player=" + player + ", bigBlind=" + bigBlind
                + "]";
    }
}
