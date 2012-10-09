package se.cygni.poker.statistics;

import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.util.PokerHandUtil;

import java.util.List;

public class HandEvaluator {

    private static int HAND_FACTOR = 1000;

    public int getHandValue(long cards) {

        List<Card> cardList = BinaryConverter.longToCards(cards);
        PokerHandUtil phu = new PokerHandUtil(cardList);


        int rankSum = 0;
        int factor = 10;
        for (Card c : phu.getBestHand().getCards()) {
            rankSum += c.getRank().getOrderValue() * factor;
            factor --;
        }

        return phu.getBestHand().getPokerHand().getOrderValue() * HAND_FACTOR + rankSum;
    }

}
