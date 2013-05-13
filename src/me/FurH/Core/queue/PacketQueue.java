package me.FurH.Core.queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.bukkit.entity.Player;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class PacketQueue {
    
    private static final List<IPacketQueue> handlers = Collections.synchronizedList(new ArrayList<IPacketQueue>());
    
    /**
     * Register a new handler to receive the packet data
     *
     * @param handler the handler interface
     */
    public static void register(IPacketQueue handler) {
        synchronized (handlers) {
            if (!handlers.contains(handler)) {
                handlers.add(handler);
            }
        }
    }
    
    /**
     * Unregister a handler from receiving packets data
     *
     * @param handler the handler interface
     */
    public static void unregister(IPacketQueue handler) {
        synchronized (handlers) {
            handlers.remove(handler);
        }
    }

    private static void callHandlers(Player player, int id, String channel) {
        synchronized (handlers) {
            Iterator<IPacketQueue> i = handlers.iterator();
            while (i.hasNext()) {
                i.next().receive(player, id, channel);
            }
        }
    }

    /**
     * Get a new PacketQueue for 1.5.R3
     *
     * @param p the player to set the queue
     * @return a new packet queue
     */
    public static PacketQueue_v1_5_R3 getLockPacketR3(Player p) {
        return new PacketQueue_v1_5_R3(p);
    }
    
    /**
     * Get a new PacketQueue for 1.5.R2
     *
     * @param p the player to set the queue
     * @return a new packet queue
     */
    public static  PacketQueue_v1_5_R2 getLockPacketR2(Player p) {
        return new PacketQueue_v1_5_R2(p);
    }

    /**
     * Builds a ConcurrentLinkedQueue to use as packet queue
     */
    public static class PacketQueue_v1_5_R3 extends ConcurrentLinkedQueue<Object> {

        private static final long serialVersionUID = -2453733818973030389L;

        private Player player;

        /**
         * Setup the packet queue with the defined player
         *
         * @param player the player to set the queue
         */
        public PacketQueue_v1_5_R3(Player player) {
            this.player = player;
        }

        @Override
        public boolean add(Object object) {

            net.minecraft.server.v1_5_R3.Packet packet = (net.minecraft.server.v1_5_R3.Packet)object;
            String channel = null;
            
            if (packet instanceof net.minecraft.server.v1_5_R3.Packet250CustomPayload) {

                net.minecraft.server.v1_5_R3.Packet250CustomPayload p250 =
                        (net.minecraft.server.v1_5_R3.Packet250CustomPayload) packet;
                
                channel = p250.tag;
            }
            
            callHandlers(player, packet.n(), channel);
            
            return super.add(packet);
        }
    }

    /**
     * Builds a ConcurrentLinkedQueue to use as packet queue
     */
    public static class PacketQueue_v1_5_R2 extends ConcurrentLinkedQueue<Object> {

        private static final long serialVersionUID = -2453733818973030389L;

        private Player player;

        /**
         * Setup the packet queue with the defined player
         *
         * @param player the player to set the queue
         */
        public PacketQueue_v1_5_R2(Player player) {
            this.player = player;
        }

        @Override
        public boolean add(Object object) {

            net.minecraft.server.v1_5_R2.Packet packet = (net.minecraft.server.v1_5_R2.Packet)object;
            String channel = null;
            
            if (packet instanceof net.minecraft.server.v1_5_R2.Packet250CustomPayload) {

                net.minecraft.server.v1_5_R2.Packet250CustomPayload p250 =
                        (net.minecraft.server.v1_5_R2.Packet250CustomPayload) packet;
                
                channel = p250.tag;
            }
            
            callHandlers(player, packet.n(), channel);

            return super.add(packet);
        }
    }
    
}
