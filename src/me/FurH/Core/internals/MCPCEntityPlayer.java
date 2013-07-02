package me.FurH.Core.internals;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.FurH.Core.exceptions.CoreException;
import me.FurH.Core.reflection.ReflectionUtils;
import net.minecraft.server.v1_6_R1.Packet;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
@SuppressWarnings({"unchecked", "rawtypes", "deprecation"})
public class MCPCEntityPlayer extends IEntityPlayer {

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
        
        for (Field field : entity.playerConnection.networkManager.getClass().getFields()) {
            if (field.getType().equals(Queue.class)) {

                Queue<Packet> syncPackets = (Queue<Packet>) ReflectionUtils.getPrivateField(entity.playerConnection.networkManager, field.getName());
                newSyncPackets.addAll(syncPackets);
                ReflectionUtils.setFinalField(entity.playerConnection.networkManager, field.getName(), newSyncPackets);

            }
        }
    }

    @Override
    public void setOutboundQueue() throws CoreException {

        for (Field field : entity.playerConnection.networkManager.getClass().getFields()) {
            if (field.getType().equals(List.class)) {     

                List newhighPriorityQueue = Collections.synchronizedList(new PriorityQueue());
                List highPriorityQueue = (List) ReflectionUtils.getPrivateField(entity.playerConnection.networkManager, field.getName());

                if (highPriorityQueue != null) {
                    newhighPriorityQueue.addAll(highPriorityQueue);
                    highPriorityQueue.clear();
                }

                ReflectionUtils.setFinalField(entity.playerConnection.networkManager, field.getName(), newhighPriorityQueue);
                
            }
        }
    }
}