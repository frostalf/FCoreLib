package me.FurH.Core.packets.objects;

import me.FurH.Core.exceptions.CoreException;
import me.FurH.Core.reflection.ReflectionUtils;
import me.FurH.Core.reflection.field.IReflectField;

/**
 *
 * @author FurmigaHumana All Rights Reserved unless otherwise explicitly stated.
 */
public class PacketMapChunk implements ICorePacket {

    private static IReflectField a;
    private static IReflectField b;
    private static IReflectField c;
    private static IReflectField d;
    private static IReflectField buffer;
    private static IReflectField inflatedBuffer;
    private static IReflectField e;
    private static IReflectField size;
    
    private Object handle;

    /**
     * Creates a new Packet51MapChunk Handler
     *
     * @param packet the original Packet51MapChunk
     */
    public PacketMapChunk(Object packet) {
       
        try {

            if (a == null) {
                a = ReflectionUtils.getNewReflectField("a", packet.getClass(), false);
            }
            
            if (b == null) {
                b = ReflectionUtils.getNewReflectField("b", packet.getClass(), false);
            }
            
            if (c == null) {
                c = ReflectionUtils.getNewReflectField("c", packet.getClass(), false);
            }
            
            if (d == null) {
                d = ReflectionUtils.getNewReflectField("d", packet.getClass(), false);
            }
            
            if (buffer == null) {
                buffer = ReflectionUtils.getNewReflectField("buffer", packet.getClass(), true);
            }
            
            if (inflatedBuffer == null) {
                inflatedBuffer = ReflectionUtils.getNewReflectField("inflatedBuffer", packet.getClass(), false);
            }
            
            if (e == null) {
                e = ReflectionUtils.getNewReflectField("e", packet.getClass(), false);
            }

            if (size == null) {
                size = ReflectionUtils.getNewReflectField("size", packet.getClass(), true);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        this.handle = packet;
    }

    /**
     * Return the packet X coordinate
     *
     * Packet51MapChunk Field "a"
     * 
     * @return
     * @throws CoreException
     */
    public int getX() throws CoreException {
        return a.getInt(handle);
    }

    /**
     * Return the packet Z coordinate
     *
     * Packet51MapChunk Field "b"
     * 
     * @return
     * @throws CoreException
     */
    public int getZ() throws CoreException {
        return b.getInt(handle);
    }

    /**
     * Return the packet C mask field
     * 
     * Packet51MapChunk Field "c"
     *
     * @return
     * @throws CoreException
     */
    public int getMaskA() throws CoreException {
        return c.getInt(handle);
    }

    /**
     * Return the packet D mask field
     *
     * Packet51MapChunk Field "d"
     * 
     * @return
     * @throws CoreException
     */
    public int getMaskB() throws CoreException {
        return d.getInt(handle);
    }

    /**
     * Return the packet compressed buffer
     *
     * Packet51MapChunk Field "buffer"
     * 
     * @return
     * @throws CoreException
     */
    public byte[] getBuffer() throws CoreException {
        return buffer.getByteArray(handle);
    }

    /**
     * Set the packet compressed buffer
     *
     * Packet51MapChunk Field "buffer"
     * 
     * @param buffer0
     * @throws CoreException
     */
    public void setBuffer(byte[] buffer0) throws CoreException {
        buffer.set(buffer0, handle);
    }

    /**
     * Return the packet inflatedBuffer
     * 
     * Packet51MapChunk Field "inflatedBuffer"
     *
     * @return
     * @throws CoreException
     */
    public byte[] getInflatedBuffer() throws CoreException {
        return inflatedBuffer.getByteArray(handle);
    }

    /**
     * Set the packet inflatedBuffer
     * 
     * Packet51MapChunk Field "inflatedBuffer"
     *
     * @param inflatedBuffer0
     * @throws CoreException
     */
    public void setInflatedBuffer(byte[] inflatedBuffer0) throws CoreException {
        inflatedBuffer.set(inflatedBuffer0, handle);
    }

    /**
     * Return the packet "e" flag
     * 
     * Packet51MapChunk Field "e"
     *
     * @return
     * @throws CoreException
     */
    public boolean e() throws CoreException {
        return e.getBoolean(handle);
    }

    /**
     * Return the packet compressed size
     * 
     * Packet51MapChunk Field "size"
     *
     * @return
     * @throws CoreException
     */
    public int getCompressedSize() throws CoreException {
        return size.getInt(handle);
    }

    /**
     * Set the packet compressed size
     * 
     * Packet51MapChunk Field "size"
     *
     * @param size0
     * @throws CoreException
     */
    public void setCompresedSize(int size0) throws CoreException {
        size.set(size0, handle);
    }

    @Override
    public int getPacketId() {
        return 51;
    }

    @Override
    public Object getHandle() {
        return this.handle;
    }
    
    /**
     * Set the Packet51MapChunk object
     *
     * @param handle the new Packet51MapChunk object
     * @return this
     */
    public PacketMapChunk setHandle(Object handle) {
        this.handle = handle; return this;
    }
    
    @Override
    public String getPacketName() {
        return this.handle.getClass().getSimpleName();
    }
}