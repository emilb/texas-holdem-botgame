package se.cygni.texasholdem.communication.message.event;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;

@IsATexasMessage
public class ServerIsShuttingDownEvent extends TexasEvent {

    public String message;
}
