package se.cygni.texasholdem.table;

import org.springframework.stereotype.Component;

import se.cygni.texasholdem.util.SystemFieldPopulator;

@Component
public class GamePlan {

    public static final String PREFIX_PROPERTY = "texas.gameplan.";

    public enum BlindRaiseStrategy {
        FACTOR,
        FIX_AMOUNT
    }

    private long startingChipsAmount;
    private int playsBetweenBlindRaise;
    private BlindRaiseStrategy blindRaiseStrategy;
    private long smalBlindStart;
    private long bigBlindStart;
    private long blindRaiseStrategyValue;

    public GamePlan() {

        this.startingChipsAmount = 1000;
        this.playsBetweenBlindRaise = 10;
        this.smalBlindStart = 25;
        this.bigBlindStart = 50;
        this.blindRaiseStrategy = BlindRaiseStrategy.FIX_AMOUNT;
        this.blindRaiseStrategyValue = 2;

        // Override values from system properties
        final SystemFieldPopulator fieldPopulator = new SystemFieldPopulator(
                this, PREFIX_PROPERTY);
        fieldPopulator.populateValuesFromSystemProperties();
    }

    public GamePlan(final long startingChipsAmount,
            final int playsBetweenBlindRaise,
            final long smalBlindStart,
            final long bigBlindStart,
            final BlindRaiseStrategy blindRaiseStrategy,
            final long blindRaiseStrategyValue) {

        this.startingChipsAmount = startingChipsAmount;
        this.playsBetweenBlindRaise = playsBetweenBlindRaise;
        this.smalBlindStart = smalBlindStart;
        this.bigBlindStart = bigBlindStart;
        this.blindRaiseStrategy = blindRaiseStrategy;
        this.blindRaiseStrategyValue = blindRaiseStrategyValue;
    }

    public long getStartingChipsAmount() {

        return startingChipsAmount;
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

    @SuppressWarnings("unused")
    private void setStartingChipsAmount(final long startingChipsAmount) {

        this.startingChipsAmount = startingChipsAmount;
    }

    @SuppressWarnings("unused")
    private void setPlaysBetweenBlindRaise(final int playsBetweenBlindRaise) {

        this.playsBetweenBlindRaise = playsBetweenBlindRaise;
    }

    @SuppressWarnings("unused")
    private void setBlindRaiseStrategy(
            final BlindRaiseStrategy blindRaiseStrategy) {

        this.blindRaiseStrategy = blindRaiseStrategy;
    }

    @SuppressWarnings("unused")
    private void setSmalBlindStart(final long smalBlindStart) {

        this.smalBlindStart = smalBlindStart;
    }

    @SuppressWarnings("unused")
    private void setBigBlindStart(final long bigBlindStart) {

        this.bigBlindStart = bigBlindStart;
    }

    @SuppressWarnings("unused")
    private void setBlindRaiseStrategyValue(final long blindRaiseStrategyValue) {

        this.blindRaiseStrategyValue = blindRaiseStrategyValue;
    }

}
