package me.FurH.Core.tps;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public interface ICycleTPS {

    /**
     * This method is called by the tps monitor, it is intended to freeze heavy operations in a last hope to release cpu for the main thread.
     * 
     * @throws Throwable
     */
    public void hold() throws Throwable;

}