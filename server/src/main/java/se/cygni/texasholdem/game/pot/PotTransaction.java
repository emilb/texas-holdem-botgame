package se.cygni.texasholdem.game.pot;

import se.cygni.texasholdem.game.ActionType;
import se.cygni.texasholdem.game.BotPlayer;

public class PotTransaction {

    private final long transactionNumber;
    private final BotPlayer player;
    private final long amount;
    private final boolean allIn;
    private final ActionType actionType;

    public PotTransaction(final long transactionNumber, final BotPlayer player,
                          final long amount, final boolean allIn, ActionType actionType) {

        this.transactionNumber = transactionNumber;
        this.player = player;
        this.amount = amount;
        this.allIn = allIn;
        this.actionType = actionType;
    }

    public long getTransactionNumber() {

        return transactionNumber;
    }

    public BotPlayer getPlayer() {

        return player;
    }

    public long getAmount() {

        return amount;
    }

    public boolean isAllIn() {

        return allIn;
    }

    public ActionType getActionType() {
        return actionType;
    }

    @Override
    public String toString() {

        return "PotTransaction [transactionNumber=" + transactionNumber
                + ", player=" + player + ", amount=" + amount + ", allIn="
                + allIn + "]";
    }

}
