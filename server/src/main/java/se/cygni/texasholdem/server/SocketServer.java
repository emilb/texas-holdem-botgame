package se.cygni.texasholdem.server;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.compression.ZlibDecoder;
import org.jboss.netty.handler.codec.compression.ZlibEncoder;
import org.jboss.netty.handler.codec.compression.ZlibWrapper;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.cygni.texasholdem.SystemSettings;
import se.cygni.texasholdem.communication.message.TexasMessage;
import se.cygni.texasholdem.communication.message.TexasMessageParser;
import se.cygni.texasholdem.communication.netty.JsonDelimiter;
import se.cygni.texasholdem.server.communication.MessageReceiver;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

@Service
public class SocketServer {

    private static Logger log = LoggerFactory
            .getLogger(SocketServer.class);

    private final MessageReceiver messageReceiver;

    private final SystemSettings systemSettings;

    ServerBootstrap bootstrap;

    @Autowired
    public SocketServer(final MessageReceiver messageReceiver,
                        final SystemSettings systemSettings) {

        this.messageReceiver = messageReceiver;
        this.systemSettings = systemSettings;
    }

    @PostConstruct
    public void startServer() {

        log.info("Starting socket server");
        init();
    }

    private void init() {

        bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        // Set up the pipeline factory.
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(
                        new ZlibEncoder(ZlibWrapper.GZIP),
                        new ZlibDecoder(ZlibWrapper.GZIP),
                        new DelimiterBasedFrameDecoder(4096, true, new ChannelBuffer[]{
                                ChannelBuffers.wrappedBuffer(JsonDelimiter.delimiter())}),
                        new StringDecoder(CharsetUtil.UTF_8),
                        new StringEncoder(CharsetUtil.UTF_8),
                        new TexasMessageHandler(messageReceiver)
                );
            }

            ;
        });

        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);

        bootstrap.bind(new InetSocketAddress("0.0.0.0", systemSettings.getPort()));

        log.info("Poker socket server listening on port {}", systemSettings.getPort());

    }

    public void sendMessage(ChannelHandlerContext context, TexasMessage message) {

        try {
            String msgStr = TexasMessageParser.encodeMessage(message) + new String(JsonDelimiter.delimiter());

            Channel channel = context.getChannel();
            ChannelFuture channelFuture = Channels.future(channel);
            ChannelEvent responseEvent = new DownstreamMessageEvent(channel, channelFuture, msgStr, channel.getRemoteAddress());
            context.sendDownstream(responseEvent);

        } catch (Exception e) {
            log.error("Failed to send message", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    static class TexasMessageHandler extends SimpleChannelHandler {

        private final MessageReceiver receiver;

        public TexasMessageHandler(MessageReceiver receiver) {
            super();
            this.receiver = receiver;
        }

        public void messageReceived(ChannelHandlerContext context, MessageEvent e) throws Exception {
            super.messageReceived(context, e);

            String message = (String) e.getMessage();
            receiver.onRequest(context, TexasMessageParser.decodeMessage(message));
        }
    }
}
