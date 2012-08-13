package se.cygni.texasholdem.communication.message.event;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.Player;

@IsATexasMessage
public class PlayerBetBigBlindEvent extends TexasEvent {

    private final Player player;
    private final long bigBlind;

    @JsonCreator
    public PlayerBetBigBlindEvent(@JsonProperty("player") final Player player,
                                  @JsonProperty("bigBlind") final long smallBlind) {

        this.player = player;
        this.bigBlind = smallBlind;
    }

    public Player getPlayer() {

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
