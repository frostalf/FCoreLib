package me.FurH.Core.packets.objects;

import me.FurH.Core.exceptions.CoreException;
import me.FurH.Core.reflection.ReflectionUtils;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class PacketMapChunkBulk implements ICorePacket {

    private Object handle;

    public PacketMapChunkBulk(Object packet) {
        this.handle = packet;
    }
    
    public int[] getX() throws CoreException {
        return (int[]) ReflectionUtils.getPrivateField(this.handle, "c");
    }
    
    public int[] getZ() throws CoreException {
        return (int[]) ReflectionUtils.getPrivateField(this.handle, "d");
    }
    
    public int[] getMaskA() throws CoreException {
        return (int[]) ReflectionUtils.getPrivateField(this.handle, "a");
    }
    
    public int[] getMaskB() throws CoreException {
        return (int[]) ReflectionUtils.getPrivateField(this.handle, "b");
    }
    
    public byte[] getBuffer() throws CoreException {
        return (byte[]) ReflectionUtils.getPrivateField(this.handle, "buffer");
    }
    
    public void setBuffer(byte[] buffer) throws CoreException {
        ReflectionUtils.setPrivateField(this.handle, "buffer", buffer);
    }
    
    public byte[][] getInflatedBuffers() throws CoreException {
        return (byte[][]) ReflectionUtils.getPrivateField(this.handle, "inflatedBuffers");
    }
    
    public void setInflatedBuffers(byte[][] inflatedBuffers) throws CoreException {
        ReflectionUtils.setPrivateField(this.handle, "inflatedBuffers", inflatedBuffers);
    }
    
    public int getCompressedSize() throws CoreException {
        return ReflectionUtils.getPrivateIntField(this.handle, "size");
    }
    
    public void setCompressedSize(int size) throws CoreException {
        ReflectionUtils.setPrivateField(this.handle, "size", size);
    }
    
    public boolean h() throws CoreException {
        return ReflectionUtils.getPrivateBooleanField(this.handle, "h");
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