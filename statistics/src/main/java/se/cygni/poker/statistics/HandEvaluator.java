package se.cygni.poker.statistics;

import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.Hand;
import se.cygni.texasholdem.game.definitions.Rank;
import se.cygni.texasholdem.game.definitions.Suit;
import se.cygni.texasholdem.game.util.PokerHandUtil;

import java.util.ArrayList;
import java.util.List;

public class HandEvaluator {

    private static int HAND_FACTOR = 10000;

    public static int getHandValue(long cards) {

        List<Card> cardList = BinaryConverter.longToCards(cards);
        PokerHandUtil phu = new PokerHandUtil(cardList);
        Hand bestHand = phu.getBestHand();


        int rankSum = 0;
        int factor = 10;
        for (Card c : bestHand.getCards()) {
            rankSum += c.getRank().getOrderValue() * factor;
            factor--;
            factor--;
        }

        return bestHand.getPokerHand().getOrderValue() * HAND_FACTOR + rankSum;
    }

    public static void main(String[] args) {
        // 2c 2d 3s 4s 8s => 10106 wrong
        // 2c 3c 2d 2h 2s => 10062 wrong

        List<Card> cards = new ArrayList<Card>(5);
        cards.add(new Card(Rank.DEUCE, Suit.CLUBS));
        cards.add(new Card(Rank.THREE, Suit.CLUBS));
        cards.add(new Card(Rank.DEUCE, Suit.DIAMONDS));
        cards.add(new Card(Rank.DEUCE, Suit.HEARTS));
        cards.add(new Card(Rank.DEUCE, Suit.SPADES));

        System.out.println(getHandValue(BinaryConverter.cardsToLong(cards)));

    }
}
