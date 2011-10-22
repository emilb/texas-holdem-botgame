package se.cygni.texasholdem.game.pot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.cygni.texasholdem.game.BotPlayer;

/**
 * This class calculates a split of a pot if there are more than one players
 * with the best hand.
 * 
 * 
 * @author emil
 * 
 */
public class PotSplitter {

    private final List<PlayerWinAmount> playersWinAmount = new ArrayList<PotSplitter.PlayerWinAmount>();

    private final Map<BotPlayer, Long> calculatedPlayerWinAmount = new HashMap<BotPlayer, Long>();

    /**
     * Add the maximum potential winnings for a player.
     * 
     * @param player
     * @param amount
     */
    public void addMaxWinAmountForPlayer(
            final BotPlayer player,
            final long amount) {

        if (amount == 0)
            return;

        playersWinAmount.add(new PlayerWinAmount(player, amount));

        // Recalculate the split since a new player has been added
        calculateSplit();
    }

    /**
     * Calculates the split of the pot.
     */
    private void calculateSplit() {

        if (playersWinAmount.size() == 0)
            return;

        calculatedPlayerWinAmount.clear();

        if (playersWinAmount.size() == 1) {
            final PlayerWinAmount pwa = playersWinAmount.get(0);
            calculatedPlayerWinAmount.put(pwa.player, pwa.amount);
            return;
        }

        sortPlayersWinAmount();
        resetCalculatedWin();

        /**
         * The idea here is to start with the player with the lowest potential
         * win amount. Split that amount over all participating players in this
         * split. Continue with the player that has the next higher potential
         * win amount and split that amount over the remaining players in the
         * list. Continue this till the last player of the split.
         */
        final int noofPlayers = playersWinAmount.size();
        for (int i = 0; i < noofPlayers; i++) {
            final PlayerWinAmount pwa = playersWinAmount.get(i);

            final long realWinAmount = pwa.calculatedAmount / (noofPlayers - i);

            pwa.calculatedAmount = realWinAmount;

            calculatedPlayerWinAmount.put(pwa.player, realWinAmount);

            for (int j = i + 1; j < noofPlayers; j++) {
                final PlayerWinAmount nextPwa = playersWinAmount.get(j);
                nextPwa.calculatedAmount = nextPwa.calculatedAmount
                        - realWinAmount;
            }
        }
    }

    /**
     * Sorts the list of players and their potential win amount in ascending
     * order.
     */
    private void sortPlayersWinAmount() {

        Collections.sort(playersWinAmount, new Comparator<PlayerWinAmount>() {

            @Override
            public int compare(
                    final PlayerWinAmount o1,
                    final PlayerWinAmount o2) {

                final Long firstAmount = Long.valueOf(o1.amount);
                final Long secondAmount = Long.valueOf(o2.amount);
                return firstAmount.compareTo(secondAmount);
            }
        });
    }

    /**
     * Resets the internal register for calculated winning amount per player.
     */
    private void resetCalculatedWin() {

        for (final PlayerWinAmount pwa : playersWinAmount)
            pwa.calculatedAmount = pwa.amount;
    }

    /**
     * Returns the calculated split for supplied player. If the player is not
     * registered in this split 0 (zero) is returned.
     * 
     * @param player
     * @return The splitted win amount for player
     */
    public long getWinAmountFor(final BotPlayer player) {

        if (!calculatedPlayerWinAmount.containsKey(player))
            return 0;

        return calculatedPlayerWinAmount.get(player);
    }

    private final class PlayerWinAmount {

        final BotPlayer player;
        final long amount;
        long calculatedAmount;

        public PlayerWinAmount(final BotPlayer player, final long amount) {

            this.player = player;
            this.amount = amount;
            this.calculatedAmount = amount;
        }

    }
}
