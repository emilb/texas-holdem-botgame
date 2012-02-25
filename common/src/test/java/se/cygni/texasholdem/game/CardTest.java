package se.cygni.texasholdem.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import se.cygni.texasholdem.game.definitions.Rank;
import se.cygni.texasholdem.game.definitions.Suit;

public class CardTest {

    @Test
    public void testHashcode() {

        final Card c1 = Card.valueOf(Rank.SEVEN, Suit.DIAMONDS);
        final Card c2 = Card.valueOf(Rank.SEVEN, Suit.DIAMONDS);
        final Card c3 = Card.valueOf(Rank.EIGHT, Suit.DIAMONDS);

        assertEquals(c1.hashCode(), c2.hashCode());
        assertFalse(c1.hashCode() == c3.hashCode());
    }

    @Test
    public void testAreEqual() {

        final Card c1 = Card.valueOf(Rank.ACE, Suit.HEARTS);
        final Card c2 = Card.valueOf(Rank.ACE, Suit.HEARTS);

        assertEquals(c1, c2);

        assertTrue(c1.equals(c2));
        assertTrue(c2.equals(c1));
        assertTrue(c2.equals(c2));
    }

    @Test
    public void testNotEqual() {

        final Card c1 = Card.valueOf(Rank.ACE, Suit.HEARTS);
        final Card c2 = Card.valueOf(Rank.ACE, Suit.SPADES);

        assertFalse(c1.equals(c2));
        assertFalse(c2.equals(c1));
        assertFalse(c1.equals(null));
        assertFalse(c1.equals("Ah"));
    }
}
