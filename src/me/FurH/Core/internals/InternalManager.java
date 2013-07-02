package me.FurH.Core.internals;

import me.FurH.Core.cache.CoreSafeCache;
import me.FurH.Core.exceptions.CoreException;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_6_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class InternalManager extends ClassLoader {
    
    private static CoreSafeCache<String, IEntityPlayer> entities = new CoreSafeCache<String, IEntityPlayer>();
    private static String version = null;

    /**
     * Get the IEntityPlayer Object for the given Player
     *
     * @param player the Bukkit Player
     * @return the IEntityPlayer object
     * @throws CoreException  
     */
    public static IEntityPlayer getEntityPlayer(Player player) throws CoreException {
        
        if (version == null) {
            String pkg = Bukkit.getServer().getClass().getPackage().getName();
            version = pkg.substring(pkg.lastIndexOf('.') + 1);
        }

        if (entities.containsKey(player.getName())) {
            return entities.get(player.getName());
        }

        IEntityPlayer entity = null;

        if (isMcPcPlusEnabled(player)) {
            entity = new MCPCEntityPlayer();
        } else if (isNettyEnabled()) {
            entity = new SpigotEntityPlayer();
        } else {
            entity = new BukkitEntityPlayer();
        }

        entity.setEntityPlayer(player);
        entities.put(player.getName(), entity);

        return entity;
    }
    
    /**
     * Remove the cached IEntityPlayer object
     *
     * @param player the player linked to the IEntityPlayer object
     */
    public static void removeEntityPlayer(Player player) {
        entities.remove(player.getName());
    }

    private static boolean isNettyEnabled() {
        try {
            Class.forName("org.spigotmc.netty.NettyNetworkManager");
            return true;
        } catch (NoClassDefFoundError ex) {
            return false;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }
    
    private static boolean isMcPcPlusEnabled(Player player) {
        return ((CraftPlayer) player).getHandle().playerConnection.networkManager.getClass().getSimpleName().equals("TcpConnection");
    }
}