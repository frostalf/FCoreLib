package me.FurH.Core.cache;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import me.FurH.Core.gc.IMemoryMonitor;
import me.FurH.Core.gc.MemoryMonitor;


/**
 *
 * @param <V> 
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class CoreSoftList<V> implements IMemoryMonitor {

    public final List<SoftReference<V>> list = 
            Collections.synchronizedList(new ArrayList<SoftReference<V>>());

    private boolean auto_clear = false;

    public CoreSoftList() {
        this.auto_clear = false; init();
    }
    
    public CoreSoftList(boolean auto_clear) {
        this.auto_clear = auto_clear; init();
    }

    private void init() {
        MemoryMonitor.register(this);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }

    public V get(int j1) {
        SoftReference<V> ref = list.get(j1);

        if (ref == null || ref.get() == null) {
            return null;
        }

        return ref.get();
    }

    public boolean add(V ref) {

        SoftReference<V> value = new SoftReference<>(ref);

        return list.add(value);
    }

    public boolean remove(V reference) {
        boolean removed = false;
        
        cleanup();
        
        synchronized (list) {

            Iterator<SoftReference<V>> it = list.iterator();
            while (it.hasNext()) {

                SoftReference<V> next = it.next();
                
                if (next != null && next.get() == reference) {
                    it.remove(); removed = true;
                }
            }
        }
        
        return removed;
    }
    
    public void cleanup() {

        synchronized (list) {

            Iterator<SoftReference<V>> it = list.iterator();
            while (it.hasNext()) {

                SoftReference<V> next = it.next();
                if (next == null || next.get() == null) {
                    it.remove(); continue;
                }
            }
        }
    }

    @Override
    public void gc() throws Throwable {
        
        if (auto_clear) {
            this.list.clear();
        } else {
            cleanup();
        }
    }
}
