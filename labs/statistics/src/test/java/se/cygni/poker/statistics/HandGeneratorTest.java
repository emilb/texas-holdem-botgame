package se.cygni.poker.statistics;

import org.junit.Assert;
import org.junit.Test;

public class HandGeneratorTest {

    @Test
    public void testMoveLowestOneBitLeftBaseCaseOfValue1() throws Exception {

        Assert.assertEquals(1, HandGenerator.moveLowestOneBitLeft(1));
    }

    @Test
    public void testMoveLowestOneBitLeftSimpleMovement() throws Exception {

        // 1110 -> 1101
        Assert.assertEquals(13, HandGenerator.moveLowestOneBitLeft(14));
    }

    @Test
    public void testMoveLowestOneBitLeftLargerValue() throws Exception {

        // 1111 1000 -> 1111 0100
        Assert.assertEquals(244, HandGenerator.moveLowestOneBitLeft(248));
    }

    @Test
    public void testMoveLowestOneBitLeftAlreadyAtEnd() throws Exception {

        // 1111 0001 -> 1111 0001
        Assert.assertEquals(241, HandGenerator.moveLowestOneBitLeft(241));
    }

}
