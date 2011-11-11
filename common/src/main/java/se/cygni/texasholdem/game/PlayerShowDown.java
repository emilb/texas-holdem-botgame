package se.cygni.texasholdem.game;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class PlayerShowDown {

    private final Player player;
    private final Hand hand;
    private final long wonAmount;

    @JsonCreator
    public PlayerShowDown(
            @JsonProperty("player") final Player player,
            @JsonProperty("hand") final Hand hand,
            @JsonProperty("wonAmount") final long wonAmount) {

        this.player = player;
        this.hand = hand;
        this.wonAmount = wonAmount;
    }

    public Player getPlayer() {

        return player;
    }

    public Hand getHand() {

        return hand;
    }

    public long getWonAmount() {

        return wonAmount;
    }

}
