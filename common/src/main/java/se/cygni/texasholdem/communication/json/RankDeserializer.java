package se.cygni.texasholdem.communication.json;

import java.io.IOException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import se.cygni.texasholdem.game.definitions.Rank;

public class RankDeserializer extends JsonDeserializer<Rank> {

    @Override
    public Rank deserialize(
            final JsonParser parser,
            final DeserializationContext context)
            throws IOException, JsonProcessingException {

        parser.nextToken();
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String fieldName = parser.getCurrentName();
            if ("r".equals(fieldName)) {
                return Rank.get(parser.getText());
            }
        }

        throw new IllegalStateException();
    }

}
