package me.FurH.Core.internals;

import me.FurH.Core.exceptions.CoreException;
import me.FurH.Core.packets.PacketCustomPayload;
import org.bukkit.entity.Player;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public interface IEntityPlayer {

    /**
     * Set the Player of this IEntityPlayer object
     * 
     * @param player the player to be set
     * @return the IEntityPlayer for the given Player
     */
    public IEntityPlayer setEntityPlayer(Player player);
    
    /**
     * Set the Player Inboud Queue
     * 
     * @throws CoreException 
     */
    public void setInboundQueue() throws CoreException;

    /**
     * Set the Player Outbound queue
     * 
     * @throws CoreException 
     */
    public void setOutboundQueue() throws CoreException;

    /**
     * Get the Player network ping
     * 
     * @return the player ping in milliseconds
     */
    public int ping();
    
    /**
     * Send the EntityPlayer a custom payload
     *
     * @param packet the custom payload
     */
    public void sendCustomPayload(PacketCustomPayload packet);
    
    /**
     * Hides the player inventory
     */
    public void hideInventory();
    
    /**
     * Revels the player inventory
     */
    public void unHideInventory();
    
    /**
     * Get if the player inventory is hidden
     *
     * @return true if the inventory is hidden, false otherwise.
     */
    public boolean isInventoryHidden();
}
