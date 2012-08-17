package se.cygni.texasholdem.communication.message.request;

import se.cygni.texasholdem.communication.message.TexasMessage;

import java.util.UUID;

public class TexasRequest extends TexasMessage {

    private String sessionId;
    private String requestId = UUID.randomUUID().toString();

    public String getSessionId() {

        return sessionId;
    }

    public void setSessionId(final String sessionId) {

        this.sessionId = sessionId;
    }

    public String getRequestId() {

        return requestId;
    }

    public void setRequestId(final String requestId) {

        this.requestId = requestId;
    }

}
