package se.cygni.texasholdem.communication.message.event;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.definitions.PlayState;

@IsATexasMessage
public class TableChangedStateEvent extends TexasEvent {

    private final PlayState state;

    @JsonCreator
    public TableChangedStateEvent(@JsonProperty("state") final PlayState state) {

        this.state = state;
    }

    public PlayState getState() {

        return state;
    }

    @Override
    public String toString() {

        return "TableChangedStateEvent [state=" + state + "]";
    }

}
