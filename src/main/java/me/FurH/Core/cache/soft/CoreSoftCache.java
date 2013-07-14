package me.FurH.Core.cache.soft;

import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @param <K> 
 * @param <V> 
 * @author FurmigaHumana
 */
public class CoreSoftCache<K, V> {

    private static final long serialVersionUID = -80132122077195160L;

    private final ReferenceQueue<V> queue = new ReferenceQueue<V>();
    private final LinkedHashMap<K, CoreSoftValue<V, K>> map;

    private int capacity = 0;
    private int reads = 0;
    private int writes = 0;

    /**
     * Creates a new memory sensitive LRU cache with a limited size, this cache is not thread-safe and should not be used on multi-thread systems
     * 
     * @param cacheSize the cache size limit
     */
    public CoreSoftCache(int cacheSize) {

        map = new LinkedHashMap<K, CoreSoftValue<V, K>>(cacheSize, 0.75f, true) {

            private static final long serialVersionUID = 2674509550119308224L;

            @Override
            protected boolean removeEldestEntry(java.util.Map.Entry<K, CoreSoftValue<V, K>> eldest) {
                return capacity > 0 && (size() > (capacity));
            }
        };
        
        this.capacity = cacheSize;
    }

    /**
     * Creates a new memory sensitive LRU cache with no size limit, this cache is not thread-safe and should not be used on multi-thread systems
     */
    public CoreSoftCache() {
        this(0);
    }
    
    /**
     * Return the value represented by this key
     *
     * @param key the key
     * @return the value
     */
    public V get(K key) {
        reads++;

        CoreSoftValue<V, K> soft = map.get(key);
        if (soft != null) {

            V result = soft.get();
            if (result == null) {
                map.remove(key); cleanup();
            }

            return result;
        }

        map.remove(key);
        return null;
    }

    /**
     * Put a new value into this map
     *
     * @param key the key
     * @param value the value
     * @return the value added
     */
    public V put(K key, V value) {
        writes++;

        CoreSoftValue<V, K> soft = new CoreSoftValue<V, K>(value, key, queue);
        map.put(key, soft);

        return soft.get();
    }
    
    /**
     * Get the key based on its value
     *
     * @param value the value to get the key
     * @return the Key of the value, or null if none
     */
    public K getKey(V value) {
        K ret = null;

        List<K> keys = new ArrayList<K>(map.keySet());
        
        for (K key : keys) {
            
            V get = get(key);
            
            if (get == null) {
                continue;
            }
            
            if (get.equals(value)) {
                ret = key;
                break;
            }
        }

        keys.clear();
        return ret;
    }

    /**
     * Remove a value from the map, the key will also be removed
     *
     * @param value the value
     * @return the key removed
     */
    public K removeValue(V value) {
        K key = getKey(value);
        
        if (key != null) {
            remove(key);
        }
        
        return key;
    }
    
    /**
     * Remove an value by its key
     *
     * @param key the value key
     * @return the value removed
     */
    public V remove(K key) {
        writes++; reads++;

        CoreSoftValue<V, K> ret = map.remove(key);
        if (ret == null) {
            return null;
        }

        return ret.get();
    }

    /**
     * Check if the map contains a key
     *
     * @param key the key
     * @return true if the map contains the key, false otherwise.
     */
    public boolean containsKey(K key) {
        reads++;
        return map.containsKey(key);
    }
    
    /**
     * Clear this map
     */
    public void clear() {
        map.clear();
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

    /**
     * Get this map size, this will also cast a cleanup to remove null values removed by gc
     *
     * @return the size of the map
     */
    public int size() {
        cleanup();
        return map.size();
    }
    
    /**
     * Clean up all null values of this map, removed by the garbage collector
     */
    public void cleanup() {
        CoreSoftValue<V, K> sv;
        while ((sv = (CoreSoftValue<V, K>) queue.poll()) != null) {
            remove(sv.getKey());
        }
    }

    /**
     * Setup a new cleanup task every minute
     */
    public void cleanupTask() {
        cleanupTask(60000);
    }

    /**
     * Setup a new cleanup task every X time
     *
     * @param delay the delay time in milliseconds
     */
    public void cleanupTask(long delay) {
        new Timer(true).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                cleanup();
            }
        }, delay, delay);
    }
    
    /**
     * Get this cache map
     *
     * @return the LinkedHashMap of this cache, changes on this map will affect the cache
     */
    public LinkedHashMap<K, CoreSoftValue<V, K>> getHandle() {
        return map;
    }
}