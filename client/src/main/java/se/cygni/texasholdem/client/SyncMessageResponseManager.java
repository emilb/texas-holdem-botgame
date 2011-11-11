package se.cygni.texasholdem.client;

import java.util.HashMap;
import java.util.Map;

import se.cygni.texasholdem.communication.lock.ResponseLock;

public class SyncMessageResponseManager {

    private final Object mapLock = new Object();
    private final Map<String, ResponseLock> responseLocks = new HashMap<String, ResponseLock>();

    public ResponseLock push(final String requestId) {

        final ResponseLock lock = new ResponseLock(requestId);
        System.out.println("pushing requestId: " + requestId);

        if (responseLocks.containsKey(requestId))
            throw new IllegalArgumentException("Request ID is already in use");

        synchronized (mapLock) {
            responseLocks.put(requestId, lock);
        }

        return lock;
    }

    public ResponseLock pop(final String requestId) {

        System.out.println("pop requestId: " + requestId);

        if (!responseLocks.containsKey(requestId))
            throw new IllegalArgumentException("Unknown Request ID");

        ResponseLock lock = null;

        synchronized (mapLock) {
            lock = responseLocks.remove(requestId);
        }

        return lock;
    }

}
