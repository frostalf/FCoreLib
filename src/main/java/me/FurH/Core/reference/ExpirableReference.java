package me.FurH.Core.reference;

import java.lang.ref.SoftReference;
import java.util.concurrent.TimeUnit;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class ExpirableReference<V> extends SoftReference<V> {
    
    private final long expiration;
    private long last_use;

    public ExpirableReference(V value, long expires, TimeUnit time) {
        
        super(value);
        
        this.last_use = System.currentTimeMillis();
        this.expiration = TimeUnit.MILLISECONDS.convert(expires, time);
        
        ExpirableMonitor.register(this);
        
    }
    
    public boolean isExpired() {
        return last_use > (System.currentTimeMillis() + expiration);
    }
    
    @Override
    public V get() {

        this.last_use = System.currentTimeMillis();

        return super.get();
    }
    
    public boolean isStillValid() {
        return !isExpired() && !isEnqueued() && super.get() != null;
    }
    
    @Override
    public void clear() {
        super.clear();
        this.enqueue();
    }
}
