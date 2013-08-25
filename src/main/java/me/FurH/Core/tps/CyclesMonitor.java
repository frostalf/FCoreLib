package me.FurH.Core.tps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;
import me.FurH.Core.CorePlugin;
import me.FurH.Core.gc.MemoryMonitor;
import me.FurH.Core.number.NumberUtils;
import me.FurH.Core.player.PlayerUtils;
import me.FurH.Core.threads.ThreadFactory;

/**
 * 
 * This class is an experimental TPS monitor based on the {@link MemoryMonitor} class
 * 
 * Usually if an heavy async task can be done async, it means it probably can be "paused" if needed without any harm.
 * This class job is to monitor the server TPS and try to freeze every registered reference if the server TPS is low.
 * It may not help at all, but if the problem was that the CPU was on limit, it may help to get the TPS back up.
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class CyclesMonitor {

    private LinkedList<Double> history = new LinkedList<Double>();

    private static final List<ICycleTPS> references =
            Collections.synchronizedList(new ArrayList<ICycleTPS>());

    private long last_hold = System.currentTimeMillis();
    private long interval = 100;
    private long last = -1;

    public CyclesMonitor(CorePlugin plugin) {

        ThreadFactory.newBukkitRunanble(new Runnable() {
            @Override
            public void run() {
                last = System.nanoTime();
            }
        }).runTaskTimer(plugin, 300L, interval);
        
        ThreadFactory.newTimer("FCoreLib TPS Monitor", true)
                .scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                
                if (references.isEmpty()) {
                    return;
                }
                
                cleanup();
                
                long now = System.currentTimeMillis();
                double tps = getCurrentTPS();

                if (tps < 10.0D) {
                    tps = getAverageTPS();
                }

                if (((now - last_hold) > 15000) && tps < 15.0D) {
                    
                    System.out.println("Freezing " + references.size() + " references");
                    
                    for (int j1 = 0; j1 < references.size(); j1++) {
                        ICycleTPS reference = references.get(j1);
                        try {
                            reference.hold();
                        } catch (Throwable ex) {
                            ex.printStackTrace();
                        }
                    }
                    
                    last_hold = System.currentTimeMillis();
                }
            }
            
        }, 16000, 5000);
    }

    private double getCurrentTPS() {

        long spent = Math.max((System.nanoTime() - last) / 1000, 1);
        double tps = Math.min(interval * 1000000.0 / spent, 20);

        if (history.size() > 10) {
            history.remove();
        }

        history.add(tps);

        return Math.floor(tps);
    }

    public static boolean register(ICycleTPS reference) {
        return references.add(reference);
    }
    
    public static boolean unregister(ICycleTPS reference) {
        return references.remove(reference);
    }
    
    public static void removeAll() {
        
        synchronized (references) {
            Iterator<ICycleTPS> it = references.iterator();
            while (it.hasNext()) {
                try {
                    it.next().cancel();
                } catch (Throwable ex) { }
            }
        }
        
        references.clear();
    }

    private void cleanup() {
        
        synchronized (references) {
            Iterator<ICycleTPS> it = references.iterator();
            while (it.hasNext()) {
                try {
                    if (!it.next().alive()) {
                        it.remove();
                    }
                } catch (Throwable ex) { }
            }
        }
        
        history.clear();
        
        ThreadFactory.gc();
    }

    public double getAverageTPS() {
        return Math.floor(NumberUtils.getInBounds(PlayerUtils.getAverage(history.toArray(new Double[ history.size()  ])), 20, 1));
    }
}