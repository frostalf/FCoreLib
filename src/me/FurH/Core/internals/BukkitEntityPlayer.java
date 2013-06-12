package me.FurH.Core.internals;

import java.util.Collections;
import java.util.List;
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
public class BukkitEntityPlayer extends IEntityPlayer {
    
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
        
        Queue syncPackets = (Queue) ReflectionUtils.getPrivateField(entity.playerConnection.networkManager, "inboundQueue");
        newSyncPackets.addAll(syncPackets);
        ReflectionUtils.setFinalField(entity.playerConnection.networkManager, "inboundQueue", newSyncPackets);
        
    }

    @Override
    public void setOutboundQueue() throws CoreException {

        List newhighPriorityQueue = Collections.singletonList(new PriorityQueue());
        List newlowPriorityQueue = Collections.singletonList(new PriorityQueue());

        List highPriorityQueue = (List) ReflectionUtils.getPrivateField(entity.playerConnection.networkManager, "highPriorityQueue");
        List lowPriorityQueue = (List) ReflectionUtils.getPrivateField(entity.playerConnection.networkManager, "lowPriorityQueue");

        if (highPriorityQueue != null) {
            newhighPriorityQueue.addAll(highPriorityQueue);
            highPriorityQueue.clear();
        }

        if (lowPriorityQueue != null) {
            newlowPriorityQueue.addAll(lowPriorityQueue);
            lowPriorityQueue.clear();
        }

        ReflectionUtils.setFinalField(entity.playerConnection.networkManager, "highPriorityQueue", newhighPriorityQueue);
        ReflectionUtils.setFinalField(entity.playerConnection.networkManager, "lowPriorityQueue", newlowPriorityQueue);
        
    }
}