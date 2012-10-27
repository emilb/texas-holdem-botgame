package se.cygni.texasholdem.server.statistics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicCounter {

    private static final ConcurrentMap<String, AtomicLong> counterMap = new ConcurrentHashMap<String, AtomicLong>();

    private AtomicCounter() {
    }

    public static long value(String key) {
        if (!counterMap.containsKey(key)) {
            counterMap.put(key, new AtomicLong(0));
        }

        return counterMap.get(key).longValue();
    }

    public static long increment(String key) {
        if (!counterMap.containsKey(key)) {
            counterMap.put(key, new AtomicLong(0));
        }

        return counterMap.get(key).incrementAndGet();
    }
}
