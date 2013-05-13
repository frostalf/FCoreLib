package me.FurH.Core.internals;

import java.util.Queue;
import me.FurH.Core.exceptions.CoreException;
import me.FurH.Core.queue.PacketQueue;
import me.FurH.Core.reflection.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class InternalManager {
    
    private static IEntityPlayer entity;

    /**
     * Setup the InternalManager
     * 
     * @return true if the current server version is supported, false otherwise.
     */
    public static boolean setup() {

        String pkg = Bukkit.getServer().getClass().getPackage().getName();
        String version = pkg.substring(pkg.lastIndexOf('.') + 1);

        if (version.equals("v1_5_R2")) {
            entity = new EntityPlayer_v1_5_R2();
        } else
        if (version.equals("v1_5_R3")) {
            entity = new EntityPlayer_v1_5_R3();
        } else {
            return false;
        }

        if (version.equals("v1_5_R2")) {
            entity = new EntityPlayer_v1_5_R2();
        } else
        if (version.equals("v1_5_R3")) {
            entity = new EntityPlayer_v1_5_R3();
        } else {
            return false;
        }
       
        return true;
    }

    /**
     * Get the IEntityPlayer Object for the given Player
     * 
     * @param player the Bukkit Player
     * @return the IEntityPlayer object
     */
    public static IEntityPlayer getEntityPlayer(Player player) {
        
        if (entity == null) {
            setup();
        }
        
        return entity.setEntityPlayer(player);
    }
    
    private static boolean isNettyEnabled() {
        
        try {
            Class.forName("org.spigotmc.netty.NettyNetworkManager");
        } catch (NoClassDefFoundError ex) {
            return false;
        } catch (ClassNotFoundException ex) {
            return false;
        }
        
        return true;
    }
    
    private static class EntityPlayer_v1_5_R2 implements IEntityPlayer {

        private net.minecraft.server.v1_5_R2.EntityPlayer player;
        private Player bukkitplayer;
        
        @Override
        public IEntityPlayer setEntityPlayer(Player player) {
            this.bukkitplayer = player;
            
            this.player = 
                    ((org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer)player).getHandle();

            return this;
        }

        @Override
        public int ping() {
            return player.ping;
        }

        @Override
        public void setInboundQueue() throws CoreException {
            if (isNettyEnabled()) {
                Queue<net.minecraft.server.v1_5_R2.Packet> syncPackets = 
                        (Queue<net.minecraft.server.v1_5_R2.Packet>) ReflectionUtils.getPrivateField(player.playerConnection.networkManager, "syncPackets");

                me.FurH.Core.queue.PacketQueue.PacketQueue_v1_5_R2 newSyncPackets = PacketQueue.getLockPacketR2(bukkitplayer);
                newSyncPackets.addAll(syncPackets);

                ReflectionUtils.setFinalField(player.playerConnection.networkManager, "syncPackets", newSyncPackets);
            } else {
                Queue inboundQueue = (Queue) ReflectionUtils.getPrivateField(player.playerConnection.networkManager, "inboundQueue");

                me.FurH.Core.queue.PacketQueue.PacketQueue_v1_5_R2 newinboundQueue = PacketQueue.getLockPacketR2(bukkitplayer);
                newinboundQueue.addAll(inboundQueue);
                
                ReflectionUtils.setFinalField(player.playerConnection.networkManager, "inboundQueue", newinboundQueue);
            }
        }

        @Override
        public void sendPacket(PacketCustomPayload packet) {
            net.minecraft.server.v1_5_R2.Packet250CustomPayload payload = new net.minecraft.server.v1_5_R2.Packet250CustomPayload();
            
            payload.tag     = packet.getChannel();
            payload.data    = packet.getData();
            payload.length  = packet.getLength();
            
            player.playerConnection.networkManager.queue(payload);
        }
    }
    
    private static class EntityPlayer_v1_5_R3 implements IEntityPlayer {

        private net.minecraft.server.v1_5_R3.EntityPlayer player;
        private Player bukkitplayer;

        @Override
        public IEntityPlayer setEntityPlayer(Player player) {
            this.bukkitplayer = player;

            this.player = 
                    ((org.bukkit.craftbukkit.v1_5_R3.entity.CraftPlayer)player).getHandle();
            
            return this;
        }

        @Override
        public int ping() {
            return player.ping;
        }

        @Override
        public void setInboundQueue() throws CoreException {
            if (isNettyEnabled()) {
                Queue<net.minecraft.server.v1_5_R3.Packet> syncPackets = 
                        (Queue<net.minecraft.server.v1_5_R3.Packet>) ReflectionUtils.getPrivateField(player.playerConnection.networkManager, "syncPackets");

                me.FurH.Core.queue.PacketQueue.PacketQueue_v1_5_R3 newSyncPackets = PacketQueue.getLockPacketR3(bukkitplayer);
                newSyncPackets.addAll(syncPackets);

                ReflectionUtils.setFinalField(player.playerConnection.networkManager, "syncPackets", newSyncPackets);
            } else {
                Queue inboundQueue = (Queue) ReflectionUtils.getPrivateField(player.playerConnection.networkManager, "inboundQueue");

                me.FurH.Core.queue.PacketQueue.PacketQueue_v1_5_R3 newinboundQueue = PacketQueue.getLockPacketR3(bukkitplayer);
                newinboundQueue.addAll(inboundQueue);
                
                ReflectionUtils.setFinalField(player.playerConnection.networkManager, "inboundQueue", newinboundQueue);
            }
        }
        
        @Override
        public void sendPacket(PacketCustomPayload packet) {
            net.minecraft.server.v1_5_R3.Packet250CustomPayload payload = new net.minecraft.server.v1_5_R3.Packet250CustomPayload();
            
            payload.tag     = packet.getChannel();
            payload.data    = packet.getData();
            payload.length  = packet.getLength();
            
            player.playerConnection.networkManager.queue(payload);
        }
    }
}
