package me.FurH.Core.cache.soft;

import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @param <K> 
 * @param <V> 
 * @author FurmigaHumana
 */
public class CoreSoftSafeCache<K, V> {
    
    private static final long serialVersionUID = 426161011525380934L;

    private final ReferenceQueue<V> queue = new ReferenceQueue<V>();
    private ConcurrentHashMap<K, CoreSoftValue<V, K>> map;

    private int capacity = -1;
    private int size = 0;
    
    private int reads = 0;
    private int writes = 0;

    /**
     * Creates a new Safe Cache with a maximum capacity
     *
     * @param cacheSize the maximum capacity of this cache
     */
    public CoreSoftSafeCache (int cacheSize) {
        map = new ConcurrentHashMap<K, CoreSoftValue<V, K>>(cacheSize, 0.75f);
        this.capacity = cacheSize;
    }
    
    /**
     * Creates a new Safe Cache with no size limit
     */
    public CoreSoftSafeCache () {
        map = new ConcurrentHashMap<K, CoreSoftValue<V, K>>();
    }

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

    public V put(K key, V value) {
        writes++;
        
        CoreSoftValue<V, K> soft = null;
        
        if (containsKey(key)) {
            
            soft = new CoreSoftValue<V, K>(value, key, queue);
            map.replace(key, soft);

            return soft.get();

        }

        size++;

        if (capacity > 0 && size > capacity) {
            try {
                map.remove(map.keySet().iterator().next());
            } catch (Throwable ex) {
                map.clear(); System.out.println("ERR: " + ex.getMessage()); // TODO: Remove debug
            }
        }

        soft = new CoreSoftValue<V, K>(value, key, queue);
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
    
    public K removeValue(V value) {
        K key = getKey(value);
        
        if (key != null) {
            remove(key);
        }
        
        return key;
    }

    public V remove(K key) {
        writes++; reads++;

        CoreSoftValue<V, K> ret = map.remove(key);
        if (ret == null) {
            return null;
        }

        return ret.get();
    }
    
    public boolean containsKey(K key) {
        reads++;
        return map.containsKey(key);
    }

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

    public int size() {
        cleanup();
        return map.size();
    }
    
    public void cleanup() {
        CoreSoftValue<V, K> sv;
        while ((sv = (CoreSoftValue<V, K>) queue.poll()) != null) {
            remove(sv.getKey());
        }
    }

    public void cleanupTask() {
        cleanupTask(60000);
    }

    public void cleanupTask(long delay) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                cleanup();
            }
        }, delay);
    }
    
    public ConcurrentHashMap<K, CoreSoftValue<V, K>> getHandle() {
        return map;
    }
}