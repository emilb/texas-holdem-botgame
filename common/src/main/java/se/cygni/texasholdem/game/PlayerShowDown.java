package se.cygni.texasholdem.game;

public class PlayerShowDown {

    private final Player player;
    private final Hand hand;
    private final long wonAmount;

    public PlayerShowDown(final Player player, final Hand hand,
            final long wonAmount) {

        this.player = player;
        this.hand = hand;
        this.wonAmount = wonAmount;
    }

    public Player getPlayer() {

        return player;
    }

    public Hand getHand() {

        return hand;
    }

    public long getWonAmount() {

        return wonAmount;
    }

}
