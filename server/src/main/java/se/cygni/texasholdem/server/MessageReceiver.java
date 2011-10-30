package se.cygni.texasholdem.server;

import org.codemonkey.swiftsocketserver.ClientContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.cygni.texasholdem.communication.message.TexasMessage;
import se.cygni.texasholdem.communication.message.request.RegisterForPlayRequest;
import se.cygni.texasholdem.server.eventbus.RequestContextWrapper;

import com.google.common.eventbus.EventBus;

@Service
public class MessageReceiver {

    private static Logger log = LoggerFactory
            .getLogger(MessageReceiver.class);

    private final EventBus eventBus;

    @Autowired
    public MessageReceiver(final EventBus eventBus) {

        this.eventBus = eventBus;
    }

    public void onRequest(
            final ClientContext clientContext,
            final TexasMessage message) {

        log.debug("Received a message: " + message.getType());

        if (message instanceof RegisterForPlayRequest) {
            eventBus.post(new RequestContextWrapper(clientContext,
                    (RegisterForPlayRequest) message));
            return;
        }

        eventBus.post(message);
    }
}
