package se.cygni.texasholdem.server;

import javax.annotation.PostConstruct;

import org.codemonkey.swiftsocketserver.SwiftSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.cygni.texasholdem.SystemSettings;
import se.cygni.texasholdem.server.message.ClientToServerMessage;
import se.cygni.texasholdem.server.message.ServerToClientMessage;

@Service
public class SocketServer {

    @SuppressWarnings("unused")
    private static Logger log = LoggerFactory
            .getLogger(SocketServer.class);

    private final MessageReceiver messageReceiver;

    private final SystemSettings systemSettings;

    private SwiftSocketServer socketServer;

    @Autowired
    public SocketServer(final MessageReceiver messageReceiver,
            final SystemSettings systemSettings) {

        this.messageReceiver = messageReceiver;
        this.systemSettings = systemSettings;
    }

    @PostConstruct
    public void startServer() {

        init();
        socketServer.start();
    }

    private void init() {

        socketServer = new SwiftSocketServer(systemSettings.getPort());
        socketServer.registerClientMessageToServerType(1,
                ClientToServerMessage.class);
        socketServer.registerServerMessageToClientId(1,
                ServerToClientMessage.class);
        socketServer
                .registerExecutionContext(ClientToServerMessage.class,
                        messageReceiver);
    }

    public void sendMessage(final ServerToClientMessage message) {

        socketServer.sendMessage(message);
    }
}
