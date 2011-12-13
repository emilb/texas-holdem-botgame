package se.cygni.texasholdem.communication.message.response;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;
import se.cygni.texasholdem.game.Action;

@IsATexasMessage
public class ActionResponse extends TexasResponse {

    private Action action;

    public Action getAction() {

        return action;
    }

    public void setAction(final Action action) {

        this.action = action;
    }

}
