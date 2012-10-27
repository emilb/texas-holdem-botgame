package se.cygni.texasholdem.game.pot;

import org.apache.commons.collections.CollectionUtils;
import se.cygni.texasholdem.game.ActionType;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.game.definitions.PlayState;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The Pot manages the state of a play and the betting statuses of each player.
 *
 * @author emil
 */
public class Pot {

    private final AtomicLong transactionCounter = new AtomicLong(0);
    private PlayState currentPlayState = PlayState.PRE_FLOP;

    private final Map<PlayState, List<PotTransaction>> transactionTable = new HashMap<PlayState, List<PotTransaction>>();
    private final List<BotPlayer> allPlayers;
    private final Set<BotPlayer> foldedPlayers = new HashSet<BotPlayer>();
    private final Set<BotPlayer> allInPlayers = new HashSet<BotPlayer>();

    private final Map<BotPlayer, PlayState> playerFoldedInStateMap = new HashMap<BotPlayer, PlayState>();
    private final Map<BotPlayer, PlayState> playerWentAllInStateMap = new HashMap<BotPlayer, PlayState>();

    public Pot(final List<BotPlayer> players) {

        allPlayers = Collections.synchronizedList(new ArrayList<BotPlayer>(
                players));
    }

    public List<BotPlayer> getAllPlayers() {
        return allPlayers;
    }

    /**
     * Calculates the bet the player has to make to Call. If player has folded 0
     * is returned.
     *
     * @param player
     *
     * @return
     */
    public long getMinimumBetForPlayerToCall(final BotPlayer player) {

        if (hasFolded(player) || isAllIn(player)) {
            return 0L;
        }

        final long max = getMaxTotalBetInCurrentPlayState();
        final long playerBet = getTotalBetAmountForPlayerInPlayState(player,
                currentPlayState);
        return max - playerBet;
    }

    /**
     * Places a bet for the player.
     *
     * @param player
     * @param amount
     *
     * @throws IllegalStateException    if in a state that doesn't allow bets
     * @throws IllegalStateException    if player has folded
     * @throws IllegalArgumentException if bet is negative
     */
    public long bet(final BotPlayer player, final long amount) {

        if (!canPlaceBetInCurrentPlayState()) {
            throw new IllegalStateException("Pot is in state: "
                    + currentPlayState.getName() + ", no more bets allowed");
        }

        if (foldedPlayers.contains(player)) {
            throw new IllegalStateException("Player: " + player
                    + " has folded, cannot bet");
        }

        if (amount < 0) {
            throw new IllegalArgumentException("Player: " + player
                    + " tried to place negative bet");
        }

        if (amount == 0) {
            return 0;
        }

        if (player.getChipAmount() <= 0) {
            throw new IllegalStateException("Player: " + player
                    + " has run out of chips, cannot bet");
        }

        if (!transactionTable.containsKey(currentPlayState)) {
            transactionTable.put(currentPlayState,
                    new ArrayList<PotTransaction>());
        }

        // Verify that player can cover the bet, otherwise withdraw as much as
        // possible and go all in.
        final long realAmount = player.getChipAmount() >= amount ? amount
                : player.getChipAmount();

        // Subtract the bet from player
        player.getChips(realAmount);

        final boolean isAllIn = player.getChipAmount() == 0;

        ActionType derivedActionType = null;
        long amountNeededToCall = getAmountNeededToCall(player);
        if (isAllIn) {
            derivedActionType = ActionType.ALL_IN;
        }
        else if (realAmount == amountNeededToCall) {
            derivedActionType = ActionType.CALL;
        }
        else if (realAmount > amountNeededToCall) {
            derivedActionType = ActionType.RAISE;
        }

        final PotTransaction transaction = new PotTransaction(
                transactionCounter.getAndIncrement(), player, realAmount,
                isAllIn, derivedActionType);

        transactionTable.get(currentPlayState).add(transaction);

        if (isAllIn) {
            allInPlayers.add(player);
            playerWentAllInStateMap.put(player, getCurrentPlayState());
        }
        return realAmount;
    }

    /**
     * Folds a player. This player will not be allowed to place any more bets.
     *
     * @param player
     */
    public void fold(final BotPlayer player) {

        foldedPlayers.add(player);
        playerFoldedInStateMap.put(player, getCurrentPlayState());
    }

    /**
     * @param player
     *
     * @return TRUE if player has folded any time during this play.
     */
    public boolean hasFolded(final BotPlayer player) {

        return foldedPlayers.contains(player);
    }

    public PlayState getPlayStateForFold(BotPlayer player) {

        return playerFoldedInStateMap.get(player);
    }

    /**
     * @param player
     *
     * @return TRUE if player has gone all in any time during this play.
     */
    public boolean isAllIn(final BotPlayer player) {

        return allInPlayers.contains(player);
    }

    public PlayState getPlayStateForAllIn(BotPlayer player) {

        return playerWentAllInStateMap.get(player);
    }

    /**
     * @param player
     *
     * @return TRUE if player is still able to bet more (i.e. has money left, has not folded or gone all in)
     */
    public boolean isAbleToBet(final BotPlayer player) {

        return !hasFolded(player) && !isAllIn(player) && player.getChipAmount() > 0;
    }

    /**
     * @return The number of players that are able to make a bet
     */
    public int getNoofPlayersAbleToBet() {
        int counter = 0;

        for (BotPlayer player : allPlayers) {
            counter += isAbleToBet(player) ? 1 : 0;
        }

        return counter;
    }

    /**
     * Changes PlayState to the next.
     *
     * @return the new PlayState
     *
     * @throws IllegalStateException if the current PlayState is not balanced.
     * @throws IllegalStateException if already at the last PlayState.
     */
    public PlayState nextPlayState() {

        if (!isCurrentPlayStateBalanced()) {
            throw new IllegalStateException(
                    "Current state is not balanced, cannot enter next state");
        }

        currentPlayState = PlayState.getNextState(currentPlayState);
        return currentPlayState;
    }

    public PlayState getCurrentPlayState() {

        return currentPlayState;
    }

    public boolean isInPlayState(PlayState state) {
        return currentPlayState == state;
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
            if (foldedPlayers.contains(player)) {
                continue;
            }

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
     *
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
     *
     * @return
     */
    public long getTotalBetAmountForPlayerInPlayState(
            final BotPlayer player,
            final PlayState playState) {

        final List<PotTransaction> transactions = transactionTable
                .get(playState);
        if (CollectionUtils.isEmpty(transactions)) {
            return 0L;
        }

        long total = 0;
        for (final PotTransaction transaction : transactions) {
            if (transaction.getPlayer().equals(player)) {
                total += transaction.getAmount();
            }
        }

        return total;
    }

    public long getAmountNeededToCall(final BotPlayer player) {

        if (foldedPlayers.contains(player)) {
            return 0;
        }

        if (allInPlayers.contains(player)) {
            return 0;
        }

        if (isCurrentPlayStateBalanced()) {
            return 0;
        }

        final long maxPlayersTotalInCurrentPlayState = getMaxTotalBetInCurrentPlayState();

        return maxPlayersTotalInCurrentPlayState
                - getTotalBetAmountForPlayerInPlayState(player,
                getCurrentPlayState());

    }

    /**
     * Returns the list of transactions that happened in the supplied state. An
     * empty list if no transactions happened during the state or if the state
     * has not yet happened.
     *
     * @param state
     *
     * @return List of transactions
     */
    public List<PotTransaction> getTransactionsForState(final PlayState state) {

        final List<PotTransaction> transactions = new ArrayList<PotTransaction>();
        if (transactionTable.containsKey(state)) {
            transactions.addAll(transactionTable.get(state));
        }

        return transactions;
    }

    /**
     * Checks that all active players have placed bets amounting to the same
     * value in the current play state.
     *
     * @return
     */
    public boolean isCurrentPlayStateBalanced() {

        final long maxPlayersTotalInCurrentPlayState = getMaxTotalBetInCurrentPlayState();

        for (final BotPlayer player : allPlayers) {
            if (foldedPlayers.contains(player)) {
                continue;
            }

            if (allInPlayers.contains(player)) {
                continue;
            }

            final long playerTotal = getTotalBetAmountForPlayerInPlayState(
                    player, currentPlayState);

            if (maxPlayersTotalInCurrentPlayState != playerTotal) {
                return false;
            }
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

    /**
     * Creates a list of all transactions, sorted by ascending event order.
     *
     * @return A sorted list of all transactions
     */
    protected List<PotTransaction> getAllTransactionsInOrder() {

        final List<PotTransaction> allTransactions = new ArrayList<PotTransaction>();

        for (final List<PotTransaction> stateTrans : transactionTable.values()) {
            allTransactions.addAll(stateTrans);
        }

        Collections.sort(allTransactions, new Comparator<PotTransaction>() {

            @Override
            public int compare(final PotTransaction o1, final PotTransaction o2) {

                final Long firstTxNo = Long.valueOf(o1.getTransactionNumber());
                final Long secondTxNo = Long.valueOf(o2.getTransactionNumber());
                return firstTxNo.compareTo(secondTxNo);
            }
        });

        return allTransactions;
    }

    /**
     * Calculates the maximum amount this player could win if he didn't have to
     * share the pot with another player.
     * <p/>
     * A player that has folded defaults to a maximum winning potential of 0
     * (zero). A player that has not played all in defaults to a maximum winning
     * potential of the whole pot. A player that sometime during play went all
     * in needs calculation of the side pot.
     *
     * @param player
     *
     * @return The maximum amount this player can win
     */
    protected long getTotalMaxWinnings(final BotPlayer player) {

        if (hasFolded(player)) {
            return 0;
        }

        if (!isAllIn(player)) {
            return getTotalPotAmount();
        }

        // This is a player that went all in, calculate how much the side
        // pot for this player is worth
        final long thisPlayersTotalBet = getTotalBetAmountForPlayer(player);

        // Simplest case, no player has folded, and only this player went all in
        if (foldedPlayers.isEmpty() && allInPlayers.size() == 1) {
            return thisPlayersTotalBet * allPlayers.size();
        }

        // Otherwise, calculate the side-pot value for this player's all in.
        long total = thisPlayersTotalBet;
        for (final BotPlayer p : allPlayers) {
            if (p.equals(player)) {
                continue;
            }

            final long otherPlayersTotalBet = getTotalBetAmountForPlayer(p);
            total += otherPlayersTotalBet > thisPlayersTotalBet ? thisPlayersTotalBet
                    : otherPlayersTotalBet;
        }

        return total;
    }

    /**
     * Calculates the payout per player.
     *
     * @param playerRanking
     *
     * @return a map with player and the amount won.
     */
    public Map<BotPlayer, Long> calculatePayout(
            final List<List<BotPlayer>> playerRanking) {

        // Init result
        final Map<BotPlayer, Long> result = new HashMap<BotPlayer, Long>();
        for (final BotPlayer player : allPlayers) {
            result.put(player, Long.valueOf(0));
        }

        long totalPotLeft = getTotalPotAmount();

        for (final List<BotPlayer> sameRank : playerRanking) {

            if (sameRank.size() == 1) {
                final BotPlayer player = sameRank.get(0);

                final long maxWin = getTotalMaxWinnings(player);
                final long winAmount = maxWin <= totalPotLeft ? maxWin
                        : totalPotLeft;
                result.put(player, winAmount);
                totalPotLeft -= winAmount;
                continue;
            }

            final PotSplitter potSplitter = new PotSplitter();
            for (final BotPlayer player : sameRank) {

                final long maxWin = getTotalMaxWinnings(player);
                if (maxWin == 0) {
                    continue;
                }

                final long winAmount = maxWin <= totalPotLeft ? maxWin
                        : totalPotLeft;

                potSplitter.addMaxWinAmountForPlayer(player, winAmount);
            }

            for (final BotPlayer player : sameRank) {
                final long amount = potSplitter.getWinAmountFor(player);
                result.put(player, amount);
                totalPotLeft -= amount;
            }
        }

        /**
         * If a player folds while having the largest stake in the pot (which of course is extremely stupid)
         * there might be a rest in the pot. Example:
         *
         * Player A goes ALL-IN with a total stake in the pot of 150
         * Player B has a stake of 200 in the pot but suddenly folds.
         *
         * According to normal rules player A wins 150+150=300 which leaves a rest of 50.
         *
         * The rest will be distributed evenly for all players with a payout on this round.
         */
        if (totalPotLeft > 0) {
            List<BotPlayer> playersWithPayout = new ArrayList<BotPlayer>();

            for (BotPlayer player : result.keySet()) {
                if (result.get(player) > 0) {
                    playersWithPayout.add(player);
                }
            }

            if (playersWithPayout.size() == 1) {
                long newPlayerResult = result.get(playersWithPayout.get(0)) + totalPotLeft;
                result.put(playersWithPayout.get(0), newPlayerResult);
            }
            else if (playersWithPayout.size() > 1) {
                int playerIndex = 0;
                while (totalPotLeft > 0) {
                    long newPlayerResult = result.get(playersWithPayout.get(playerIndex)) + 1;
                    result.put(playersWithPayout.get(playerIndex), newPlayerResult);

                    totalPotLeft--;
                    playerIndex = playerIndex == playersWithPayout.size() - 1 ? 0 : playerIndex + 1;
                }
            }
        }

        /**
         * Ultra special case for when all players suddenly fold. In this case, all bets are returned.
         *
         */
        if (totalPotLeft > 0 && allPlayersFolded()) {
            for (final BotPlayer player : allPlayers) {
                result.put(player, getTotalBetAmountForPlayer(player));
            }
        }
        return result;
    }

    private boolean allPlayersFolded() {
        for (final BotPlayer player : allPlayers) {
            if (!hasFolded(player)) {
                return false;
            }
        }

        return true;
    }

    public int getNoofCallsFor(BotPlayer player) {
        int noofCalls = 0;
        for (PotTransaction transaction : getAllTransactionsInOrder()) {
            if (transaction.getPlayer().equals(player)) {
                noofCalls += transaction.getActionType() == ActionType.CALL ? 1 : 0;
            }
        }

        return noofCalls;
    }

    public int getNoofRaisesFor(BotPlayer player) {
        int noofCalls = 0;
        for (PotTransaction transaction : getAllTransactionsInOrder()) {
            if (transaction.getPlayer().equals(player)) {
                noofCalls += transaction.getActionType() == ActionType.RAISE ? 1 : 0;
            }
        }

        return noofCalls;
    }
}
