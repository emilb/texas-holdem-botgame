package se.cygni.texasholdem.server;

import java.util.UUID;
import java.util.concurrent.Executors;

import javax.annotation.PreDestroy;

import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.cygni.texasholdem.communication.ClientServer;
import se.cygni.texasholdem.communication.ClientServer.Login;
import se.cygni.texasholdem.communication.ClientServer.LoginResult;
import se.cygni.texasholdem.communication.ClientServer.Ping;
import se.cygni.texasholdem.communication.ClientServer.Void;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.Service;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.RpcConnectionEventNotifier;
import com.googlecode.protobuf.pro.duplex.execute.RpcServerCallExecutor;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;
import com.googlecode.protobuf.pro.duplex.listener.RpcConnectionEventListener;
import com.googlecode.protobuf.pro.duplex.server.DuplexTcpServerBootstrap;

public class TexasHoldemServerImpl implements
        ClientServer.TexasHoldemServer.Interface {

    private static Logger log = LoggerFactory
            .getLogger(TexasHoldemServerImpl.class);

    private DuplexTcpServerBootstrap bootstrap;

    @Override
    public void ping(
            final RpcController controller,
            final Void request,
            final RpcCallback<Ping> done) {

        log.debug("Server received a Ping request");
        final Ping pingResponse = Ping.newBuilder().build();
        done.run(pingResponse);
    }

    @Override
    public void login(
            final RpcController controller,
            final Login request,
            final RpcCallback<LoginResult> done) {

        log.debug("Server received a Login request from: {}", request.getName());
        final LoginResult loginResult = LoginResult.newBuilder()
                .setName(request.getName())
                .setSessionId(UUID.randomUUID().toString()).build();
        done.run(loginResult);

    }

    public void init() {

        runServer();
    }

    public void runServer() {

        // SERVER
        final PeerInfo serverInfo = new PeerInfo("localhost", 4711);
        final RpcServerCallExecutor executor = new ThreadPoolCallExecutor(10,
                10);

        bootstrap = new DuplexTcpServerBootstrap(serverInfo,
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()), executor);
        log.info("Proto Serverbootstrap created");
        bootstrap.setOption("connectTimeoutMillis", 10000);
        bootstrap.setOption("connectResponseTimeoutMillis", 10000);
        bootstrap.setOption("receiveBufferSize", 1048576);
        bootstrap.setOption("tcpNoDelay", false);

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

        // Register Texas Service
        final Service texasService = ClientServer.TexasHoldemServer
                .newReflectiveService(this);

        bootstrap.getRpcServiceRegistry().registerService(texasService);
        log.info("Proto Registerservice executed");

        bootstrap.bind();
        log.info("Proto Server Bound to port " + 4711);
    }

    @PreDestroy
    protected void unbind() throws Throwable {

        super.finalize();
        bootstrap.releaseExternalResources();
        log.info("Texas Holdem Server Unbound");
    }

    // public void sendMessageToAllClients() {
    // bootstrap.getRpcClientRegistry().
    // final
    // se.cygni.texasholdem.communication.ClientServer.Client.BlockingInterface
    // clientService = ClientServer.Client
    // .newBlockingStub(clientChannel);
    //
    // final RpcController controller = clientChannel
    // .newRpcController();
    // try {
    // clientService.serverIsShuttingDown(controller, Message
    // .newBuilder().setMessage("Oh yeas").build());
    // } catch (final ServiceException e) {
    // e.printStackTrace();
    // }
    // }

    public static void main(final String[] args) {

        final TexasHoldemServerImpl server = new TexasHoldemServerImpl();
        server.init();

        while (true) {
            try {
                Thread.sleep(250);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
