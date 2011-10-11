package se.cygni.texasholdem.game;

public class Action {

    private final ActionType actionType;
    private final long amount;

    public Action(final ActionType actionType, final long amount) {

        this.actionType = actionType;
        this.amount = amount;
    }

    public ActionType getActionType() {

        return actionType;
    }

    public long getAmount() {

        return amount;
    }

}
