package me.FurH.Core.internals;

import com.comphenix.protocol.AsynchronousManager;
import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.FurH.Core.exceptions.CoreException;
import me.FurH.Core.packets.PacketManager;
import me.FurH.Core.packets.objects.PacketCustomPayload;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author FurmigaHumana All Rights Reserved unless otherwise explicitly stated.
 */
public class ProtocolEntityPlayer extends IEntityPlayer {

    private static AsynchronousManager manager;
    private static JavaPlugin plugin;
    
    private static boolean inboudSet = false;
    private static boolean outboundSet = false;
    
    public ProtocolEntityPlayer(JavaPlugin plugin) throws CoreException {
        manager = ProtocolLibrary.getProtocolManager().getAsynchronousManager();
        ProtocolEntityPlayer.plugin = plugin;
    }

    @Override
    public void setInboundQueue() throws CoreException {
        
        if (inboudSet) {
            return;
        }
        
        inboudSet = true;
        
        manager.registerAsyncHandler(new PacketAdapter(plugin, ConnectionSide.SERVER_SIDE, ListenerPriority.NORMAL,
                new Integer[] { Packets.Client.LOCALE_AND_VIEW_DISTANCE, Packets.Client.CUSTOM_PAYLOAD }) {

            @Override
            public void onPacketSending(PacketEvent e) {
                switch (e.getPacketID()) {
                    case Packets.Client.CUSTOM_PAYLOAD:
                        
                        PacketManager.callAsyncCustomPayload(e.getPlayer(), new PacketCustomPayload(e.getPacket().getHandle()));
                        
                        break;
                    case Packets.Client.LOCALE_AND_VIEW_DISTANCE:
                        
                        PacketManager.callAsyncClientSettings(e.getPlayer());
                        
                        break;
                }
            }
        });
    }

    @Override
    public void setOutboundQueue() throws CoreException {
        
        if (outboundSet) {
            return;
        }
        
        outboundSet = true;

        manager.registerAsyncHandler(new PacketAdapter(plugin, ConnectionSide.SERVER_SIDE, ListenerPriority.NORMAL,
                new Integer[] { Packets.Server.MAP_CHUNK_BULK, Packets.Server.MAP_CHUNK }) {
                    
            @Override
            public void onPacketSending(PacketEvent e) {
                
                int id = e.getPacketID();
                
                if (isInventoryHidden()) {
                    if (id == 103 || id == 104) {
                        e.setCancelled(true); return;
                    }
                }

                Object handle = handlePacket(e.getPacket().getHandle());
                
                if (handle == null) {
                    e.setCancelled(true); return;
                }
                
                if (id == 56) {
                    e.setPacket(new PacketContainer(Packets.Server.MAP_CHUNK_BULK, handle));
                } else if (id == 51) {
                    e.setPacket(new PacketContainer(Packets.Server.MAP_CHUNK, handle));
                }
            }
        });
    }
}