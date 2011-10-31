package se.cygni.texasholdem.communication.message.event;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;

@IsATexasMessage
public class ServerIsShuttingDownEvent extends TexasEvent {

    private final String message;

    @JsonCreator
    public ServerIsShuttingDownEvent(
            @JsonProperty("message") final String message) {

        this.message = message;
    }

    public String getMessage() {

        return message;
    }

    @Override
    public String toString() {

        return "ServerIsShuttingDownEvent [message=" + message + "]";
    }

}
