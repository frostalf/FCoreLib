package me.FurH.Core.packets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.bukkit.entity.Player;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class PacketManager {
        
    private static IPacketQueue[] inn250 = new IPacketQueue[0];
    private static IPacketQueue[] inn204 = new IPacketQueue[0];

    private static IPacketQueue[] out056 = new IPacketQueue[0];
    private static IPacketQueue[] out051 = new IPacketQueue[0];

    private static IPacketQueue[] out250 = new IPacketQueue[0];

    /**
     * Register a new handler for the given packet id, use the negative value to register outcoming packets
     * 
     * Current supported packet id are:
     * - 250
     * - 204
     * - 56
     * - 51
     *
     * @param handler the IPacketQueue object
     * @param packetId the packet id
     * @return true if the handler is registerd
     */
    public static boolean register(IPacketQueue handler, int packetId) {

        if (packetId == -250) {
            out250 = addElement(out250, handler);
        } else
        if (packetId == 250) {
            inn250 = addElement(inn250, handler);
        } else
        if (packetId == 204) {
            inn204 = addElement(inn204, handler);
        } else
        if (packetId == 56) {
            out056 = addElement(out056, handler);
        } else
        if (packetId == 51) {
            out051 = addElement(out051, handler);
        }

        return true;
    }

    /**
     * Unregister a handler object for the given packet id, use the negative value to unregister outcoming packets.
     *
     * @param handler the IPacketQueue object
     * @param packetId the packet id
     * @return true if the handler was unregistred
     */
    public static boolean unregister(IPacketQueue handler, int packetId) {

        if (packetId == -250) {
            out250 = removeElement(out250, handler);
        } else
        if (packetId == 250) {
            inn250 = removeElement(inn250, handler);
        } else
        if (packetId == 204) {
            inn204 = removeElement(inn204, handler);
        } else
        if (packetId == 56) {
            out056 = removeElement(out056, handler);
        } else
        if (packetId == 51) {
            out051 = removeElement(out051, handler);
        }

        return true;
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
    public static boolean callAsyncCustomPayload(Player player, byte[] data, int length, String channel) {

        for (int j1 = 0; j1 < inn250.length; j1++) {
            if (!inn250[ j1 ].handleAsyncCustomPayload(player, channel, length, data)) {
                return false;
            }
        }

        return true;
    }
    
    /**
     * Fire all handlers with the sent custom payload
     *
     * @param player the player
     * @param object the original packet
     * @return the modified packet
     */
    public static Object callOutAsyncCustomPayload(Player player, Object object) {

        Object obj = null;
        
        for (int j1 = 0; j1 < out250.length; j1++) {
            obj = out250[ j1 ].handleAndSetAsyncCustomPayload(player, obj == null ? object : obj);
        }

        return obj == null ? object : obj;
    }
    
    /**
     * Fire all handlers with the received client settings packet
     *
     * @param player the player
     * @return true if the packet is ment to be handled by the server, false otherwise
     */
    public static boolean callAsyncClientSettings(Player player) {

        for (int j1 = 0; j1 < inn204.length; j1++) {
            if (!inn204[ j1 ].handleAsyncClientSettings(player)) {
                return false;
            }
        }

        return true;
    }
    
    /**
     * Fire all handlers with the received chunk packet
     *
     * @param player the player
     * @param object the packet object
     * @return the modified packet object
     */
    public static Object callAsyncMapChunk(Player player, Object object) {

        Object obj = null;
        
        for (int j1 = 0; j1 < out051.length; j1++) {
            obj = out051[ j1 ].handleAsyncMapChunk(player, obj == null ? object : obj);
        }

        return obj == null ? object : obj;
    }
    
    /**
     * Fire all handlers with the received chunk bulk packet
     *
     * @param player the player
     * @param object the packet object
     * @return the modified packet object
     */
    public static Object callAsyncMapChunkBulk(Player player, Object object) {

        Object obj = null;
        
        for (int j1 = 0; j1 < out056.length; j1++) {
            obj = out056[ j1 ].handleAsyncMapChunkBulk(player, obj == null ? object : obj);
        }

        return obj == null ? object : obj;
    }
    
    private static IPacketQueue[] addElement(IPacketQueue[] source, IPacketQueue element) {
        List<IPacketQueue> list = new ArrayList<IPacketQueue>(Arrays.asList(source));
        
        list.add(element);

        return list.toArray(new IPacketQueue[ list.size() ]);
    }
    
    private static IPacketQueue[] removeElement(IPacketQueue[] source, IPacketQueue element) {

        List<IPacketQueue> list = new ArrayList<IPacketQueue>(Arrays.asList(source));

        Iterator<IPacketQueue> i = list.iterator();
        while (i.hasNext()) {
            if (i.next() == element) {
                i.remove();
            }
        }

        return list.toArray(new IPacketQueue[ list.size() ]);
    }
}
