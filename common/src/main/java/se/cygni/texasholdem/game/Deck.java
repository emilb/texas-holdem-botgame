package se.cygni.texasholdem.game;

import se.cygni.texasholdem.game.definitions.Rank;
import se.cygni.texasholdem.game.definitions.Suit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Deck {

    private static final List<Card> protoDeck = new ArrayList<Card>();

    private final List<Card> cards;

    // Initialize prototype deck
    static {
        for (final Suit suit : Suit.values()) {
            for (final Rank rank : Rank.values()) {
                protoDeck.add(Card.valueOf(rank, suit));
            }
        }
    }

    private Deck(final List<Card> cards) {

        this.cards = cards;
    }

    public static Deck getShuffledDeck() {

        final long randomSeed = System.currentTimeMillis();
        final List<Card> copyOfProtoDec = new ArrayList<Card>(protoDeck);
        Collections.shuffle(copyOfProtoDec, new Random(randomSeed));

        return new Deck(copyOfProtoDec);
    }

    public static List<Card> getOrderedListOfCards() {

        return new ArrayList<Card>(protoDeck);
    }

    public int getCardsLeft() {

        return cards.size();
    }

    public Card getNextCard() {

        return cards.remove(0);
    }

    public void burn() {

        cards.remove(0);
    }
}
