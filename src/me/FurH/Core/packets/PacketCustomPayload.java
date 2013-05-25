package me.FurH.Core.packets;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class PacketCustomPayload {

    private String   channel;
    private byte[]   data;
    private int      length;
    
    /**
     * Set this custom payload channel
     *
     * @param channel the channel to set
     */
    public void setChannel(String channel) {
        this.channel = channel;
    }
    
    /**
     * Get this custom payload channel
     *
     * @return the channel
     */
    public String getChannel() {
        return this.channel;
    }

    /**
     * Set this custom payload data
     *
     * @param data the data to set
     */
    public void setData(byte[] data) {
        this.data = data;
    }
    
    /**
     * Get this custom payload data
     *
     * @return the data
     */
    public byte[] getData() {
        return this.data;
    }

    /**
     * Set this custom payload length
     *
     * @param length the length to set
     */
    public void setLength(int length) {
        this.length = length;
    }
    
    /**
     * Get this custom payload length
     *
     * @return this custom payload length
     */
    public int getLength() {
        return this.length;
    }
}
