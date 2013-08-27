package me.FurH.Core;

import me.FurH.Core.database.CoreSQLDatabase;
import me.FurH.Core.gc.MemoryMonitor;
import me.FurH.Core.packets.PacketManager;
import me.FurH.Core.perm.CorePermissions;
import me.FurH.Core.perm.ICorePermissions;
import me.FurH.Core.threads.ThreadFactory;
import me.FurH.Core.tps.CyclesMonitor;
import me.FurH.Core.util.Communicator;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author FurmigaHumana
 */
public abstract class CorePlugin extends JavaPlugin {

    private static CorePlugin           handler;
    
    public CoreSQLDatabase              coredatabase;
    private Communicator                communicator;

    public  static Thread               main_thread;
    private static ICorePermissions     permissions;

    private static MemoryMonitor        monitor;
    private static CyclesMonitor        _monitor;

    public static long start            = 0;
    
    private boolean outbound            = false;
    private boolean inbound             = false;

    /**
     * Initializes a new CorePlugin Objects
     * 
     * @param tag the default chat tag to be used
     */
    public CorePlugin(String tag) {
        setup(tag, false, false);
    }
    
    /**
     * Initializes a new CorePlugin Objects
     * 
     * @param tag the default chat tag to be used
     * @param inbound whatever the plugin should or should not hook the player inbound queue 
     */
    public CorePlugin(String tag, boolean inbound) {
        setup(tag, inbound, false);
    }

    /**
     * Initializes a new CorePlugin Objects
     * 
     * @param tag the default chat tag to be used
     * @param inbound whatever the plugin should or should not hook the player inbound queue
     * @param outbound whatever the plugin should or should not hook the player outbound queue 
     */
    public CorePlugin(String tag, boolean inbound, boolean outbound) {
        setup(tag, inbound, outbound);
    }
    
    private void setup(String tag, boolean inbound, boolean outbound) {

        this.communicator = new Communicator(handler, tag);
        
        this.inbound = inbound;
        this.outbound = outbound;

    }
    
    public void onEnable(long start) {
        
        if (handler == null) {
            handler = this;
        }

        if (start == 0) {
            start = System.currentTimeMillis();
        }
        
        if (main_thread == null) {
            main_thread = Thread.currentThread();
        }
        
        if (permissions == null) {
            permissions = CorePermissions.getPermissionsBridge(this);
        }

        if (monitor == null) {
            monitor = new MemoryMonitor();
        }
        
        if (_monitor == null) {
            _monitor = new CyclesMonitor(this);
        }

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new CoreMainListener(inbound, outbound), handler);

        logEnable(System.currentTimeMillis() - start);
        
    }
    
    public void onDisable(long start) {
        
        Bukkit.getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);

        ThreadFactory.stopAll();
        MemoryMonitor.clear();
        CyclesMonitor.removeAll();
        PacketManager.clear();
        
        logDisable(System.currentTimeMillis() - start);
        
    }
    
    /**
     * Check if the player has an permission
     *
     * @param sender the player to check
     * @param node the permission node
     * @return true if the player has permission, otherwise false
     */
    public static boolean hasPermS(CommandSender sender, String node) {

        if (permissions == null) {
            return sender.hasPermission(node);
        }

        return permissions.has(sender, node);
    }
    
    /**
     * Check if the player has an permission
     *
     * @param sender the player to check
     * @param node the permission node
     * @return true if the player has permission, otherwise false
     */
    public boolean hasPerm(CommandSender sender, String node) {
        return hasPermS(sender, node);
    }
    
    /**
     * Send a message to the command sender or to a player
     * 
     * @param sender the command sender or the player
     * @param message the message to display
     * @param objects the message objects
     */
    public void msg(CommandSender sender, String message, Object...objects) {
        communicator.msg(sender, message, objects);
    }
    
    /**
     * Log the plugin enabled message with ms count
     * @param took the total ms count
     */
    private void logEnable(long took) {
        log("[TAG] {0} v{1} loaded in {2} ms!", getDescription().getName(), getDescription().getVersion(), took);
    }
    
    /**
     * Log the plugin disabled message with ms count
     * @param took the total ms count
     */
    private void logDisable(long took) {
        log("[TAG] {0} v{1} disabled in {2} ms!", getDescription().getName(), getDescription().getVersion(), took);
    }

    /**
     * Log a message as info
     * 
     * @param message the message to display
     * @param objects the message objects
     */
    public void log(String message, Object...objects) {
        communicator.log(message, objects);
    }
    
    /**
     * Handle an exception
     * 
     * @param ex the exception
     */
    public void error(Throwable ex) {
        communicator.error(ex);
    }
    
    /**
     * Handle and exception
     * 
     * @param ex the exception
     * @param message the message to display in console
     * @param objects the message objects
     */
    public void error(Throwable ex, String message, Object...objects) {
        communicator.error(ex, message, objects);
    }

    /**
     * Get the CorePlugin default Communicator
     *
     * @return the communicator
     */
    public Communicator getCommunicator() {
        return communicator;
    }
    
    /**
     * Get the ICorePermissions bridge
     *
     * @return the ICorePermissions objects, might be null.
     */
    public static ICorePermissions getPermissions() {
        return permissions;
    }
    
    public static MemoryMonitor getMemoryMonitor() {
        return monitor;
    }
    
    public static CyclesMonitor getCyclesMonitor() {
        return _monitor;
    }
    
    public static CorePlugin getHandler() {
        return handler;
    }

    public ClassLoader _getClassLoader() {
        return super.getClassLoader();
    }
    
    public static boolean _validate() {
        return true;
    }
    
    public static boolean isMainThread() {
        return main_thread != null && Thread.currentThread() == main_thread;
    }
}