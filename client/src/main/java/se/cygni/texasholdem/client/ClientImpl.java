package se.cygni.texasholdem.client;

import java.io.IOException;
import java.util.concurrent.Executors;

import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.cygni.texasholdem.communication.ClientServer;
import se.cygni.texasholdem.communication.ClientServer.Client;
import se.cygni.texasholdem.communication.ClientServer.Message;
import se.cygni.texasholdem.communication.ClientServer.Ping;
import se.cygni.texasholdem.communication.ClientServer.Void;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.Service;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.CleanShutdownHandler;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.RpcConnectionEventNotifier;
import com.googlecode.protobuf.pro.duplex.client.DuplexTcpClientBootstrap;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;
import com.googlecode.protobuf.pro.duplex.listener.RpcConnectionEventListener;

public class ClientImpl implements
        ClientServer.Client.Interface {

    private static Logger log = LoggerFactory.getLogger(ClientImpl.class);

    @Override
    public void ping(
            final RpcController controller,
            final Void request,
            final RpcCallback<Ping> done) {

        log.debug("Client received a Ping request");

        final Ping pingResponse = Ping.newBuilder().build();
        done.run(pingResponse);

    }

    @Override
    public void serverIsShuttingDown(
            final RpcController controller,
            final Message request,
            final RpcCallback<Void> done) {

        log.debug("Client received a server shutdown notification");
        done.run(Void.getDefaultInstance());
    }

    public void initServerConnection(
            final String serverHostName,
            final int serverPort,
            final String clientHostName,
            final int clientPort) throws IOException, ServiceException {

        log.debug("initServerConnection....");

        final PeerInfo client = new PeerInfo(clientHostName, clientPort);
        final PeerInfo server = new PeerInfo(serverHostName, serverPort);

        final CleanShutdownHandler shutdownHandler = new CleanShutdownHandler();
        try {
            final DuplexTcpClientBootstrap bootstrap = new DuplexTcpClientBootstrap(
                    client, new NioClientSocketChannelFactory(
                            Executors.newCachedThreadPool(),
                            Executors.newCachedThreadPool()),
                    new ThreadPoolCallExecutor(3, 10));
            bootstrap.setOption("connectTimeoutMillis", 10000);
            bootstrap.setOption("connectResponseTimeoutMillis", 10000);
            bootstrap.setOption("receiveBufferSize", 1048576);
            bootstrap.setOption("tcpNoDelay", false);

            // register myself as acting server of ClientIface
            final Service clientService = Client.newReflectiveService(this);
            bootstrap.getRpcServiceRegistry().registerService(clientService);

            shutdownHandler.addResource(bootstrap);

            // Set up the event pipeline factory.
            // setup a RPC event listener - it just logs what happens
            final RpcConnectionEventNotifier rpcEventNotifier = new RpcConnectionEventNotifier();
            final RpcConnectionEventListener listener = new RpcConnectionEventListener() {

                @Override
                public void connectionReestablished(
                        final RpcClientChannel clientChannel) {

                    log.info("connectionReestablished " + clientChannel);
                }

                @Override
                public void connectionOpened(
                        final RpcClientChannel clientChannel) {

                    log.info("connectionOpened " + clientChannel);
                }

                @Override
                public void connectionLost(final RpcClientChannel clientChannel) {

                    log.info("connectionLost " + clientChannel);
                    System.exit(1);
                }

                @Override
                public void connectionChanged(
                        final RpcClientChannel clientChannel) {

                    log.info("connectionChanged " + clientChannel);
                }
            };
            rpcEventNotifier.setEventListener(listener);
            bootstrap.registerConnectionEventListener(rpcEventNotifier);

            final RpcClientChannel channel = bootstrap.peerWith(server);

            final se.cygni.texasholdem.communication.ClientServer.TexasHoldemServer.BlockingInterface myService = ClientServer.TexasHoldemServer
                    .newBlockingStub(channel);

            long startTS = 0;
            long endTS = 0;

            startTS = System.currentTimeMillis();
            for (int i = 0; i < 0; i++) {

                final RpcController controller = channel.newRpcController();

                final Void voidVar = Void.getDefaultInstance();
                myService.ping(controller, voidVar);
                log.debug("Got PING from server as response!");
            }
            endTS = System.currentTimeMillis();
            log.info("BlockingCalls " + 500 + " in " + (endTS - startTS)
                    / 1000 + "s");

        } finally {

        }
    }

    public static void main(final String[] args) throws Exception {

        final ClientImpl client = new ClientImpl();
        client.initServerConnection("localhost", 4711, "localhost", 4712);
    }
}
