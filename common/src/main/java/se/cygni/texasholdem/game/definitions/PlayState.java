package se.cygni.texasholdem.game.definitions;

public enum PlayState {

    PRE_FLOP("Pre-flop"),
    FLOP("Flop"),
    TURN("Turn"),
    RIVER("River"),
    SHOWDOWN("Showdown");

    private final String name;

    private PlayState(final String name) {

        this.name = name;
    }

    public String getName() {

        return name;
    }

    public static PlayState getNextState(final PlayState givenState) {

        final PlayState[] allStates = PlayState.values();
        if (givenState.ordinal() == allStates.length - 1)
            throw new IllegalStateException("Already at last PlayState: "
                    + givenState);

        return allStates[givenState.ordinal() + 1];
    }

    public static PlayState getPreviousState(final PlayState givenState) {

        final PlayState[] allStates = PlayState.values();
        if (givenState.ordinal() == 0)
            throw new IllegalStateException("Already at first PlayState: "
                    + givenState);

        return allStates[givenState.ordinal() - 1];
    }

    public static boolean hasNextState(final PlayState givenState) {

        return givenState.ordinal() == PlayState.values().length - 1 ? false
                : true;
    }

    public static boolean hasPreviousState(final PlayState givenState) {

        return givenState.ordinal() == 0 ? false : true;
    }
}
