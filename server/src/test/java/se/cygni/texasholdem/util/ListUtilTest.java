package se.cygni.texasholdem.util;

import static org.junit.Assert.assertEquals;
import static se.cygni.texasholdem.test.util.TestUtil.getCards;

import java.util.List;

import org.junit.Test;
import org.springframework.util.CollectionUtils;

import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.definitions.Rank;

public class ListUtilTest {

    @Test
    public void testGetHighestSortedAndExcludeWithNullExclude() {
        final List<Card> cards = getCards("3c", "8d", "Qh", "Ad", "7c", "9h",
                "Tc");
        final List<Card> expected = getCards("Ad", "Qh", "Tc");

        assertEquals(expected,
                ListUtil.getHighestSortedAndExclude(3, cards, null));
    }

    @Test
    public void testGetHighestSortedAndExclude() {
        final List<Card> cards = getCards("3c", "8d", "Qh", "Ad", "7c", "9h",
                "Tc");
        final List<Card> exclude = getCards("9h", "3c");
        final List<Card> expected = getCards("Ad", "Qh", "Tc");

        assertEquals(expected,
                ListUtil.getHighestSortedAndExclude(3, cards, exclude));
    }

    @Test
    public void testGetLongestConsecutiveSubset() {

        assertEquals("Subset begining", getCards("Ad", "2c", "3c", "4h"),
                ListUtil.getLongestConsecutiveSubset(getCards("Ad", "4h", "Qs",
                        "2c", "3c", "Kd")));

        assertEquals("Subset middle", getCards("Ts", "Jh", "Qc"),
                ListUtil.getLongestConsecutiveSubset(getCards("Ad", "4h", "Ts",
                        "2c", "Qc", "Jh")));

        assertEquals("Subset end", getCards("Ts", "Jh", "Qc"),
                ListUtil.getLongestConsecutiveSubset(getCards("4h", "Ts", "2c",
                        "Qc", "Jh")));

        assertEquals("Full set", getCards("2c", "3c", "4h", "5s", "6d"),
                ListUtil.getLongestConsecutiveSubset(getCards("6d", "4h", "5s",
                        "2c", "3c")));

        assertEquals("Several subsets", getCards("3c", "4h", "5s", "6d"),
                ListUtil.getLongestConsecutiveSubset(getCards("6d", "9h", "4h",
                        "8c", "5s", "Tc", "3c", "Ac", "Qd", "Ks")));

        assertEquals("Valid consecutive subset with duplicate rank",
                getCards("2c", "3c", "4d", "5s", "6d"),
                ListUtil.getLongestConsecutiveSubset(getCards("6d", "4h", "4d",
                        "5s", "2c", "3c", "Qs")));

        assertEquals("No consecutive subset", true,
                CollectionUtils.isEmpty(ListUtil
                        .getLongestConsecutiveSubset(getCards("Jd", "9h", "4h",
                                "Kc", "2s"))));
    }

    @Test
    public void testRemoveDuplicatesByRankAndSortByRank() {
        assertEquals(getCards("2c", "3c", "4d", "5s", "6d", "Qs", "Ac"),
                ListUtil.removeDuplicatesByRankAndSortByRank(getCards("Ad",
                        "6d", "4h", "4d", "5s", "2c", "3c", "Qs", "Ac")));
    }

    @Test
    public void testGetHighestOfSameRankExcluding() {
        assertEquals("Two subsets", getCards("4d", "4h", "4s"),
                ListUtil.getHighestOfSameRankExcluding(3,
                        getCards("4d", "4h", "4s", "5s", "Qc", "5h", "5d"),
                        Rank.FIVE));
    }

    @Test
    public void testGetHighestOfSameRank() {
        assertEquals(
                "One subset",
                getCards("5d", "5h", "5s"),
                ListUtil.getHighestOfSameRank(3,
                        getCards("Ad", "4h", "Ts", "5s", "Qc", "5h", "5d")));

        assertEquals(
                "Two subsets",
                getCards("5d", "5h", "5s"),
                ListUtil.getHighestOfSameRank(3,
                        getCards("4d", "4h", "4s", "5s", "Qc", "5h", "5d")));
    }
}
