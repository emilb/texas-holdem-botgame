package se.cygni.texasholdem.communication.json;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import se.cygni.texasholdem.game.Card;

public class CardSerializer extends JsonSerializer<Card> {

    @Override
    public void serialize(
            final Card card,
            final JsonGenerator generator,
            final SerializerProvider serializerProvider)
            throws IOException, JsonProcessingException {

        generator.writeStartObject();
        generator.writeFieldName("c");
        generator.writeString(card.getRank().getName()
                + card.getSuit().getShortName());
        generator.writeEndObject();
    }

}
