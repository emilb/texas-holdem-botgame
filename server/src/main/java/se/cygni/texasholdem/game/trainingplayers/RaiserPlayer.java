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
    public void serverIsShuttingDown(ServerIsShuttingDownEvent event) {
    }

    @Override
    public void onPlayIsStarted(PlayIsStartedEvent event) {
    }

    @Override
    public void onTableChangedStateEvent(TableChangedStateEvent event) {
    }

    @Override
    public void onYouHaveBeenDealtACard(YouHaveBeenDealtACardEvent event) {
    }

    @Override
    public void onCommunityHasBeenDealtACard(CommunityHasBeenDealtACardEvent event) {
    }

    @Override
    public void onPlayerBetBigBlind(PlayerBetBigBlindEvent event) {
    }

    @Override
    public void onPlayerBetSmallBlind(PlayerBetSmallBlindEvent event) {
    }

    @Override
    public void onPlayerFolded(PlayerFoldedEvent event) {
    }

    @Override
    public void onTableIsDone(TableIsDoneEvent event) {
    }

    @Override
    public void onPlayerCalled(PlayerCalledEvent event) {
    }

    @Override
    public void onPlayerRaised(PlayerRaisedEvent event) {
    }

    @Override
    public void onPlayerWentAllIn(PlayerWentAllInEvent event) {
    }

    @Override
    public void onPlayerChecked(PlayerCheckedEvent event) {
    }

    @Override
    public void onYouWonAmount(YouWonAmountEvent event) {
    }

    @Override
    public void onShowDown(ShowDownEvent event) {
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
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

        // 60% chance of check
        if (checkAction != null && randomVal < 0.60)
            action = checkAction;

        // 90% chance of call
        else if (callAction != null && raiseAction != null) {
            if (randomVal < 0.90)
                action = callAction;
            else
                action = raiseAction;
        }

        else if (raiseAction != null)
            action = raiseAction;

        else if (callAction != null)
            action = callAction;

        else if (checkAction != null)
            action = checkAction;

        else
            action = foldAction;

        return action;
    }

    @Override
    public void connectionToGameServerLost() {
    }

    @Override
    public void connectionToGameServerEstablished() {
    }
}
