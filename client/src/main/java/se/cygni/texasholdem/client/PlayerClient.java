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
import se.cygni.texasholdem.player.Player;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PlayerClient extends SimpleChannelHandler {

    private static final long RESPONSE_TIMEOUT_MS = 80000;
    private static final long CONNECT_WAIT_MS = 1200;

    private final ClientEventDispatcher clientEventDispatcher;
    private final SyncMessageResponseManager responseManager;
    private final Player player;
    private Channel channel;
    private boolean isConnected = false;

    public PlayerClient(final Player player) {

        this.player = player;

        responseManager = new SyncMessageResponseManager();
        clientEventDispatcher = new ClientEventDispatcher(player);

        try {
            connect();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    protected void connect() throws Exception {

        Executor bossPool = Executors.newCachedThreadPool();
        Executor workerPool = Executors.newCachedThreadPool();
        ChannelFactory channelFactory = new NioClientSocketChannelFactory(bossPool, workerPool);
        ChannelPipelineFactory pipelineFactory = new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(
                        new DelimiterBasedFrameDecoder(4096, true, new ChannelBuffer[]{
                                ChannelBuffers.wrappedBuffer(JsonDelimiter.delimiter())}),
                        new StringDecoder(CharsetUtil.UTF_8),
                        new StringEncoder(CharsetUtil.UTF_8),
                        PlayerClient.this);
            }
        };
        ClientBootstrap bootstrap = new ClientBootstrap(channelFactory);
        bootstrap.setPipelineFactory(pipelineFactory);

        // Phew. Ok. We built all that. Now what ?
        String remoteHost = "localhost";
        int remotePort = 4711;
        InetSocketAddress addressToConnectTo = new InetSocketAddress(remoteHost, remotePort);
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
            if (System.currentTimeMillis() > startTime + CONNECT_WAIT_MS)
                throw new RuntimeException("Connection to server timed out, is it alive?");
        }
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

        final TexasMessage resp = sendAndWaitForResponse(request);

        if (resp instanceof RegisterForPlayResponse)
            return true;

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

        if (lock.getResponse() == null)
            throw new RuntimeException("Did not get response in time");

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
}
