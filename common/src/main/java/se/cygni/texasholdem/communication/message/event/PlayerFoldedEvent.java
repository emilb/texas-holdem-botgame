package se.cygni.texasholdem.communication.message.event;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.Player;

@IsATexasMessage
public class PlayerFoldedEvent extends TexasEvent {

    private final Player player;
    private final long investmentInPot;

    @JsonCreator
    public PlayerFoldedEvent(@JsonProperty("player") final Player player,
            @JsonProperty("investmentInPot") final long investmentInPot) {

        this.player = player;
        this.investmentInPot = investmentInPot;
    }

    public Player getPlayer() {

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
