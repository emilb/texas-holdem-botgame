package se.cygni.texasholdem.game.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.cygni.texasholdem.game.BestHand;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.game.Card;

public class PokerHandRankUtil {

    private static Logger log = LoggerFactory
            .getLogger(PokerHandRankUtil.class);

    public static List<List<BotPlayer>> getPlayerRanking(
            final List<Card> communityCards,
            final List<BotPlayer> players) {

        final List<List<BotPlayer>> result = new ArrayList<List<BotPlayer>>();

        final List<BestHand> bestHands = new ArrayList<BestHand>();

        for (final BotPlayer player : players) {

            final PokerHandUtil phu = new PokerHandUtil(communityCards,
                    player.getCards());

            final BestHand bestHand = phu.getBestHand();
            bestHand.setPlayer(player);

            bestHands.add(bestHand);
        }

        Collections.sort(bestHands);
        /*
         * Collections.sort(bestHands, new Comparator<BestHand>() {
         * 
         * @Override public int compare(final BestHand a, final BestHand b) {
         * 
         * final int aVal = a.getPokerHand().getOrderValue(); final int bVal =
         * b.getPokerHand().getOrderValue();
         * 
         * if (aVal == bVal) return 0;
         * 
         * if (aVal < bVal) return 1;
         * 
         * return -1; } });
         */

        BestHand previousBestHand = null;
        for (final BestHand bestHand : bestHands) {

            if (previousBestHand == null ||
                    previousBestHand.compareTo(bestHand) != 0) {

                final List<BotPlayer> currPos = new ArrayList<BotPlayer>();
                currPos.add(bestHand.getPlayer());
                result.add(currPos);
            }

            else if (previousBestHand.compareTo(bestHand) == 0) {
                result.get(result.size() - 1).add(bestHand.getPlayer());
            }

            previousBestHand = bestHand;

            log.debug(bestHand.toString());
        }

        return result;
    }
}
