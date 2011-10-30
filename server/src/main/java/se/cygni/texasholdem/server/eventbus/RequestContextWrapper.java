package se.cygni.texasholdem.server.eventbus;

import org.codemonkey.swiftsocketserver.ClientContext;

import se.cygni.texasholdem.communication.message.request.TexasRequest;

public class RequestContextWrapper {

    private final ClientContext clientContext;
    private final TexasRequest request;

    public RequestContextWrapper(final ClientContext clientContext,
            final TexasRequest request) {

        this.clientContext = clientContext;
        this.request = request;
    }

    public ClientContext getClientContext() {

        return clientContext;
    }

    public TexasRequest getRequest() {

        return request;
    }

}
