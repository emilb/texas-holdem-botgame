package se.cygni.texasholdem.game;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class Player {

    private final String name;
    private final long chipCount;

    @JsonCreator
    public Player(@JsonProperty("name") final String name,
                  @JsonProperty("chipCount") final long chipCount) {

        this.name = name;
        this.chipCount = chipCount;
    }

    public String getName() {

        return name;
    }

    public long getChipCount() {

        return chipCount;
    }

    @Override
    public String toString() {

        return "Player [name=" + name + "]";
    }

}
