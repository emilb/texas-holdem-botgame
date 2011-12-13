package se.cygni.texasholdem.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.cygni.texasholdem.communication.message.request.ActionRequest;
import se.cygni.texasholdem.game.Action;

public class DummyPlayer extends BasicPlayer {

    private static Logger log = LoggerFactory
            .getLogger(DummyPlayer.class);

    private final String name = "dummmy" + (int) (90 * Math.random() + 10);

    @Override
    public String getName() {

        return name;
    }

    @Override
    public Action actionRequired(final ActionRequest request) {

        Action callAction = null;
        Action checkAction = null;
        Action foldAction = null;

        for (final Action action : request.getPossibleActions()) {
            switch (action.getActionType()) {
                case CALL:
                    callAction = action;
                    break;
                case CHECK:
                    checkAction = action;
                    break;
                case FOLD:
                    foldAction = action;
                    break;
                default:
                    break;
            }
        }

        Action action = null;
        if (callAction != null)
            action = callAction;
        else if (checkAction != null)
            action = checkAction;
        else
            action = foldAction;

        log.debug("{} returning action: {}", getName(), action);
        return action;
    }

}
