 package me.FurH.Core.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.List;
import me.FurH.Core.CorePlugin;
import me.FurH.Core.cache.CoreSafeCache;
import me.FurH.Core.exceptions.CoreException;
import me.FurH.Core.file.FileUtils;
import me.FurH.Core.list.CollectionUtils;
import me.FurH.Core.object.ObjectUtils;
import me.FurH.Core.util.Communicator;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;

/**
 *
 * @author FurmigaHumana
 */
public class Configuration {

    private CoreSafeCache<String, ConfigLoader> yamlcache = new CoreSafeCache<String, ConfigLoader>(true);

    private String default_setting = "settings.yml";
    private String default_world = "world.yml";
    private String default_message = "messages.yml";

    private HashSet<String> update_required = new HashSet<String>();
    private boolean single_config = false;

    private SoftReference<ConfigLoader> settings;
    private SoftReference<ConfigLoader> messages;

    /**
     * The CorePlugin object used to handle this Configuration
     */
    protected CorePlugin plugin;
    private ConfigUpdater updater;

    /**
     * Creates a new Configuration object
     * 
     * @param plugin the CorePlugin used to handle this Configuration
     */
    public Configuration(CorePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Set the single configuration mode
     * 
     * @param single_config1 the single configuration mod
     */
    public void setSingleConfig(boolean single_config1) {
        single_config = single_config1;
    }

    /**
     * Update all the files that need to be updated
     */
    public void updateConfig() {

        for (String dir : update_required) {
            File file = new File(dir);
            updateLines(file, getInputStream(file));
        }

        update_required.clear(); unload();
    }
    
    /**
     * Unload all loaded configurations
     */
    public void unload() {
        yamlcache.clear();
        
        settings = null;
        messages = null;
    }

    /**
     * Return an boolean based on the given path
     *
     * @param w the World
     * @param node the configuration node
     * @return the boolean value
     */
    protected boolean getBoolean(World w, String node) {
        return ObjectUtils.toBoolean(get(getWorldFile(w), node));
    }
    
    /**
     * Return an boolean based on the given path
     *
     * @param node the configuration node
     * @return the boolean value
     */
    protected boolean getBoolean(String node) {
        return ObjectUtils.toBoolean(get(getSettingsFile(), node));
    }
    
    /**
     * Return an integer based on the given path
     *
     * @param w the World
     * @param node the configuration node
     * @return the integer value
     */
    protected int getInteger(World w, String node) {
        return ObjectUtils.toInteger(get(getWorldFile(w), node));
    }

    /**
     * Return an integer based on the given path
     *
     * @param node the configuration node
     * @return the integer value
     */
    protected int getInteger(String node) {
        return ObjectUtils.toInteger(get(getSettingsFile(), node));
    }
    
    /**
     * Return a double based on the given path
     *
     * @param w the World
     * @param node the configuration node
     * @return the double value
     */
    protected double getDouble(World w, String node) {
        return ObjectUtils.toDouble(get(getWorldFile(w), node));
    }

    /**
     * Return a double based on the given path
     *
     * @param node the configuration node
     * @return the double value
     */
    protected double getDouble(String node) {
        return ObjectUtils.toDouble(get(getSettingsFile(), node));
    }

    /**
     * Return a string based on the given path
     *
     * @param node the configuration node
     * @return the string value
     */
    public String getString(String node) {
        return ObjectUtils.toString(get(getSettingsFile(), node));
    }

    /**
     * Return a string based on the given path
     *
     * @param w the World
     * @param node the configuration node
     * @return the string value
     */
    public String getString(World w, String node) {
        return ObjectUtils.toString(get(getWorldFile(w), node));
    }

    /**
     * Return a long based on the given path
     *
     * @param node the configuration node
     * @return the long value
     */
    protected long getLong(String node) {
        return ObjectUtils.toLong(get(getSettingsFile(), node));
    }

    /**
     * Return a long based on the given path
     *
     * @param w the World
     * @param node the configuration node
     * @return the long value
     */
    protected long getLong(World w, String node) {
        return ObjectUtils.toLong(get(getWorldFile(w), node));
    }

    /**
     * Return a String converted to String HashSet based on the given path
     *
     * @param w the World
     * @param node the configuration node
     * @return the HashSet
     */
    protected HashSet<String> getStringAsStringSet(World w, String node) {

        try {
            return CollectionUtils.toStringHashSet(getString(w, node).replaceAll(" ", ""), ",");
        } catch (CoreException ex) {
            plugin.error(ex);
        }

        return null;
    }

    /**
     * Return a String converted to String HashSet based on the given path
     *
     * @param node the configuration node
     * @return the HashSet
     */
    protected HashSet<String> getStringAsStringSet(String node) {

        try {
            return CollectionUtils.toStringHashSet(getString(node).replaceAll(" ", ""), ",");
        } catch (CoreException ex) {
            plugin.error(ex);
        }

        return null;
    }

    /**
     * Return a String converted to Integer HashSet based on the given path
     *
     * @param w the World
     * @param node the configuration node
     * @return the HashSet
     */
    protected HashSet<Integer> getStringAsIntegerSet(World w, String node) {

        try {
            return CollectionUtils.toIntegerHashSet(getString(w, node).replaceAll(" ", ""), ",");
        } catch (CoreException ex) {
            plugin.error(ex);
        }

        return null;
    }
    
    /**
     * Return a String converted to Integer HashSet based on the given path
     *
     * @param node the configuration node
     * @return the HashSet
     */
    protected HashSet<Integer> getStringAsIntegerSet(String node) {

        try {
            return CollectionUtils.toIntegerHashSet(getString(node).replaceAll(" ", ""), ",");
        } catch (CoreException ex) {
            plugin.error(ex);
        }

        return null;
    }
    
    /**
     * Return a String converted to Integer ArrayList based on the given path
     *
     * @param w the World
     * @param node the configuration node
     * @return the ArrayList
     */
    protected List<Integer> getStringAsIntegerList(World w, String node) {

        try {
            return CollectionUtils.toIntegerList(getString(w, node).replaceAll(" ", ""), ",");
        } catch (CoreException ex) {
            plugin.error(ex);
        }

        return null;
    }
    
    /**
     * Return a String converted to Integer ArrayList based on the given path
     *
     * @param node the configuration node
     * @return the ArrayList
     */
    protected List<Integer> getStringAsIntegerList(String node) {
        
        try {
            return CollectionUtils.toIntegerList(getString(node).replaceAll(" ", ""), ",");
        } catch (CoreException ex) {
            plugin.error(ex);
        }
        
        return null;
    }

    /**
     * Get the message string from the messages file
     *
     * @param node the configuration node
     * @return the message string
     */
    public String getMessage(String node) {
        return ObjectUtils.toString(get(getMessagesFile(), node));
    }
    
    /**
     * Return a String ArrayList based on the given path
     *
     * @param node the configuration node
     * @return the ArrayList
     */
    public List<String> getStringList(String node) {
        return CollectionUtils.getStringList(get(getSettingsFile(), node));
    }

    /**
     * Return a String ArrayList based on the given path
     *
     * @param w the World
     * @param node the configuration node
     * @return the ArrayList
     */
    public List<String> getStringList(World w, String node) {
        return CollectionUtils.getStringList(get(getWorldFile(w), node));
    }
    
    /**
     * Return a Integer ArrayList based on the given path
     *
     * @param node the configuration node
     * @return the ArrayList
     */
    public List<Integer> getIntegerList(String node) {
        return CollectionUtils.getIntegerList(get(getSettingsFile(), node));
    }
    
    /**
     * Return a Integer ArrayList based on the given path
     *
     * @param w the World
     * @param node the configuration node
     * @return the ArrayList
     */
    public List<Integer> getIntegerList(World w, String node) {
        return CollectionUtils.getIntegerList(get(getWorldFile(w), node));
    }

    /**
     * Get if the configuration file have a node
     *
     * @param w the World
     * @param node the configuration node
     * @return true if the configuration file have the node, false otherwise.
     */
    public boolean hasNode(World w, String node) {
        return hasNode(getWorldFile(w), node);
    }
    
    /**
     * Get if the configuration file have a node
     *
     * @param node the configuration node
     * @return true if the configuration file have the node, false otherwise.
     */
    public boolean hasNode(String node) {
        return hasNode(getSettingsFile(), node);
    }
    
    /**
     * Get if the configuration file have a node
     *
     * @param file the file to check
     * @param node the configuration node
     * @return true if the configuration file have the node, false otherwise.
     */
    public boolean hasNode(File file, String node) {
        return load(file).contains(node);
    }

    /**
     * Defines the value of some node in some file
     *
     * @param file the file to be changed
     * @param node the node to be set
     * @param value the value of the node
     */
    public void set(File file, String node, Object value) {
        Communicator com    = plugin.getCommunicator();

        ConfigLoader config = load(file);
        config.set(node, value);
        
        try {
            config.save(file);
        } catch (IOException ex) {
            com.error(ex, "Failed to update the '" + node + ":" + value + "' " + file.getName() + " node.");
        }

        update_required.add(file.getAbsolutePath());
    }
    
    /**
     * Get an Object from the configuration file based on the given path
     *
     * @param file the file to get the node
     * @param node the node to get the object
     * @return the Object, or null if not present.
     */
    public Object get(File file, String node) {

        Communicator com    = plugin.getCommunicator();
        Object backup = null;

        try {
            
            if (!load(file).contains(node)) {

                ConfigLoader rsconfig = new ConfigLoader();
                rsconfig.load(getInputStream(file));

                if (rsconfig.contains(node)) {
                    com.log("[TAG] Settings file updated, check at: \n" + node.replace(".", " &3>>&f "));
                    backup = rsconfig.get(node);
                    update_required.add(file.getAbsolutePath());
                } else {
                    com.log("[TAG] Invalid node at: "+node+", contact the developer!");
                }
            }

        } catch (IOException ex) {
            com.error(ex, "Can't load the "+file.getName()+" file: "+ex.getMessage()+", node " + node);
        } catch (InvalidConfigurationException ex) {
            com.log("[TAG] You have a broken node in your " + file.getName() + " file, use http://yaml-online-parser.appspot.com/ to find errors! " + ex.getMessage());
            update_required.add(file.getAbsolutePath());
        }

        Object value = load(file).get(node);
        if (value == null) {

            if (backup != null) {
                value = backup;
            } else {
                value = "0";
                com.log("[TAG] Can't get "+file.getName()+" node: "+node+", contact the developer.");
                update_required.add(file.getAbsolutePath());
            }
        }

        return value;
    }

    private ConfigLoader load(File file) {

        Communicator com    = plugin.getCommunicator();
        ConfigLoader config = null;

        try {
            
            if (file.getName().equals(default_setting)) {

                if (settings == null || settings.get() == null) {

                    ConfigLoader _settings = new ConfigLoader();
                    _settings.load(file);

                    settings = new SoftReference<ConfigLoader>(_settings);
                }

                return settings.get();
            }

            if (file.getName().equals(default_message)) {

                if (messages == null || messages.get() == null) {
                    
                    ConfigLoader _messages = new ConfigLoader();
                    _messages.load(file);
                    
                    messages = new SoftReference<ConfigLoader>(_messages);
                }

                return messages.get();
            }

            if (yamlcache.containsKey(file.getName())) {
                return yamlcache.get(file.getName());
            }

            config = new ConfigLoader();
            config.load(file);

        } catch (FileNotFoundException ex) {
            com.error(ex, "The file " + file.getName() + "  was not found in the plugin directory!");
        } catch (IOException ex) {
            com.error(ex, "Failed to load the " + file.getName() + " configuration file!");
        } catch (InvalidConfigurationException ex) {
            com.log("[TAG] You have a broken node in your " + file.getName() + " file, use http://yaml-online-parser.appspot.com/ to find errors! " + ex.getMessage());
            update_required.add(file.getAbsolutePath());
        }

        if (config != null) {
            yamlcache.put(file.getName(), config);
        }

        return config;
    }

    /**
     * Get the file used for some world
     *
     * @param w the World
     * @return the file for the world
     */
    public File getWorldFile(World w) {

        File file = new File(plugin.getDataFolder() + File.separator + "worlds", (w != null || single_config) ? w.getName() + ".yml" : default_world);

        if (!file.exists()) {

            try {
                FileUtils.copyFile(plugin.getResource(default_world), file);
            } catch (CoreException ex) {
                plugin.error(ex);
            }

            updateLines(file, getInputStream(file));
        }

        return file;
    }
    
    /**
     * Get the file used to store the messages
     * 
     * @return the messages file
     */
    public File getMessagesFile() {

        File file = new File(plugin.getDataFolder(), default_message);

        if (!file.exists()) { 

            try { 
                FileUtils.copyFile(plugin.getResource(default_message), file);
            } catch (CoreException ex) {
                plugin.error(ex);
            }

            updateLines(file, getInputStream(file));
        }

        return file;
    }
    
    /**
     * Get the main settings file
     *
     * @return the settings file
     */
    public File getSettingsFile() {

        File file = new File(plugin.getDataFolder(), default_setting);

        if (!file.exists()) { 

            try { 
                FileUtils.copyFile(plugin.getResource(default_setting), file);
            } catch (CoreException ex) {
                plugin.error(ex);
            }

            updateLines(file, getInputStream(file));
        }

        return file;
    }
    
    private InputStream getInputStream(File file) {

        String source = default_setting;

        if (file.getName().equals(default_setting)) {
            source = default_setting;
        } else
        if (file.getName().equals(default_message)) {
            source = default_message;
        } else {
            source = default_world;
        }

        return plugin.getResource(source);
    }
    
    private void updateLines(File file, InputStream stream) {

        if (updater == null) {
            updater = new ConfigUpdater();
        }

        updater.updateLines(file, stream);
    }
}