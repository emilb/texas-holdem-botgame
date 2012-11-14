package se.cygni.texasholdem.client;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.texasholdem.communication.lock.ResponseLock;
import se.cygni.texasholdem.communication.message.TexasMessage;
import se.cygni.texasholdem.communication.message.TexasMessageParser;
import se.cygni.texasholdem.communication.message.event.TexasEvent;
import se.cygni.texasholdem.communication.message.exception.TexasException;
import se.cygni.texasholdem.communication.message.request.ActionRequest;
import se.cygni.texasholdem.communication.message.request.RegisterForPlayRequest;
import se.cygni.texasholdem.communication.message.request.TexasRequest;
import se.cygni.texasholdem.communication.message.response.ActionResponse;
import se.cygni.texasholdem.communication.message.response.RegisterForPlayResponse;
import se.cygni.texasholdem.communication.message.response.TexasResponse;
import se.cygni.texasholdem.communication.netty.JsonDelimiter;
import se.cygni.texasholdem.game.Action;
import se.cygni.texasholdem.game.Room;
import se.cygni.texasholdem.game.util.ValidPlayerNameVerifier;
import se.cygni.texasholdem.player.Player;

import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class handles the communication between client and server.
 */
public class PlayerClient extends SimpleChannelHandler {

    private static final long RESPONSE_TIMEOUT_MS = 80000;
    private static final long CONNECT_WAIT_MS = 1200;

    private static Logger log = LoggerFactory.getLogger(PlayerClient.class);

    private final ClientEventDispatcher clientEventDispatcher;
    private final ClientEventDispatcher currentPlayStateEventDispatcher;
    private final SyncMessageResponseManager responseManager;
    private final Player player;
    private Channel channel;
    private boolean isConnected = false;
    private final String serverHost;
    private final int serverPort;
    private Timer connectionChecker;
    private final CurrentPlayState currentPlayState;


    /**
     * @param player     the Player in game
     * @param serverHost the host name or IP adr to the server
     * @param serverPort the port at which the server is accepting connections
     */
    public PlayerClient(final Player player, final String serverHost, final int serverPort) {

        this.player = player;
        this.serverHost = serverHost;
        this.serverPort = serverPort;

        currentPlayState = new CurrentPlayState(player.getName());
        responseManager = new SyncMessageResponseManager();
        clientEventDispatcher = new ClientEventDispatcher(player);
        currentPlayStateEventDispatcher = new ClientEventDispatcher(currentPlayState.getPlayerImpl());
    }

    /**
     * The reference to the CurrentPlayState is valid through the whole session and can
     * safely be referenced.
     *
     * @return the CurrentPlayState
     */
    public CurrentPlayState getCurrentPlayState() {
        return currentPlayState;
    }

    public void connect() throws Exception {

        log.info("Connecting to {} at port {}", serverHost, serverPort);

        if (connectionChecker != null) {
            connectionChecker.cancel();
        }

        Executor bossPool = Executors.newCachedThreadPool();
        Executor workerPool = Executors.newCachedThreadPool();
        ChannelFactory channelFactory = new NioClientSocketChannelFactory(bossPool, workerPool);
        ChannelPipelineFactory pipelineFactory = new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(
                        // new ZlibEncoder(ZlibWrapper.GZIP),
                        // new ZlibDecoder(ZlibWrapper.GZIP),
                        new DelimiterBasedFrameDecoder(4096, true, new ChannelBuffer[]{
                                ChannelBuffers.wrappedBuffer(JsonDelimiter.delimiter())}),
                        new StringDecoder(CharsetUtil.UTF_8),
                        new StringEncoder(CharsetUtil.UTF_8),
                        PlayerClient.this);
            }
        };
        ClientBootstrap bootstrap = new ClientBootstrap(channelFactory);
        bootstrap.setPipelineFactory(pipelineFactory);
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);

        InetSocketAddress addressToConnectTo = new InetSocketAddress(serverHost, serverPort);
        ChannelFuture cf = bootstrap.connect(addressToConnectTo);
        cf.await();
        cf.await(2000, TimeUnit.MILLISECONDS);
        cf.awaitUninterruptibly();
        cf.awaitUninterruptibly(2000, TimeUnit.MILLISECONDS);
        cf.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                // chek to see if we succeeded
                if (future.isSuccess()) {

                    isConnected = true;
                    channel = future.getChannel();
                    player.connectionToGameServerEstablished();
                }
            }
        });

        waitForClientConnected();


    }

    private void waitForClientConnected() {
        long startTime = System.currentTimeMillis();
        while (!isConnected) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
            }
            if (System.currentTimeMillis() > startTime + CONNECT_WAIT_MS) {
                throw new RuntimeException("Connection to server timed out, is it alive?");
            }
        }
        connectionChecker = initConnectionStatusTimer();
    }

    public void disconnect() {

        channel.disconnect();
        isConnected = false;
    }

    public Player getPlayer() {

        return player;
    }

    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        String message = (String) e.getMessage();
        onMessageReceived(TexasMessageParser.decodeMessage(message));
    }

    protected void onMessageReceived(final TexasMessage message) {

        if (message instanceof TexasEvent) {
            currentPlayStateEventDispatcher.onEvent((TexasEvent) message);
            clientEventDispatcher.onEvent((TexasEvent) message);
            return;
        }

        if (message instanceof ActionRequest) {
            final Action action = player
                    .actionRequired((ActionRequest) message);
            final ActionResponse response = new ActionResponse();
            response.setRequestId(((ActionRequest) message).getRequestId());
            response.setAction(action);
            sendMessage(response);
        }

        if (message instanceof TexasResponse) {
            final TexasResponse response = (TexasResponse) message;
            final String requestId = response.getRequestId();

            final ResponseLock lock = responseManager.pop(requestId);
            lock.setResponse(response);

            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }

    public boolean registerForPlay(Room room)
            throws se.cygni.texasholdem.game.exception.GameException {

        final RegisterForPlayRequest request = new RegisterForPlayRequest();
        request.setRequestId(getUniqueRequestId());
        request.name = getPlayerName();
        request.room = room;

        // Validate player name
        ValidPlayerNameVerifier.verifyName(request.name);

        final TexasMessage resp = sendAndWaitForResponse(request);

        if (resp instanceof RegisterForPlayResponse) {
            return true;
        }

        if (resp instanceof TexasException) {
            final TexasException ge = (TexasException) resp;
            ge.throwException();
        }

        return false;
    }

    protected String getPlayerName() {
        return player.getName();
    }

    protected TexasResponse sendAndWaitForResponse(final TexasRequest request) {

        final ResponseLock lock = responseManager.push(request.getRequestId());
        sendMessage(request);
        synchronized (lock) {
            if (lock.getResponse() == null) {
                try {
                    lock.wait(RESPONSE_TIMEOUT_MS);
                } catch (final InterruptedException e) {
                }
            }
        }

        if (lock.getResponse() == null) {
            throw new RuntimeException("Did not get response in time");
        }

        return lock.getResponse();
    }

    protected void sendMessage(final TexasMessage message) {
        try {
            channel.write(TexasMessageParser.encodeMessage(message) + new String(JsonDelimiter.delimiter()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    protected String getUniqueRequestId() {

        return UUID.randomUUID().toString();
    }

    private Timer initConnectionStatusTimer() {
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!channel.isConnected() || !channel.isOpen()) {
                    cancel();
                    try {
                        channel.disconnect();
                    } catch (Exception e) {
                    }
                    player.connectionToGameServerLost();
                }
            }
        }, 50, 250);
        return timer;
        // delay, repeat every ms
    }

    public void exceptionCaught(
            ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {

        log.info("Client exception: {} (Don't worry this can happen if for example the server disconnects you just before you " +
                "disconnect yourself)", e.getCause() != null ? e.getCause().getClass().getCanonicalName() : e.getClass().getCanonicalName());
    }
}
