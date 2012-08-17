package se.cygni.texasholdem.communication.message.request;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.Action;

import java.util.List;

@IsATexasMessage
public class ActionRequest extends TexasRequest {

    private List<Action> possibleActions;

    public ActionRequest() {

    }

    public ActionRequest(final List<Action> possibleActions) {

        this.possibleActions = possibleActions;
    }

    public List<Action> getPossibleActions() {

        return possibleActions;
    }

    public void setPossibleActions(final List<Action> possibleActions) {

        this.possibleActions = possibleActions;
    }

}
