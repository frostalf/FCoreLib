package me.FurH.Core.cache;

import java.util.Collection;
import java.util.HashSet;
import me.FurH.Core.gc.IMemoryMonitor;

/**
 *
 * @param <E> 
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class CoreHashSet<E> extends HashSet<E> implements IMemoryMonitor {

    private static final long serialVersionUID = -3587365391140026898L;

    public CoreHashSet(Collection<? extends E> c) {
        super(c);
    }

    public CoreHashSet() {
        super();
    }

    @Override
    public void gc() throws Throwable {
        super.clear();
    }
}