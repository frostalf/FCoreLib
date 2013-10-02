package me.FurH.Core.database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import me.FurH.Core.exceptions.CoreException;
import me.FurH.Core.tps.HoldThread;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class CoreSQLWorker extends HoldThread {
    
    private Queue<Runnable> queue = new ConcurrentLinkedQueue<>();

    private AtomicBoolean kill = new AtomicBoolean(false);
    private AtomicBoolean lock = new AtomicBoolean(false);

    private boolean commited        = false;
    private int     queue_runs      = 0;
    private int     queue_lock      = 0;

    private final CoreSQLDatabase db;

    public CoreSQLWorker(CoreSQLDatabase db, String name) {
        super(name);
        this.db = db;
        this.setPriority(1);
        this.setDaemon(true);
        this.start();
    }

    public void enqueue(Runnable command) {
        queue.add(command);
    }

    @Override
    public void _do() {

        if (queue.isEmpty()) {
            sleep(); return;
        }

        Runnable task = queue.poll();

        if (task == null) {
            sleep(); return;
        }

        queue_runs++;
        task.run();
        commited = false;

        if (queue_runs >= getQueueSpeed()) {

            queue_runs = 0;

            try {
                db.commit();
            } catch (CoreException ex) {
                ex.printStackTrace();
            }

            sleep();
        }
    }

    public void shutdown() {
        kill.set(true);
    }

    public boolean isShutdown() {
        return kill.get();
    }

    public List<Runnable> shutdownNow() {
        kill.set(true);

        List<Runnable> ret = new ArrayList<>();

        Iterator<Runnable> it = queue.iterator();
        while (it.hasNext()) {
            ret.add(it.next()); it.remove();
        }

        return ret;
    }
    
    public void cleanup() {

        this.queue_lock = queue.size();
        this.lock.set(true);

    }
    
    public void unlock() {
        this.lock.set(false);
    }
    
    private void sleep() {

        if (!commited) {
            try {
                db.commit(); commited = true;
            } catch (CoreException ex) {
                ex.printStackTrace();
            }
        }

        if (lock.get()) {

            this.queue_lock--;

            if (queue.isEmpty() || this.queue_lock <= 0) {
                lock.set(false);
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) { }

            return;
        }
        
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) { }
    }

    public int getQueueSpeed() {

        int count = (int) (((double) queue.size()) * db.queue_speed);
        
        if (count < 100) {
            count = 100;
        }
        
        if (count > 10000) {
            count = 10000;
        }
        
        return count;
    }

    public int size() {
        return queue.size();
    }
}