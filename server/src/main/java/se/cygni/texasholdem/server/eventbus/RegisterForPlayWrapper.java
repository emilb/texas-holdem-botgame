package se.cygni.texasholdem.server.eventbus;

import org.jboss.netty.channel.ChannelHandlerContext;
import se.cygni.texasholdem.communication.message.request.TexasRequest;

public class RegisterForPlayWrapper {

    private final ChannelHandlerContext context;
    private final TexasRequest request;

    public RegisterForPlayWrapper(final ChannelHandlerContext context,
            final TexasRequest request) {

        this.context = context;
        this.request = request;
    }

    public ChannelHandlerContext getChannelHandlerContext() {

        return context;
    }

    public TexasRequest getRequest() {

        return request;
    }

}
