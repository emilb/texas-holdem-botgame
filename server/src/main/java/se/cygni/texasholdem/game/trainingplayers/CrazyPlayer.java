package se.cygni.texasholdem.game.trainingplayers;

import org.apache.commons.lang.math.RandomUtils;
import se.cygni.texasholdem.communication.message.event.*;
import se.cygni.texasholdem.communication.message.request.ActionRequest;
import se.cygni.texasholdem.game.Action;
import se.cygni.texasholdem.game.ActionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CrazyPlayer extends TrainingPlayer {

    private static final List<ActionTypeWeigth> actionWeight = new ArrayList<CrazyPlayer.ActionTypeWeigth>();

    static {
        actionWeight.add(new ActionTypeWeigth(800, ActionType.CHECK));
        actionWeight.add(new ActionTypeWeigth(600, ActionType.CALL));
        actionWeight.add(new ActionTypeWeigth(200, ActionType.RAISE));
        actionWeight.add(new ActionTypeWeigth(50, ActionType.FOLD));
        actionWeight.add(new ActionTypeWeigth(10, ActionType.ALL_IN));
    }

    public CrazyPlayer(String name, String sessionId, long chipAmount) {
        super(name, sessionId, chipAmount);
    }

    public CrazyPlayer(String name, String sessionId) {
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
    public void onTableIsDone(TableIsDoneEvent event) {
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
    }

    @Override
    public Action actionRequired(ActionRequest request) {
        final List<ActionTypeWeigth> actionWeights = new ArrayList<ActionTypeWeigth>();
        for (final ActionTypeWeigth w : actionWeight) {
            if (hasActionType(request.getPossibleActions(), w.getType()))
                actionWeights.add(w);
        }

        Collections.sort(actionWeights);
        final int totalActionWeight = getTotalActionWeight(actionWeights);
        int randomNumber = RandomUtils.nextInt(totalActionWeight);

        ActionType choosenActionType = null;
        for (final ActionTypeWeigth w : actionWeights) {
            if (randomNumber < w.getWeight()) {
                choosenActionType = w.getType();
                break;
            } else
                randomNumber -= w.getWeight();
        }

        Action action = getActionOfType(request.getPossibleActions(),
                choosenActionType);

        if (choosenActionType == ActionType.RAISE) {
            action = new Action(choosenActionType,
                    getReasonableRaiseAmount(action.getAmount()));
        }
        return action;
    }

    private long getReasonableRaiseAmount(final long minRaise) {
        return minRaise;
//        return (long) (Math.random() * minRaise / 2);
    }

    private int getTotalActionWeight(final List<ActionTypeWeigth> actionWeights) {

        int sum = 0;
        for (final ActionTypeWeigth w : actionWeights)
            sum += w.getWeight();
        return sum;
    }

    private boolean hasActionType(
            final List<Action> actions,
            final ActionType type) {

        for (final Action action : actions) {
            if (action.getActionType() == type)
                return true;
        }

        return false;
    }

    private Action getActionOfType(
            final List<Action> actions,
            final ActionType type) {

        for (final Action action : actions) {
            if (action.getActionType() == type)
                return action;
        }

        return null;
    }

    @Override
    public void connectionToGameServerLost() {
    }

    @Override
    public void connectionToGameServerEstablished() {
    }

    public static final class ActionTypeWeigth implements
            Comparable<ActionTypeWeigth> {

        final int weight;
        final ActionType type;

        public ActionTypeWeigth(final int weight, final ActionType type) {

            this.weight = weight;
            this.type = type;
        }

        public int getWeight() {

            return weight;
        }

        public ActionType getType() {

            return type;
        }

        @Override
        public int compareTo(final ActionTypeWeigth other) {

            final Integer thisWeight = Integer.valueOf(weight);
            final Integer otherWeight = Integer.valueOf(other.getWeight());
            return thisWeight.compareTo(otherWeight) * -1;
        }

    }
}
