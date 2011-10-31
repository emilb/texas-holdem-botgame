package se.cygni.texasholdem.communication.json;

import java.io.IOException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.definitions.Rank;
import se.cygni.texasholdem.game.definitions.Suit;

public class CardDeserializer extends JsonDeserializer<Card> {

    @Override
    public Card deserialize(
            final JsonParser parser,
            final DeserializationContext context)
            throws IOException, JsonProcessingException {

        parser.nextToken();
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String fieldName = parser.getCurrentName();
            if ("shorthand".equals(fieldName)) {
                final String rankAndSuit = parser.getText();

                final Rank r = Rank.get(rankAndSuit.substring(0, 1));
                final Suit s = Suit.get(rankAndSuit.substring(1, 2));

                return Card.valueOf(r, s);
            }
        }

        throw new IllegalStateException();
    }

}
