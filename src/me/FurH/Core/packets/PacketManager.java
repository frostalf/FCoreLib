package me.FurH.Core.packets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.server.v1_5_R3.Packet;
import net.minecraft.server.v1_5_R3.Packet14BlockDig;
import net.minecraft.server.v1_5_R3.Packet15Place;
import net.minecraft.server.v1_5_R3.Packet250CustomPayload;
import net.minecraft.server.v1_5_R3.Packet51MapChunk;
import net.minecraft.server.v1_5_R3.Packet56MapChunkBulk;
import org.bukkit.entity.Player;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class PacketManager {

    private static final List<IPacketQueue> inn250 = Collections.synchronizedList(new ArrayList<IPacketQueue>());
    private static final List<IPacketQueue> inn204 = Collections.synchronizedList(new ArrayList<IPacketQueue>());

    private static final List<IPacketQueue> inn015 = Collections.synchronizedList(new ArrayList<IPacketQueue>());
    private static final List<IPacketQueue> inn014 = Collections.synchronizedList(new ArrayList<IPacketQueue>());

    private static final List<IPacketQueue> out056 = Collections.synchronizedList(new ArrayList<IPacketQueue>());
    private static final List<IPacketQueue> out051 = Collections.synchronizedList(new ArrayList<IPacketQueue>());
    
    private static final List<IPacketQueue> out250 = Collections.synchronizedList(new ArrayList<IPacketQueue>());

    /**
     * Handle the inbound packet
     *
     * @param player the player
     * @param packet the packet
     */
    public static void handleInboundPacketAsync(Player player, Packet packet) {
        if (packet.n() == 250) {
            Packet250CustomPayload p250 = (Packet250CustomPayload) packet;
            PacketManager.callAsyncCustomPayload(player, p250.data, p250.length, p250.tag);
        } else
        if (packet.n() == 204) {
            PacketManager.callAsyncClientSettings(player);
        } else
        if (packet.n() == 15) {
            Packet15Place p15 = (Packet15Place) packet;
            PacketManager.callAsyncBlockPlace(player, p15.getItemStack().id, p15.d(), p15.f(), p15.g());
        } else
        if (packet.n() == 14) {
            Packet14BlockDig p14 = (Packet14BlockDig) packet;
            if (p14.e == 0) { PacketManager.callAsyncBlockBreak(player, p14.a, p14.b, p14.c); }
        }
    }
    
    /**
     * Handle the outbound packet
     *
     * @param player the player
     * @param packet the packet
     * @return the modified packet
     */
    public static Packet handleOutboundPacketAsync(Player player, Packet packet) {

        if (packet instanceof Packet56MapChunkBulk) {
            packet = (Packet56MapChunkBulk) PacketManager.callAsyncMapChunkBulk(player, (Packet56MapChunkBulk) packet);
        } else
        if (packet instanceof Packet51MapChunk) {
            packet = (Packet51MapChunk) PacketManager.callAsyncMapChunk(player, (Packet51MapChunk) packet);
        } else
        if (packet.n() == 250) {
            Packet250CustomPayload p250 = (Packet250CustomPayload) packet;
            packet = (Packet250CustomPayload) PacketManager.callOutAsyncCustomPayload(player, p250);
        }
        
        return packet;
    }
    
    /**
     * Register a new handler for the given packet id, use the negative value to register outcoming packets
     * 
     * Current supported packet id are:
     * - 250
     * - 204
     * - 15
     * - 14
     *
     * @param handler the IPacketQueue object
     * @param packetId the packet id
     * @return true if the handler is registerd
     */
    public static boolean register(IPacketQueue handler, int packetId) {

        if (packetId == -250) {
            synchronized (out250) {
                if (!out250.contains(handler)) {
                    return out250.add(handler);
                }
            }
        } else
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
        } else
        if (packetId == 56) {
            synchronized (out056) {
                if (!out056.contains(handler)) {
                    return out056.add(handler);
                }
            }
        } else
        if (packetId == 51) {
            synchronized (out051) {
                if (!out051.contains(handler)) {
                    return out051.add(handler);
                }
            }
        } else
        if (packetId == 15) {
            synchronized (inn015) {
                if (!inn015.contains(handler)) {
                    return inn015.add(handler);
                }
            }
        } else
        if (packetId == 14) {
            synchronized (inn014) {
                if (!inn014.contains(handler)) {
                    return inn014.add(handler);
                }
            }
        }

        return false;
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
            synchronized (out250) {
                return out250.remove(handler);
            }
        } else
        if (packetId == 250) {
            synchronized (inn250) {
                return inn250.remove(handler);
            }
        } else
        if (packetId == 204) {
            synchronized (inn204) {
                return inn204.remove(handler);
            }
        } else
        if (packetId == 56) {
            synchronized (out056) {
                return out056.remove(handler);
            }
        } else
        if (packetId == 51) {
            synchronized (out051) {
                return out051.remove(handler);
            }
        } else
        if (packetId == 15) {
            synchronized (inn015) {
                return inn015.remove(handler);
            }
        } else
        if (packetId == 14) {
            synchronized (inn014) {
                return inn014.remove(handler);
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
    public static boolean callAsyncCustomPayload(Player player, byte[] data, int length, String channel) {

        synchronized (inn250) {
            Iterator<IPacketQueue> i = inn250.iterator();
            while (i.hasNext()) {
                if (!i.next().handleAsyncCustomPayload(player, channel, length, data)) {
                    return false;
                }
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

        synchronized (out250) {
            Iterator<IPacketQueue> i = out250.iterator();
            Object obj = null;

            while (i.hasNext()) {
                obj = i.next().handleAndSetAsyncCustomPayload(player, obj == null ? object : obj);
            }
            
            return obj == null ? object : obj;
        }
    }
    
    /**
     * Fire all handlers with the received client settings packet
     *
     * @param player the player
     * @return true if the packet is ment to be handled by the server, false otherwise
     */
    public static boolean callAsyncClientSettings(Player player) {

        synchronized (inn204) {
            Iterator<IPacketQueue> i = inn204.iterator();
            while (i.hasNext()) {
                if (!i.next().handleAsyncClientSettings(player)) {
                    return false;
                }
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

        synchronized (out051) {
            Iterator<IPacketQueue> i = out051.iterator();
            Object obj = null;

            while (i.hasNext()) {
                obj = i.next().handleAsyncMapChunk(player, obj == null ? object : obj);
            }
            
            return obj == null ? object : obj;
        }
        
    }
    
    /**
     * Fire all handlers with the received chunk bulk packet
     *
     * @param player the player
     * @param object the packet object
     * @return the modified packet object
     */
    public static Object callAsyncMapChunkBulk(Player player, Object object) {

        synchronized (out056) {
            Iterator<IPacketQueue> i = out056.iterator();
            Object obj = null;

            while (i.hasNext()) {
                obj = i.next().handleAsyncMapChunkBulk(player, obj == null ? object : obj);
            }
            
            return obj == null ? object : obj;
        }
    }

    /**
     * Fire all handlers with the received block place packet
     * 
     * @param player the player
     * @param id the block id
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     */
    public static void callAsyncBlockPlace(Player player, int id, int x, int y, int z) {
        
        synchronized (inn015) {
            Iterator<IPacketQueue> i = inn015.iterator();

            while (i.hasNext()) {
                i.next().handleAsyncBlockPlace(player, id, x, y, z);
            }
        }
        
    }
    
    /**
     * Fire all handlres with the received block dig packet
     *
     * @param player the player
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     */
    public static void callAsyncBlockBreak(Player player, int x, int y, int z) {
        
        synchronized (inn014) {
            Iterator<IPacketQueue> i = inn014.iterator();

            while (i.hasNext()) {
                i.next().handleAsyncBlockBreak(player, x, y, z);
            }
        }
        
    }
}
