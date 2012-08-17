package se.cygni.texasholdem.test.util;

import org.apache.commons.math.random.RandomData;
import org.apache.commons.math.random.RandomDataImpl;
import se.cygni.texasholdem.game.*;
import se.cygni.texasholdem.game.definitions.PokerHand;

import java.util.ArrayList;
import java.util.List;

public class DomainUtil {

    private static Deck deck = Deck.getShuffledDeck();
    private static final RandomData random = new RandomDataImpl();

    private static void reshuffleDeck() {

        deck = Deck.getShuffledDeck();

    }

    public static Card card() {

        if (deck.getCardsLeft() == 0)
            reshuffleDeck();

        return deck.getNextCard();
    }

    public static List<Card> cards(final int length) {

        final List<Card> cards = new ArrayList<Card>();
        for (int i = 0; i < length; i++) {
            cards.add(card());
        }
        return cards;
    }

    public static Player player() {

        return new Player(randomString(8), randomLong());
    }

    public static List<Player> players(final int length) {

        final List<Player> players = new ArrayList<Player>();
        for (int i = 0; i < length; i++) {
            players.add(player());
        }
        return players;

    }

    public static Hand hand() {

        return new Hand(cards(5), PokerHand.values()[random.nextInt(0,
                PokerHand.values().length - 1)]);
    }

    public static PlayerShowDown playerShowdown() {

        return new PlayerShowDown(player(), hand(),
                randomLong());
    }

    public static List<PlayerShowDown> playerShowdowns(final int length) {

        final List<PlayerShowDown> players = new ArrayList<PlayerShowDown>();
        for (int i = 0; i < length; i++) {
            players.add(playerShowdown());
        }
        return players;

    }

    public static String randomString(final int maxLength) {

        return random.nextHexString(maxLength);
    }

    public static long randomLong() {

        return random.nextLong(0, 4000);
    }
}
