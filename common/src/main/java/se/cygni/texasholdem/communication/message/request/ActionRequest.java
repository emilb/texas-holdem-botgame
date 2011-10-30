package se.cygni.texasholdem.communication.message.request;

import java.util.List;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.Action;

@IsATexasMessage
public class ActionRequest extends TexasRequest {

    public List<Action> possibleActions;
}
