package se.cygni.texasholdem.communication.message.request;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.Room;

@IsATexasMessage
public class RegisterForPlayRequest extends TexasRequest {

    public String name;
    public Room room;
}
