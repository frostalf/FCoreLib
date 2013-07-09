package me.FurH.Core.cache;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @param <K> 
 * @param <V> 
 * @author FurmigaHumana
 */
public class CoreLRUCache<K, V> extends LinkedHashMap<K, V> {
    
    private static final long serialVersionUID = -80132122077195160L;

    private int capacity = 0;
    private int reads = 0;
    private int writes = 0;

    /**
     * Creates a new LRU cache with a limited size, this cache is not thread-safe and should not be used on multi-thread systems
     * 
     * @param cacheSize the cache size limit
     */
    public CoreLRUCache (int cacheSize) {
        super(cacheSize, 0.75f, true);
        this.capacity = cacheSize;
    }

    @Override
    public V get(Object key) {
        reads++;
        return super.get(key);
    }

    @Override
    public V put(K key, V value) {
        writes++;
        return super.put(key, value);
    }
    
    /**
     * Get the key based on its value
     *
     * @param value the value to get the key
     * @return the Key of the value, or null if none
     */
    public K getKey(V value) {
        K ret = null;

        List<K> keys = new ArrayList<K>(keySet());
        for (K key : keys) {
            if (get(key).equals(value)) {
                ret = key;
                break;
            }
        }
        
        keys.clear();
        return ret;
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

    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
        return (size() > (capacity));
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
