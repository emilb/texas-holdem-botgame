package se.cygni.texasholdem.server;

import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.cygni.texasholdem.communication.ClientServer;

import com.google.protobuf.Service;
import com.googlecode.protobuf.pro.duplex.CleanShutdownHandler;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.RpcConnectionEventNotifier;
import com.googlecode.protobuf.pro.duplex.execute.RpcServerCallExecutor;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;
import com.googlecode.protobuf.pro.duplex.listener.RpcConnectionEventListener;
import com.googlecode.protobuf.pro.duplex.server.DuplexTcpServerBootstrap;

public class RpcServer {

    private static Logger log = LoggerFactory
            .getLogger(RpcServer.class);

    private final String serverHostname;
    private final int serverPort;

    private final GameServiceImpl gameService;

    private final GameServer gameServer;

    private DuplexTcpServerBootstrap bootstrap;

    public RpcServer(final String serverHostname, final int serverPort,
            final GameServiceImpl gameService, final GameServer gameServer) {

        this.serverHostname = serverHostname;
        this.serverPort = serverPort;
        this.gameService = gameService;
        this.gameServer = gameServer;
    }

    private void initServer() {

        final PeerInfo serverInfo = new PeerInfo(serverHostname, serverPort);

        final RpcServerCallExecutor executor = new ThreadPoolCallExecutor(3, 10);

        // Configure the server.
        bootstrap = new DuplexTcpServerBootstrap(
                serverInfo,
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()),
                executor);

        bootstrap.setOption("sendBufferSize", 1048576);
        bootstrap.setOption("receiveBufferSize", 1048576);
        bootstrap.setOption("child.receiveBufferSize", 1048576);
        bootstrap.setOption("child.sendBufferSize", 1048576);
        bootstrap.setOption("tcpNoDelay", false);

        final CleanShutdownHandler shutdownHandler = new CleanShutdownHandler();
        shutdownHandler.addResource(bootstrap);

        final RpcConnectionEventNotifier rpcEventNotifier = new RpcConnectionEventNotifier();
        rpcEventNotifier.setEventListener(createRpcConnectionEventListener());
        bootstrap.registerConnectionEventListener(rpcEventNotifier);

        // Register the GameService
        final Service gamePbService = ClientServer.GameService
                .newReflectiveService(gameService);
        bootstrap.getRpcServiceRegistry().registerService(gamePbService);
    }

    @PostConstruct
    public void startServer() {

        initServer();
        bootstrap.bind();

        log.debug("RpcServer started for host name: {}, listening to port: {}",
                serverHostname, serverPort);
    }

    private RpcConnectionEventListener createRpcConnectionEventListener() {

        return new RpcConnectionEventListener() {

            @Override
            public void connectionReestablished(
                    final RpcClientChannel clientChannel) {

                gameServer.registerNewClientConnection(clientChannel
                        .getPeerInfo().toString(), clientChannel);
                log.info("connectionReestablished " + clientChannel);
            }

            @Override
            public void connectionOpened(final RpcClientChannel clientChannel) {

                gameServer.registerNewClientConnection(clientChannel
                        .getPeerInfo().toString(), clientChannel);
                log.info("connectionOpened " + clientChannel);

            }

            @Override
            public void connectionLost(final RpcClientChannel clientChannel) {

                gameServer.registerClientDisconnect(clientChannel.getPeerInfo()
                        .toString());
                log.info("connectionLost " + clientChannel);
            }

            @Override
            public void connectionChanged(final RpcClientChannel clientChannel) {

                gameServer.registerNewClientConnection(clientChannel
                        .getPeerInfo().toString(), clientChannel);
                log.info("connectionChanged " + clientChannel);
            }
        };
    }
}
