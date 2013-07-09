package me.FurH.Core.packets.objects;

import me.FurH.Core.exceptions.CoreException;
import me.FurH.Core.reflection.ReflectionUtils;
import me.FurH.Core.reflection.field.IReflectField;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class PacketMapChunkBulk implements ICorePacket {

    private static IReflectField c = null;
    private static IReflectField d = null;
    private static IReflectField a = null;
    private static IReflectField b = null;
    private static IReflectField buffer = null;
    private static IReflectField inflatedBuffers = null;
    private static IReflectField size = null;
    private static IReflectField h = null;
    
    private Object handle;

    public PacketMapChunkBulk(Object packet) {

        this.handle = packet;

        try {

            if (c == null) {
                System.out.println("NEW C");
                c = ReflectionUtils.getNewReflectField("c", packet.getClass(), false);
            }
            
            if (d == null) {
                d = ReflectionUtils.getNewReflectField("d", packet.getClass(), false);
            }
            
            if (a == null) {
                a = ReflectionUtils.getNewReflectField("a", packet.getClass(), false);
            }
            
            if (b == null) {
                b = ReflectionUtils.getNewReflectField("b", packet.getClass(), false);
            }
            
            if (buffer == null) {
                buffer = ReflectionUtils.getNewReflectField("buffer", packet.getClass(), true);
            }
            
            if (inflatedBuffers == null) {
                inflatedBuffers = ReflectionUtils.getNewReflectField("inflatedBuffers", packet.getClass(), false);
            }
            
            if (h == null) {
                h = ReflectionUtils.getNewReflectField("h", packet.getClass(), false);
            }

            if (size == null) {
                size = ReflectionUtils.getNewReflectField("size", packet.getClass(), true);
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public int[] getX() throws CoreException {
        return c.getIntArray(handle);
    }
    
    public int[] getZ() throws CoreException {
        return d.getIntArray(handle);
    }
    
    public int[] getMaskA() throws CoreException {
        return a.getIntArray(handle);
    }
    
    public int[] getMaskB() throws CoreException {
        return b.getIntArray(handle);
    }
    
    public byte[] getBuffer() throws CoreException {
        return buffer.getByteArray(handle);
    }
    
    public void setBuffer(byte[] buffer0) throws CoreException {
        buffer.set(buffer0, handle);
    }
    
    public byte[][] getInflatedBuffers() throws CoreException {
        return inflatedBuffers.getDoubleByteArray(handle);
    }
    
    public void setInflatedBuffers(byte[][] inflatedBuffers0) throws CoreException {
        inflatedBuffers.set(inflatedBuffers0, handle);
    }
    
    public int getCompressedSize() throws CoreException {
        return size.getInt(handle);
    }
    
    public void setCompressedSize(int size0) throws CoreException {
        size.set(size0, handle);
    }
    
    public boolean h() throws CoreException {
        return h.getBoolean(handle);
    }

    @Override
    public int getPacketId() {
        return 56;
    }

    @Override
    public Object getHandle() {
        return this.handle;
    }

    public PacketMapChunkBulk setHandle(Object handle) {
        this.handle = handle; return this;
    }
    
    @Override
    public String getPacketName() {
        return this.handle.getClass().getSimpleName();
    }
}