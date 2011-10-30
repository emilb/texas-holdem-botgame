package se.cygni.texasholdem.server.message;

import org.codemonkey.swiftsocketserver.ClientContext;
import org.codemonkey.swiftsocketserver.ServerMessageToClient;

import se.cygni.texasholdem.communication.message.TexasMessage;
import se.cygni.texasholdem.communication.message.TexasMessageParser;

/**
 * For sending a chat message to the client.
 */
public class ServerToClientMessage extends ServerMessageToClient {

    private final TexasMessage message;

    public ServerToClientMessage(final ClientContext clientContext,
            final TexasMessage message) {

        super(clientContext);
        this.message = message;
    }

    @Override
    public String encode() {

        try {
            return TexasMessageParser.encodeMessage(message);
        } catch (final Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
