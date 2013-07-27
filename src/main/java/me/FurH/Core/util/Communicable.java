package me.FurH.Core.util;

import me.FurH.Core.CorePlugin;
import org.bukkit.command.CommandSender;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class Communicable {

    public CorePlugin plugin;
    public Communicator com;

    public Communicable(CorePlugin plugin) {
        this.com = plugin.getCommunicator();
        this.plugin = plugin;
    }

    public void msg(CommandSender sender, String message, Object...objects) {
        com.msg(sender, message, objects);
    }

    public void log(String message, Object...objects) {
        com.log(message, objects);
    }

    public void error(Throwable ex) {
        com.error(ex);
    }

    public void error(Throwable ex, String message, Object...objects) {
        com.error(ex, message, objects);
    }
}