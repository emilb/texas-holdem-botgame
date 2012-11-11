package se.cygni.texasholdem.communication.message.event;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.GamePlayer;

@IsATexasMessage
public class PlayerForcedFoldedEvent extends TexasEvent {

    private final GamePlayer player;
    private final long investmentInPot;

    @JsonCreator
    public PlayerForcedFoldedEvent(@JsonProperty("player") final GamePlayer player,
                                   @JsonProperty("investmentInPot") final long investmentInPot) {

        this.player = player;
        this.investmentInPot = investmentInPot;
    }

    public GamePlayer getPlayer() {

        return player;
    }

    public long getInvestmentInPot() {

        return investmentInPot;
    }

    @Override
    public String toString() {

        return "PlayerFoldedEvent [player=" + player + ", investmentInPot="
                + investmentInPot + "]";
    }

}
