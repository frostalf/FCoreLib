package me.FurH.Core.location;

import me.FurH.Core.exceptions.CoreException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 *
 * @author FurmigaHumana
 */
public class LocationUtils {

    /**
     * Transform the given arguments into a location string in the X:Z:Y format
     * 
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return the transformed string
     */
    public static String getKeyFromLocation(int x, int y, int z) {
        return x + ":" + z + ":" + y;
    }
    
    /**
     * Transform the given arguments into a location string in the X:Z:Y:WORLD format
     * 
     * @param world the world name
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return the transformed string
     */
    public static String getKeyFromLocation(String world, int x, int y, int z) {
        return x + ":" + z + ":" + y + ":" + world;
    }
    
    /**
     * Parse the location from the given string, this string must be in the X:Z:Y:WORLD format
     * 
     * @param location the string to be parsed
     * @return the parsed location, or null if the world does not exists
     * @throws CoreException 
     */
    public static Location getLocationFromKey(String location) throws CoreException {
        
        String[] split = location.split(":");
        
        try {

            World world = Bukkit.getWorld(split[ 3 ]);

            if (world != null) {
                return new Location(world, Integer.parseInt(split[ 0 ]), Integer.parseInt(split[ 2 ]), Integer.parseInt(split[ 1 ]));
            }

        } catch (Exception ex) {
            throw new CoreException(ex, location + " is not a valid location key!");
        }

        return null;
    }
    
    /**
     * Parse the location from the given string, this string must be in the X:Z:Y format
     * 
     * @param world the world name be used on this string, must be a valid world
     * @param location the string to be parsed
     * @return the parsed location, or null if the world does not exists
     * @throws CoreException 
     */
    public static Location getLocationFromKey(String world, String location) throws CoreException {
        
        String[] split = location.split(":");
        
        try {

            World w = Bukkit.getWorld(world);

            if (w != null) {
                return new Location(w, Integer.parseInt(split[ 0 ]), Integer.parseInt(split[ 2 ]), Integer.parseInt(split[ 1 ]));
            }

        } catch (Exception ex) {
            throw new CoreException(ex, location + " is not a valid location key!");
        }

        return null;
    }
    
    /**
     * Transform the given location into a location string in the WORLD:X:Y:Z format
     * 
     * shortcut to {@link #getStringFromLocation(java.lang.String, int, int, int) }
     * 
     * @param loc the location to be transformed
     * @return the transformed string
     */
    public static String getStringFromLocation(Location loc) {
        return getStringFromLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
    
    /**
     * Transform the given arguments into a location string in the WORLD:X:Y:Z format
     * 
     * shortcut to {@link #getStringFromLocation(java.lang.String, int, int, int) }
     * 
     * @param world the world name
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return the transformed string
     */
    public static String getStringFromLocation(String world, double x, double y, double z) {
        return getStringFromLocation(world, (int) x, (int) y, (int) z);
    }

    /**
     * Transform the given arguments into a location string in the WORLD:X:Y:Z format
     * 
     * @param world the world name
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return the transformed string
     */
    public static String getStringFromLocation(String world, int x, int y, int z) {
        return world + ":" + x + ":" + y + ":" + z;
    }
    
    /**
     * Parse the location from the given string, this string must be in the WORLD:X:Y:Z format
     * 
     * @param location the string to be parsed
     * @return the parsed location, or null if the world does not exists
     * @throws CoreException 
     */
    public static Location getLocationFromString(String location) throws CoreException {
        
        String[] split = location.split(":");
        
        try {

            World world = Bukkit.getWorld(split[ 0 ]);

            if (world != null) {
                return new Location(world, Integer.parseInt(split[ 1 ]), Integer.parseInt(split[ 2 ]), Integer.parseInt(split[ 3 ]));
            }

        } catch (Exception ex) {
            throw new CoreException(ex, location + " is not a valid location!");
        }

        return null;
    }

    /**
     * Transform the given location into a position string in the WORLD:X:Y:Z:YAW:PITCH format
     * 
     * shortcut to {@link #getStringFromPosition(java.lang.String, double, double, double, float, float) }
     * 
     * @param loc the location to be transformed
     * @return the transformed string
     */
    public static String getStringFromPosition(Location loc) {
        return getStringFromPosition(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }
    
    /**
     * Transform the given arguments into a position string in the WORLD:X:Y:Z:YAW:PITCH format
     * 
     * @param world the world name
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @param yaw the yaw
     * @param pitch the pitch
     * @return the transformed string
     */
    public static String getStringFromPosition(String world, double x, double y, double z, float yaw, float pitch) {
        return world + ":" + x + ":" + y + ":" + z + ":" + yaw + ":" + pitch;
    }
    
    /**
     * Parse the position from the given string, this string must be in the WORLD:X:Y:Z:YAW:PITCH format
     * 
     * @param location the string to be parsed
     * @return the parsed location, or null if the world does not exists
     * @throws CoreException 
     */
    public static Location getPositionFromString(String location) throws CoreException {
        
        String[] split = location.split(":");
        
        try {

            World world = Bukkit.getWorld(split[ 0 ]);

            if (world != null) {
                return new Location(world, Double.parseDouble(split[ 1 ]), Double.parseDouble(split[ 2 ]), Double.parseDouble(split[ 3 ]), Float.parseFloat(split[ 4 ]), Float.parseFloat(split[ 5 ]));
            }

        } catch (Exception ex) {
            throw new CoreException(ex, location + " is not a valid position!");
        }

        return null;
    }

    @Deprecated
    public static String positionToString2(Location loc) {
        return positionToString2(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    @Deprecated
    public static String positionToString2(String world, double x, double y, double z, float yaw, float pitch) {
        return world + ":" + x + ":" + y + ":" + z + ":" + yaw + ":" + pitch;
    }

    @Deprecated
    public static String positionToString(Location loc) {
        return positionToString(loc.getX(), loc.getZ(), loc.getY(), loc.getYaw(), loc.getPitch(), loc.getWorld().getName());
    }
    
    @Deprecated
    public static String positionToString(double x, double z, double y, float yaw, float pitch, String world) {
        return x + ":" + z + ":" + y + ":" + yaw + ":" + pitch + ":" + world;
    }
    
    @Deprecated
    public static String locationToString2(Location loc) {
        return locationToString2(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
    
    @Deprecated
    public static String locationToString2(String world, int x, int y, int z) {
        return world + ":" + x + ":" + y + ":" + z;
    }
    
    @Deprecated
    public static String locationToString(Location loc) {
        return locationToString(loc.getBlockX(), loc.getBlockZ(), loc.getBlockY(), loc.getWorld().getName());
    }
    
    @Deprecated
    public static String locationToString(int x, int z, int y, String world) {
        return x + ":" + z + ":" + y + ":" + world;
    }

    @Deprecated
    public static Location stringToPosition2(String string) throws CoreException {

        try {
            
            String[] split = string.split(":");
            World w = Bukkit.getWorld(split[0]);

            if (w != null) {
                return new Location(w, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
            }

        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to parse the position string: " + string);
        }

        return null;
    }
    
    @Deprecated
    public static Location stringToPosition(String string) throws CoreException {

        try {

            String[] split = string.split(":");
            World w = Bukkit.getWorld(split[5]);

            if (w != null) {
                return new Location(w, Double.parseDouble(split[0]), Double.parseDouble(split[2]), Double.parseDouble(split[1]), Float.parseFloat(split[3]), Float.parseFloat(split[4]));
            }

        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to parse the position string: " + string);
        }

        return null;
    }

    @Deprecated
    public static Location stringToLocation(String string) throws CoreException {

        try {

            String[] split = string.split(":");
            World w = Bukkit.getWorld(split[3]);

            if (w != null) {
                return new Location(w, Double.parseDouble(split[0]), Double.parseDouble(split[2]), Double.parseDouble(split[1]));
            }

        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to parse the location string: " + string);
        }

        return null;
    }
    
    @Deprecated
    public static Location stringToLocation2(String string) throws CoreException {

        try {

            String[] split = string.split(":");
            World w = Bukkit.getWorld(split[0]);

            if (w != null) {
                return new Location(w, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
            }

        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to parse the location string: " + string);
        }

        return null;
    }
}