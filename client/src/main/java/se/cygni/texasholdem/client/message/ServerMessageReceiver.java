package se.cygni.texasholdem.client.message;

import se.cygni.texasholdem.communication.message.TexasMessage;

public interface ServerMessageReceiver {

	void onMessageReceived(TexasMessage message);

}
