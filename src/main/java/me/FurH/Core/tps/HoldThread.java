package me.FurH.Core.tps;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public abstract class HoldThread extends Thread implements ICycleTPS {

    private volatile boolean _sleep = false;
    private volatile boolean _sdone = false;

    public HoldThread() {
        super(); init();
    }

    public HoldThread(Runnable target) {
        super(target); init();
    }

    public HoldThread(String name) {
        super(name); init();
    }

    public HoldThread(Runnable target, String name) {
        super(target, name); init();
    }

    private void init() {
        CyclesMonitor.register(this);
    }

    @Override
    public void run() {

        while (!_sdone) {

            while (!_sleep) {
                this._do();
            }

            try {
                sleep(5000);
            } catch (InterruptedException ex) { }

        }

        interrupt();
    }
    
    public abstract void _do();
    
    public void cancel() {
        _sdone = true;
    }

    @Override
    public void hold() throws Throwable {
        this._sleep = true;
    }
}