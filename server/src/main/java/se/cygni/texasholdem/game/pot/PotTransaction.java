package se.cygni.texasholdem.game.pot;

import se.cygni.texasholdem.game.BotPlayer;

public class PotTransaction {

    private final long transactionNumber;
    private final BotPlayer player;
    private final long amount;

    public PotTransaction(final long transactionNumber, final BotPlayer player,
            final long amount) {

        this.transactionNumber = transactionNumber;
        this.player = player;
        this.amount = amount;
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
}
