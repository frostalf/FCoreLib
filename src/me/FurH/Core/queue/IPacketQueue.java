package me.FurH.Core.queue;

import org.bukkit.entity.Player;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public interface IPacketQueue {

    /**
     * Receive the data sent by the queue
     *
     * @param player the player
     * @param id the packet id
     * @param channel the packet250 channel 
     */
    public void receive(Player player, int id, String channel);
    
}
