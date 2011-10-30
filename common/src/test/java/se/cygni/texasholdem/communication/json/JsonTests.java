package se.cygni.texasholdem.communication.json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.Assert;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import se.cygni.texasholdem.game.definitions.Rank;

public class JsonTests {

    private static ObjectMapper mapper = new ObjectMapper();

    private static JsonFactory factory = new JsonFactory();

    @Test
    public void testRankToAndFromJson() throws JsonGenerationException,
            JsonMappingException, IOException {

        final Rank rank = Rank.FOUR;

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        mapper.writeValue(out, rank);
        final String json = out.toString();

        System.out.println(json);

        final Rank r = mapper.readValue(json, Rank.class);
        Assert.assertEquals(rank, r);

    }

}
