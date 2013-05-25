package me.FurH.Core.packets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bukkit.entity.Player;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class PacketManager {
    
    private static final List<IPacketQueue> p250 = Collections.synchronizedList(new ArrayList<IPacketQueue>());

    /**
     * Register a new handler for the given packet id
     * 
     * Current supported packet id are:
     * - 250
     *
     * @param handler the IPacketQueue object
     * @param packetId the packet id
     * @return true if the handler is registerd
     */
    public synchronized static boolean register(IPacketQueue handler, int packetId) {

        if (packetId == 250) {
            if (!p250.contains(handler)) {
                return p250.add(handler);
            }
        }

        return false;
    }

    /**
     * Unregister a handler object for the given packet id
     *
     * @param handler the IPacketQueue object
     * @param packetId the packet id
     * @return true if the handler was unregistred
     */
    public synchronized static boolean unregister(IPacketQueue handler, int packetId) {
        
        if (packetId == 250) {
            return p250.remove(handler);
        }
        
        return false;
    }

    /**
     * Fire all handlers with the received custom payload
     *
     * @param player the player that sent the custom payload
     * @param data the custom payload data
     * @param length the custom payload length
     * @param channel the custom payload channel
     * @return true if the packet is ment to be handled by the server, false otherwise
     */
    public static boolean callCustomPayload(Player player, byte[] data, int length, String channel) {

        synchronized (p250) {
            Iterator<IPacketQueue> i = p250.iterator();
            while (i.hasNext()) {
                if (!i.next().handleCustomPayload(player, channel, length, data)) {
                    return false;
                }
            }
        }

        return true;
    }
}
