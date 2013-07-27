package me.FurH.Core.events;

import me.FurH.Core.CorePlugin;
import me.FurH.Core.util.Communicable;
import org.bukkit.event.Listener;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class CoreListener extends Communicable implements Listener {

    public CoreListener(CorePlugin plugin) {
        super(plugin);
    }
}