package se.cygni.texasholdem.game.trainingplayers;

import org.apache.commons.lang.math.RandomUtils;
import se.cygni.texasholdem.communication.message.event.*;
import se.cygni.texasholdem.communication.message.request.ActionRequest;
import se.cygni.texasholdem.game.Action;

public class RaiserPlayer extends TrainingPlayer {

    public RaiserPlayer(String name, String sessionId, long chipAmount) {
        super(name, sessionId, chipAmount);
    }

    public RaiserPlayer(String name, String sessionId) {
        super(name, sessionId);
    }

    @Override
    public Action actionRequired(ActionRequest request) {
        Action callAction = null;
        Action checkAction = null;
        Action foldAction = null;
        Action raiseAction = null;

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
                case RAISE:
                    raiseAction = action;
                    break;
                default:
                    break;
            }
        }

        Action action = null;

        double randomVal = RandomUtils.nextDouble();

        // 70% chance of raise
        if (raiseAction != null && randomVal < 0.70) {
            return raiseAction;
        }

        if (callAction != null) {
            return callAction;
        }

        if (checkAction != null) {
            return checkAction;
        }

        return foldAction;
    }
}
