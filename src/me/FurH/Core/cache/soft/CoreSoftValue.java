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

    public CoreSoftValue(V value, K key, ReferenceQueue<? super V> queue) {
        super(value, queue);
        this.key = key;
    }

    public K getKey() {
        return key;
    }

    @Override
    public V get() {
        return super.get();
    }
}
