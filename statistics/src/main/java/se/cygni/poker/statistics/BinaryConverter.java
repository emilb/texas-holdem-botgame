package se.cygni.poker.statistics;

import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.Deck;
import se.cygni.texasholdem.game.definitions.Rank;
import se.cygni.texasholdem.game.definitions.Suit;

import java.util.ArrayList;
import java.util.List;

public class BinaryConverter {

    private static List<Card> deck = Deck.getOrderedListOfCards();


    public static long cardsToLong(List<Card> cards) {

        long val = 0;
        for (Card c : cards) {
            val = val | cardToLong(c);
        }

        return val;
    }

    public static List<Card> longToCards(long binaryCards) {

        List<Card> cards = new ArrayList<Card>();
        long val = binaryCards;

        while (val > 0) {
            long firstCard = Long.lowestOneBit(val);
            cards.add(longToCard(firstCard));
            val = val ^ firstCard;
        }

        return cards;
    }

    public static Card longToCard(long binaryCard) {

        return deck.get(Long.numberOfTrailingZeros(binaryCard));
    }

    public static long cardToLong(Card card) {

        return 1L << deck.indexOf(card);
    }

    public static String printBinaryValue(Long l) {
        return String.format("%52s", Long.toBinaryString(l)).replace(" ", "0");
    }

    public static void main(String[] args) {
        BinaryConverter bc = new BinaryConverter();

        long val = bc.cardToLong(new Card(Rank.ACE, Suit.CLUBS));
        printBinaryValue(val);

        System.out.println(bc.longToCard((long)Math.pow(2, 12)));

        List<Card> cards = bc.longToCards(3 + 4096);
        for (Card c: cards) {
            System.out.println(c);
        }

        System.out.println(bc.cardsToLong(cards));
    }
}
