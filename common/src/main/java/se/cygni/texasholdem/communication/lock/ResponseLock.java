package se.cygni.texasholdem.communication.lock;

import org.apache.commons.lang.StringUtils;
import se.cygni.texasholdem.communication.message.response.TexasResponse;

public class ResponseLock {

    private final String requestId;
    private TexasResponse response;

    public ResponseLock(final String requestId) {

        this.requestId = requestId;
    }

    public TexasResponse getResponse() {

        return response;
    }

    public void setResponse(final TexasResponse response) {

        if (response == null) {
            throw new IllegalArgumentException("Response is null");
        }

        if (!StringUtils.equals(requestId, response.getRequestId())) {
            throw new IllegalArgumentException("Response has wrong request ID");
        }

        this.response = response;
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((requestId == null) ? 0 : requestId.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final ResponseLock other = (ResponseLock) obj;
        if (requestId == null) {
            if (other.requestId != null) {
                return false;
            }
        }
        else if (!requestId.equals(other.requestId)) {
            return false;
        }

        return true;
    }
}
