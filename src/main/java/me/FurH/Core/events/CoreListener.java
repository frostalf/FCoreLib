package me.FurH.Core.events;

import me.FurH.Core.CorePlugin;
import me.FurH.Core.util.Communicator;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class CoreListener implements Listener {

    public Communicator com;
    
    public CoreListener(CorePlugin plugin) {
        this.com = plugin.getCommunicator();
    }
    
    public void msg(CommandSender sender, String message, Object...objects) {
        
        if (!com.validate()) {
            return;
        }
            
        com.msg(sender, message, objects);
    }

    public void log(String message, Object...objects) {
        
        if (!com.validate()) {
            return;
        }
        
        com.log(message, objects);
    }

    public void error(Throwable ex) {

        if (!com.validate()) {
            return;
        }
        
        com.error(ex);
    }

    public void error(Throwable ex, String message, Object...objects) {

        if (!com.validate()) {
            return;
        }
        
        com.error(ex, message, objects);
    }
}