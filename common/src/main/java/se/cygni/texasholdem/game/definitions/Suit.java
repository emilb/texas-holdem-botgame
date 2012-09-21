package se.cygni.texasholdem.game.definitions;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum Suit {

    CLUBS("c", "Clubs"),
    DIAMONDS("d", "Diamonds"),
    HEARTS("h", "Hearts"),
    SPADES("s", "Spades");

    private final String shortName;
    private final String longName;

    private Suit(final String shortName, final String longName) {

        this.shortName = shortName;
        this.longName = longName;
    }

    public String getShortName() {

        return shortName;
    }

    public String getLongName() {

        return longName;
    }

    @Override
    public String toString() {

        return longName;
    }

    private static final Map<String, Suit> lookup = new HashMap<String, Suit>();

    static {
        for (final Suit s : EnumSet.allOf(Suit.class)) {
            lookup.put(s.getShortName(), s);
        }
    }

    public static Suit get(final String name) {

        return lookup.get(name.toLowerCase());
    }
}
