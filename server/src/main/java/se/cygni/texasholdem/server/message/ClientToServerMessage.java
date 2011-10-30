package se.cygni.texasholdem.server.message;

import org.codemonkey.swiftsocketserver.ClientContext;
import org.codemonkey.swiftsocketserver.ClientMessageToServer;

import se.cygni.texasholdem.communication.message.TexasMessage;
import se.cygni.texasholdem.communication.message.TexasMessageParser;
import se.cygni.texasholdem.server.MessageReceiver;

/**
 * For receiving a chat message from the client.
 */
public class ClientToServerMessage extends
        ClientMessageToServer<MessageReceiver> {

    /**
     * @see ClientMessageToServer#ClientMessageToServer
     */
    public ClientToServerMessage(final ClientContext clientContext) {

        super(clientContext);
    }

    private TexasMessage message;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void decode(final String requestStr) {

        try {
            message = TexasMessageParser.decodeMessage(requestStr);
        } catch (final Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final MessageReceiver controller) {

        controller.onRequest(getClientContext(), message);
    }
}
