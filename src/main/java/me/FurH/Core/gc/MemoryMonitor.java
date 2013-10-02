package me.FurH.Core.gc;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;
import me.FurH.Core.threads.ThreadFactory;
import me.FurH.Core.util.Utils;
import org.bukkit.Bukkit;

/**
 *
 * This class is an experimental alternative to SoftReference, it does not work like it at all but is used for the same purpose.
 * Unfortunately this class may leak memory and totally fail its purpose, I'm still investigating this possibility.
 * Registered references will not be auto-removed if the object is gone and it will hold the object in memory forever,
 * The only objects that should be registered are objects created within the class constructor, objects created inside methods and/or during runtime should never use this class.
 * 
 * This class also hold 10MB of memory so we can properly monitor the garbage collector and we can release a few bits of memory to process all references.
 * 
 * @author FurmigaHumana All Rights Reserved unless otherwise explicitly stated.
 */
public class MemoryMonitor {

    private final static List<IMemoryMonitor> references = 
            Collections.synchronizedList(new ArrayList<IMemoryMonitor>());

    private static SoftReference<byte[]> monitor;
    private static int calls = 0;

    public MemoryMonitor() {
        
        monitor = new SoftReference<>(new byte[ 1048576 * 10 ]);

        ThreadFactory.newTimer("FCoreLib Memory Monitor", true)
                .scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                
                if (monitor == null || monitor.get() == null) {

                    long free = getTotalFree();
                    
                    Runtime.getRuntime().runFinalization();
                    System.gc();

                    try {
                        HeapDumper.dump();
                    } catch (OutOfMemoryError ex) {
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    }

                    if (!references.isEmpty()) {
                        System.out.println("Cleaning up " + references.size() + " references");
                    }

                    for (int j1 = 0; j1 < references.size(); j1++) {
                        IMemoryMonitor reference = references.get(j1);
                        try {
                            reference.gc();
                        } catch (OutOfMemoryError ex) {
                        } catch (Throwable ex) {
                            ex.printStackTrace();
                        }
                    }

                    Runtime.getRuntime().runFinalization();
                    System.gc();

                    Bukkit.getPluginManager().callEvent(new GarbageEvent());

                    Runtime.getRuntime().runFinalization();
                    System.gc();

                    long freed = Math.abs(free - getTotalFree());

                    if (!references.isEmpty()) {
                        System.out.println("Memory Released " + Utils.getFormatedBytes(freed));
                    }

                    monitor = new SoftReference<>(new byte[ 1048576 * 10 ]); calls++;
                }
            }
        }, 50L, 50L);
    }

    public long getTotalFree() {
        Runtime rt = Runtime.getRuntime();

        long free = rt.freeMemory();
        long total = rt.totalMemory();
        long max = rt.maxMemory();
        long used = (total - free);

        return (max - used);
    }

    public static boolean register(IMemoryMonitor cleanable) {
        return references.add(cleanable);
    }

    public int getGCCalls() {
        return calls;
    }
    
    public static boolean unregister(IMemoryMonitor cleanable) {
        return references.remove(cleanable);
    }
    
    public static void clear() {

        synchronized (references) {
            Iterator<IMemoryMonitor> it = references.iterator();
            while (it.hasNext()) {

                try {
                    it.next().gc();
                } catch (Throwable ex) { }

                it.remove();
            }
        }
        
        references.clear();
    }
}