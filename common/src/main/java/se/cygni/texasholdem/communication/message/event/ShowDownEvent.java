package se.cygni.texasholdem.communication.message.event;

import java.util.List;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.PlayerShowDown;

@IsATexasMessage
public class ShowDownEvent extends TexasEvent {

    private final List<PlayerShowDown> playersShowDown;

    @JsonCreator
    public ShowDownEvent(
            @JsonProperty("playersShowDown") final List<PlayerShowDown> playersShowDown) {

        this.playersShowDown = playersShowDown;
    }

    public List<PlayerShowDown> getPlayersShowDown() {

        return playersShowDown;
    }

    @Override
    public String toString() {

        return "ShowDownEvent [playersShowDown=" + playersShowDown + "]";
    }

}
