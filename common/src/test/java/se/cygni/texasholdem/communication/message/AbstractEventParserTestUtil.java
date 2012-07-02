package se.cygni.texasholdem.communication.message;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import se.cygni.texasholdem.game.Hand;
import se.cygni.texasholdem.game.Player;
import se.cygni.texasholdem.game.PlayerShowDown;

public abstract class AbstractEventParserTestUtil {

    protected <T extends TexasMessage> T assertEncodeDecode(final T message)
            throws JsonParseException, JsonMappingException, IOException {

        final String jsonMsg = TexasMessageParser.encodeMessage(message);

        assertEquals(message.getClass(),
                TexasMessageParser.parseAndGetClassForMessage(jsonMsg));

        final TexasMessage tMessage = TexasMessageParser
                .decodeMessage(jsonMsg);

        assertEquals(message.getClass(), tMessage.getClass());

        return (T)tMessage;
    }

    protected static void assertEqualss(
            final Player expected,
            final Player actual) {

        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getChipCount(), actual.getChipCount());
    }

    protected static void assertEqualss(
            final PlayerShowDown expected,
            final PlayerShowDown actual) {

        assertEqualss(expected.getPlayer(), actual.getPlayer());
        assertEqualss(expected.getHand(), actual.getHand());
        assertEquals(expected.getWonAmount(), actual.getWonAmount());
    }

    protected static void assertEqualss(
            final Hand expected,
            final Hand actual) {

        assertEquals(expected.getPokerHand(), actual.getPokerHand());
        assertEquals(expected.getCards().size(), actual.getCards().size());
        for (int i = 0; i < expected.getCards().size(); i++) {
            assertEquals(expected.getCards().get(i), actual.getCards().get(i));
        }
    }
}
