package src.util.collection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @since 2.0.0
 */
public class LatchHashMap<K, V> implements Map<K, V> {

    private Map<K, ValueLatch> internalMap;

    public LatchHashMap() {
        this.internalMap = new HashMap<K, ValueLatch>();
    }


    public int size() {
        return internalMap.size();
    }

    public boolean isEmpty() {
        return internalMap.isEmpty();
    }

    public boolean containsKey(Object key) {
        return internalMap.containsKey(key);
    }

    public boolean containsValue(Object value) {
        throw new IllegalStateException("Not implemented yet!");
    }

    public V get(Object key) {
        return getLatch(key).get();
    }

    public V put(K key, V value) {
        return getLatch(key).set(value);
    }

    public V remove(Object key) {
        throw new IllegalStateException("Not implemented yet!");
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        throw new IllegalStateException("Not implemented yet!");
    }

    public void clear() {
        throw new IllegalStateException("Not implemented yet!");
    }

    public Set<K> keySet() {
        throw new IllegalStateException("Not implemented yet!");
    }

    public Collection<V> values() {
        throw new IllegalStateException("Not implemented yet!");
    }

    public Set<Entry<K,V>> entrySet() {
        throw new IllegalStateException("Not implemented yet!");
    }

    @Override
    public boolean equals(Object o) {
        return internalMap.equals(o);
    }

    @Override
    public int hashCode() {
        return internalMap.hashCode();
    }


    protected ValueLatch<V> getLatch(Object key) {
        ValueLatch<V> latch;
        synchronized (this) {
            if (containsKey(key))
                latch = internalMap.get(key);
            else {
                latch = new ValueLatch();
                internalMap.put((K) key, latch);
            }
        }
        return latch;
    }


    class ValueLatch<V> extends CountDownLatch {

        V value;

        ValueLatch() {
            super(1);
        }

        V set(V value) {
            V old = this.value;
            this.value = value;

            countDown();

            return old;
        }

        V get() {
            try {
                await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return value;
        }
    }

}
