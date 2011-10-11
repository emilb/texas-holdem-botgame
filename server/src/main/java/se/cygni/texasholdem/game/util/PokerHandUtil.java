package se.cygni.texasholdem.game.util;

import static se.cygni.texasholdem.game.util.ListUtil.getHighestOfSameRank;
import static se.cygni.texasholdem.game.util.ListUtil.getHighestOfSameRankExcluding;
import static se.cygni.texasholdem.game.util.ListUtil.getHighestSortedAndExclude;
import static se.cygni.texasholdem.game.util.ListUtil.getLongestConsecutiveSubset;
import static se.cygni.texasholdem.game.util.ListUtil.getRankDistribution;
import static se.cygni.texasholdem.game.util.ListUtil.getSuitDistribution;
import static se.cygni.texasholdem.game.util.ListUtil.merge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;

import se.cygni.texasholdem.game.BestHand;
import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.definitions.CardSortBy;
import se.cygni.texasholdem.game.definitions.PokerHand;
import se.cygni.texasholdem.game.definitions.Rank;
import se.cygni.texasholdem.game.definitions.Suit;

/**
 * PokerHandUtil extracts the best possible hand for a Player.
 * 
 * @author emil
 * 
 */
public class PokerHandUtil {

    private final Map<Rank, List<Card>> rankDistribution;
    private final Map<Suit, List<Card>> suitDistribution;
    private final List<Card> cards;

    public PokerHandUtil(final List<Card> communityCards,
            final List<Card> playerCards) {

        cards = merge(communityCards, playerCards);
        rankDistribution = getRankDistribution(cards);
        suitDistribution = getSuitDistribution(cards);
    }

    /**
     * Extracts the best possible hand and returns the type of PokerHand and the
     * List of cards that make that hand.
     * 
     * @return The BestHand given the current list of cards
     */
    public BestHand getBestHand() {

        try {
            return isRoyalFlush();
        } catch (final Exception e) {
        }
        try {
            return isStraightFlush();
        } catch (final Exception e) {
        }
        try {
            return isFourOfAKind();
        } catch (final Exception e) {
        }
        try {
            return isFullHouse();
        } catch (final Exception e) {
        }
        try {
            return isFlush();
        } catch (final Exception e) {
        }
        try {
            return isStraight();
        } catch (final Exception e) {
        }
        try {
            return isThreeOfAKind();
        } catch (final Exception e) {
        }
        try {
            return isTwoPairs();
        } catch (final Exception e) {
        }
        try {
            return isOnePair();
        } catch (final Exception e) {
        }

        return isHighHand();
    }

    protected BestHand isRoyalFlush() {

        // Is there a suit that contains 5 cards?
        List<Card> potentialRoyalFlush = null;
        for (final Entry<Suit, List<Card>> entry : suitDistribution.entrySet()) {
            if (entry.getValue().size() >= 5) {
                potentialRoyalFlush = entry.getValue();
                break;
            }
        }

        if (potentialRoyalFlush == null)
            throw new RuntimeException("No Royal Flush found");

        potentialRoyalFlush = getHighestSortedAndExclude(5,
                potentialRoyalFlush, null);

        int counter = 4;
        for (final Rank r : EnumSet.range(Rank.TEN, Rank.ACE)) {
            final Card c = potentialRoyalFlush.get(counter--);
            if (r != c.rank())
                throw new RuntimeException("No Royal Flush found");
        }

        return new BestHand(potentialRoyalFlush, PokerHand.ROYAL_FLUSH);
    }

    protected BestHand isStraightFlush() {

        // Is there a suit that contains 5 cards?
        List<Card> potentialStraightFlush = null;
        for (final Entry<Suit, List<Card>> entry : suitDistribution.entrySet()) {
            if (entry.getValue().size() >= 5) {
                potentialStraightFlush = entry.getValue();
                break;
            }
        }

        if (potentialStraightFlush == null)
            throw new RuntimeException("No Straight Flush found");

        potentialStraightFlush = getLongestConsecutiveSubset(potentialStraightFlush);

        if (potentialStraightFlush.size() < 5)
            throw new RuntimeException(
                    "No Straight Flush of sufficient length found");

        final int size = potentialStraightFlush.size();
        if (size > 5) {
            potentialStraightFlush = potentialStraightFlush.subList(size - 5,
                    size);
        }

        Collections.reverse(potentialStraightFlush);

        return new BestHand(potentialStraightFlush, PokerHand.STRAIGHT_FLUSH);
    }

    protected BestHand isFourOfAKind() {

        // Is there a rank that contains 4 cards?
        List<Card> potentialFourOfAKind = null;
        for (final Entry<Rank, List<Card>> entry : rankDistribution.entrySet()) {
            if (entry.getValue().size() == 4) {
                potentialFourOfAKind = entry.getValue();
                break;
            }
        }

        if (potentialFourOfAKind == null)
            throw new RuntimeException("No Four of a kind found");

        // Sort by suit
        Collections.sort(potentialFourOfAKind, CardSortBy.SUIT.getComparator());
        potentialFourOfAKind.addAll(getHighestSortedAndExclude(1, cards,
                potentialFourOfAKind));

        return new BestHand(potentialFourOfAKind, PokerHand.FOUR_OF_A_KIND);
    }

    protected BestHand isFullHouse() {

        final List<Card> highestThreeOfAKind = getHighestOfSameRank(3, cards);
        if (CollectionUtils.isEmpty(highestThreeOfAKind))
            throw new RuntimeException("No three of a kind found in Full House");

        final List<Card> highestTwoOfAKind = getHighestOfSameRankExcluding(2,
                cards, highestThreeOfAKind.get(0).rank());
        if (CollectionUtils.isEmpty(highestTwoOfAKind))
            throw new RuntimeException("No two of a kind found in Full House");

        final List<Card> fullHouse = new ArrayList<Card>();
        fullHouse.addAll(highestThreeOfAKind);
        fullHouse.addAll(highestTwoOfAKind);

        return new BestHand(fullHouse, PokerHand.FULL_HOUSE);
    }

    protected BestHand isFlush() {

        List<Card> potentialFlush = null;
        for (final Entry<Suit, List<Card>> entry : suitDistribution.entrySet()) {
            if (entry.getValue().size() >= 5) {
                potentialFlush = entry.getValue();
                break;
            }
        }

        if (potentialFlush == null)
            throw new RuntimeException("No Flush found");

        potentialFlush = getHighestSortedAndExclude(5, potentialFlush, null);

        return new BestHand(potentialFlush, PokerHand.FLUSH);

    }

    protected BestHand isStraight() {

        List<Card> potentialStraight = getLongestConsecutiveSubset(cards);

        if (potentialStraight.size() < 5)
            throw new RuntimeException("No straight found");

        final int size = potentialStraight.size();
        if (size > 5) {
            potentialStraight = potentialStraight.subList(size - 5, size);
        }

        Collections.reverse(potentialStraight);

        return new BestHand(potentialStraight, PokerHand.STRAIGHT);
    }

    protected BestHand isThreeOfAKind() {

        // Is there a rank that contains 3 cards?
        List<Card> potentialThreeOfAKind = null;
        for (final Entry<Rank, List<Card>> entry : rankDistribution.entrySet()) {
            if (entry.getValue().size() == 3) {

                final List<Card> threeOfAKind = entry.getValue();

                // There might be more than one set of three-of-a-kind, choose
                // the highest ranking one.
                if (potentialThreeOfAKind != null) {
                    if (potentialThreeOfAKind.get(0).rank().getOrderValue() < threeOfAKind
                            .get(0).rank().getOrderValue())
                        potentialThreeOfAKind = threeOfAKind;
                }

                potentialThreeOfAKind = entry.getValue();
            }
        }

        if (CollectionUtils.isEmpty(potentialThreeOfAKind))
            throw new RuntimeException("No Three of a kind found");

        Collections
                .sort(potentialThreeOfAKind, CardSortBy.SUIT.getComparator());
        potentialThreeOfAKind.addAll(getHighestSortedAndExclude(2, cards,
                potentialThreeOfAKind));

        return new BestHand(potentialThreeOfAKind, PokerHand.THREE_OF_A_KIND);
    }

    protected BestHand isTwoPairs() {

        final List<Card> highestTwoOfAKind = getHighestOfSameRank(2, cards);
        if (CollectionUtils.isEmpty(highestTwoOfAKind))
            throw new RuntimeException("No two of a kind found in Two pairs");

        final List<Card> nextHighestTwoOfAKind = getHighestOfSameRankExcluding(
                2, cards, highestTwoOfAKind.get(0).rank());
        if (CollectionUtils.isEmpty(nextHighestTwoOfAKind))
            throw new RuntimeException(
                    "No second two of a kind found in Two pairs");

        final List<Card> twoPairs = new ArrayList<Card>();
        twoPairs.addAll(highestTwoOfAKind);
        twoPairs.addAll(nextHighestTwoOfAKind);
        twoPairs.addAll(getHighestSortedAndExclude(1, cards, twoPairs));

        return new BestHand(twoPairs, PokerHand.TWO_PAIRS);
    }

    protected BestHand isOnePair() {

        // Is there a rank that contains 2 cards and not 3 or 4?
        List<Card> potentialOnePair = null;
        for (final Entry<Rank, List<Card>> entry : rankDistribution.entrySet()) {
            if (entry.getValue().size() == 2) {

                if (potentialOnePair != null)
                    throw new RuntimeException(
                            "Already found a pair, this hand contains two pairs");

                potentialOnePair = entry.getValue();
            } else if (entry.getValue().size() > 2) {
                throw new RuntimeException(
                        "There exists a better match than one pair");
            }
        }

        if (potentialOnePair == null)
            throw new RuntimeException("No One pair found");

        // The best hand is the one pair plus the highest other cards.
        final List<Card> restOfCards = getHighestSortedAndExclude(3, cards,
                potentialOnePair);

        // Sort by suit
        Collections.sort(potentialOnePair, CardSortBy.SUIT.getComparator());
        potentialOnePair.addAll(restOfCards);

        return new BestHand(potentialOnePair, PokerHand.ONE_PAIR);
    }

    protected BestHand isHighHand() {

        return new BestHand(getHighestSortedAndExclude(5, cards, null),
                PokerHand.HIGH_HAND);
    }

}
