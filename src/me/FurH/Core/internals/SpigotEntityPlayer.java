package me.FurH.Core.internals;

import io.netty.channel.Channel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.FurH.Core.exceptions.CoreException;
import me.FurH.Core.reflection.ReflectionUtils;
import net.minecraft.server.v1_5_R3.Packet;

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

                handleInboundPacketAsync(player, packet);

                return super.add(packet);
            }
        };
        
        Queue<Packet> syncPackets = (Queue<Packet>) ReflectionUtils.getPrivateField(entity.playerConnection.networkManager, "syncPackets");
        newSyncPackets.addAll(syncPackets);
        ReflectionUtils.setFinalField(entity.playerConnection.networkManager, "syncPackets", newSyncPackets);
        
    }

    @Override
    public void setOutboundQueue() throws CoreException {

        Channel channel = (io.netty.channel.Channel) ReflectionUtils.getPrivateField(entity.playerConnection.networkManager, "channel");
        channel.pipeline().replace("encoder", "encoder", new FPacketEncoder());
        ReflectionUtils.setFinalField(entity.playerConnection.networkManager, "channel", channel);

    }

    private class FPacketEncoder extends org.spigotmc.netty.PacketEncoder {

        private FPacketEncoder() {
            super((org.spigotmc.netty.NettyNetworkManager) entity.playerConnection.networkManager);
        }

        @Override
        public void encode(io.netty.channel.ChannelHandlerContext ctx, Packet packet, io.netty.buffer.ByteBuf out) throws Exception {

            if (packet != null) {

                if (isInventoryHidden() && (packet.n() == 103 || packet.n() == 104)) {
                    return;
                }

                packet = handleOutboundPacketAsync(player, packet);
            }

            super.encode(ctx, packet, out);
        }
    }
}
