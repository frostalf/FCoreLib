package me.FurH.Core.tps;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public interface ICycleTPS {

    /**
     * This method is called by the tps monitor, it is intended to freeze heavy operations in a last hope to release CPU for the main thread.
     * 
     * @throws Throwable
     */
    public void hold() throws Throwable;
    
    /**
     * Cancel this thread execution and remove all its references, should be called every time you done using a thread to avoid memory leaks
     * 
     * @throws Throwable 
     */
    public void cancel() throws Throwable;
    
    /**
     * Checks whatever this thread is alive and in use or not, if it is not in use anymore, it is cleaned up and all its references are removed
     * 
     * @return true if the thread is still in use, false otherwise
     * @throws Throwable 
     */
    public boolean alive() throws Throwable;

}