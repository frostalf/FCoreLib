package me.FurH.Core.gc;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import me.FurH.Core.util.Utils;
import org.bukkit.Bukkit;

/**
 *
 * @author FurmigaHumana All Rights Reserved unless otherwise explicitly stated.
 */
public class MemoryMonitor {

    private static List<IMemoryMonitor> references = new ArrayList<IMemoryMonitor>();
    private static SoftReference<byte[]> monitor;
    private static int calls = 0;

    static {
        monitor = new SoftReference<byte[]>(new byte[ 1048576 * 10 ]);
    }
    
    public MemoryMonitor() {

        new Timer("FCoreLib Memory Monitor", true)
                .scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                
                if (monitor == null || monitor.get() == null) {

                    long free = getTotalFree();
                    
                    if (!references.isEmpty()) {
                        System.out.println("Cleaning up " + references.size() + " references");
                    }

                    for (int j1 = 0; j1 < references.size(); j1++) {
                        IMemoryMonitor reference = references.get(j1);
                        try {
                            reference.gc();
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

                    monitor = new SoftReference<byte[]>(new byte[ 1048576 * 10 ]); calls++;
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
}