package me.FurH.Core.internals;

import java.util.regex.Pattern;
import me.FurH.Core.cache.CoreSafeCache;
import me.FurH.Core.exceptions.CoreException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class InternalManager extends ClassLoader {
    
    private static final CoreSafeCache<String, Integer> packets = new CoreSafeCache<String, Integer>();
    
    private static CoreSafeCache<String, IEntityPlayer> entities = new CoreSafeCache<String, IEntityPlayer>();
    private static String version = null;

    private static final Pattern brand = Pattern.compile("v[0-9][_.][0-9][_.][R0-9]*"); // would be easier just to check if starts with v1, but I like to be fancy :3
    private static JavaPlugin plugin;
    
    public static void setup(JavaPlugin plugin) {

        InternalManager.plugin = plugin;

        Plugin protocol = Bukkit.getPluginManager().getPlugin("ProtocolLib");
        if (protocol == null || !protocol.isEnabled()) {
            Thread.dumpStack(); // Anoy, so people can se it.
            System.out.println("You must have ProtocolLib installed to use with Spigot!");
        }
    }

    static {
        
        String pkg = Bukkit.getServer().getClass().getPackage().getName();
        String version0 = pkg.substring(pkg.lastIndexOf('.') + 1);

        if (!brand.matcher(version0).matches()) {
            version0 = "";
        }

        version = version0;

        // Match Regex:
        //    a\(([0-9]*), ((true)|(false)), ((true)|(false)), (\w*).class\);

        // Replace Regex:
        // packets.put\("$8", $1\);
        
        packets.put("Packet0KeepAlive", 0);
        packets.put("Packet1Login", 1);
        packets.put("Packet2Handshake", 2);
        packets.put("Packet3Chat", 3);
        packets.put("Packet4UpdateTime", 4);
        packets.put("Packet5EntityEquipment", 5);
        packets.put("Packet6SpawnPosition", 6);
        packets.put("Packet7UseEntity", 7);
        packets.put("Packet8UpdateHealth", 8);
        packets.put("Packet9Respawn", 9);
        packets.put("Packet10Flying", 10);
        packets.put("Packet11PlayerPosition", 11);
        packets.put("Packet12PlayerLook", 12);
        packets.put("Packet13PlayerLookMove", 13);
        packets.put("Packet14BlockDig", 14);
        packets.put("Packet15Place", 15);
        packets.put("Packet16BlockItemSwitch", 16);
        packets.put("Packet17EntityLocationAction", 17);
        packets.put("Packet18ArmAnimation", 18);
        packets.put("Packet19EntityAction", 19);
        packets.put("Packet20NamedEntitySpawn", 20);
        packets.put("Packet22Collect", 22);
        packets.put("Packet23VehicleSpawn", 23);
        packets.put("Packet24MobSpawn", 24);
        packets.put("Packet25EntityPainting", 25);
        packets.put("Packet26AddExpOrb", 26);
        packets.put("Packet28EntityVelocity", 28);
        packets.put("Packet29DestroyEntity", 29);
        packets.put("Packet30Entity", 30);
        packets.put("Packet31RelEntityMove", 31);
        packets.put("Packet32EntityLook", 32);
        packets.put("Packet33RelEntityMoveLook", 33);
        packets.put("Packet34EntityTeleport", 34);
        packets.put("Packet35EntityHeadRotation", 35);
        packets.put("Packet38EntityStatus", 38);
        packets.put("Packet39AttachEntity", 39);
        packets.put("Packet40EntityMetadata", 40);
        packets.put("Packet41MobEffect", 41);
        packets.put("Packet42RemoveMobEffect", 42);
        packets.put("Packet43SetExperience", 43);
        packets.put("Packet51MapChunk", 51);
        packets.put("Packet52MultiBlockChange", 52);
        packets.put("Packet53BlockChange", 53);
        packets.put("Packet54PlayNoteBlock", 54);
        packets.put("Packet55BlockBreakAnimation", 55);
        packets.put("Packet56MapChunkBulk", 56);
        packets.put("Packet60Explosion", 60);
        packets.put("Packet61WorldEvent", 61);
        packets.put("Packet62NamedSoundEffect", 62);
        packets.put("Packet63WorldParticles", 63);
        packets.put("Packet70Bed", 70);
        packets.put("Packet71Weather", 71);
        packets.put("Packet100OpenWindow", 100);
        packets.put("Packet101CloseWindow", 101);
        packets.put("Packet102WindowClick", 102);
        packets.put("Packet103SetSlot", 103);
        packets.put("Packet104WindowItems", 104);
        packets.put("Packet105CraftProgressBar", 105);
        packets.put("Packet106Transaction", 106);
        packets.put("Packet107SetCreativeSlot", 107);
        packets.put("Packet108ButtonClick", 108);
        packets.put("Packet130UpdateSign", 130);
        packets.put("Packet131ItemData", 131);
        packets.put("Packet132TileEntityData", 132);
        packets.put("Packet200Statistic", 200);
        packets.put("Packet201PlayerInfo", 201);
        packets.put("Packet202Abilities", 202);
        packets.put("Packet203TabComplete", 203);
        packets.put("Packet204LocaleAndViewDistance", 204);
        packets.put("Packet205ClientCommand", 205);
        packets.put("Packet206SetScoreboardObjective", 206);
        packets.put("Packet207SetScoreboardScore", 207);
        packets.put("Packet208SetScoreboardDisplayObjective", 208);
        packets.put("Packet209SetScoreboardTeam", 209);
        packets.put("Packet250CustomPayload", 250);
        packets.put("Packet252KeyResponse", 252);
        packets.put("Packet253KeyRequest", 253);
        packets.put("Packet254GetInfo", 254);
        packets.put("Packet255KickDisconnect", 255);

    }
    
    public static int getPacketId(Object packet) {
        return getPacketId(packet.getClass().getSimpleName());
    }
    
    public static int getPacketId(String packet) {

        if (packets.containsKey(packet)) {
            return packets.get(packet);
        }

        return 0;
    }

    public static String getServerVersion() {
        return !"".equals(version) ? version + "." : "";
    }

    /**
     * Get the IEntityPlayer Object for the given Player
     *
     * @param player the Bukkit Player
     * @return the IEntityPlayer object
     * @throws CoreException  
     */
    public static IEntityPlayer getEntityPlayer(Player player) throws CoreException {
        
        if (entities.containsKey(player.getName())) {
            return entities.get(player.getName());
        }

        IEntityPlayer entity = null;

        if (isMcPcPlusEnabled()) {
            entity = new MCPCEntityPlayer();
        } else if (isNettyEnabled()) {
            entity = new ProtocolEntityPlayer(plugin);
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
        return Bukkit.getVersion().toLowerCase().contains("spigot");
    }
    
    private static boolean isMcPcPlusEnabled() {
        return Bukkit.getVersion().toLowerCase().contains("mcpc");
    }
}