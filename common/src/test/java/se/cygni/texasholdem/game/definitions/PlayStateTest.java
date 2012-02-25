package se.cygni.texasholdem.game.definitions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PlayStateTest {

    @Test
    public void testGetNextState() {

        assertEquals(PlayState.FLOP, PlayState.getNextState(PlayState.PRE_FLOP));
        assertEquals(PlayState.TURN, PlayState.getNextState(PlayState.FLOP));
        assertEquals(PlayState.RIVER, PlayState.getNextState(PlayState.TURN));
        assertEquals(PlayState.SHOWDOWN,
                PlayState.getNextState(PlayState.RIVER));
    }

    @Test(expected = IllegalStateException.class)
    public void testGetNextStateException() {

        PlayState.getNextState(PlayState.SHOWDOWN);
    }

    @Test
    public void testGetPreviousState() {

        assertEquals(PlayState.PRE_FLOP,
                PlayState.getPreviousState(PlayState.FLOP));
        assertEquals(PlayState.FLOP, PlayState.getPreviousState(PlayState.TURN));
        assertEquals(PlayState.TURN,
                PlayState.getPreviousState(PlayState.RIVER));
        assertEquals(PlayState.RIVER,
                PlayState.getPreviousState(PlayState.SHOWDOWN));
    }

    @Test(expected = IllegalStateException.class)
    public void testGetPreviousStateException() {

        PlayState.getPreviousState(PlayState.PRE_FLOP);
    }

    @Test
    public void testHasNextState() {

        assertTrue(PlayState.hasNextState(PlayState.PRE_FLOP));
        assertTrue(PlayState.hasNextState(PlayState.FLOP));
        assertTrue(PlayState.hasNextState(PlayState.TURN));
        assertTrue(PlayState.hasNextState(PlayState.RIVER));
        assertFalse(PlayState.hasNextState(PlayState.SHOWDOWN));
    }

    @Test
    public void testHasPreviousState() {

        assertFalse(PlayState.hasPreviousState(PlayState.PRE_FLOP));
        assertTrue(PlayState.hasPreviousState(PlayState.FLOP));
        assertTrue(PlayState.hasPreviousState(PlayState.TURN));
        assertTrue(PlayState.hasPreviousState(PlayState.RIVER));
        assertTrue(PlayState.hasPreviousState(PlayState.SHOWDOWN));

    }
}
