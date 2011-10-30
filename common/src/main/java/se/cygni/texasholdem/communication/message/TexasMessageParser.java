package se.cygni.texasholdem.communication.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import se.cygni.texasholdem.communication.message.type.IsATexasMessage;

@SuppressWarnings("unchecked")
public class TexasMessageParser {

    private static ObjectMapper mapper = new ObjectMapper();

    private static JsonFactory factory = new JsonFactory();
    private static String TYPE_IDENTIFIER = "type";

    private static Map<String, Class<? extends TexasMessage>> typeToClass = new HashMap<String, Class<? extends TexasMessage>>();
    static {
        final ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(
                true);
        scanner.addIncludeFilter(new AnnotationTypeFilter(IsATexasMessage.class));

        for (final BeanDefinition bd : scanner
                .findCandidateComponents("se.cygni.texasholdem.communication.message")) {

            try {
                typeToClass.put(bd.getBeanClassName(),
                        (Class<? extends TexasMessage>) Class.forName(bd
                                .getBeanClassName()));
            } catch (final ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static TexasMessage decodeMessage(final String msg)
            throws JsonParseException, JsonMappingException, IOException {

        final TexasMessage message = mapper
                .readValue(msg,
                        TexasMessageParser.parseAndGetClassForMessage(msg));

        return message;
    }

    public static String encodeMessage(final TexasMessage message)
            throws JsonGenerationException, JsonMappingException, IOException {

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        mapper.writeValue(out, message);
        return out.toString();
    }

    public static Class<? extends TexasMessage> parseAndGetClassForMessage(
            final String message) {

        JsonParser parser = null;
        try {
            parser = factory.createJsonParser(message);
            parser.nextToken();
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                final String nameField = parser.getCurrentName();
                parser.nextToken();
                if (nameField.equals(TYPE_IDENTIFIER))
                    return getClassForIdentifier(parser.getText());
            }
        } catch (final Exception e) {
            // JSON exception
            throw new IllegalArgumentException("Could not parse message: "
                    + message, e);
        } finally {
            if (parser != null)
                try {
                    parser.close();
                } catch (final IOException e) {
                }
        }

        // Nothing found
        throw new IllegalArgumentException("Could not find [" + TYPE_IDENTIFIER
                + "] in message: " + message);
    }

    public static Class<? extends TexasMessage> getClassForIdentifier(
            final String identifier) {

        if (typeToClass.containsKey(identifier))
            return typeToClass.get(identifier);

        throw new IllegalArgumentException("Unknown identifier: " + identifier);
    }
}
