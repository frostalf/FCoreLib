package me.FurH.Core.ip;

import java.lang.ref.SoftReference;
import java.util.regex.Pattern;
import org.bukkit.entity.Player;

/**
 *
 * @author FurmigaHumana
 */
public class IpUtils {

    private static SoftReference<Pattern> IPv4;
    private static SoftReference<Pattern> IPv6;

    /**
     * Check if the string is a valid IP address, IPv4 or IPv6
     *
     * @param ip the string to check
     * @return true if it is a IPv4 or IPv6, false otherwise.
     */
    public static boolean isAnyIp(String ip) {
        return isIPv4(ip) || isIPv6(ip);
    }

    /**
     * Get if the given string is an IPv4 address
     * 
     * @param ip the IP string
     * @return true if the string is an IPv4 address, false otherwise.
     */
    public static boolean isIPv4(String ip) {

        if (IPv4 == null || IPv4.get() == null) {
            IPv4 = new SoftReference<Pattern>(Pattern.compile
                    ("((\\d{1,3}(?:\\.\\d{1,3}){3}(?::\\d{1,5})?)|(\\d{1,3}(?:\\,\\d{1,3}){3}(?::\\d{1,5})?)|(\\d{1,3}(?:\\-\\d{1,3}){3}(?::\\d{1,5})?)|(\\d{1,3}(?: \\d{1,3}){3}(?::\\d{1,5})?))"));
        }

        return IPv4.get().matcher(ip).matches();
    }
    
    /**
     * Get if the given string is an IPv6 address
     * 
     * @param ip the IP string
     * @return true if the string is an IPv6 address, false otherwise.
     */
    public static boolean isIPv6(String ip) {
        
        if (IPv6 == null || IPv6.get() == null) {
            IPv6 = new SoftReference<Pattern>(Pattern.compile("(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}"));
        }

        return IPv6.get().matcher(ip).matches();
    }
    
    /**
     * Replace the . of the IP to - to allow better database compatibility
     * 
     * @param ip the IP to replace
     * @return the IP with the dots replaced
     */
    public static String getIpFromDb(String ip) {
        return ip.replaceAll("-", ".");
    }
    
    /**
     * Replace the - of the IP to . to retrieve the original IP after the usage of the @getIpFromDb method
     * 
     * @param ip the IP to restore
     * @return the IP with the dots restored
     */
    public static String getIpToDb(String ip) {
        return ip.replaceAll("\\.", "-");
    }

    /**
     * Get the decimal representation of the given IP
     * 
     * @param ip the IP address
     * @return the decimal representation of the IP
     */
    public static long ipToDecimal(String ip) {
        long result = 0;
        String[] split = ip.split("\\.");

        for (int i = 3; i >= 0; i--) {
            result |= (Long.parseLong(split[3 - i]) << (i * 8));
        }

        return result & 0xFFFFFFFF;
    }

    /**
     * Get the IP address from the decimal representation
     * 
     * @param ip the decimal IP
     * @return the IP address
     */
    public static String decimalToIp(long ip) {
        StringBuilder sb = new StringBuilder(15);

        for (int i = 0; i < 4; i++) {
            sb.insert(0, Long.toString(ip & 0xff));

            if (i < 3) {
                sb.insert(0, '.');
            }

            ip >>= 8;
        }

        return sb.toString();
    }
    
    /**
     * Get the player IP address
     * 
     * @param player the player to get the IP
     * @return the IP address without ports
     */
    public static String getPlayerIp(Player player) {
        return player.getAddress().toString().substring(1, player.getAddress().toString().indexOf(':'));
    }
}
