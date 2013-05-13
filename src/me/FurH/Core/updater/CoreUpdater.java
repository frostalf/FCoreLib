package me.FurH.Core.updater;

import java.net.URL;
import javax.xml.parsers.DocumentBuilderFactory;
import me.FurH.Core.CorePlugin;
import me.FurH.Core.exceptions.CoreException;
import me.FurH.Core.number.NumberUtils;
import me.FurH.Core.util.Communicator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class CoreUpdater {

    private String url;
    private CorePlugin plugin;

    private String currentVersion;
    private String lastestVersion;

    private boolean updateAvailable = false;

    /**
     * Creates a new CoreUpdater object, used to check for updates with the bukkit site
     * 
     * @param plugin the CorePlugin object which will handle this updater
     * @param url the bukkit url
     */
    public CoreUpdater(CorePlugin plugin, String url) {
        this.url = url;
        this.plugin = plugin;
        currentVersion = plugin.getDescription().getVersion();
    }

    /**
     * Get if is an update available
     * 
     * @return true if an update is available, false otherwise.
     */
    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    /**
     * Get the lastest version number
     * 
     * @return the laster version
     * @throws CoreException
     */
    public double getVersionNumber() throws CoreException {
        return NumberUtils.toDouble(getLastestVersion());
    }

    /**
     * Get the lastest version string, which may include letters an some other things
     * 
     * @return the lastest version string
     */
    public String getLastestVersion() {
        String version = currentVersion;

        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new URL(this.url + "files.rss").openConnection().getInputStream());
            doc.getDocumentElement().normalize();

            Node node = doc.getElementsByTagName("item").item(0);

            if (node != null && node.getNodeType() == 1) {
                lastestVersion = ((Element)node).getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue();
                version = lastestVersion;
            }

        } catch (Exception ex) {
            return version;
        }

        return version;
    }

    /**
     * Setup the CoreUpdater to start checking for updates every 12 hours
     */
    public void setup() {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                checkUpdate(); start();
            }
        }, 20L);
    }

    private void start() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                checkUpdate();
            }
        }, 43200 * 20, 43200 * 20);
    }

    private void checkUpdate() {
        try {
            double newVersion = getVersionNumber();
            double curVersion = NumberUtils.toDouble(currentVersion);

            if (curVersion < newVersion) {
                announce(null);
                updateAvailable = true;
            }

        } catch (CoreException ex) { }
    }
    
    /**
     * Announces that a new update is found
     * 
     * @param player the player used to sent the message, might be null.
     */
    public void announce(Player player) {
        Communicator com = plugin.getCommunicator();
        com.msg(player, "[TAG] New version found&8: &3{0}&f &8(&fYou have&8: &3{1}&8)", lastestVersion, currentVersion);
        com.msg(player, "&3Visit:&r " + url);
    }
}
