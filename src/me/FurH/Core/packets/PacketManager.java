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

    private static final List<IPacketQueue> inn250 = Collections.synchronizedList(new ArrayList<IPacketQueue>());
    private static final List<IPacketQueue> inn204 = Collections.synchronizedList(new ArrayList<IPacketQueue>());

    /**
     * Register a new handler for the given packet id
     * 
     * Current supported packet id are:
     * - 250
     * - 204
     *
     * @param handler the IPacketQueue object
     * @param packetId the packet id
     * @return true if the handler is registerd
     */
    public static boolean register(IPacketQueue handler, int packetId) {

        if (packetId == 250) {
            synchronized (inn250) {
                if (!inn250.contains(handler)) {
                    return inn250.add(handler);
                }
            }
        } else
        if (packetId == 204) {
            synchronized (inn204) {
                if (!inn204.contains(handler)) {
                    return inn204.add(handler);
                }
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
    public static boolean unregister(IPacketQueue handler, int packetId) {

        if (packetId == 250) {
            synchronized (inn250) {
                return inn250.remove(handler);
            }
        } else
        if (packetId == 204) {
            synchronized (inn204) {
                return inn204.remove(handler);
            }
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

        synchronized (inn250) {
            Iterator<IPacketQueue> i = inn250.iterator();
            while (i.hasNext()) {
                if (!i.next().handleCustomPayload(player, channel, length, data)) {
                    return false;
                }
            }
        }

        return true;
    }
    
    /**
     * Fire all handlers with the received client settings packet
     *
     * @param player
     * @return
     */
    public static boolean callClientSettings(Player player) {

        synchronized (inn204) {
            Iterator<IPacketQueue> i = inn204.iterator();
            while (i.hasNext()) {
                if (!i.next().handleClientSettings(player)) {
                    return false;
                }
            }
        }

        return true;
    }
}
