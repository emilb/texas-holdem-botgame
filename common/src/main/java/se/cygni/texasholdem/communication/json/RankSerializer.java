package se.cygni.texasholdem.communication.json;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import se.cygni.texasholdem.game.definitions.Rank;

public class RankSerializer extends JsonSerializer<Rank> {

    @Override
    public void serialize(
            final Rank rank,
            final JsonGenerator generator,
            final SerializerProvider serializerProvider)
            throws IOException, JsonProcessingException {

        generator.writeStartObject();
        generator.writeFieldName("r");
        generator.writeString(rank.getName());
        generator.writeEndObject();
    }

}
