package me.FurH.Core.internals;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class PacketCustomPayload {

    private String   channel;
    private byte[]   data;
    private int      length;
    
    public void setChannel(String channel) {
        this.channel = channel;
    }
    
    public String getChannel() {
        return this.channel;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    
    public byte[] getData() {
        return this.data;
    }

    public void setLength(int length) {
        this.length = length;
    }
    
    public int getLength() {
        return this.length;
    }
}
