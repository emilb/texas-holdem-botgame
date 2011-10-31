package se.cygni.texasholdem.communication.message.request;

import java.util.List;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.Action;

@IsATexasMessage
public class ActionRequest extends TexasRequest {

    private final List<Action> possibleActions;

    public ActionRequest(final List<Action> possibleActions) {

        this.possibleActions = possibleActions;
    }

    public List<Action> getPossibleActions() {

        return possibleActions;
    }
}
