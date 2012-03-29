package se.cygni.texasholdem.server.communication;

import org.codemonkey.swiftsocketserver.ClientContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.cygni.texasholdem.communication.lock.ResponseLock;
import se.cygni.texasholdem.communication.message.TexasMessage;
import se.cygni.texasholdem.communication.message.request.TexasRequest;
import se.cygni.texasholdem.communication.message.response.TexasResponse;
import se.cygni.texasholdem.server.SocketServer;
import se.cygni.texasholdem.server.message.ServerToClientMessage;

@Service
public class MessageSender {

    private static final long RESPONSE_TIMEOUT = 30000;

    private static Logger log = LoggerFactory
            .getLogger(MessageSender.class);

    private final SocketServer socketServer;
    private final ResponseLockManager responseLockManager;

    @Autowired
    public MessageSender(final SocketServer socketServer,
            final ResponseLockManager responseLockManager) {

        this.socketServer = socketServer;
        this.responseLockManager = responseLockManager;

    }

    public void sendMessage(
            final ClientContext clientContext,
            final TexasMessage message) {

        socketServer.sendMessage(new ServerToClientMessage(clientContext,
                message));
    }

    public TexasResponse sendAndWaitForResponse(
            final ClientContext clientContext,
            final TexasRequest request) {

        if (clientContext == null || !clientContext.isActive())
            throw new RuntimeException("Client context not valid");

        final ResponseLock lock = responseLockManager.push(request
                .getRequestId());

        System.currentTimeMillis();
        sendMessage(clientContext, request);

        synchronized (lock) {
            try {
                lock.wait(RESPONSE_TIMEOUT);
            } catch (final InterruptedException e) {
            }
        }

        if (lock.getResponse() == null)
            throw new RuntimeException("Did not get response in time");

        // log.debug("It took {}ms to get reply from client",
        // (System.currentTimeMillis() - startTime));
        return lock.getResponse();
    }
}
