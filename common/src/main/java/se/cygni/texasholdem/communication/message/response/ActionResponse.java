package se.cygni.texasholdem.communication.message.response;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.Action;

@IsATexasMessage
public class ActionResponse extends TexasResponse {

    public Action action;
}
