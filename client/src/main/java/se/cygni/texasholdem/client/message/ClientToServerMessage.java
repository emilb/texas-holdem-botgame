package se.cygni.texasholdem.client.message;

import org.codemonkey.swiftsocketclient.ClientMessageToServer;

import se.cygni.texasholdem.communication.message.TexasMessage;
import se.cygni.texasholdem.communication.message.TexasMessageParser;

public class ClientToServerMessage extends ClientMessageToServer {

    private final TexasMessage message;

    public ClientToServerMessage(final TexasMessage message) {

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
