package me.FurH.Core.reference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;
import me.FurH.Core.threads.ThreadFactory;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class ExpirableMonitor {

    private static final List<ExpirableReference<?>> references = Collections.synchronizedList(new ArrayList<ExpirableReference<?>>());

    public ExpirableMonitor() {
        
        ThreadFactory.newTimer("FCoreLib Expiration Monitor", true)
                .scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                
                synchronized (references) {
                    Iterator<ExpirableReference<?>> it = references.iterator();
                    while (it.hasNext()) {
                        ExpirableReference<?> reference = it.next();

                        if (reference == null) {
                            it.remove(); continue;
                        }

                        if (!reference.isStillValid()) {
                            reference.clear(); it.remove();
                        }
                    }
                }
            }
            
        }, 50L, 50L);
    }
    
    public static boolean register(ExpirableReference<?> reference) {
        return references.add(reference);
    }
    
    public static boolean unregister(ExpirableReference<?> reference) {
        return references.remove(reference);
    }
    
    public static void clear() {

        synchronized (references) {
            Iterator<ExpirableReference<?>> it = references.iterator();
            while (it.hasNext()) {

                try {
                    it.next().clear();
                } catch (Throwable ex) { }

                it.remove();
            }
        }
        
        references.clear();
    }
}