package se.cygni.texasholdem.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.cygni.texasholdem.game.definitions.Rank;
import se.cygni.texasholdem.game.definitions.Suit;

public class DeckTest {

    private Deck deck;

    @Before
    public void setUp() {

        deck = Deck.getShuffledDeck();
    }

    @Test
    public void testGetCardsLeft() {

        assertEquals(52, deck.getCardsLeft());
    }

    @Test
    public void testGetNextCard() {

        deck.getNextCard();
        assertEquals(51, deck.getCardsLeft());
    }

    @Test
    public void testBurn() {

        deck.burn();
        assertEquals(51, deck.getCardsLeft());
    }

    /**
     * Three players, all playing to SHOWDOWN
     */
    @Test
    public void testPlayScenario() {

        final int noofPlayers = 3;

        // PRE_FLOP
        for (int i = 0; i < noofPlayers * 2; i++) {
            deck.getNextCard();
        }

        // FLOP
        for (int i = 0; i < 3; i++) {
            deck.burn();
            deck.getNextCard();
        }

        // TURN
        deck.burn();
        deck.getNextCard();

        // RIVER
        deck.burn();
        deck.getNextCard();

        assertEquals(36, deck.getCardsLeft());
    }

    @Test
    public void testAllCardsArePresent() {

        final List<Card> allCardsFromDeck = new ArrayList<Card>();
        for (int i = 0; i < 52; i++) {
            allCardsFromDeck.add(deck.getNextCard());
        }

        assertEquals(0, deck.getCardsLeft());

        for (final Rank r : Rank.values()) {
            for (final Suit s : Suit.values()) {
                assertTrue(allCardsFromDeck.contains(Card.valueOf(r, s)));
            }
        }
    }
}
