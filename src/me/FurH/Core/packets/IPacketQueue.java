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

    /**
     * Receive a chunk packet (Packet56MapChunkBulk)
     *
     * @param player the player
     * @param object the packet object
     * @return the modified (or not) chunk packet
     */
    public Object handlerMapChunkBulk(Player player, Object object);
    
    /**
     * Receive a chunk packet (Packet51MapChunk)
     *
     * @param player the player
     * @param object the packet object
     * @return the modified (or not) chunk packet
     */
    public Object handlerMapChunk(Player player, Object object);
}
