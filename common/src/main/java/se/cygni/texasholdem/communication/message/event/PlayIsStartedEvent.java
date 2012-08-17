package se.cygni.texasholdem.communication.message.event;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.Player;

import java.util.List;

@IsATexasMessage
public class PlayIsStartedEvent extends TexasEvent {

    private final List<Player> players;
    private final long smallBlindAmount;
    private final long bigBlindAmount;
    private final Player dealer;
    private final Player smallBlindPlayer;
    private final Player bigBlindPlayer;

    @JsonCreator
    public PlayIsStartedEvent(
            @JsonProperty("players") final List<Player> players,
            @JsonProperty("smallBlindAmount") final long smallBlindAmount,
            @JsonProperty("bigBlindAmount") final long bigBlindAmount,
            @JsonProperty("dealer") final Player dealer,
            @JsonProperty("smallBlindPlayer") final Player smallBlindPlayer,
            @JsonProperty("bigBlindPlayer") final Player bigBlindPlayer) {

        this.players = players;
        this.smallBlindAmount = smallBlindAmount;
        this.bigBlindAmount = bigBlindAmount;
        this.dealer = dealer;
        this.smallBlindPlayer = smallBlindPlayer;
        this.bigBlindPlayer = bigBlindPlayer;
    }

    public List<Player> getPlayers() {

        return players;
    }

    public long getSmallBlindAmount() {

        return smallBlindAmount;
    }

    public long getBigBlindAmount() {

        return bigBlindAmount;
    }

    public Player getDealer() {

        return dealer;
    }

    public Player getSmallBlindPlayer() {

        return smallBlindPlayer;
    }

    public Player getBigBlindPlayer() {

        return bigBlindPlayer;
    }

    @Override
    public String toString() {

        return "PlayIsStartedEvent [players=" + players + ", smallBlindAmount="
                + smallBlindAmount + ", bigBlindAmount=" + bigBlindAmount
                + ", dealer=" + dealer + ", smallBlindPlayer="
                + smallBlindPlayer + ", bigBlindPlayer=" + bigBlindPlayer + "]";
    }

}
