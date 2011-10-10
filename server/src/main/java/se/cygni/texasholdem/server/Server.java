package se.cygni.texasholdem.server;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.cygni.texasholdem.communication.ClientServer;
import se.cygni.texasholdem.communication.ClientServer.Client.BlockingInterface;
import se.cygni.texasholdem.communication.ClientServer.Message;

import com.google.protobuf.RpcController;
import com.google.protobuf.Service;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.CleanShutdownHandler;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.RpcConnectionEventNotifier;
import com.googlecode.protobuf.pro.duplex.execute.RpcServerCallExecutor;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;
import com.googlecode.protobuf.pro.duplex.listener.RpcConnectionEventListener;
import com.googlecode.protobuf.pro.duplex.server.DuplexTcpServerBootstrap;

public class Server {

    private static Logger log = LoggerFactory
            .getLogger(Server.class);

    private final int serverPort = 4711;
    private final String serverHostname = "localhost";

    private DuplexTcpServerBootstrap bootstrap;
    private final Set<RpcClientChannel> clients = new HashSet<RpcClientChannel>();

    public Server() {

    }

    private void init() {

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

        // setup a RPC event listener - it just logs what happens
        final RpcConnectionEventNotifier rpcEventNotifier = new RpcConnectionEventNotifier();
        final RpcConnectionEventListener listener = new RpcConnectionEventListener() {

            @Override
            public void connectionReestablished(
                    final RpcClientChannel clientChannel) {

                clients.add(clientChannel);
                log.info("connectionReestablished " + clientChannel);
            }

            @Override
            public void connectionOpened(final RpcClientChannel clientChannel) {

                clients.add(clientChannel);
                log.info("connectionOpened " + clientChannel);
                log.debug("peerInfo.name: "
                        + clientChannel.getPeerInfo().getName());
            }

            @Override
            public void connectionLost(final RpcClientChannel clientChannel) {

                clients.remove(clientChannel);
                log.info("connectionLost " + clientChannel);
            }

            @Override
            public void connectionChanged(final RpcClientChannel clientChannel) {

                clients.remove(clientChannel);
                log.info("connectionChanged " + clientChannel);
            }
        };

        rpcEventNotifier.setEventListener(listener);
        bootstrap.registerConnectionEventListener(rpcEventNotifier);

        // Register Texas Service
        final Service texasService = ClientServer.TexasHoldemServer
                .newReflectiveService(new TexasHoldemServerImpl());

        bootstrap.getRpcServiceRegistry().registerService(texasService);
    }

    public void startServer() {

        init();
        bootstrap.bind();
        log.debug("Server started");
    }

    public void sendMessageToAllClients(final String msg)
            throws ServiceException {

        if (clients.size() == 0)
            return;

        log.debug("Sending msg to all clients");
        final Message message = Message.newBuilder().setMessage(msg).build();

        for (final RpcClientChannel channel : clients) {
            final BlockingInterface clientService = ClientServer.Client
                    .newBlockingStub(channel);
            final RpcController clientController = channel.newRpcController();

            clientService.serverIsShuttingDown(clientController, message);
        }
    }

    /**
     * @param args
     */
    public static void main(final String[] args) {

        final Server server = new Server();
        server.startServer();

        final Thread t = new Thread(new Runnable() {

            @Override
            public void run() {

                int i = 0;
                while (true) {
                    try {
                        server.sendMessageToAllClients("test: " + i++);
                    } catch (final ServiceException e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(3000);
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }

}
