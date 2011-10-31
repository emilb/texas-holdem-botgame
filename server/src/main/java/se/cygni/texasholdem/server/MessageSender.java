package se.cygni.texasholdem.server;

import org.codemonkey.swiftsocketserver.ClientContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.cygni.texasholdem.communication.message.TexasMessage;
import se.cygni.texasholdem.server.message.ServerToClientMessage;

import com.google.common.eventbus.EventBus;

@Service
public class MessageSender {

    private static Logger log = LoggerFactory
            .getLogger(MessageSender.class);

    private final SocketServer socketServer;

    private final EventBus eventBus;

    @Autowired
    public MessageSender(final SocketServer socketServer,
            final EventBus eventBus) {

        this.socketServer = socketServer;
        this.eventBus = eventBus;

    }

    public void sendMessage(
            final ClientContext clientContext,
            final TexasMessage message) {

        socketServer.sendMessage(new ServerToClientMessage(clientContext,
                message));
    }
}
