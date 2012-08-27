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
    private long smallBlindStart;
    private long smallBlindRaiseStrategyValue;
    private long bigBlindStart;
    private long bigBlindRaiseStrategyValue;
    private int maxNoofTurnsPerState;
    private int maxNoofActionRetries;

    public GamePlan() {

        this.startingChipsAmount = 10000;
        this.playsBetweenBlindRaise = 10;
        this.smallBlindStart = 5;
        this.bigBlindStart = 10;
        this.blindRaiseStrategy = BlindRaiseStrategy.FIX_AMOUNT;
        this.smallBlindRaiseStrategyValue = 5;
        this.bigBlindRaiseStrategyValue = 10;
        this.maxNoofTurnsPerState = 10;
        this.maxNoofActionRetries = 3;

        // Override values from system properties
        final SystemFieldPopulator fieldPopulator = new SystemFieldPopulator(
                this, PREFIX_PROPERTY);
        fieldPopulator.populateValuesFromSystemProperties();
    }

    public GamePlan(final long startingChipsAmount,
                    final int playsBetweenBlindRaise,
                    final long smallBlindStart,
                    final long bigBlindStart,
                    final BlindRaiseStrategy blindRaiseStrategy,
                    final long bigBlindRaiseStrategyValue,
                    final long smallBlindRaiseStrategyValue,
                    final int maxNoofTurnsPerState,
                    final int maxNoofActionRetries) {

        this.startingChipsAmount = startingChipsAmount;
        this.playsBetweenBlindRaise = playsBetweenBlindRaise;
        this.smallBlindStart = smallBlindStart;
        this.bigBlindStart = bigBlindStart;
        this.blindRaiseStrategy = blindRaiseStrategy;
        this.bigBlindRaiseStrategyValue = bigBlindRaiseStrategyValue;
        this.smallBlindRaiseStrategyValue = smallBlindRaiseStrategyValue;
        this.maxNoofTurnsPerState = maxNoofTurnsPerState;
        this.maxNoofActionRetries = maxNoofActionRetries;
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

    public long getSmallBlindStart() {

        return smallBlindStart;
    }

    public long getBigBlindStart() {

        return bigBlindStart;
    }

    public long getBigBlindRaiseStrategyValue() {

        return bigBlindRaiseStrategyValue;
    }

    public long getSmallBlindRaiseStrategyValue() {

        return smallBlindRaiseStrategyValue;
    }

    public int getMaxNoofTurnsPerState() {
        return maxNoofTurnsPerState;
    }

    public int getMaxNoofActionRetries() {
        return maxNoofActionRetries;
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
    private void setSmallBlindStart(final long smallBlindStart) {

        this.smallBlindStart = smallBlindStart;
    }

    @SuppressWarnings("unused")
    private void setBigBlindStart(final long bigBlindStart) {

        this.bigBlindStart = bigBlindStart;
    }

    @SuppressWarnings("unused")
    private void setBigBlindRaiseStrategyValue(
            final long bigBlindRaiseStrategyValue) {

        this.bigBlindRaiseStrategyValue = bigBlindRaiseStrategyValue;
    }

    @SuppressWarnings("unused")
    private void setSmallBlindRaiseStrategyValue(
            final long smallBlindRaiseStrategyValue) {

        this.smallBlindRaiseStrategyValue = smallBlindRaiseStrategyValue;
    }

    @SuppressWarnings("unused")
    private void setMaxNoofTurnsPerState(int maxNoofTurnsPerState) {
        this.maxNoofTurnsPerState = maxNoofTurnsPerState;
    }

    @SuppressWarnings("unused")
    private void setMaxNoofActionRetries(int maxNoofActionRetries) {
        this.maxNoofActionRetries = maxNoofActionRetries;
    }

    public GamePlan createCopy() {
        return new GamePlan(startingChipsAmount,
                playsBetweenBlindRaise,
                smallBlindStart,
                bigBlindStart,
                blindRaiseStrategy,
                bigBlindRaiseStrategyValue,
                smallBlindRaiseStrategyValue,
                maxNoofTurnsPerState,
                maxNoofActionRetries);
    }
}
