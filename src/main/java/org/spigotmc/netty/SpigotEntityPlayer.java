package org.spigotmc.netty;

import io.netty.channel.Channel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.FurH.Core.exceptions.CoreException;
import me.FurH.Core.internals.IEntityPlayer;
import me.FurH.Core.packets.PacketManager;
import me.FurH.Core.packets.objects.PacketCustomPayload;
import me.FurH.Core.packets.objects.PacketMapChunk;
import me.FurH.Core.packets.objects.PacketMapChunkBulk;
import me.FurH.Core.reflection.ReflectionUtils;
import net.minecraft.server.v1_6_R2.Packet;
import net.minecraft.server.v1_6_R2.Packet0KeepAlive;

/**
*
* @author FurmigaHumana
* All Rights Reserved unless otherwise explicitly stated.
*/
@SuppressWarnings({"unchecked", "rawtypes", "deprecation"})
public class SpigotEntityPlayer extends IEntityPlayer {

    @Override
    public void setInboundQueue() throws CoreException {
        
        Queue<Packet> newSyncPackets = new ConcurrentLinkedQueue<Packet>() {

            private static final long serialVersionUID = 7299839519835756010L;

            @Override
            public boolean add(Packet packet) {
                
                try {

                    int id = packet.n();

                    if (id == 250) {

                        PacketManager.callAsyncCustomPayload(player, new PacketCustomPayload(packet));

                    } else
                    if (id == 204) {

                        PacketManager.callAsyncClientSettings(player);

                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                return super.add(packet);
            }
        };
        
        Queue<Packet> syncPackets = (Queue<Packet>) ReflectionUtils.getPrivateField(networkManager, "syncPackets");
        newSyncPackets.addAll(syncPackets);
        ReflectionUtils.setFinalField(networkManager, "syncPackets", newSyncPackets);
        
    }

    @Override
    public void setOutboundQueue() throws CoreException {

        ReflectionUtils.setFinalField(networkManager, "writer", new FPacketWriter());

    }
    
    public class FPacketWriter<U> extends PacketWriter {

        @Override
        void write(Channel channel, NettyNetworkManager networkManager, Packet packet) {

            if (packet != null) {

                if (isInventoryHidden() && (packet.n() == 103 || packet.n() == 104)) {
                    return;
                }

                if (!send_later.isEmpty()) {

                    Packet old = packet;
                    packet = (Packet) send_later.remove(0);

                    if (packet.n() != old.n()) {
                        send_replace.add(packet);
                    }

                    super.write(channel, networkManager, packet);

                    return;
                }

                if (!send_replace.isEmpty()) {

                    packet = (Packet) send_replace.remove(0);
                    super.write(channel, networkManager, packet);

                    return;

                }
                
                int id = packet.n();
                
                try {
                    if (id == 56) {
                        packet = (Packet) PacketManager.callAsyncMapChunkBulk(player, new PacketMapChunkBulk(packet)).getHandle();
                    } else if (id == 51) {
                        packet = (Packet) PacketManager.callAsyncMapChunk(player, new PacketMapChunk(packet)).getHandle();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                if (packet == null) {
                    packet = new Packet0KeepAlive(1);
                }
            }

            super.write(channel, networkManager, packet);
        }
    }
}

