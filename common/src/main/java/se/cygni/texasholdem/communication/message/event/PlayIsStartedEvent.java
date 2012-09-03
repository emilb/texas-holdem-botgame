package se.cygni.texasholdem.communication.message.event;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.GamePlayer;

import java.util.List;

@IsATexasMessage
public class PlayIsStartedEvent extends TexasEvent {

    private final List<GamePlayer> players;
    private final long smallBlindAmount;
    private final long bigBlindAmount;
    private final GamePlayer dealer;
    private final GamePlayer smallBlindPlayer;
    private final GamePlayer bigBlindPlayer;
    private final long tableId;

    @JsonCreator
    public PlayIsStartedEvent(
            @JsonProperty("players") final List<GamePlayer> players,
            @JsonProperty("smallBlindAmount") final long smallBlindAmount,
            @JsonProperty("bigBlindAmount") final long bigBlindAmount,
            @JsonProperty("dealer") final GamePlayer dealer,
            @JsonProperty("smallBlindPlayer") final GamePlayer smallBlindPlayer,
            @JsonProperty("bigBlindPlayer") final GamePlayer bigBlindPlayer,
            @JsonProperty("tableId") final long tableId ) {

        this.players = players;
        this.smallBlindAmount = smallBlindAmount;
        this.bigBlindAmount = bigBlindAmount;
        this.dealer = dealer;
        this.smallBlindPlayer = smallBlindPlayer;
        this.bigBlindPlayer = bigBlindPlayer;
        this.tableId = tableId;
    }

    public List<GamePlayer> getPlayers() {

        return players;
    }

    public long getSmallBlindAmount() {

        return smallBlindAmount;
    }

    public long getBigBlindAmount() {

        return bigBlindAmount;
    }

    public GamePlayer getDealer() {

        return dealer;
    }

    public GamePlayer getSmallBlindPlayer() {

        return smallBlindPlayer;
    }

    public GamePlayer getBigBlindPlayer() {

        return bigBlindPlayer;
    }

    public long getTableId() {
        return tableId;
    }

    @Override
    public String toString() {

        return "PlayIsStartedEvent [players=" + players + ", smallBlindAmount="
                + smallBlindAmount + ", bigBlindAmount=" + bigBlindAmount
                + ", dealer=" + dealer + ", smallBlindPlayer="
                + smallBlindPlayer + ", bigBlindPlayer=" + bigBlindPlayer + "]";
    }

}
