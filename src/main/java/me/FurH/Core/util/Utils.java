package me.FurH.Core.util;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.regex.Pattern;
import me.FurH.Core.CorePlugin;
import me.FurH.Core.exceptions.CoreException;
import org.bukkit.Chunk;
import org.bukkit.Location;

/**
 *
 * @author FurmigaHumana
 */
public class Utils {

    private static SoftReference<Pattern> pattern;

    /**
     * Generate a unique long key for this chunk
     *
     * @param chunk the chunk to generate the key
     * @return the chunk key
     */
    public static long chunkKey(Chunk chunk) {
        return chunkKey((chunk.getX() << 4) >> 4, (chunk.getZ() << 4) >> 4);
    }

    /**
     * 
     * Generate a unique long key for the chunk at the given location
     *
     * @param loc the location to generate the key
     * @return the chunk key
     */
    public static long chunkKey(Location loc) {
        return chunkKey(loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
    }

    /**
     * Generate a unique long key for the given chunk coordinate
     * 
     * This key is supposed to be unique, tested with 10.000.000 random coordinates with 0 collisions.
     *
     * @param x the chunk X coordinate
     * @param z the chunk Z coordinate
     * @return a unique key for this chunk
     */
    public static long chunkKey(int x, int z) {

        long key = ((((long) x) & 0xFFFF0000L) << 16 ) | ((((long) x) & 0x0000FFFFL));
        key     |= ((((long) z) & 0xFFFF0000L) << 32 ) | ((((long) z) & 0x0000FFFFL) << 16);

        return key;
    }

    /**
     * Return the X position from the generated chunk key
     * 
     * @param key the chunk key
     * @return the X position
     */
    public static int chunkKeyX(long key) {
        return (int) ((( key >> 16 ) & 0xFFFF0000) | (key & 0x0000FFFF));
    }

    /**
     * Return the Z position from the generated chunk key
     *
     * @param k the chunk key
     * @return the Z position
     */
    public static int chunkKeyZ(long k) {
        return (int) (((k >> 32) & 0xFFFF0000L) | ((k >> 16 ) & 0x0000FFFF));
    }

    /**
     * Get the server ping based on the java host network
     * 
     * @param address the server address to ping, it must include the ip and port in this format: IP:PORT, the ip might be numeric or not
     * @return the ping in milliseconds, -1 if the process fails
     * @throws CoreException
     */
    public static long pingServer(String address) throws CoreException {
        long ping = -1;
        
        try {

            if (!address.contains(":")) {
                throw new CoreException("Wrong usage, port is required! Eg: 127.0.0.1:80");
            }

            String[] hostSplit = address.split(":");
            
            String host = hostSplit[0];
            int port = 80;
            
            try {
                port = Integer.parseInt(hostSplit[1]);
            } catch (Exception ex) {
                throw new CoreException(ex, hostSplit[1] + " is not a valid number!");
            }

            InetAddress addr = InetAddress.getByName(host);

            ping = ping(new InetSocketAddress(addr, port));

        } catch (Exception ex) {
            throw new CoreException(ex, "Error on server ping!");
        }

        return ping;
    }

    /**
     * Get the server ping based on the java host network, this is not a valid test an may or may not give the right result.
     * 
     * @param address the server address to ping, it must be a valid InetSocketAddress
     * @return the ping in milliseconds, -1 if the process fails
     * @throws CoreException
     */
    public static long ping(InetSocketAddress address) throws CoreException {
        long ping = -1;
        
        Socket socket = null;
        
        try {
            
            socket = new Socket();
            
            socket.setKeepAlive(false);
            
            long start = System.currentTimeMillis();
            socket.connect(address, 10000);
            ping = (System.currentTimeMillis() - start);

        } catch (IOException ex) {
            throw new CoreException(ex, "Error on server ping!");
        } finally {
            if (socket != null) {
                try {
                    socket.shutdownInput();
                } catch (Throwable ex) { }
                try {
                    socket.shutdownOutput();
                } catch (Throwable ex) { }
                try {
                    socket.close();
                } catch (Throwable ex) { }
            }
        }

        return ping;
    }

    /**
     * Get if the string is in the email format
     *
     * @param email The string to be checked
     * @return true if the string is in the email format, otherwise false
     */
    public static boolean isValidEmail(String email) {

        if (pattern == null || pattern.get() == null) {
            pattern = new SoftReference<>(Pattern.compile(".+@.+\\.[a-z]+"));
        }

        return pattern.get().matcher(email).matches();
    }

    /**
     * Get the formated server current uptime
     *
     * @return the server uptime in this format: {days}d {hours}h {minutes}m {seconds}s
     */
    public static String getServerUptime() {
        
        long time = (System.currentTimeMillis() - CorePlugin.start);
        
        return (int)(time / 86400000) + "d " + (int)(time / 3600000 % 24) + "h " + (int)(time / 60000 % 60) + "m " + (int)(time / 1000 % 60) + "s";
    }
    
    /**
     * Draw a simple string based progress bar
     *
     * @param size the total size of the progress bar
     * @param progress the current progress
     * @return the progress bar string using || for completed and '  ' for not completed.
     */
    public String drawProgressBar(int size, int progress) {
        StringBuilder sb = new StringBuilder();

        String empty = "";
        String done = "";

        for (int k = 0; k < (((size) / 2) / 100); k++) {
            empty += " ";
            done += "||";
        }

        for (int k = 0; k < 50; k++) {
            sb.append((((progress / 2) <= k) ? empty : done));
        }

        return sb.toString();
    }

    /**
     *Get a formated string from the current bytes value
     * 
     * @param bytes the bytes to be formated
     * @return the formated string value based on the @param bytes
     */
    public static String getFormatedBytes(double bytes) {
        
        DecimalFormat decimal = new DecimalFormat("#.##");
        
        if (bytes >= 1099511627776.0D) {
            return new StringBuilder().append(decimal.format(bytes / 1099511627776.0D)).append(" TB").toString();
        } else
        if (bytes >= 1073741824.0D) {
            return new StringBuilder().append(decimal.format(bytes / 1073741824.0D)).append(" GB").toString();
        } else
        if (bytes >= 1048576.0D) {
            return new StringBuilder().append(decimal.format(bytes / 1048576.0D)).append(" MB").toString();
        } else
        if (bytes >= 1024.0D) {
            return new StringBuilder().append(decimal.format(bytes / 1024.0D)).append(" KB").toString();
        }
        
        return new StringBuilder().append("").append((int)bytes).append(" bytes").toString();
    }

    /**
     * Split the string from the end to the start with the given max length.
     * 
     * E.g:
     * String message = "a big string with lots of things written"
     * if @param max = 27 the result will be "...with lots of things written"
     *
     * @param message the message to split
     * @param max the max length
     * @return the splitted string
     */
    public static String substring(String message, int max) {

        int size = message.length();
        int oversize = 0;

        if (size > max) {
            oversize = size - max;
        }

        if (oversize < 0) {
            oversize = 0;
        }

        return size > max ? "..." + message.substring(oversize, size) : message;
    }
}