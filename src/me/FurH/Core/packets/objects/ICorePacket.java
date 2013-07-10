package me.FurH.Core.packets.objects;

import me.FurH.Core.exceptions.CoreException;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public interface ICorePacket {

    /**
     * Return the packet id
     *
     * @return
     * @throws CoreException  
     */
    public int getPacketId() throws CoreException;
    
    /**
     * Return the packet class name
     *
     * @return
     */
    public String getPacketName();
    
    /**
     * Return the modified (or not) packet
     *
     * @return
     */
    public Object getHandle();
    
}
