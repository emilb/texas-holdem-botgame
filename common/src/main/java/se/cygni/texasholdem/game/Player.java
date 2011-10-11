package se.cygni.texasholdem.game;

public class Player {

    private final String name;
    private final long chipCount;
    private final boolean inPlay;

    public Player(final String name, final long chipCount, final boolean inPlay) {

        this.name = name;
        this.chipCount = chipCount;
        this.inPlay = inPlay;
    }

    public String getName() {

        return name;
    }

    public long getChipCount() {

        return chipCount;
    }

    public boolean isInPlay() {

        return inPlay;
    }

}
