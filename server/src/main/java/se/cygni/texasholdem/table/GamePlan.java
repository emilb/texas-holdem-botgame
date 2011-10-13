package se.cygni.texasholdem.table;

public class GamePlan {

    public enum BlindRaiseStrategy {
        FACTOR,
        FIX_AMOUNT
    }

    private final int playsBetweenBlindRaise;
    private final BlindRaiseStrategy blindRaiseStrategy;
    private final long smalBlindStart;
    private final long bigBlindStart;
    private final long blindRaiseStrategyValue;

    public GamePlan() {

        this.playsBetweenBlindRaise = 10;
        this.smalBlindStart = 1;
        this.bigBlindStart = 2;
        this.blindRaiseStrategy = BlindRaiseStrategy.FIX_AMOUNT;
        this.blindRaiseStrategyValue = 2;
    }

    public GamePlan(final int playsBetweenBlindRaise,
            final long smalBlindStart,
            final long bigBlindStart,
            final BlindRaiseStrategy blindRaiseStrategy,
            final long blindRaiseStrategyValue) {

        this.playsBetweenBlindRaise = playsBetweenBlindRaise;
        this.smalBlindStart = smalBlindStart;
        this.bigBlindStart = bigBlindStart;
        this.blindRaiseStrategy = blindRaiseStrategy;
        this.blindRaiseStrategyValue = blindRaiseStrategyValue;
    }

    public int getPlaysBetweenBlindRaise() {

        return playsBetweenBlindRaise;
    }

    public BlindRaiseStrategy getBlindRaiseStrategy() {

        return blindRaiseStrategy;
    }

    public long getSmalBlindStart() {

        return smalBlindStart;
    }

    public long getBigBlindStart() {

        return bigBlindStart;
    }

    public long getBlindRaiseStrategyValue() {

        return blindRaiseStrategyValue;
    }

}
