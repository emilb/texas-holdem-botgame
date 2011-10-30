package se.cygni.texasholdem.communication.json;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import se.cygni.texasholdem.game.definitions.Suit;

public class CardSerializer extends JsonSerializer<Suit> {

    @Override
    public void serialize(
            final Suit suit,
            final JsonGenerator generator,
            final SerializerProvider serializerProvider)
            throws IOException, JsonProcessingException {

        generator.writeStartObject();
        generator.writeFieldName("suit"); // TODO: Fix card serializser
        generator.writeString(suit.getShortName());
        generator.writeEndObject();
    }

}
