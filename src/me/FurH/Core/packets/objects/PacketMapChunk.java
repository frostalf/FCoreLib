package me.FurH.Core.packets.objects;

import me.FurH.Core.exceptions.CoreException;
import me.FurH.Core.reflection.ReflectionUtils;

/**
 *
 * @author FurmigaHumana All Rights Reserved unless otherwise explicitly stated.
 */
public class PacketMapChunk implements ICorePacket {

    private Object handle;

    /**
     * Creates a new Packet51MapChunk Handler
     *
     * @param packet the original Packet51MapChunk
     */
    public PacketMapChunk(Object packet) {
        this.handle = packet;
    }

    /**
     * Return the packet X coordinate
     *
     * @return
     * @throws CoreException
     */
    public int getX() throws CoreException {
        return ReflectionUtils.getPrivateIntField(this.handle, "a");
    }

    /**
     * Return the packet Z coordinate
     *
     * @return
     * @throws CoreException
     */
    public int getZ() throws CoreException {
        return ReflectionUtils.getPrivateIntField(this.handle, "b");
    }

    /**
     * Return the packet C mask field
     *
     * @return
     * @throws CoreException
     */
    public int getMaskA() throws CoreException {
        return ReflectionUtils.getPrivateIntField(this.handle, "c");
    }

    /**
     * Return the packet D mask field
     *
     * @return
     * @throws CoreException
     */
    public int getMaskB() throws CoreException {
        return ReflectionUtils.getPrivateIntField(this.handle, "d");
    }

    /**
     * Return the packet compressed buffer
     *
     * @return
     * @throws CoreException
     */
    public byte[] getBuffer() throws CoreException {
        return (byte[]) ReflectionUtils.getPrivateField(this.handle, "buffer");
    }

    /**
     * Set the packet compressed buffer
     *
     * @param buffer
     * @throws CoreException
     */
    public void setBuffer(byte[] buffer) throws CoreException {
        ReflectionUtils.setPrivateField(this.handle, "buffer", buffer);
    }

    /**
     * Return the packet inflatedBuffer
     *
     * @return
     * @throws CoreException
     */
    public byte[] getInflatedBuffer() throws CoreException {
        return (byte[]) ReflectionUtils.getPrivateField(this.handle, "inflatedBuffer");
    }

    /**
     * Set the packet inflatedBuffer
     *
     * @param inflatedBuffer
     * @throws CoreException
     */
    public void setInflatedBuffer(byte[] inflatedBuffer) throws CoreException {
        ReflectionUtils.setPrivateField(this.handle, "inflatedBuffer", inflatedBuffer);
    }

    /**
     * Return the packet "e" flag
     *
     * @return
     * @throws CoreException
     */
    public boolean e() throws CoreException {
        return ReflectionUtils.getPrivateBooleanField(this.handle, "e");
    }

    /**
     * Return the packet compressed size
     *
     * @return
     * @throws CoreException
     */
    public int getCompressedSize() throws CoreException {
        return ReflectionUtils.getPrivateIntField(this.handle, "size");
    }

    /**
     * Set the packet compressed size
     *
     * @param size
     * @throws CoreException
     */
    public void setCompresedSize(int size) throws CoreException {
        ReflectionUtils.setPrivateField(this.handle, "size", size);
    }

    @Override
    public int getPacketId() {
        return 51;
    }

    @Override
    public Object getHandle() {
        return this.handle;
    }
    
    public PacketMapChunk setHandle(Object handle) {
        this.handle = handle; return this;
    }
    
    @Override
    public String getPacketName() {
        return this.handle.getClass().getSimpleName();
    }
}