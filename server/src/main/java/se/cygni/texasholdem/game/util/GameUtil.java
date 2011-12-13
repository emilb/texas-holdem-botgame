package se.cygni.texasholdem.game.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import se.cygni.texasholdem.game.BotPlayer;

public class GameUtil {

    /**
     * Returns the next player that still has chips to play for.
     * 
     * @param players
     * @param startingFromPlayer
     * @return
     */
    public static BotPlayer getNextPlayerInPlay(
            final List<BotPlayer> players,
            final BotPlayer startingFromPlayer) {

        final int ix = startingFromPlayer == null ? -1 : players
                .indexOf(startingFromPlayer);

        // Start from where startingFromPlayer is positioned
        for (int currIx = ix + 1; currIx < players.size(); currIx++) {
            final BotPlayer nextPlayer = players.get(currIx);
            if (playerHasChips(nextPlayer))
                return nextPlayer;
        }

        // Didn't find a player, start from beginning
        for (int currIx = 0; currIx < ix + 1; currIx++) {
            final BotPlayer nextPlayer = players.get(currIx);
            if (playerHasChips(nextPlayer))
                return nextPlayer;
        }

        return null;
    }

    public static List<BotPlayer> getOrderedListOfPlayersInPlay(
            final List<BotPlayer> players,
            final BotPlayer startingFromPlayer) {

        final List<BotPlayer> result = new ArrayList<BotPlayer>();

        final int startPos = startingFromPlayer == null ? -1 : players
                .indexOf(startingFromPlayer);

        for (int currIx = startPos + 1; currIx < players.size(); currIx++) {
            final BotPlayer nextPlayer = players.get(currIx);
            if (playerHasChips(nextPlayer))
                result.add(nextPlayer);
        }

        for (int currIx = 0; currIx < startPos + 1; currIx++) {
            final BotPlayer nextPlayer = players.get(currIx);
            if (playerHasChips(nextPlayer))
                result.add(nextPlayer);
        }

        return result;
    }

    /**
     * Creates a list of players still active in current game (i.e. has not
     * folded and has money left)
     * 
     * @return
     */
    public static List<BotPlayer> getActivePlayersWithChipsLeft(
            final List<BotPlayer> players) {

        final List<BotPlayer> playersWithChipsLeft = new ArrayList<BotPlayer>();

        final Iterator<BotPlayer> iter = players.iterator();
        while (iter.hasNext()) {
            final BotPlayer player = iter.next();
            if (GameUtil.playerHasChips(player))
                playersWithChipsLeft.add(player);
        }

        return playersWithChipsLeft;
    }

    public static boolean playerHasChips(final BotPlayer nextPlayer) {

        return nextPlayer.getChipAmount() > 0;
    }

}
