package me.FurH.Core.threads;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class ThreadFactory {

    public static Thread newThread(Runnable runnable) {
        return newThread(null, Thread.NORM_PRIORITY, true, runnable);
    }
    
    public static Thread newThread(String name, Runnable runnable) {
        return newThread(name, Thread.NORM_PRIORITY, true, runnable);
    }
    
    public static Thread newThread(String name, int priority, Runnable runnable) {
        return newThread(name, priority, true, runnable);
    }
    
    public static Thread newThread(String name, boolean daemon, Runnable runnable) {
        return newThread(name, Thread.NORM_PRIORITY, daemon, runnable);
    }
    
    public static Thread newThread(String name, int priority, boolean daemon, Runnable runnable) {
        Thread thread = new Thread(runnable);

        if (name != null) {
            thread.setName(name);
        }
        
        thread.setPriority(priority);
        thread.setDaemon(true);

        return thread;
    }
}
