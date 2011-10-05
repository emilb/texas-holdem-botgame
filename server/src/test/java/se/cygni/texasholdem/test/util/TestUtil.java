package se.cygni.texasholdem.test.util;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.definitions.Rank;
import se.cygni.texasholdem.game.definitions.Suit;
import se.cygni.texasholdem.util.PokerHandUtil;

public class TestUtil {

    /**
     * Expects an array of cards defined in shorthand. 9c = 9 of clubs ks = King
     * of spades
     * 
     * @param shorthand
     * @return a list of Cards
     */
    public static List<Card> getCards(final String... shorthand) {

        final List<Card> cards = new ArrayList<Card>();

        for (final String s : shorthand) {
            final Rank rank = Rank.get(s.substring(0, 1));
            final Suit suit = Suit.get(s.substring(1, 2));
            cards.add(Card.valueOf(rank, suit));
        }

        return cards;
    }

    /**
     * Convenience method for creating a PokerHandUtil with the last two cards
     * in the shorthand array as player cards and anything before that as
     * community cards.
     * 
     * @param shorthand
     * @return
     */
    public static PokerHandUtil createPokerHandUtil(final String... shorthand) {

        assertTrue(shorthand.length >= 2);

        List<Card> communityCards = null;
        List<Card> playerCards = null;

        if (shorthand.length > 2) {
            communityCards = getCards((String[]) ArrayUtils.subarray(shorthand,
                    0, shorthand.length - 2));
        }
        playerCards = getCards((String[]) ArrayUtils.subarray(shorthand,
                shorthand.length - 2, shorthand.length));

        return new PokerHandUtil(communityCards, playerCards);
    }

}
