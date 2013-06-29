package me.FurH.Core.internals;

import java.util.ArrayList;
import java.util.List;
import me.FurH.Core.exceptions.CoreException;
import me.FurH.Core.packets.PacketCustomPayload;
import me.FurH.Core.packets.PacketManager;
import net.minecraft.server.v1_5_R3.EntityPlayer;
import net.minecraft.server.v1_5_R3.ItemStack;
import net.minecraft.server.v1_5_R3.Packet;
import net.minecraft.server.v1_5_R3.Packet0KeepAlive;
import net.minecraft.server.v1_5_R3.Packet250CustomPayload;
import net.minecraft.server.v1_5_R3.Packet51MapChunk;
import net.minecraft.server.v1_5_R3.Packet56MapChunkBulk;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_5_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_5_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
@SuppressWarnings({"unchecked", "rawtypes", "deprecation"})
public abstract class IEntityPlayer {

    public List<Packet> send_later = new ArrayList<Packet>();
    //public List<Packet> send_replace = new ArrayList<Packet>();

    protected boolean inventory_hidden = false;
    protected EntityPlayer entity;
    protected Player player;

    /**
     * Set the Player of this IEntityPlayer object
     * 
     * @param player the player to be set
     * @return the IEntityPlayer for the given Player
     */
    public IEntityPlayer setEntityPlayer(Player player) {

        this.player = player;

        this.entity =
                ((CraftPlayer) player).getHandle();

        return this;
    }

    public EntityPlayer getHandle() {
        return this.entity;
    }
    
    /**
     * Get the Player network ping
     * 
     * @return the player ping in milliseconds
     */
    public int ping() {
        return entity.ping;
    }

    /**
     * Send the EntityPlayer a custom payload
     *
     * @param packet the custom payload
     */
    public void sendCustomPayload(PacketCustomPayload packet) {
        Packet250CustomPayload payload = new Packet250CustomPayload();

        payload.tag = packet.getChannel();
        payload.data = packet.getData();
        payload.length = packet.getLength();

        entity.playerConnection.networkManager.queue(payload);
    }

    /**
     * Hides the player inventory
     */
    public void hideInventory() {
        inventory_hidden = false;
        
        ItemStack stack = CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR, 1));

        List stacks = new ArrayList();
        for (int j1 = 0; j1 < entity.activeContainer.a().size(); j1++) {
            stacks.add(stack);
        }

        entity.a(entity.activeContainer, stacks);
        inventory_hidden = true;
    }
    
    /**
     * Revels the player inventory
     */
    public void unHideInventory() {
        inventory_hidden = false;
        player.updateInventory();
    }
    
    /**
     * Get if the player inventory is hidden
     *
     * @return true if the inventory is hidden, false otherwise.
     */
    public boolean isInventoryHidden() {
        return this.inventory_hidden;
    }
    
    /**
     * Set the Player Inboud Queue
     * 
     * @throws CoreException 
     */
    public abstract void setInboundQueue() throws CoreException;

    /**
     * Set the Player Outbound queue
     * 
     * @throws CoreException 
     */
    public abstract void setOutboundQueue() throws CoreException;

    public void resendPacket(Packet packet) {
        send_later.add(packet);
    }

    protected static void handleInboundPacketAsync(Player player, Packet packet) {
        if (packet.n() == 250) {
            Packet250CustomPayload p250 = (Packet250CustomPayload) packet;
            PacketManager.callAsyncCustomPayload(player, p250.data, p250.length, p250.tag);
        } else
        if (packet.n() == 204) {
            PacketManager.callAsyncClientSettings(player);
        }
    }

    protected static Packet handleOutboundPacketAsync(Player player, Packet packet) {

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
    
    public class PriorityQueue extends ArrayList<Packet> {

        private static final long serialVersionUID = 927895363924203624L;

        @Override
        public boolean add(Packet packet) {

            if (isInventoryHidden() && (packet.n() == 103 || packet.n() == 104)) {
                return false;
            }

            return super.add(packet);
        }

        public boolean add0(Packet packet) {
            return super.add(packet);
        }

        @Override
        public Packet remove(int index) {

            Packet packet = super.remove(index);

            if (!send_later.isEmpty()) {

                //send_replace.add(packet);
                packet = send_later.remove(0);

                return packet;
            }
            
            /*if (!send_replace.isEmpty()) {

                packet = send_replace.remove(0);
                return packet;

            }*/
            
            if (packet != null) {
                packet = handleOutboundPacketAsync(player, packet);
            }

            if (packet == null) {
                return new Packet0KeepAlive(1);
            }

            return packet;
        }
    }
}
