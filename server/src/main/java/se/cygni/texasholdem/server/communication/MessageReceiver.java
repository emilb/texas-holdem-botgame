package se.cygni.texasholdem.server.communication;

import com.google.common.eventbus.EventBus;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.cygni.texasholdem.communication.lock.ResponseLock;
import se.cygni.texasholdem.communication.message.TexasMessage;
import se.cygni.texasholdem.communication.message.request.RegisterForPlayRequest;
import se.cygni.texasholdem.communication.message.response.TexasResponse;
import se.cygni.texasholdem.server.eventbus.RegisterForPlayWrapper;

@Service
public class MessageReceiver {

    private final ResponseLockManager responseLockManager;
    private final EventBus eventBus;

    @Autowired
    public MessageReceiver(final ResponseLockManager responseLockManager,
                           final EventBus eventBus) {

        this.responseLockManager = responseLockManager;
        this.eventBus = eventBus;
    }

    public void onRequest(
            final ChannelHandlerContext context,
            final TexasMessage message) {

        if (message instanceof RegisterForPlayRequest) {
            eventBus.post(new RegisterForPlayWrapper(context,
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
