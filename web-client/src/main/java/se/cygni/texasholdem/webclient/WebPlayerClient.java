package se.cygni.texasholdem.webclient;

import org.atmosphere.cpr.AtmosphereResource;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.compression.ZlibDecoder;
import org.jboss.netty.handler.codec.compression.ZlibEncoder;
import org.jboss.netty.handler.codec.compression.ZlibWrapper;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.texasholdem.client.SyncMessageResponseManager;
import se.cygni.texasholdem.communication.lock.ResponseLock;
import se.cygni.texasholdem.communication.message.TexasMessage;
import se.cygni.texasholdem.communication.message.TexasMessageParser;
import se.cygni.texasholdem.communication.message.event.TexasEvent;
import se.cygni.texasholdem.communication.message.exception.TexasException;
import se.cygni.texasholdem.communication.message.request.ActionRequest;
import se.cygni.texasholdem.communication.message.request.RegisterForPlayRequest;
import se.cygni.texasholdem.communication.message.request.TexasRequest;
import se.cygni.texasholdem.communication.message.response.RegisterForPlayResponse;
import se.cygni.texasholdem.communication.message.response.TexasResponse;
import se.cygni.texasholdem.communication.netty.JsonDelimiter;
import se.cygni.texasholdem.game.Room;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Client for web player.
 * <p/>
 * TODO: merge common code with PlayerClient
 *
 * @author jonas
 */
public class WebPlayerClient extends SimpleChannelHandler {

    // Warning! If these values are changed be sure to update Launcher as well.
    public static final String SYS_PROP_REMOTE_HOST = "remote.host";
    public static final String SYS_PROP_REMOTE_PORT = "remote.port";


    private static final long RESPONSE_TIMEOUT_MS = 80000;
    private static final long CONNECT_WAIT_MS = 1200;

    private static Logger log = LoggerFactory
            .getLogger(WebPlayerClient.class);

    private AtmosphereResource atmosphereResource;
    private final SyncMessageResponseManager responseManager;
    private Channel channel;
    private boolean isConnected = false;
    private String playerName;
    private Room room;

    public WebPlayerClient(final String playerName, final Room room, final AtmosphereResource atmosphereResource) throws Exception {
        this.playerName = playerName;
        this.room = room;
        responseManager = new SyncMessageResponseManager();
        this.atmosphereResource = atmosphereResource;
        connect();
    }

    protected void connect() throws Exception {

        Executor bossPool = Executors.newCachedThreadPool();
        Executor workerPool = Executors.newCachedThreadPool();
        ChannelFactory channelFactory = new NioClientSocketChannelFactory(bossPool, workerPool);
        ChannelPipelineFactory pipelineFactory = new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(
                        new ZlibEncoder(ZlibWrapper.GZIP),
                        new ZlibDecoder(ZlibWrapper.GZIP),
                        new DelimiterBasedFrameDecoder(4096, true, new ChannelBuffer[]{
                                ChannelBuffers.wrappedBuffer(JsonDelimiter.delimiter())}),
                        new StringDecoder(CharsetUtil.UTF_8),
                        new StringEncoder(CharsetUtil.UTF_8),
                        WebPlayerClient.this);
            }
        };
        ClientBootstrap bootstrap = new ClientBootstrap(channelFactory);
        bootstrap.setPipelineFactory(pipelineFactory);
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);

        // Phew. Ok. We built all that. Now what ?
        String remoteHost = getRemoteHostFromSystemProperties();
        int remotePort = getRemotePortFromSystemProperties();

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

    public String getRemoteHostFromSystemProperties() {
        return System.getProperty(SYS_PROP_REMOTE_HOST, "localhost");
    }

    public int getRemotePortFromSystemProperties() {
        String remotePort = System.getProperty(SYS_PROP_REMOTE_PORT);
        try {
            return Integer.parseInt(remotePort);
        } catch (Exception e) {
            log.warn("Failed to parse remote port: {} to integer value. Defaulting to 4711", remotePort);
            return 4711;
        }
    }

    // TODO: rensa upp, blockar inte här - i så fall känn av att respons kommit i webklienten
    // TODO: Ta bort hårdkodning för room = Room.TRAINING
    public boolean registerForPlay()
            throws se.cygni.texasholdem.game.exception.GameException {

        final RegisterForPlayRequest request = new RegisterForPlayRequest();
        request.setRequestId(getUniqueRequestId());
        request.name = getPlayerName();
        request.room = room;

        // Skicka request och vänta på svar
        final TexasMessage resp = sendAndWaitForResponse(request);

        String responseJson = null;
        try {
            responseJson = TexasMessageParser.encodeMessage(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("Sent RegisterForPlayRequest, response: " + responseJson);

        if (resp instanceof RegisterForPlayResponse) {
            try {
                respondAndFlush(responseJson);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        if (resp instanceof TexasException) {
            final TexasException ge = (TexasException) resp;
            ge.throwException();
        }

        return false;
    }

    /**
     * SwiftSocketClient takes a few 10ths of a seconds to start up.
     */
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
    }

    protected String getPlayerName() {
        return playerName;
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

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        String message = (String) e.getMessage();
        onMessageReceived(TexasMessageParser.decodeMessage(message));
    }

    /**
     * Handler for server-to-client messages
     */
    public void onMessageReceived(final TexasMessage message) {

        if (message instanceof TexasEvent) {
            String texasEventJson;
            try {
                texasEventJson = TexasMessageParser.encodeMessage(message);
                respondAndFlush(texasEventJson);
            } catch (Exception e) {
                log.warn("Failed to transfer message to web client. Disconnecting.");
                disconnect();
            }
            return;
        }

        // Defer this to web client by resuming suspended websocket/request with
        // ActionRequest response. This will cause the javascript webclient to respond to
        // with Action taken by that player, sent as a POST-message to the handler
        // which won't be suspended
        if (message instanceof ActionRequest) {
            try {
                String actionRequestJson = TexasMessageParser.encodeMessage(message);
                respondAndFlush(actionRequestJson);
            } catch (IOException e) {
                log.warn("Failed to transfer message to web client. Disconnecting.");
                disconnect();
            }
        }

        if (message instanceof TexasResponse) {
            final TexasResponse response = (TexasResponse) message;
            final String requestId = response.getRequestId();

            log.info("onEvent: TexasRepsponse: " + message);
            final ResponseLock lock = responseManager.pop(requestId);
            lock.setResponse(response);

            synchronized (lock) {
                lock.notifyAll();
            }
        }

        // Variant om vi inte gör sendAndWaitForResponse() - svara klienten direkt här
//        if (message instanceof TexasResponse) {
//        	try {
//				String actionRequestJson = TexasMessageParser.encodeMessage(message);
//				respondAndFlush(actionRequestJson);
//			} catch (IOException e) {
//				// TODO felhantering
//				throw new RuntimeException("Error on forwarding TexasResponse to client", e);
//			}
//        }
    }

    private void disconnect() {
        try {
            channel.disconnect();
            atmosphereResource.session().invalidate();
        } catch (Exception e) {
        }
    }

    private void respondAndFlush(String jsonResponse) throws IOException {
        if (atmosphereResource.isCancelled()) {
            log.info("My web client is gone. Terminating poker server session");
            disconnect();
            return;
        }
        atmosphereResource.getResponse().getWriter().write(jsonResponse);
        atmosphereResource.getResponse().getWriter().flush();
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
