package se.cygni.texasholdem.game.definitions;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import se.cygni.texasholdem.communication.json.SuitDeserializer;
import se.cygni.texasholdem.communication.json.SuitSerializer;

@JsonSerialize(using = SuitSerializer.class)
@JsonDeserialize(using = SuitDeserializer.class)
public enum Suit {

    CLUBS("c", "Clubs"),
    DIAMONDS("d", "Diamomds"),
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
        for (final Suit s : EnumSet.allOf(Suit.class))
            lookup.put(s.getShortName(), s);
    }

    public static Suit get(final String name) {

        return lookup.get(name.toLowerCase());
    }
}