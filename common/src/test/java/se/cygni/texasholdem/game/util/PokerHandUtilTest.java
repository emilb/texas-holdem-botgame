package se.cygni.texasholdem.game.util;

import org.junit.Test;
import se.cygni.texasholdem.game.Hand;
import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.definitions.PokerHand;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static se.cygni.texasholdem.test.util.TestUtil.createPokerHandUtil;
import static se.cygni.texasholdem.test.util.TestUtil.getCards;

public class PokerHandUtilTest {

    /**
     * Real matching of a hand
     */

    @Test
    public void testGetHandRoyalFlush() {
        final List<Card> royalFlush = getCards("Ac", "Kc", "Qc", "Jc", "Tc");
        final PokerHandUtil phu = createPokerHandUtil("7s", "Tc", "Ac", "Kc",
                "Jc", "Qc", "8h");

        final Hand bestHand = phu.getBestHand();

        assertEquals(PokerHand.ROYAL_FLUSH, bestHand.getPokerHand());
        assertEquals(royalFlush, bestHand.getCards());
    }

    @Test
    public void testGetHandStraightFlush() {
        final List<Card> straightFlush = getCards("Th", "9h", "8h", "7h", "6h");
        final PokerHandUtil phu = createPokerHandUtil("8h", "Tc", "Ac", "7h",
                "6h", "9h", "Th");

        final Hand bestHand = phu.getBestHand();

        assertEquals(PokerHand.STRAIGHT_FLUSH, bestHand.getPokerHand());
        assertEquals(straightFlush, bestHand.getCards());
    }

    @Test
    public void testGetHandFourOfAKind() {
        final List<Card> fourOfAKind = getCards("3c", "3d", "3h", "3s", "Jh");
        final PokerHandUtil phu = createPokerHandUtil("3d", "5s", "3h", "3s",
                "4h", "3c", "Jh");

        final Hand bestHand = phu.getBestHand();

        assertEquals(PokerHand.FOUR_OF_A_KIND, bestHand.getPokerHand());
        assertEquals(fourOfAKind, bestHand.getCards());
    }

    @Test
    public void testGetHandFullHouse() {
        final List<Card> fourOfAKind = getCards("3c", "3d", "3h", "Jd", "Jh");
        final PokerHandUtil phu = createPokerHandUtil("3d", "5s", "3h", "Jd",
                "4h", "3c", "Jh");

        final Hand bestHand = phu.getBestHand();

        assertEquals(PokerHand.FULL_HOUSE, bestHand.getPokerHand());
        assertEquals(fourOfAKind, bestHand.getCards());
    }

    @Test
    public void testGetHandFlush() {
        final List<Card> fourOfAKind = getCards("Jc", "Tc", "5c", "3c", "2c");
        final PokerHandUtil phu = createPokerHandUtil("3d", "5c", "2c", "Jd",
                "Tc", "3c", "Jc");

        final Hand bestHand = phu.getBestHand();

        assertEquals(PokerHand.FLUSH, bestHand.getPokerHand());
        assertEquals(fourOfAKind, bestHand.getCards());
    }

    @Test
    public void testGetHandStraight() {
        final List<Card> fourOfAKind = getCards("Jc", "Td", "9h", "8d", "7h");
        final PokerHandUtil phu = createPokerHandUtil("9h", "Td", "7h", "8d",
                "4h", "Jc", "Jh");

        final Hand bestHand = phu.getBestHand();

        assertEquals(PokerHand.STRAIGHT, bestHand.getPokerHand());
        assertEquals(fourOfAKind, bestHand.getCards());
    }

    @Test
    public void testGetHandThreeOfAKind() {
        final List<Card> fourOfAKind = getCards("Tc", "Td", "Ts", "8d", "7h");
        final PokerHandUtil phu = createPokerHandUtil("2h", "Td", "7h", "8d",
                "4h", "Tc", "Ts");

        final Hand bestHand = phu.getBestHand();

        assertEquals(PokerHand.THREE_OF_A_KIND, bestHand.getPokerHand());
        assertEquals(fourOfAKind, bestHand.getCards());
    }

    @Test
    public void testGetHandTwoPairs() {
        final List<Card> fourOfAKind = getCards("9c", "9d", "8d", "8s", "Kh");
        final PokerHandUtil phu = createPokerHandUtil("9d", "6d", "7h", "8d",
                "8s", "9c", "Kh");

        final Hand bestHand = phu.getBestHand();

        assertEquals(PokerHand.TWO_PAIRS, bestHand.getPokerHand());
        assertEquals(fourOfAKind, bestHand.getCards());
    }

    @Test
    public void testGetHandOnePair() {
        final List<Card> fourOfAKind = getCards("Kc", "Ks", "Ad", "Jh", "8s");
        final PokerHandUtil phu = createPokerHandUtil("Ks", "3h", "7h", "Jh",
                "8s", "Ad", "Kc");

        final Hand bestHand = phu.getBestHand();

        assertEquals(PokerHand.ONE_PAIR, bestHand.getPokerHand());
        assertEquals(fourOfAKind, bestHand.getCards());
    }

    @Test
    public void testGetHandHighHand() {
        final List<Card> fourOfAKind = getCards("Kc", "Td", "8s", "5h", "4s");
        final PokerHandUtil phu = createPokerHandUtil("2s", "3h", "4s", "5h",
                "8s", "Td", "Kc");

        final Hand bestHand = phu.getBestHand();

        assertEquals(PokerHand.HIGH_HAND, bestHand.getPokerHand());
        assertEquals(fourOfAKind, bestHand.getCards());
    }

    /**
     * Basic matching tests
     */

    @Test(expected = RuntimeException.class)
    public void testIsNotRoyalFlush() {
        final PokerHandUtil phu = createPokerHandUtil("Ah", "Tc", "2h", "Th",
                "Qh", "Kh", "8s");

        phu.isRoyalFlush();
    }

    @Test
    public void testIsLargeRoyalFlush() {
        final List<Card> royalStraightFlush = getCards("Ah", "Kh", "Qh", "Jh",
                "Th");

        final PokerHandUtil phu = createPokerHandUtil("Ah", "Tc", "jh", "Th",
                "Qh", "Kh", "9h");

        final Hand bestHand = phu.isRoyalFlush();

        assertEquals(PokerHand.ROYAL_FLUSH, bestHand.getPokerHand());
        assertEquals(royalStraightFlush, bestHand.getCards());
    }

    @Test
    public void testIsRoyalFlush() {
        final List<Card> royalStraightFlush = getCards("Ah", "Kh", "Qh", "Jh",
                "Th");

        final PokerHandUtil phu = createPokerHandUtil("Ah", "Tc", "jh", "Th",
                "Qh", "Kh", "8s");

        final Hand bestHand = phu.isRoyalFlush();

        assertEquals(PokerHand.ROYAL_FLUSH, bestHand.getPokerHand());
        assertEquals(royalStraightFlush, bestHand.getCards());
    }

    @Test(expected = RuntimeException.class)
    public void testIsNotStraightFlush() {
        final PokerHandUtil phu = createPokerHandUtil("6s", "8s", "Jh", "7s",
                "9s", "Js", "Kh");

        phu.isStraightFlush();
    }

    @Test
    public void testIsLargeStraightFlushWithAceAsOne() {
        final List<Card> straightFlush = getCards("6s", "5s", "4s", "3s", "2s");

        final PokerHandUtil phu = createPokerHandUtil("5s", "6s", "4s", "2s",
                "9s", "3s", "As");

        final Hand bestHand = phu.isStraightFlush();

        assertEquals(PokerHand.STRAIGHT_FLUSH, bestHand.getPokerHand());
        assertEquals(straightFlush, bestHand.getCards());
    }

    @Test
    public void testIsStraightFlushWithAceAsOne() {
        final List<Card> straightFlush = getCards("5s", "4s", "3s", "2s", "As");

        final PokerHandUtil phu = createPokerHandUtil("5s", "8s", "4s", "2s",
                "9s", "3s", "As");

        final Hand bestHand = phu.isStraightFlush();

        assertEquals(PokerHand.STRAIGHT_FLUSH, bestHand.getPokerHand());
        assertEquals(straightFlush, bestHand.getCards());
    }

    @Test
    public void testIsLargeStraightFlush() {
        final List<Card> straightFlush = getCards("Ts", "9s", "8s", "7s", "6s");

        final PokerHandUtil phu = createPokerHandUtil("6s", "8s", "Jh", "7s",
                "9s", "Ts", "5s");

        final Hand bestHand = phu.isStraightFlush();

        assertEquals(PokerHand.STRAIGHT_FLUSH, bestHand.getPokerHand());
        assertEquals(straightFlush, bestHand.getCards());
    }

    @Test
    public void testIsStraightFlush() {
        final List<Card> straightFlush = getCards("Ts", "9s", "8s", "7s", "6s");

        final PokerHandUtil phu = createPokerHandUtil("6s", "8s", "Jh", "7s",
                "9s", "Ts", "Kh");

        final Hand bestHand = phu.isStraightFlush();

        assertEquals(PokerHand.STRAIGHT_FLUSH, bestHand.getPokerHand());
        assertEquals(straightFlush, bestHand.getCards());
    }

    @Test(expected = RuntimeException.class)
    public void testIsNotFourOfAKind() {
        final PokerHandUtil phu = createPokerHandUtil("Ah", "Tc", "2h", "Th",
                "Qh", "As", "8s");

        phu.isFourOfAKind();
    }

    @Test
    public void testIsFourOfAKind() {
        final List<Card> fourOfAKind = getCards("Tc", "Td", "Th", "Ts", "8s");

        final PokerHandUtil phu = createPokerHandUtil("4d", "Td", "Ts", "2h",
                "Th", "Tc", "8s");

        final Hand bestHand = phu.isFourOfAKind();

        assertEquals(PokerHand.FOUR_OF_A_KIND, bestHand.getPokerHand());
        assertEquals(fourOfAKind, bestHand.getCards());
    }

    @Test(expected = RuntimeException.class)
    public void testIsNotFullHouse() {
        final PokerHandUtil phu = createPokerHandUtil("Kd", "Tc", "Jh", "7h",
                "9s", "Ts", "Td");

        phu.isFullHouse();
    }

    @Test
    public void testIsFullHouseBestChoice() {
        final List<Card> fullHouse = getCards("Tc", "Td", "Ts", "7h", "7s");

        final PokerHandUtil phu = createPokerHandUtil("7s", "Tc", "3h", "7h",
                "3s", "Ts", "Td");

        final Hand bestHand = phu.isFullHouse();

        assertEquals(PokerHand.FULL_HOUSE, bestHand.getPokerHand());
        assertEquals(fullHouse, bestHand.getCards());
    }

    @Test
    public void testIsFullHouse() {
        final List<Card> fullHouse = getCards("Tc", "Td", "Ts", "7h", "7s");

        final PokerHandUtil phu = createPokerHandUtil("7s", "Tc", "Jh", "7h",
                "9s", "Ts", "Td");

        final Hand bestHand = phu.isFullHouse();

        assertEquals(PokerHand.FULL_HOUSE, bestHand.getPokerHand());
        assertEquals(fullHouse, bestHand.getCards());
    }

    @Test(expected = RuntimeException.class)
    public void testIsNotFlush() {
        final PokerHandUtil phu = createPokerHandUtil("Jd", "7h", "3c", "Js",
                "2d", "5d", "Tc");

        phu.isFlush();
    }

    @Test
    public void testIsLargeFlush() {
        final List<Card> flush = getCards("Ah", "Kh", "Jh", "7h", "3h");

        final PokerHandUtil phu = createPokerHandUtil("Ad", "7h", "Jh", "Ah",
                "2h", "3h", "Kh");

        final Hand bestHand = phu.isFlush();

        assertEquals(PokerHand.FLUSH, bestHand.getPokerHand());
        assertEquals(flush, bestHand.getCards());
    }

    @Test
    public void testIsFlush() {
        final List<Card> flush = getCards("Kh", "Jh", "7h", "3h", "2h");

        final PokerHandUtil phu = createPokerHandUtil("Ad", "7h", "Jh", "Js",
                "2h", "3h", "Kh");

        final Hand bestHand = phu.isFlush();

        assertEquals(PokerHand.FLUSH, bestHand.getPokerHand());
        assertEquals(flush, bestHand.getCards());
    }

    @Test(expected = RuntimeException.class)
    public void testIsNotStraight() {
        final PokerHandUtil phu = createPokerHandUtil("7s", "Tc", "9d", "5h",
                "9s", "Qs", "8h");
        phu.isStraight();
    }

    @Test
    public void testIsStraightWithDuplicate() {
        final List<Card> straight = getCards("Tc", "9d", "8h", "7s", "6h");

        final PokerHandUtil phu = createPokerHandUtil("7s", "Tc", "9d", "6h",
                "9s", "Qs", "8h");

        final Hand bestHand = phu.isStraight();

        assertEquals(PokerHand.STRAIGHT, bestHand.getPokerHand());
        assertEquals(straight, bestHand.getCards());
    }

    @Test
    public void testIsStraightLargeWithAceAsOne() {
        final List<Card> straight = getCards("6c", "5c", "4d", "3h", "2s");

        final PokerHandUtil phu = createPokerHandUtil("5c", "6c", "3h", "Th",
                "2s", "4d", "Ah");

        final Hand bestHand = phu.isStraight();

        assertEquals(PokerHand.STRAIGHT, bestHand.getPokerHand());
        assertEquals(straight, bestHand.getCards());
    }

    @Test
    public void testIsStraightWithAceAsOne() {
        final List<Card> straight = getCards("5c", "4d", "3h", "2s", "Ah");

        final PokerHandUtil phu = createPokerHandUtil("5c", "Tc", "3h", "Th",
                "2s", "4d", "Ah");

        final Hand bestHand = phu.isStraight();

        assertEquals(PokerHand.STRAIGHT, bestHand.getPokerHand());
        assertEquals(straight, bestHand.getCards());
    }

    @Test
    public void testIsStraight() {
        final List<Card> straight = getCards("Tc", "9d", "8h", "7s", "6h");

        final PokerHandUtil phu = createPokerHandUtil("7s", "Tc", "9d", "6h",
                "2s", "Qs", "8h");

        final Hand bestHand = phu.isStraight();

        assertEquals(PokerHand.STRAIGHT, bestHand.getPokerHand());
        assertEquals(straight, bestHand.getCards());
    }

    @Test(expected = RuntimeException.class)
    public void testIsNotThreeOfAKind() {
        final PokerHandUtil phu = createPokerHandUtil("Jd", "7h", "3c", "Js",
                "2d", "5d", "Tc");

        phu.isThreeOfAKind();
    }

    @Test
    public void testIsThreeOfAKind() {
        final List<Card> threeOfAKind = getCards("Jc", "Jd", "Js", "7h", "5d");

        final PokerHandUtil phu = createPokerHandUtil("Jd", "7h", "3c", "Js",
                "2d", "5d", "Jc");

        final Hand bestHand = phu.isThreeOfAKind();

        assertEquals(PokerHand.THREE_OF_A_KIND, bestHand.getPokerHand());
        assertEquals(threeOfAKind, bestHand.getCards());
    }

    @Test(expected = RuntimeException.class)
    public void testIsNotTwoPairs() {
        final PokerHandUtil phu = createPokerHandUtil("Kd", "Tc", "Jh", "7h",
                "9s", "Ts", "Td");

        phu.isTwoPairs();
    }

    @Test
    public void testIsTwoPairsBestChoice() {
        final List<Card> twoPairs = getCards("Tc", "Td", "9c", "9s", "Jh");

        final PokerHandUtil phu = createPokerHandUtil("7s", "Tc", "Jh", "7h",
                "9s", "9c", "Td");

        final Hand bestHand = phu.isTwoPairs();

        assertEquals(PokerHand.TWO_PAIRS, bestHand.getPokerHand());
        assertEquals(twoPairs, bestHand.getCards());
    }

    @Test
    public void testIsTwoPairs() {
        final List<Card> twoPairs = getCards("Tc", "Td", "7h", "7s", "Jh");

        final PokerHandUtil phu = createPokerHandUtil("7s", "Tc", "Jh", "7h",
                "9s", "Ts", "Td");

        final Hand bestHand = phu.isTwoPairs();

        assertEquals(PokerHand.TWO_PAIRS, bestHand.getPokerHand());
        assertEquals(twoPairs, bestHand.getCards());
    }

    @Test(expected = RuntimeException.class)
    public void testIsOnePairButIsActuallyTwoPair() {
        final PokerHandUtil phu = createPokerHandUtil("4d", "7h", "5c", "Jc",
                "Td", "5d", "4h");

        phu.isOnePair();
    }

    @Test(expected = RuntimeException.class)
    public void testIsOnePairButThreeOfAKind() {
        final PokerHandUtil phu = createPokerHandUtil("4d", "7h", "5c", "4c",
                "Td", "5d", "4h");

        phu.isOnePair();
    }

    @Test
    public void testIsOnePair() {
        final List<Card> onePair = getCards("5c", "5d", "Jc", "Td", "7h");

        final PokerHandUtil phu = createPokerHandUtil("4d", "7h", "5c", "2h",
                "Td", "5d", "Jc");

        final Hand bestHand = phu.isOnePair();

        assertEquals(PokerHand.ONE_PAIR, bestHand.getPokerHand());
        assertEquals(onePair, bestHand.getCards());
    }

    @Test
    public void testIsHighHand() {
        final List<Card> highHand = getCards("Jc", "Td", "7h", "5c", "4d");

        final PokerHandUtil phu = createPokerHandUtil("4d", "7h", "5c", "2h",
                "Td", "3d", "Jc");

        final Hand bestHand = phu.isHighHand();

        assertEquals(PokerHand.HIGH_HAND, bestHand.getPokerHand());
        assertEquals(highHand, bestHand.getCards());
    }
}
