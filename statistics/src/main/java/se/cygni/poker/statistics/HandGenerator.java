package se.cygni.poker.statistics;

import gnu.trove.map.hash.TLongLongHashMap;
import org.apache.commons.math.util.MathUtils;
import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.util.PokerHandUtil;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandGenerator {

    private static TLongLongHashMap binaryCardsToHandMap = new TLongLongHashMap();

    public static void main(String[] args) {

        long now = System.currentTimeMillis();
        createCombination_K_of_N(7, 52);
        System.out.println("Time taken: " + (System.currentTimeMillis() - now));
    }

    public static long moveLowestOneBitLeft(long val) {
        long lowestBitVal = Long.lowestOneBit(val);

        if (lowestBitVal == 1)
            return val;

        long restVal = val ^ lowestBitVal;

        lowestBitVal = lowestBitVal >> 1;

        return restVal | lowestBitVal;
    }

    private static void printBinaryValue(Long l) {
        String binaryVal = String.format("%52s", Long.toBinaryString(l)).replace(" ", "0");
        System.out.println(binaryVal + " " + l);
    }



    public static long calculateNoofCombinations(int k, int n) {
        return MathUtils.binomialCoefficient(n, k);
    }

    private static void writeToFile(PrintWriter out, long val, int pokerHandOrderValue) {

        String cards = String.format("%52s", Long.toBinaryString(val)).replace(" ", "0");
        out.write(cards);
        out.write(" ");
        out.write(pokerHandOrderValue + " ");
        List<Card> cardsList = BinaryConverter.longToCards(val);
        for (Card c : cardsList) {
            out.write(c.toShortString());
            out.write(" ");
        }
        out.write("\n");
    }

    private static PrintWriter getPrintWriter(int k, int n) {
        try {
            FileWriter outFile = new FileWriter("binaryCardsToHandMap_" + k + "_" + n + ".data");
            return new PrintWriter(outFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void closePrintWriter(PrintWriter out) {
        try {
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createCombination_K_of_N(int k, int n) {

        System.out.println("Getting combinations " + k + " out of " + n);

        PrintWriter out = getPrintWriter(k, n);

        long noofCombinations = calculateNoofCombinations(k, n);
//        binaryCardsToHandMap = new TLongLongHashMap((int)noofCombinations);
                //readMapFromFile(k, n);

        System.out.println("Calculating " + noofCombinations + " combinations...");

        long startVal = createStartValue(k, n);
        long endVal = createEndValue(k, n);

        int positionArray[] = createStartPositionArray(k, n);

        long currentVal = startVal;
        long currentIteration = 0;
        long lastTStamp = System.currentTimeMillis();
        long divider = 50000;

        while (currentVal != endVal) {

            if (currentIteration % divider == 0 && currentIteration != 0) {
                System.out.println(currentIteration + " delta took: " + (System.currentTimeMillis() - lastTStamp) + "ms");
                lastTStamp = System.currentTimeMillis();
                out.flush();
            }
            writeToFile(out, currentVal, getPokerHandOrderValue(currentVal));
//            binaryCardsToHandMap.put(currentVal, getPokerHandOrderValue(currentVal));

            long newVal = moveLowestOneBitLeft(currentVal);
            if (newVal != currentVal) {
                currentVal = newVal;
                currentIteration ++;
                positionArray[k-1] = positionArray[k-1] - 1;
                continue;
            }
            else {
                positionArray = updateIndices(positionArray);
                currentVal = getValueOfPositions(positionArray);
                currentIteration ++;
            }

        }

        writeToFile(out, currentVal, getPokerHandOrderValue(currentVal));
//        binaryCardsToHandMap.put(currentVal, getPokerHandOrderValue(currentVal));
        currentIteration ++;

        if (currentIteration != noofCombinations) {
            System.out.println("Warning, I didn't find all combinations, expected: " + noofCombinations + " but got: " + currentIteration + " Diff: " + (noofCombinations-currentIteration));
        }

        closePrintWriter(out);
//        System.out.println("Writing map to file...");
//        writeMapToFile(k, n);
    }

    private static int getPokerHandOrderValue(long value) {

//        return (int)binaryCardsToHandMap.get(value);
        return HandEvaluatorFromMap.getHandValue(value);
//        List<Card> cards = BinaryConverter.longToCards(value);
//        PokerHandUtil phu = new PokerHandUtil(cards);
//        return phu.getBestHand().getPokerHand().getOrderValue();
    }

    private static long getValueOfPositions(int[] positionArray) {

        long value = 0;

        for (int i = 0; i < positionArray.length; i++) {
            value = value | (1L << positionArray[i]);
        }

        return value;
    }

    private static int[] updateIndices(int[] positionArray) {

        if (positionArray[positionArray.length-1] != 0)
            throw new RuntimeException("Last position was not 0");

        int[] newPositionArray = new int[positionArray.length];
        for (int i = 0; i < positionArray.length; i++) {
            newPositionArray[i] = positionArray[i];
        }

        // Find which index to move
        int ixToMove = -1;
        for (int i = positionArray.length-1; i >= 0; i--) {
            if (positionArray[i] > positionArray.length-1-i) {
                ixToMove = i;
                break;
            }
        }

        // Nothing to move, end value should have been reached
        if (ixToMove == -1) {
            return newPositionArray;
        }

        // Update the array from ixToMove
        newPositionArray[ixToMove] = positionArray[ixToMove] - 1;

        for (int i = ixToMove+1; i < positionArray.length; i++) {
            newPositionArray[i] = newPositionArray[i-1] - 1;
        }

        return newPositionArray;
    }

    private static int[] createStartPositionArray(int k, int n) {
        int positionArray[] = new int[k];
        for (int i = 0; i < k; i++) {
            positionArray[i] = n-i-1; // Zero based indexes
        }
        return positionArray;
    }

    private static long createEndValue(int k, int n) {

        long lowest = 0;
        for (int i = 0; i < k; i++) {
            lowest = lowest | (1 << i);
        }
        return lowest;
    }

    private static long createStartValue(int k, int n) {

        return createEndValue(k, n) << (n - k);
    }

    private static void writeMapToFile(int k, int n) {
        try{
            //use buffering
            OutputStream file = new FileOutputStream( "binaryCardsToHandMap_" + k + "_" + n + ".ser" );
            OutputStream buffer = new BufferedOutputStream( file );
            ObjectOutput output = new ObjectOutputStream( buffer );
            try{
                output.writeObject(binaryCardsToHandMap);
            }
            finally{
                output.close();
            }
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    private static TLongLongHashMap readMapFromFile(int k, int n) {
        try {
            //use buffering
            InputStream file = new FileInputStream("binaryCardsToHandMap_" + k + "_" + n + ".ser");
            InputStream buffer = new BufferedInputStream(file);
            ObjectInput input = new ObjectInputStream(buffer);
            try {
                //deserialize the List
                TLongLongHashMap recoveredMap = (TLongLongHashMap) input.readObject();
                //display its data
                System.out.println("Read map with " + recoveredMap.size() + " number of keys");
                return recoveredMap;
            } finally {
                input.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
