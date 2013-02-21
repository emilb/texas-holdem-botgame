package se.cygni.poker.statistics;

import junit.framework.Assert;
import org.junit.Test;
import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.definitions.Rank;
import se.cygni.texasholdem.game.definitions.Suit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BinaryConverterTest {

    @Test
    public void testCardsToLong() throws Exception {

        List<Card> cardList = new ArrayList<Card>(5);

        cardList.add(new Card(Rank.EIGHT, Suit.SPADES));
        cardList.add(new Card(Rank.FIVE, Suit.CLUBS));
        cardList.add(new Card(Rank.QUEEN, Suit.SPADES));
        cardList.add(new Card(Rank.DEUCE, Suit.HEARTS));
        cardList.add(new Card(Rank.ACE, Suit.DIAMONDS));

        long cards = BinaryConverter.cardsToLong(cardList);

        int counter = 0;

        while (cards > 0) {
            counter++;
            cards = cards ^ (1L << Long.numberOfTrailingZeros(cards));
        }

        Assert.assertEquals(5, counter);
    }

    @Test
    public void testLongToCards() throws Exception {

        long cards = 0;
        for (int i = 0; i < 7; i++) {
            cards = cards | 1L << (int) (Math.pow(i, 2));
        }

        List<Card> cardList = BinaryConverter.longToCards(cards);

        Assert.assertEquals(7, cardList.size());
    }

    @Test
    public void testLongToCard() throws Exception {

        long firstCardInDeck = 1L;

        Set<Card> allCards = new HashSet<Card>();
        for (int i = 0; i < 52; i++) {
            Card c = BinaryConverter.longToCard(firstCardInDeck << i);
            allCards.add(c);
            System.out.println(c.toShortString() + " " + BinaryConverter.printBinaryValue(firstCardInDeck << i));
        }

        Assert.assertEquals(52, allCards.size());
    }

    @Test
    public void testCardToLong() throws Exception {

        long aceClubsAsLong = BinaryConverter.cardToLong(new Card(Rank.ACE, Suit.CLUBS));
        Assert.assertEquals(new Card(Rank.ACE, Suit.CLUBS), BinaryConverter.longToCard(aceClubsAsLong));

    }

    @Test
    public void testPrintBinaryValue() throws Exception {


    }
}
