package se.cygni.texasholdem.game.definitions;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum Rank {

    DEUCE(2, "2"),
    THREE(3, "3"),
    FOUR(4, "4"),
    FIVE(5, "5"),
    SIX(6, "6"),
    SEVEN(7, "7"),
    EIGHT(8, "8"),
    NINE(9, "9"),
    TEN(10, "T"),
    JACK(11, "J"),
    QUEEN(12, "Q"),
    KING(13, "K"),
    ACE(14, "A");

    private final int orderValue;
    private final String name;

    private Rank(final int orderValue, final String name) {

        this.orderValue = orderValue;
        this.name = name;
    }

    public int getOrderValue() {

        return orderValue;
    }

    public String getName() {

        return name;
    }

    @Override
    public String toString() {

        return name;
    }

    private static final Map<String, Rank> lookup = new HashMap<String, Rank>();

    static {
        for (final Rank r : EnumSet.allOf(Rank.class)) {
            lookup.put(r.getName(), r);
        }
    }

    public static Rank get(final String name) {

        return lookup.get(name.toUpperCase());
    }
}
