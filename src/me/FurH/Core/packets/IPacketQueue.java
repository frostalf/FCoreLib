package me.FurH.Core.packets;

import me.FurH.Core.CorePlugin;
import org.bukkit.entity.Player;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public abstract class IPacketQueue {
    
    private String owner;

    public IPacketQueue(CorePlugin plugin) {
        this.owner = plugin.getDescription().getName();
    }
    
    /**
     * Receive a Custom Payload (Packet250CustomPayload)
     *
     * @param player the player
     * @param channel the packet channel
     * @param length the packet data length
     * @param data the packet data
     * @return true if the packet is ment to be procesed by the server, false otherwise 
     */
    public boolean handleAsyncCustomPayload(Player player, String channel, int length, byte[] data) {
        return true;
    }
    
    /**
     * Receive and set a custom Payload (Packet250CustomPayload)
     *
     * @param player the player
     * @param object the packet object
     * @return the modified packet object
     */
    public Object handleAndSetAsyncCustomPayload(Player player, Object object) {
        return object;
    }
    
    /**
     * Receive the Client Settings (Packet204LocaleAndViewDistance)
     *
     * @param player the player
     * @return true if the packet is ment to be processe by the server, false otherwise
     */
    public boolean handleAsyncClientSettings(Player player) {
        return true;
    }
    
    /**
     * Receive the Client Settings (Packet204LocaleAndViewDistance)
     *
     * @param player the player
     * @param packet the settings packet
     * @return true if the packet is ment to be processe by the server, false otherwise
     */
    public boolean handleAsyncClientSettings(Player player, Object packet) {
        return true;
    }

    /**
     * Receive a chunk packet (Packet56MapChunkBulk)
     *
     * @param player the player
     * @param object the packet object
     * @return the modified (or not) chunk packet
     */
    public Object handleAsyncMapChunkBulk(Player player, Object object) {
        return object;
    }

    /**
     * Receive a chunk packet (Packet51MapChunk)
     *
     * @param player the player
     * @param object the packet object
     * @return the modified (or not) chunk packet
     */
    public Object handleAsyncMapChunk(Player player, Object object) {
        return object;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 89 * hash + (this.owner != null ? this.owner.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final IPacketQueue other = (IPacketQueue) obj;
        if ((this.owner == null) ? (other.owner != null) : !this.owner.equals(other.owner)) {
            return false;
        }

        return true;
    }
}
