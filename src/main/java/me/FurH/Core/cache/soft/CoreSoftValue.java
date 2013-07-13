package me.FurH.Core.cache.soft;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

/**
 *
 * @param <V> 
 * @param <K> 
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class CoreSoftValue<V, K> extends SoftReference<V> {
    
    private final K key;

    /**
     * Creates a new SoftReference and stores the cache key with it, used to fast remove the value from the cache
     *
     * @param value the reference value
     * @param key the cache key
     * @param queue the ReferenceQueue used by this reference
     */
    public CoreSoftValue(V value, K key, ReferenceQueue<? super V> queue) {
        super(value, queue);
        this.key = key;
    }

    /**
     * The cache Key used by this value
     *
     * @return the cache key
     */
    public K getKey() {
        return key;
    }

    @Override
    public V get() {
        return super.get();
    }
}
