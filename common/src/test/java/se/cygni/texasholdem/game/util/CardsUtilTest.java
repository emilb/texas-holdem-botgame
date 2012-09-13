package se.cygni.texasholdem.game.util;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.CollectionUtils;
import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.definitions.Rank;
import static se.cygni.texasholdem.test.util.TestUtil.*;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CardsUtilTest {

    @Test
    public void testGetHighestSortedAndExcludeWithNullExclude() {
        final List<Card> cards = getCards("3c", "8d", "Qh", "Ad", "7c", "9h",
                "Tc");
        final List<Card> expected = getCards("Ad", "Qh", "Tc");

        assertEquals(expected,
                CardsUtil.getHighestSortedAndExclude(3, cards, null));
    }

    @Test
    public void testGetHighestSortedAndExclude() {
        final List<Card> cards = getCards("3c", "8d", "Qh", "Ad", "7c", "9h",
                "Tc");
        final List<Card> exclude = getCards("9h", "3c");
        final List<Card> expected = getCards("Ad", "Qh", "Tc");

        assertEquals(expected,
                CardsUtil.getHighestSortedAndExclude(3, cards, exclude));
    }

    @Test
    public void testGetLongestConsecutiveSubset() {

        Assert.assertEquals("Subset begining", getCards("Ad", "2c", "3c", "4h"),
                CardsUtil.getLongestConsecutiveSubset(getCards("Ad", "4h", "Qs",
                        "2c", "3c", "Kd")));

        Assert.assertEquals("Subset middle", getCards("Ts", "Jh", "Qc"),
                CardsUtil.getLongestConsecutiveSubset(getCards("Ad", "4h", "Ts",
                        "2c", "Qc", "Jh")));

        Assert.assertEquals("Subset end", getCards("Ts", "Jh", "Qc"),
                CardsUtil.getLongestConsecutiveSubset(getCards("4h", "Ts", "2c",
                        "Qc", "Jh")));

        Assert.assertEquals("Full set", getCards("2c", "3c", "4h", "5s", "6d"),
                CardsUtil.getLongestConsecutiveSubset(getCards("6d", "4h", "5s",
                        "2c", "3c")));

        Assert.assertEquals("Several subsets", getCards("3c", "4h", "5s", "6d"),
                CardsUtil.getLongestConsecutiveSubset(getCards("6d", "9h", "4h",
                        "8c", "5s", "Tc", "3c", "Ac", "Qd", "Ks")));

        Assert.assertEquals("Valid consecutive subset with duplicate rank",
                getCards("2c", "3c", "4d", "5s", "6d"),
                CardsUtil.getLongestConsecutiveSubset(getCards("6d", "4h", "4d",
                        "5s", "2c", "3c", "Qs")));

        assertEquals("No consecutive subset", true,
                CollectionUtils.isEmpty(CardsUtil
                        .getLongestConsecutiveSubset(getCards("Jd", "9h", "4h",
                                "Kc", "2s"))));
    }

    @Test
    public void testRemoveDuplicatesByRankAndSortByRank() {
        Assert.assertEquals(getCards("2c", "3c", "4d", "5s", "6d", "Qs", "Ac"),
                CardsUtil.removeDuplicatesByRankAndSortByRank(getCards("Ad",
                        "6d", "4h", "4d", "5s", "2c", "3c", "Qs", "Ac")));
    }

    @Test
    public void testGetHighestOfSameRankExcluding() {
        Assert.assertEquals("Two subsets", getCards("4d", "4h", "4s"),
                CardsUtil.getHighestOfSameRankExcluding(3,
                        getCards("4d", "4h", "4s", "5s", "Qc", "5h", "5d"),
                        Rank.FIVE));
    }

    @Test
    public void testGetHighestOfSameRank() {
        Assert.assertEquals(
                "One subset",
                getCards("5d", "5h", "5s"),
                CardsUtil.getHighestOfSameRank(3,
                        getCards("Ad", "4h", "Ts", "5s", "Qc", "5h", "5d")));

        Assert.assertEquals(
                "Two subsets",
                getCards("5d", "5h", "5s"),
                CardsUtil.getHighestOfSameRank(3,
                        getCards("4d", "4h", "4s", "5s", "Qc", "5h", "5d")));
    }
}
