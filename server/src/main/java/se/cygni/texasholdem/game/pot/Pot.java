package se.cygni.texasholdem.game.pot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.collections.CollectionUtils;

import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.game.definitions.PlayState;

/**
 * The Pot manages the state of a play and the betting statuses of each player.
 * 
 * @author emil
 * 
 */
public class Pot {

    private final AtomicLong transactionCounter = new AtomicLong(0);
    private PlayState currentPlayState = PlayState.PRE_FLOP;

    private final Map<PlayState, List<PotTransaction>> transactionTable = new HashMap<PlayState, List<PotTransaction>>();
    private final List<BotPlayer> allPlayers;
    private final Set<BotPlayer> foldedPlayers = new HashSet<BotPlayer>();

    public Pot(final List<BotPlayer> players) {

        allPlayers = new ArrayList<BotPlayer>(players);
    }

    /**
     * Calculates the bet the player has to make to Call. If player has folded 0
     * is returned.
     * 
     * @param player
     * @return
     */
    public long getMinimumBetForPlayerToCall(final BotPlayer player) {

        if (hasFolded(player))
            return 0L;

        final long max = getMaxTotalBetInCurrentPlayState();
        final long playerBet = getTotalBetAmountForPlayerInPlayState(player,
                currentPlayState);
        return max - playerBet;
    }

    /**
     * Places a bet for the player.
     * 
     * @throws IllegalStateException
     *             if in a state that doesn't allow bets
     * @throws IllegalStateException
     *             if player has folded
     * @throws IllegalArgumentException
     *             if bet is negative
     * @param player
     * @param amount
     */
    public void bet(final BotPlayer player, final long amount) {

        if (!canPlaceBetInCurrentPlayState())
            throw new IllegalStateException("Pot is in state: "
                    + currentPlayState.getName() + ", no more bets allowed");

        if (foldedPlayers.contains(player))
            throw new IllegalStateException("Player: " + player
                    + " has folded, cannot bet");

        if (amount < 0)
            throw new IllegalArgumentException("Player: " + player
                    + " tried to place negative bet");

        if (amount == 0)
            return;

        if (!transactionTable.containsKey(currentPlayState)) {
            transactionTable.put(currentPlayState,
                    new ArrayList<PotTransaction>());
        }

        final PotTransaction transaction = new PotTransaction(
                transactionCounter.getAndIncrement(), player, amount);
        transactionTable.get(currentPlayState).add(transaction);
    }

    /**
     * Folds a player. This player will not be allowed to place any more bets.
     * 
     * @param player
     */
    public void fold(final BotPlayer player) {

        foldedPlayers.add(player);
    }

    /**
     * 
     * @param player
     * @return TRUE if player has folded any time during this play.
     */
    public boolean hasFolded(final BotPlayer player) {

        return foldedPlayers.contains(player);
    }

    /**
     * Changes PlayState to the next.
     * 
     * @throws IllegalStateException
     *             if the current PlayState is not balanced.
     * @throws IllegalStateException
     *             if already at the last PlayState.
     * @return the new PlayState
     */
    public PlayState nextPlayState() {

        if (!isCurrentPlayStateBalanced())
            throw new IllegalStateException(
                    "Current state is not balanced, cannot enter next state");

        currentPlayState = PlayState.getNextState(currentPlayState);
        return currentPlayState;
    }

    public PlayState getCurrentPlayState() {

        return currentPlayState;
    }

    /**
     * Calculates the total amount in the Pot.
     * 
     * @return
     */
    public long getTotalPotAmount() {

        long total = 0;

        for (final Entry<PlayState, List<PotTransaction>> entry : transactionTable
                .entrySet()) {
            for (final PotTransaction transaction : entry.getValue()) {
                total += transaction.getAmount();
            }
        }

        return total;
    }

    /**
     * Calculates the total bet amount per player in the current PlayState and
     * returns the highest.
     * 
     * @return
     */
    public long getMaxTotalBetInCurrentPlayState() {

        long highest = 0;

        for (final BotPlayer player : allPlayers) {
            if (foldedPlayers.contains(player))
                continue;

            final long playerTotal = getTotalBetAmountForPlayerInPlayState(
                    player, currentPlayState);
            highest = highest < playerTotal ? playerTotal : highest;
        }

        return highest;
    }

    /**
     * Calculates the total amount for all bets placed by Player during this
     * whole play.
     * 
     * @param player
     * @return
     */
    public long getTotalBetAmountForPlayer(final BotPlayer player) {

        long total = 0;
        PlayState state = currentPlayState;
        total += getTotalBetAmountForPlayerInPlayState(player, state);

        while (PlayState.hasPreviousState(state)) {
            state = PlayState.getPreviousState(state);
            total += getTotalBetAmountForPlayerInPlayState(player, state);
        }

        return total;
    }

    /**
     * Calculates the amount of all the bets placed by the Player in specified
     * PlayState.
     * 
     * @param player
     * @param playState
     * @return
     */
    public long getTotalBetAmountForPlayerInPlayState(
            final BotPlayer player,
            final PlayState playState) {

        final List<PotTransaction> transactions = transactionTable
                .get(playState);
        if (CollectionUtils.isEmpty(transactions))
            return 0L;

        long total = 0;
        for (final PotTransaction transaction : transactions) {
            if (transaction.getPlayer().equals(player))
                total += transaction.getAmount();
        }

        return total;
    }

    /**
     * Checks that all active players have placed bets amounting to the same
     * value in the current play state.
     * 
     * @return
     */
    public boolean isCurrentPlayStateBalanced() {

        Long lastTotal = null;
        for (final BotPlayer player : allPlayers) {
            if (foldedPlayers.contains(player))
                continue;

            final long playerTotal = getTotalBetAmountForPlayerInPlayState(
                    player, currentPlayState);
            if (lastTotal == null)
                lastTotal = Long.valueOf(playerTotal);
            else if (lastTotal != playerTotal)
                return false;
        }

        return true;
    }

    /**
     * Bets are allowed in all states except the last.
     * 
     * @return TRUE if allowed to place bet.
     */
    protected boolean canPlaceBetInCurrentPlayState() {

        return PlayState.hasNextState(currentPlayState);
    }
}
