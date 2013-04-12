package se.cygni.webapp.controllers.controllers.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class WebSocketCommand {

    public String command;
    public String value;

    @JsonCreator
    public WebSocketCommand(
            @JsonProperty("command") String command,
            @JsonProperty("value") String value) {
        this.command = command;
        this.value = value;
    }
}
