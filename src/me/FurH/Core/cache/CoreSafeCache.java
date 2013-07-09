package me.FurH.Core.cache;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @param <K> 
 * @param <V> 
 * @author FurmigaHumana
 */
public class CoreSafeCache<K, V> extends ConcurrentHashMap<K, V> {
    
    private static final long serialVersionUID = 426161011525380934L;

    private int capacity = -1;
    private int size = 0;
    
    private int reads = 0;
    private int writes = 0;

    /**
     * Creates a new Safe Cache with a maximum capacity
     *
     * @param cacheSize the maximum capacity of this cache
     */
    public CoreSafeCache (int cacheSize) {
        super(cacheSize, 0.75f);
        this.capacity = cacheSize;
    }
    
    /**
     * Creates a new Safe Cache with no size limit
     */
    public CoreSafeCache () {
        super();
    }

    @Override
    public V get(Object key) {
        reads++;
        return super.get(key);
    }

    @Override
    public V put(K key, V value) {
        writes++;

        if (containsKey(key)) {
            return super.replace(key, value);
        }
        
        size++;
        
        if (capacity != -1) {
            if (size > capacity) {
                super.clear();
            }
        }
        
        return super.put(key, value);
    }
    
    @Override
    public V remove(Object key) {
        
        size--;
        
        return super.remove(key);
    }
    
    /**
     * Get the key based on its value
     *
     * @param value the value to get the key
     * @return the Key of the value, or null if none
     */
    public K getKey(V value) {
        
        for (K key : keySet()) {
            if (get(key).equals(value)) {
                return key;
            }
        }
        
        return null;
    }
    
    public K removeValue(V value) {
        K key = getKey(value);
        
        if (key != null) {
            remove(key);
        }
        
        return key;
    }
    
    @Override
    public boolean containsValue(Object value) {
        reads++;
        return super.containsValue(value);
    }
    
    @Override
    public boolean containsKey(Object key) {
        reads++;
        return super.containsKey(key);
    }

    @Override
    public void clear() {
        super.clear();
    }

    /**
     * Get the total reads of this cache
     *
     * @return the total cache reads
     */
    public int getReads() {
        return reads;
    }

    /**
     * Get the total writes of this cache
     *
     * @return the total cache writes
     */
    public int getWrites() {
        return writes;
    }
    
    /**
     * Get the maximum size of this cache
     *
     * @return the cache capacity
     */
    public int getMaxSize() {
        return capacity;
    }
}