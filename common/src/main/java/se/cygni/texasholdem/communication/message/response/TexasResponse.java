package se.cygni.texasholdem.communication.message.response;

import se.cygni.texasholdem.communication.message.TexasMessage;

public class TexasResponse extends TexasMessage {

    private String requestId;

    public String getRequestId() {

        return requestId;
    }

    public void setRequestId(final String requestId) {

        this.requestId = requestId;
    }

}
