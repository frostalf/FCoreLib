package me.FurH.Core.command;

import me.FurH.Core.CorePlugin;
import me.FurH.Core.util.Communicable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class CoreCommand extends Communicable implements CommandExecutor {

    public CoreCommand(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }
}
