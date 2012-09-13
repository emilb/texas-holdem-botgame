package se.cygni.texasholdem.game.definitions;

public enum PokerHand {

    ROYAL_FLUSH(10, "Royal flush", 5),
    STRAIGHT_FLUSH(9, "Straight flush", 5),
    FOUR_OF_A_KIND(8, "Four of a kind", 4),
    FULL_HOUSE(7, "Full house", 5),
    FLUSH(6, "Flush", 5),
    STRAIGHT(5, "Straight", 5),
    THREE_OF_A_KIND(4, "Three of a kind", 3),
    TWO_PAIRS(3, "Two pair", 4),
    ONE_PAIR(2, "One pair", 2),
    HIGH_HAND(1, "High hand", 1),
    NOTHING(0, "No hand", 0);

    private final int orderValue;
    private final String name;
    private final int cardsRequired;

    private PokerHand(final int orderValue, final String name, final int cardsRequired) {

        this.orderValue = orderValue;
        this.name = name;
        this.cardsRequired = cardsRequired;
    }

    public int getOrderValue() {

        return orderValue;
    }

    public String getName() {

        return name;
    }

    public int getCardsRequired() {
        return cardsRequired;
    }
}
