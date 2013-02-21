package se.cygni.poker.statistics;

import gnu.trove.map.hash.TLongLongHashMap;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HandEvaluatorFromMap {

    private static int CARDS_TO_MATCH = 5;

    private static TLongLongHashMap fiveCardLookupMap;

    public static int getHandValue(long cards) {
        List<int[]> removalCombinations = getAllCombinationsOfRemovals(getPositionsForOneBits(cards));
        int highestHandValue = 0;
        for (int[] removalCombo : removalCombinations) {
            int handValue = getHandValueFiveCards(getCardsByRemoving(cards, removalCombo));
            highestHandValue = handValue > highestHandValue ? handValue : highestHandValue;
        }

        return highestHandValue;
    }

    public static int getHandValueFiveCards(long cards) {
        if (fiveCardLookupMap == null) {
            initMap();
        }

        return (int) fiveCardLookupMap.get(cards);
    }

    public static long getCardsByRemoving(long cards, int[] removePositions) {
        for (int removePos : removePositions) {
            cards = unsetBit(removePos, cards);
        }

        return cards;
    }

    /**
     * @param positions
     *
     * @return a List containing all permutations for removals
     */
    public static List<int[]> getAllCombinationsOfRemovals(int positions[]) {

        List<int[]> result = new ArrayList<int[]>();
        int noofToRemove = positions.length - CARDS_TO_MATCH;

        if (noofToRemove == 0) {
            return result;
        }

        // noofToRemove can be 1 or 2
        if (noofToRemove == 1) {
            for (int pos : positions) {
                result.add(new int[]{pos});
            }
            return result;
        }

        if (noofToRemove == 2) {
            for (int outer = 0; outer < positions.length; outer++) {
                for (int inner = outer + 1; inner < positions.length; inner++) {
                    result.add(new int[]{positions[outer], positions[inner]});
                }
            }

            return result;
        }

        throw new RuntimeException("Invalid number of positions");
    }

    public static int[] getPositionsForOneBits(long value) {

        int positions[] = new int[getNumberOfBitsSet(value)];

        int counter = 0;
        int lastPos = 0;
        while (value > 0) {
            int firstPos = Long.numberOfTrailingZeros(value);
            positions[counter] = firstPos + lastPos;
            value = value >> firstPos + 1;
            counter++;
            lastPos += firstPos + 1;
        }

        return positions;
    }

    public static int getNumberOfBitsSet(long value) {
        int counter = 0;
        while (value > 0) {
            int firstPos = Long.numberOfTrailingZeros(value);
            value = value >> firstPos + 1;
            counter++;
        }
        return counter;
    }

    public static long setBit(int pos, long value) {
        long mask = 1L << pos;

        return value | mask;
    }

    public static long unsetBit(int pos, long value) {
        long mask = ~(1L << pos);
        return value & mask;
    }

    private static void initMap() {
        try {
            //use buffering
            InputStream file = new FileInputStream("binaryCardsToHandMap_5_52.ser");
            InputStream buffer = new BufferedInputStream(file);
            ObjectInput input = new ObjectInputStream(buffer);
            try {
                //deserialize the List
                fiveCardLookupMap = (TLongLongHashMap) input.readObject();
                //display its data
                System.out.println("Read map with " + fiveCardLookupMap.size() + " number of keys");

            } finally {
                input.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
