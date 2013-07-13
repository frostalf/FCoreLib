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

    /**
     * Creates a new PacketMapChunkBulk implementation using reflection, this class will only be used when there is no nms implementation available.
     *
     * @param packet the original Packet56MapChunkBulk
     */
    public PacketMapChunkBulk(Object packet) {

        this.handle = packet;

        try {

            if (c == null) {
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
    
    /**
     * Return the 'c' field value
     *
     * @return an integer array with all the x coordinates
     * @throws CoreException
     */
    public int[] getX() throws CoreException {
        return c.getIntArray(handle);
    }
    
    /**
     * Return the 'd' field value
     *
     * @return an integer array with all the z coordinates
     * @throws CoreException
     */
    public int[] getZ() throws CoreException {
        return d.getIntArray(handle);
    }
    
    /**
     * Return the 'a' field value
     *
     * @return an integer array with all sort of things
     * @throws CoreException
     */
    public int[] getMaskA() throws CoreException {
        return a.getIntArray(handle);
    }
    
    /**
     * Return the 'b' field value
     *
     * @return an integer array with all sort of things
     * @throws CoreException
     */
    public int[] getMaskB() throws CoreException {
        return b.getIntArray(handle);
    }
    
    /**
     * Return the 'buffer' field value
     *
     * @return a byte array with the compressed? buffer
     * @throws CoreException
     */
    public byte[] getBuffer() throws CoreException {
        return buffer.getByteArray(handle);
    }
    
    /**
     * Set the 'buffer' field value
     * 
     * @param buffer0 the compressed byte array to be set
     * @throws CoreException
     */
    public void setBuffer(byte[] buffer0) throws CoreException {
        buffer.set(buffer0, handle);
    }
    
    /**
     * Return the 'inflatedBuffers' field value
     *
     * @return a 2d byte array with the uncompressed buffer
     * @throws CoreException
     */
    public byte[][] getInflatedBuffers() throws CoreException {
        return inflatedBuffers.getDoubleByteArray(handle);
    }
    
    /**
     * Set the 'inflatedBuffers' field value
     *
     * @param inflatedBuffers0 the uncompressed buffer
     * @throws CoreException
     */
    public void setInflatedBuffers(byte[][] inflatedBuffers0) throws CoreException {
        inflatedBuffers.set(inflatedBuffers0, handle);
    }
    
    /**
     * Return the 'size' field value
     *
     * @return the integer value of the size of the compressed buffer
     * @throws CoreException
     */
    public int getCompressedSize() throws CoreException {
        return size.getInt(handle);
    }
    
    /**
     * Set the 'size' field value
     *
     * @param size0 the size of the compressed buffer
     * @throws CoreException
     */
    public void setCompressedSize(int size0) throws CoreException {
        size.set(size0, handle);
    }
    
    /**
     * Return the 'h' field value
     *
     * @return a boolean value of something
     * @throws CoreException
     */
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

    /**
     * Set the Packet56MapChunkBulk object
     *
     * @param handle the new Packet56MapChunkBulk object
     * @return this
     */
    public PacketMapChunkBulk setHandle(Object handle) {
        this.handle = handle; return this;
    }
    
    @Override
    public String getPacketName() {
        return this.handle.getClass().getSimpleName();
    }
}