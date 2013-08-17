package me.FurH.Core.command;

import me.FurH.Core.CorePlugin;
import me.FurH.Core.util.Communicator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class CoreCommand implements CommandExecutor {

    public Communicator com;

    public CoreCommand(CorePlugin plugin) {
        this.com = plugin.getCommunicator();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
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