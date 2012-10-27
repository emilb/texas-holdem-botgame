package se.cygni.poker.statistics;

import junit.framework.Assert;
import org.junit.Test;

import java.util.List;

public class HandEvaluatorFromMapTest {

    @Test
    public void testGetCardsByRemoving_2() throws Exception {
        long cardsBefore = 20026L;
        long expectedCards = 3640L;

        int[] removals = new int[]{1, 14};
        long resultingCards = HandEvaluatorFromMap.getCardsByRemoving(cardsBefore, removals);

        Assert.assertEquals(expectedCards, resultingCards);
    }

    @Test
    public void testGetCardsByRemoving_1() throws Exception {
        long cardsBefore = 19514L;
        long expectedCards = 3130L;

        int[] removals = new int[]{14};
        long resultingCards = HandEvaluatorFromMap.getCardsByRemoving(cardsBefore, removals);

        Assert.assertEquals(expectedCards, resultingCards);
    }

    @Test
    public void testGetCardsByRemoving_1_large() throws Exception {
        long cardsBefore = 4433230883192832L;
        long expectedCards = 4292493394837504L;

        int[] removals = new int[]{47};
        long resultingCards = HandEvaluatorFromMap.getCardsByRemoving(cardsBefore, removals);

        Assert.assertEquals(expectedCards, resultingCards);
    }

    @Test
    public void testGetAllCombinationsOfRemovals_6() throws Exception {
        int[] positions = new int[]{0, 3, 8, 22, 34, 51};
        List<int[]> removalPermutations = HandEvaluatorFromMap.getAllCombinationsOfRemovals(positions);

        Assert.assertEquals(6, removalPermutations.size());
        int counter = 0;
        for (int[] removal : removalPermutations) {
            Assert.assertEquals(1, removal.length);
            Assert.assertEquals(positions[counter], removal[0]);
            counter++;
        }
    }

    @Test
    public void testGetAllCombinationsOfRemovals_7() throws Exception {
        int[] positions = new int[]{2, 3, 8, 22, 34, 49, 50};
        List<int[]> removalPermutations = HandEvaluatorFromMap.getAllCombinationsOfRemovals(positions);

        Assert.assertEquals(21, removalPermutations.size());
        for (int[] removal : removalPermutations) {
            Assert.assertEquals(2, removal.length);
        }
    }

    @Test
    public void testGetPositionsForOneBits() throws Exception {
        int[] expected = new int[]{1, 3, 4, 5, 9, 14};
        int[] result = HandEvaluatorFromMap.getPositionsForOneBits(16954L);

        Assert.assertEquals(expected.length, result.length);
        for (int i = 0; i < expected.length; i++) {
            Assert.assertEquals(expected[i], result[i]);
        }

        expected = new int[]{0};
        result = HandEvaluatorFromMap.getPositionsForOneBits(1L);
        Assert.assertEquals(expected.length, result.length);
        for (int i = 0; i < expected.length; i++) {
            Assert.assertEquals(expected[i], result[i]);
        }

        expected = new int[0];
        result = HandEvaluatorFromMap.getPositionsForOneBits(0L);
        Assert.assertEquals(expected.length, result.length);
    }

    @Test
    public void testGetNumberOfBitsSet() throws Exception {
        Assert.assertEquals(3, HandEvaluatorFromMap.getNumberOfBitsSet(7L));
        Assert.assertEquals(3, HandEvaluatorFromMap.getNumberOfBitsSet(56L));
        Assert.assertEquals(4, HandEvaluatorFromMap.getNumberOfBitsSet(58L));
        Assert.assertEquals(5, HandEvaluatorFromMap.getNumberOfBitsSet(2106L));
    }

    @Test
    public void testSetBit() throws Exception {
        Assert.assertEquals(58L, HandEvaluatorFromMap.setBit(1, 56L));
    }

    @Test
    public void testUnsetBit() throws Exception {
        Assert.assertEquals(56L, HandEvaluatorFromMap.unsetBit(1, 58L));
    }
}
