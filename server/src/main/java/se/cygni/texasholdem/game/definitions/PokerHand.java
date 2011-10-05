package se.cygni.texasholdem.game.definitions;

public enum PokerHand {

    ROYAL_FLUSH(10, "Royal flush"),
    STRAIGHT_FLUSH(9, "Straight flush"),
    FOUR_OF_A_KIND(8, "Four of a kind"),
    FULL_HOUSE(7, "Full house"),
    FLUSH(6, "Flush"),
    STRAIGHT(5, "Straight"),
    THREE_OF_A_KIND(4, "Three of a kind"),
    TWO_PAIRS(3, "Two pair"),
    ONE_PAIR(2, "One pair"),
    HIGH_HAND(1, "High hand");

    private final int orderValue;
    private final String name;

    private PokerHand(final int orderValue, final String name) {

        this.orderValue = orderValue;
        this.name = name;
    }

    public int getOrderValue() {

        return orderValue;
    }

    public String getName() {

        return name;
    }
}
