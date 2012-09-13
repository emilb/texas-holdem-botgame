package se.cygni.texasholdem.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * A lock-free thread safe circular fixed length buffer.
 * <p/>
 * Uses an AtomicInteger as index counter and an AtomicReferenceArray
 * to hold the references to the values.
 * <p/>
 * When the buffer is full, the oldest item is overwritten.
 */
public class CircularBuffer<T> {

    private final AtomicInteger index = new AtomicInteger(-1);
    private final AtomicReferenceArray<T> buffer;
    private final int size;
    private final Comparator<T> comparator;
    private T lastAdded = null;

    public CircularBuffer(int size, Comparator<T> comparator) {
        this.size = size;
        this.comparator = comparator;
        buffer = new AtomicReferenceArray<T>(this.size);
    }

    public void add(T item) {
        if (index.compareAndSet(size - 1, 0)) {
            buffer.set(0, item);
        }
        else {
            buffer.set(index.incrementAndGet(), item);
        }

        lastAdded = item;
    }

    /**
     * Get contents of buffer, as a list.
     * <p/>
     * Note that the list always has the size of the buffer,
     * no matter how many elements there actually are.
     * <p/>
     * The ordering is defined by the comparator.
     *
     * @return contents
     */
    public List<T> getAll() {
        List<T> list = new ArrayList<T>(size);
        for (int i = 0; i < size; i++) {
            T obj = buffer.get(i);
            if (obj != null) {
                list.add(buffer.get(i));
            }
        }

        Collections.sort(list, comparator);
        return list;
    }

    public T getLast() {
        return lastAdded;
    }

    public T get(ObjectMatcher<T> matcher) {
        for (int i = 0; i < size; i++) {
            T obj = buffer.get(i);
            if (obj != null && matcher.matches(obj)) {
                return buffer.get(i);
            }
        }

        return null;
    }
}
