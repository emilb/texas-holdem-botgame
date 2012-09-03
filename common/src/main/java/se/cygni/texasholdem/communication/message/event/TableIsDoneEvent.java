package se.cygni.texasholdem.communication.message.event;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.GamePlayer;

import java.util.List;

@IsATexasMessage
public class TableIsDoneEvent extends TexasEvent {

    private final List<GamePlayer> players;

    @JsonCreator
    public TableIsDoneEvent(
            @JsonProperty("players") final List<GamePlayer> players) {

        this.players = players;
    }

    public List<GamePlayer> getPlayers() {

        return players;
    }

    @Override
    public String toString() {

        return "TableIsDoneEvent [players=" + players + "]";
    }

}
