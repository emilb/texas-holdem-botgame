package se.cygni.texasholdem.server.communication;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.cygni.texasholdem.communication.lock.ResponseLock;
import se.cygni.texasholdem.communication.message.TexasMessage;
import se.cygni.texasholdem.communication.message.request.TexasRequest;
import se.cygni.texasholdem.communication.message.response.TexasResponse;
import se.cygni.texasholdem.server.SocketServer;

@Service
public class MessageSender {

    private static final long RESPONSE_TIMEOUT = 3000;

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

    public ChannelFuture sendMessage(
            final ChannelHandlerContext context,
            final TexasMessage message) {

        return socketServer.sendMessage(context, message);
    }

    public TexasResponse sendAndWaitForResponse(
            final ChannelHandlerContext context,
            final TexasRequest request) {


        if (context == null || !context.getChannel().isConnected()) {
            throw new RuntimeException("Client context not valid");
        }

        final ResponseLock lock = responseLockManager.push(request
                .getRequestId());

        long sendMessageTStamp = System.currentTimeMillis();
        ChannelFuture future = sendMessage(context, request);

        try {
            future.await();
        } catch (InterruptedException e) {
            log.info("ChannelFuture was interrupted");
        }

        if (!future.isDone() || !future.isSuccess()) {
            log.warn("Failed to send message to client {}, cause: ",
                    context.getChannel().getRemoteAddress(),
                    future.getCause());
        }

        log.info("Took {}ms to send message to client at {}",
                (System.currentTimeMillis() - sendMessageTStamp),
                context.getChannel().getRemoteAddress());

        synchronized (lock) {
            if (lock.getResponse() == null) {
                // If we have no response yet, then we want to continue only when we have one
                try {
                    lock.wait(RESPONSE_TIMEOUT);
                } catch (final InterruptedException e) {
                }
            }
        }

        if (lock.getResponse() == null) {
            throw new RuntimeException("Did not get response in time");
        }

        return lock.getResponse();
    }
}
