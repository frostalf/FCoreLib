package me.FurH.Core.packets;

import org.bukkit.entity.Player;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public interface IPacketQueue {

    /**
     * Receive a Custom Payload (Packet250CustomPayload)
     *
     * @param player the player
     * @param channel the packet channel
     * @param length the packet data length
     * @param data the packet data
     * @return true if the packet is ment to be procesed by the server, false otherwise 
     */
    public boolean handleCustomPayload(Player player, String channel, int length, byte[] data);
    
    /**
     * Receive the Client Settings (Packet204LocaleAndViewDistance)
     *
     * @param player the player
     * @return true if the packet is ment to be processe by the server, false otherwise
     */
    public boolean handleClientSettings(Player player);
    
}
