package me.FurH.Core;

import me.FurH.Core.exceptions.CoreException;
import me.FurH.Core.internals.InternalManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class CoreListener implements Listener {
    
    private boolean internals;
    
    public CoreListener(boolean internals) {
        this.internals = internals;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent e) {
        try {
            if (internals) {
                InternalManager.getEntityPlayer(e.getPlayer()).setInboundQueue();
            }
        } catch (CoreException ex) {
            ex.printStackTrace();
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent e) {
        InternalManager.removeEntityPlayer(e.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerQuit(PlayerKickEvent e) {
        InternalManager.removeEntityPlayer(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.isCancelled()) {
            try {
                
                if (!(e.getWhoClicked() instanceof Player)) {
                    return;
                }
                
                Player p = (Player) e.getWhoClicked();
                
                if (InternalManager.getEntityPlayer(p).isInventoryHidden()) {
                    e.setCancelled(true);
                }
                
            } catch (CoreException ex) {
                ex.printStackTrace();
            }
        }
    }
}