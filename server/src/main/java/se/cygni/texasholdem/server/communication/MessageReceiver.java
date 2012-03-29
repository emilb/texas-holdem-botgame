package se.cygni.texasholdem.server.communication;

import org.codemonkey.swiftsocketserver.ClientContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.cygni.texasholdem.communication.lock.ResponseLock;
import se.cygni.texasholdem.communication.message.TexasMessage;
import se.cygni.texasholdem.communication.message.request.RegisterForPlayRequest;
import se.cygni.texasholdem.communication.message.response.TexasResponse;
import se.cygni.texasholdem.server.eventbus.RegisterForPlayWrapper;

import com.google.common.eventbus.EventBus;

@Service
public class MessageReceiver {

    private static Logger log = LoggerFactory
            .getLogger(MessageReceiver.class);

    private final ResponseLockManager responseLockManager;
    private final EventBus eventBus;

    @Autowired
    public MessageReceiver(final ResponseLockManager responseLockManager,
            final EventBus eventBus) {

        this.responseLockManager = responseLockManager;
        this.eventBus = eventBus;
    }

    public void onRequest(
            final ClientContext clientContext,
            final TexasMessage message) {

        // log.debug("Received a message: " + message.getType());

        if (message instanceof RegisterForPlayRequest) {
            eventBus.post(new RegisterForPlayWrapper(clientContext,
                    (RegisterForPlayRequest) message));
            return;
        }

        // Check if there is a waiting lock for this response
        if (message instanceof TexasResponse) {
            final TexasResponse response = (TexasResponse) message;
            if (responseLockManager.containsRequestId(response.getRequestId())) {
                final ResponseLock lock = responseLockManager.pop(response
                        .getRequestId());

                lock.setResponse(response);

                synchronized (lock) {
                    lock.notifyAll();
                }
            }

            return;
        }

        eventBus.post(message);
    }
}
