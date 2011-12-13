package se.cygni.texasholdem.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.cygni.texasholdem.communication.message.request.ActionRequest;
import se.cygni.texasholdem.game.Action;
import se.cygni.texasholdem.game.ActionType;

public class RandomPlayer extends BasicPlayer {

    private static Logger log = LoggerFactory
            .getLogger(RandomPlayer.class);

    private final String name = "random" + (int) (90 * Math.random() + 10);

    private static final List<ActionTypeWeigth> actionWeight = new ArrayList<RandomPlayer.ActionTypeWeigth>();

    static {
        actionWeight.add(new ActionTypeWeigth(800, ActionType.CHECK));
        actionWeight.add(new ActionTypeWeigth(600, ActionType.CALL));
        actionWeight.add(new ActionTypeWeigth(200, ActionType.RAISE));
        actionWeight.add(new ActionTypeWeigth(100, ActionType.FOLD));
        actionWeight.add(new ActionTypeWeigth(1, ActionType.ALL_IN));
    }

    private static final Map<ActionType, Integer> actionDistributionMap = new HashMap<ActionType, Integer>();

    static {
        actionDistributionMap.put(ActionType.RAISE, 50);
        actionDistributionMap.put(ActionType.CALL, 30);
        actionDistributionMap.put(ActionType.FOLD, 10);
        actionDistributionMap.put(ActionType.ALL_IN, 5);
        actionDistributionMap.put(ActionType.CHECK, 5);
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public Action actionRequired(final ActionRequest request) {

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
            if (randomNumber < w.getWeight())
                choosenActionType = w.getType();
            else
                randomNumber -= w.getWeight();
        }

        Action action = getActionOfType(request.getPossibleActions(),
                choosenActionType);

        if (choosenActionType == ActionType.RAISE) {
            action = new Action(choosenActionType,
                    getReasonableRaiseAmount(action.getAmount()));
        }

        log.debug("{} returning action: {}", getName(), action);
        return action;
    }

    private long getReasonableRaiseAmount(final long maxRaise) {

        return (long) (Math.random() * maxRaise / 2);
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
