package me.FurH.Core.cache;

import java.util.Collection;
import java.util.HashSet;
import me.FurH.Core.gc.IMemoryMonitor;
import me.FurH.Core.gc.MemoryMonitor;

/**
 *
 * @param <E> 
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class CoreHashSet<E> extends HashSet<E> implements IMemoryMonitor {

    private static final long serialVersionUID = -3587365391140026898L;
    private boolean softCache = false;

    public CoreHashSet(Collection<? extends E> c) {
        super(c);
    }

    public CoreHashSet() {
        super();
    }

    /**
     * Set this cache to soft cache, it means that it can be automatically cleaned out to release memory.
     *
     * @param softCache true if this is a soft cache, false otherwise.
     */
    public void setSoftCache(boolean softCache) {
        this.softCache = softCache;

        if (this.softCache) {
            MemoryMonitor.register(this);
        }
    }

    @Override
    public void gc() throws Throwable {
        super.clear();
    }
}