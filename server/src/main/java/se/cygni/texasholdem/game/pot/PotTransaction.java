package se.cygni.texasholdem.game.pot;

import se.cygni.texasholdem.game.Player;

public class PotTransaction {

    private final long transactionNumber;
    private final Player player;
    private final long amount;

    public PotTransaction(final long transactionNumber, final Player player,
            final long amount) {

        this.transactionNumber = transactionNumber;
        this.player = player;
        this.amount = amount;
    }

    public long getTransactionNumber() {

        return transactionNumber;
    }

    public Player getPlayer() {

        return player;
    }

    public long getAmount() {

        return amount;
    }
}
