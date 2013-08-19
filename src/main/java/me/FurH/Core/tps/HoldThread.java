package me.FurH.Core.tps;

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
    
    @Override
    public void cancel() {
        _sdone = true;
    }
    
    @Override
    public boolean alive() throws Throwable {

        if (_sdone) {
            return false;
        }
        
        if (isInterrupted()) {
            return false;
        }
        
        return isAlive();
    }

    @Override
    public void hold() throws Throwable {
        this._sleep = true;
    }
    
    @Override
    public void interrupt() {
        CyclesMonitor.unregister(this);
        super.interrupt();
    }
}