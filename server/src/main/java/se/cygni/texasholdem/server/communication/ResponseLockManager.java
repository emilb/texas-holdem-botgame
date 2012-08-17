package se.cygni.texasholdem.server.communication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.cygni.texasholdem.communication.lock.ResponseLock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ResponseLockManager {

    private static final Logger log = LoggerFactory
            .getLogger(ResponseLockManager.class);

    private final Map<String, ResponseLock> responseLocks = new ConcurrentHashMap<String, ResponseLock>();

    public ResponseLock push(final String requestId) {

        final ResponseLock lock = new ResponseLock(requestId);

        if (responseLocks.containsKey(requestId))
            throw new IllegalArgumentException("Request ID is already in use");

        responseLocks.put(requestId, lock);

        return lock;
    }

    public ResponseLock pop(final String requestId) {

        if (!responseLocks.containsKey(requestId))
            throw new IllegalArgumentException("Unknown Request ID");

        return responseLocks.remove(requestId);
    }

    public boolean containsRequestId(final String requestId) {

        return responseLocks.containsKey(requestId);
    }
}
