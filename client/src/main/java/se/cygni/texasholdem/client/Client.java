package se.cygni.texasholdem.client;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Executors;

import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.cygni.texasholdem.communication.ClientServer;
import se.cygni.texasholdem.communication.ClientServer.TexasHoldemServer;
import se.cygni.texasholdem.communication.ClientServer.TexasHoldemServer.BlockingInterface;
import se.cygni.texasholdem.communication.ClientServer.Void;

import com.google.protobuf.RpcController;
import com.google.protobuf.Service;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.CleanShutdownHandler;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.RpcConnectionEventNotifier;
import com.googlecode.protobuf.pro.duplex.client.DuplexTcpClientBootstrap;
import com.googlecode.protobuf.pro.duplex.execute.RpcServerCallExecutor;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;
import com.googlecode.protobuf.pro.duplex.listener.RpcConnectionEventListener;

public class Client {

    private static Logger log = LoggerFactory.getLogger(Client.class);

    private final int serverPort = 4711;
    private final String serverHostname = "localhost";
    private final int clientPort = 1147;
    private final String clientHostname = UUID.randomUUID().toString();
    private DuplexTcpClientBootstrap bootstrap;
    private PeerInfo client;
    private PeerInfo server;

    private RpcClientChannel serverChannel;
    private BlockingInterface serverService;

    public Client() {

    }

    public void init() {

        client = new PeerInfo(clientHostname, clientPort);
        server = new PeerInfo(serverHostname, serverPort);

        final RpcServerCallExecutor executor = new ThreadPoolCallExecutor(3, 10);

        bootstrap = new DuplexTcpClientBootstrap(
                client,
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()),
                executor);

        // Configure the client.

        final Service clientService = ClientServer.Client
                .newReflectiveService(new ClientImpl());
        bootstrap.getRpcServiceRegistry().registerService(clientService);

        // Set up the event pipeline factory.
        bootstrap.setOption("connectTimeoutMillis", 10000);
        bootstrap.setOption("connectResponseTimeoutMillis", 10000);
        bootstrap.setOption("sendBufferSize", 1048576);
        bootstrap.setOption("receiveBufferSize", 1048576);
        bootstrap.setOption("tcpNoDelay", false);

        final CleanShutdownHandler shutdownHandler = new CleanShutdownHandler();
        shutdownHandler.addResource(bootstrap);

        // setup a RPC event listener - it just logs what happens
        final RpcConnectionEventNotifier rpcEventNotifier = new RpcConnectionEventNotifier();
        final RpcConnectionEventListener listener = new RpcConnectionEventListener() {

            @Override
            public void connectionReestablished(
                    final RpcClientChannel clientChannel) {

                log.info("connectionReestablished " + clientChannel);
            }

            @Override
            public void connectionOpened(final RpcClientChannel clientChannel) {

                log.info("connectionOpened " + clientChannel);
                log.debug("peerInfo.name: "
                        + clientChannel.getPeerInfo().getName());
            }

            @Override
            public void connectionLost(final RpcClientChannel clientChannel) {

                log.info("connectionLost " + clientChannel);
            }

            @Override
            public void connectionChanged(final RpcClientChannel clientChannel) {

                log.info("connectionChanged " + clientChannel);
            }
        };
        rpcEventNotifier.setEventListener(listener);
        bootstrap.registerConnectionEventListener(rpcEventNotifier);
    }

    public void connect() throws IOException {

        init();
        serverChannel = bootstrap.peerWith(server);
        serverService = TexasHoldemServer.newBlockingStub(serverChannel);
    }

    public void closeConnection() {

        serverChannel.close();
    }

    public void pingServer() throws ServiceException {

        final RpcController controller = serverChannel.newRpcController();
        final Void voidRequest = Void.getDefaultInstance();
        serverService.ping(controller, voidRequest);
    }

    /**
     * @param args
     */
    public static void main(final String[] args) throws Exception {

        final Client client = new Client();
        client.connect();

        final Thread t = new Thread(new Runnable() {

            @Override
            public void run() {

                while (true) {
                    try {
                        client.pingServer();
                    } catch (final ServiceException e1) {
                        e1.printStackTrace();
                    }

                    try {
                        Thread.sleep(4000);
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }
}
