package me.FurH.Core.threads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import me.FurH.Core.gc.IMemoryMonitor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class ThreadFactory implements IMemoryMonitor {
    
    public static final List<BukkitRunnable> runnables = Collections.synchronizedList(new ArrayList<BukkitRunnable>());
    public static final List<Thread> threads = Collections.synchronizedList(new ArrayList<Thread>());
    public static final List<Timer> timers = Collections.synchronizedList(new ArrayList<Timer>());

    public static Timer newTimer(String name) {
        return newTimer(name, true);
    }
    
    public static Timer newTimer(String name, boolean daemon) {
        Timer timer = new Timer(name, daemon);
        
        timers.add(timer);
        
        return timer;
    }
    
    public static BukkitRunnable newBukkitRunanble(final Runnable runnable) {

        BukkitRunnable bukkit = new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        };
        
        runnables.add(bukkit);
        
        return bukkit;
    }

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
        
        threads.add(thread);

        return thread;
    }

    public static void stopAll() {
        
        synchronized (runnables) {
            
            Iterator<BukkitRunnable> it = runnables.iterator();
            while (it.hasNext()) {
                try {
                    it.next().cancel(); it.remove();
                } catch (Throwable ex) { }
            }
            
            runnables.clear();
        }
        
        synchronized (threads) {
            
            Iterator<Thread> it = threads.iterator();
            while (it.hasNext()) {
                try {
                    it.next().stop(); it.remove();
                } catch (Throwable ex) { }
            }
            
            threads.clear();
        }
        
        synchronized (timers) {
            
            Iterator<Timer> it = timers.iterator();
            while (it.hasNext()) {
                try {
                    it.next().cancel(); it.remove();
                } catch (Throwable ex) { }
            }
            
            timers.clear();
        }
        
    }
    
    @Override
    public void gc() throws Throwable {
        
        synchronized (runnables) {
            
            Iterator<BukkitRunnable> it = runnables.iterator();
            BukkitScheduler scheduler = Bukkit.getScheduler();

            while (it.hasNext()) {

                BukkitRunnable runnable = it.next();

                if (!scheduler.isCurrentlyRunning(runnable.getTaskId()) 
                        && !scheduler.isQueued(runnable.getTaskId())) {
                    
                    it.remove();
                }
            }
        }
        
        synchronized (threads) {
            
            Iterator<Thread> it = threads.iterator();
            while (it.hasNext()) {
                
                Thread thread = it.next();
                if (thread.isInterrupted() || !thread.isAlive()) {
                    it.remove();
                }
            }
        }
        
        synchronized (timers) {
            
            Iterator<Timer> it = timers.iterator();
            while (it.hasNext()) {
                Timer timer = it.next();

                try {

                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() { }
                    }, 10L);

                } catch (Throwable ex) {
                    it.remove();
                }
            }
        }
    }
}
