package me.FurH.Core.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.FurH.Core.Core;
import me.FurH.Core.CorePlugin;
import me.FurH.Core.cache.CoreSafeCache;
import me.FurH.Core.exceptions.CoreException;
import me.FurH.Core.util.Communicator;
import me.FurH.Core.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class NCoreSQLDatabase implements ICoreSQLDatabase {

    private CoreSafeCache<String, PreparedStatement> cache = new CoreSafeCache<String, PreparedStatement>();
    private ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();

    private AtomicBoolean lock = new AtomicBoolean(false);
    private AtomicBoolean kill = new AtomicBoolean(false);

    private boolean allow_mainthread    = true;
    private boolean auto_commit         = false;

    private String database_engine      = "SQLite";
    private String database_host        = "localhost";
    private String database_port        = "3306";
    private String database_table       = "minecraft";
    private String database_user        = "root";
    private String database_pass        = "123";
    private String database_prefix      = "core_";

    private Runnable keepAliveTask      = null;

    public double queue_speed           = 0.1;
    public int queue_threads            = 1;

    private int database_version        = 1;
    private int database_writes         = 0;
    private int database_reads          = 0;
    private int database_fixes          = 0;

    private CorePlugin plugin           = null;
    private type database_type          = null;
    
    private File sqlite_file            = null;
    private File h2_dir                 = null;
    
    private Connection connection       = null;

    /**
     * Creates a new CoreSQLDatabase for SQL functions
     * 
     * @param plugin the CorePlugin used to handle this object
     * @param prefix the database table prefix
     * @param engine the database engine
     */
    public NCoreSQLDatabase(CorePlugin plugin, String prefix, String engine) {

        this.plugin = plugin;
        this.database_prefix = prefix;
        this.database_engine = engine;

        //plugin.coredatabase = this;
    }
    
    /**
     * Creates a new CoreSQLDatabase for SQL functions
     * 
     * @param plugin the CorePlugin used to handle this object
     * @param prefix the database table prefix
     * @param engine the database engine
     * @param database_host the database host
     * @param database_port the database port
     * @param database_table the database table
     * @param database_user the database user
     * @param database_pass the database password
     */
    public NCoreSQLDatabase(CorePlugin plugin, String prefix, String engine, String database_host, String database_port, String database_table, String database_user, String database_pass) {
        this.database_host = database_host;
        this.database_port = database_port;
        this.database_table = database_table;
        
        this.database_pass = database_pass;
        this.database_user = database_user;
        
        this.plugin = plugin;
        this.database_prefix = prefix;
        this.database_engine = engine;
        
        //plugin.coredatabase = this;
    }
    
    @Override
    public void setSQLiteFile(File file) {
        this.sqlite_file = file;
    }
    
    public void setH2Dir(File file) {
        this.h2_dir = file;
    }

    @Override
    public void setDatabaseVersion(int version) {
        this.database_version = version;
    }

    @Override
    public String getDatabasePrefix() {
        return this.database_prefix;
    }

    @Override
    public String getDatabaseHost() {
        return this.database_host;
    }

    @Override
    public type getDatabaseEngine() {
        return this.database_type;
    }

    @Override
    public int getDatabaseVersion() {
        return this.database_version;
    }

    @Override
    public void setupQueue(double queue_speed, int queue_threads) {
        this.queue_speed = queue_speed;
        this.queue_threads = queue_threads;
    }

    @Override
    public void setAllowMainThread(boolean thread) {
        this.allow_mainthread = thread;;
    }

    @Override
    public long ping() throws CoreException {
        long ping = 0;

        if (database_type == type.MySQL) {
            ping = Utils.pingServer(database_host + ":" + database_port);
        }

        return ping;
    }

    @Override
    public String getAutoVariable() {
        if (this.database_type == type.MySQL || this.database_type == type.H2) {
            return "id INT AUTO_INCREMENT, PRIMARY KEY (id)";
        } else {
            return "id INTEGER PRIMARY KEY AUTOINCREMENT";
        }
    }

    @Override
    public int getQueueSize() {
        return this.queue.size();
    }

    @Override
    public int getReads() {
        return this.database_reads;
    }

    @Override
    public int getWrites() {
        return this.database_writes;
    }

    @Override
    public long getTableCount(String table) throws CoreException {
        long count = 0;

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            if (database_type.equals(type.MySQL)) {

                ps = prepare("SELECT table_rows FROM information_schema.TABLES WHERE TABLE_NAME = '"+table+"' AND TABLE_SCHEMA = '"+database_table+"' LIMIT 1;");
                rs = ps.executeQuery();

                if (rs.next()) {
                    count += rs.getLong("table_rows");
                }

            } else {

                ps = prepare("SELECT COUNT(1) AS total FROM '"+table+"';");
                rs = ps.executeQuery();

                if (rs.next()) {
                    count += rs.getInt("total");
                }

            }
        } catch (SQLException ex) {
            throw new CoreException(ex, "Failed to count the table '" + table + "' rows");
        } finally {
            closeQuietly(ps);
            closeQuietly(rs);
        }
        
        return count;
    }

    @Override
    public long getTableSize(String table) throws CoreException {
        long size = 0;

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            if (database_type.equals(type.MySQL)) {

                ps = prepare("SELECT table_schema, table_name, data_length, index_length FROM information_schema.TABLES WHERE TABLE_NAME = '"+table+"' AND TABLE_SCHEMA = '"+database_table+"' LIMIT 1;");
                rs = ps.executeQuery();

                if (rs.next()) {
                    size += rs.getLong("data_length");
                    size += rs.getLong("index_length");
                }

            } else {
                
                size += sqlite_file.length();
                
            }

        } catch (SQLException ex) {
            throw new CoreException(ex, "Failed to get the table '" + table + "' size");
        } finally {
            closeQuietly(ps);
            closeQuietly(rs);
        }
        
        return size;
    }

    @Override
    public long getTableFree(String table) throws CoreException {
        long size = 0;
        
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            if (database_type.equals(type.MySQL)) {

                ps = prepare("SELECT table_schema, table_name, data_free FROM information_schema.TABLES WHERE TABLE_NAME = '"+table+"' AND TABLE_SCHEMA = '"+database_table+"' LIMIT 1;");
                rs = ps.executeQuery();

                if (rs.next()) {
                    size += rs.getLong("data_free");
                }

            } else {

                size += sqlite_file.getFreeSpace();

            }

        } catch (SQLException ex) {
            throw new CoreException(ex, "Failed to get the table '" + table + "' free space");
        } finally {
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return size;
    }

    @Override
    public void connect() throws CoreException {
        Communicator com = plugin.getCommunicator();

        com.log("[TAG] Connecting to the "+database_engine+" database...");

        if (database_engine.equalsIgnoreCase("MySQL")) {

            database_type = type.MySQL;
            connection = getMySQLConnection();

        } else
        if (database_engine.equalsIgnoreCase("H2")) {

            database_type = type.H2;
            connection = getH2Connection();

        } else {

            database_type = type.SQLite;
            connection = getSQLiteConnection();

        }

        if (connection != null) {

            kill.set(false);
            lock.set(false);

            queue();
            garbage();
            keepAliveTask();

            createTable("CREATE TABLE IF NOT EXISTS `"+database_prefix+"internal` (version INT);");
            com.log("[TAG] "+database_type+" database connected Successfuly!");

        }
    }

    @Override
    public Connection getSQLiteConnection() throws CoreException {
        return getSQLiteConnection(new File(plugin.getDataFolder(), "database.db"));
    }

    @Override
    public Connection getSQLiteConnection(File sqlite) throws CoreException {
        
        if (this.sqlite_file != null) {
            sqlite = this.sqlite_file;
        }
        
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            throw new CoreException(ex, "You don't have the required "+database_type+" driver");
        }

        try {
            sqlite.createNewFile();
        } catch (IOException ex) {
            throw new CoreException(ex, "Failed to create the "+database_type+" file");
        }
        
        this.sqlite_file = sqlite;

        try {
            return DriverManager.getConnection("jdbc:sqlite:" + sqlite.getAbsolutePath());
        } catch (SQLException ex) {
            throw new CoreException(ex, "Failed open the "+database_type+" connection");
        }
    }

    @Override
    public Connection getMySQLConnection() throws CoreException {

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new CoreException(ex, "You don't have the required "+database_type+" driver");
        }

        String url = "jdbc:mysql://" + database_host + ":" + database_port + "/" + database_table +"?autoReconnect=true";

        try {
            return DriverManager.getConnection(url, database_user, database_pass);
        } catch (SQLException ex) {
            throw new CoreException(ex, "Failed open the "+database_type+" connection");
        }
    }

    @Override
    public Connection getH2Connection() throws CoreException {
        return getH2Connection(plugin.getDataFolder(), plugin.getName());
    }

    @Override
    public Connection getH2Connection(File dir, String name) throws CoreException {
        Communicator com = plugin.getCommunicator();
        
        if (this.h2_dir != null) {
            dir = this.h2_dir;
        }

        File h2 = new File("lib", "h2.jar");
        if (!h2.exists()) {
            com.log("[TAG] You must have the h2.jar library file in your lib folder!");
            com.log("[TAG] Download it here: \n http://hsql.sourceforge.net/m2-repo/com/h2database/h2/1.3.170/h2-1.3.170.jar");
            return getSQLiteConnection();
        }

        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException ex) {
            throw new CoreException(ex, "You don't have the required "+database_type+" driver");
        }

        try {

            if (dir.isFile()) {
                dir.createNewFile();
            }

            dir.mkdirs();
        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to create the "+database_type+" file");
        }
        
        this.h2_dir = dir;

        try {
            if (!dir.isFile()) {
                return DriverManager.getConnection("jdbc:h2:" + dir.getAbsolutePath() + File.separator + name + ";MODE=MySQL;IGNORECASE=TRUE", "sa", "");
            } else {
                return DriverManager.getConnection("jdbc:h2:file:" + dir.getAbsolutePath() + ";MODE=MySQL;IGNORECASE=TRUE", "sa", "");
            }
        } catch (SQLException ex) {
            throw new CoreException(ex, "Failed open the "+database_type+" connection");
        }
    }

    @Override
    public void disconnect(boolean skipqueue) throws CoreException {
        Communicator com = plugin.getCommunicator();

        com.log("[TAG] Closing the "+database_type+" connection...");

        if (!skipqueue) {

            lock.set(true);

            if (!queue.isEmpty()) {

                com.log("[TAG] Queue isn't empty! Running the remaining queue...");

                for (World world : Bukkit.getWorlds()) {
                    world.save();
                }
                
                Bukkit.savePlayers();
                
                double process = 0;
                double total = queue.size();
                double done = total - queue.size();

                double last = 0;

                commit();
                setAutoCommit(false);

                while (!queue.isEmpty()) {
                    done = total - queue.size();

                    String query = queue.poll();
                    if (query == null) { continue; }

                    process = ((done / total) * 100.0D);
                    
                    if (process - last > 1) {
                        System.gc();
                        com.log("[TAG] Processed {0} of {1} queries, {2}%", done, total, String.format("%d", (int) process));
                        last = process;
                    }

                    execute(query);
                }

                commit();

                System.gc();
            }
        }

        kill.set(true);
        
        try {

            if (connection != null) {
                
                commit();

                connection.close();

                if (connection.isClosed()) {
                    com.log("[TAG] "+database_type+" connection closed successfuly!");
                }
            }
            
        } catch (SQLException ex) {
            throw new CoreException(ex, "Can't close the "+database_type+" connection");
        }
    }

    @Override
    public void createTable(String query) throws CoreException {

        Statement st = null;

        try {

            if (query.contains("{auto}")) {
                query = query.replace("{auto}", getAutoVariable());
            }

            st = connection.createStatement();
            st.executeUpdate(query);

            commit();

        } catch (SQLException ex) {
            throw new CoreException(ex, "Failed to create table in the "+database_type+" database, query: " + query);
        } finally {
            closeQuietly(st);
        }
    }

    @Override
    public void createIndex(String query) throws CoreException {

        Statement st = null;

        try {

            st = connection.createStatement();
            st.executeUpdate(query);

            commit();
            
        } catch (SQLException ex) {

            if (ex.getMessage().contains("syntax") || ex.getMessage().contains("SYNTAX")) {
                throw new CoreException(ex, "Failed to create index in the "+database_type+" database, query: " + query);
            }

        } finally {
            closeQuietly(st);
        }
    }

    @Override
    public void incrementVersion(int version) throws CoreException {
        execute("DELETE FROM `"+database_prefix+"internal`");
        execute("INSERT INTO `"+database_prefix+"internal` VALUES ('"+version+"');");
    }

    @Override
    public boolean isUpdateAvailable() throws CoreException {
        return getLatestVersion() > getCurrentVersion();
    }

    @Override
    public int getLatestVersion() {
        return database_version;
    }

    @Override
    public int getCurrentVersion() throws CoreException {
        int ret = -1;

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            
            ps = prepare("SELECT version FROM `"+database_prefix+"internal`;");
            rs = ps.executeQuery();

            if (rs.next()) {
                database_reads++;
                ret = rs.getInt("version");
            } else {
                execute("INSERT INTO `"+database_prefix+"internal` VALUES ('"+this.database_version+"');");
                ret = this.database_version;
            }

        } catch (Exception ex) {
            throw new CoreException(ex, "Can't retrieve "+database_type+" database version");
        } finally {
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return ret;
    }

    @Override
    public void queue(String query) {
        queue.add(query);
    }

    @Override
    public void execute(String query, Object... objects) throws CoreException {

        if (objects != null && objects.length > 0) {
            query = MessageFormat.format(query, objects);
        }

        if (!allow_mainthread && Thread.currentThread() == Core.main_thread) {
            throw new IllegalStateException("This method cannot be cast from the main thread!");
        }

        PreparedStatement ps = null;

        try {

            ps = connection.prepareStatement(query);
            ps.execute();

        } catch (SQLException ex) {
            throw new CoreException(ex, "Can't write in the "+database_type+" database, query: " + query);
        } finally {
            closeLater(query, ps);
        }
    }

    @Override
    public PreparedStatement getQuery(String query, Object... objects) throws CoreException {
        
        if (objects != null && objects.length > 0) {
            query = MessageFormat.format(query, objects);
        }

        if (!allow_mainthread && Thread.currentThread() == Core.main_thread) {
            throw new IllegalStateException("This method cannot be cast from the main thread!");
        }

        try {
                        
            PreparedStatement ps = prepare(query);

            try {
                System.out.println("TRY 1");
                ps.execute();
            } catch (Throwable ex) {
                if (!ex.getMessage().contains("closed")) {
                    ex.printStackTrace();
                    System.out.println("CLOSED");
                } else {
                    System.out.println("TRY 2");
                    ps = connection.prepareStatement(query);
                    ps.execute();
                }
            }

            return ps;
        } catch (Exception ex) {
            throw new CoreException(ex, "Can't read the "+database_type+" database, query: " + query);
        }
    }

    @Override
    public boolean hasTable(String table) {

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            ps = prepare("SELECT * FROM `"+table+"` LIMIT 1;");
            rs = ps.executeQuery();

            return rs.next();
        } catch (Exception ex) {
            return false;
        } finally {
            closeQuietly(ps);
            closeQuietly(rs);
        }
    }

    @Override
    public int getQueueSpeed() {
        
        if (lock.get()) {
            return queue.size();
        }
        
        int count = (int) (((double) queue.size()) * queue_speed);

        if (count < 100) {
            count = 100;
        }

        if (count > 10000) {
            count = 10000;
        }

        return count;
    }

    @Override
    public void queue() {
        
        for (int j1 = 1; j1 < queue_threads+1; j1++) {
            Thread thread = new Thread() {

                @Override
                public void run() {

                    boolean commited = false;
                    int count = 0;

                    while (!kill.get()) {

                        String query = null;

                        try {

                            if (queue.isEmpty()) {

                                if (!commited) {
                                    commit(); commited = true;
                                }

                                sleep(1000);
                                continue;
                            }

                            query = queue.poll();

                            if (query == null) {

                                if (!commited) {
                                    commit(); commited = true;
                                }

                                sleep(1000);
                                continue;
                            }

                            count++;
                            execute(query);
                            commited = false;

                            if (!lock.get()) {
                                sleep(50);
                            }

                            if (count >= getQueueSpeed()) {

                                if (!commited) {
                                    commit(); commited = true;
                                }

                                count = 0;
                                sleep(1000);
                            }

                        } catch (CoreException ex) {
                            plugin.getCommunicator().error(ex);
                        } catch (InterruptedException ex) { }
                    }
                }
            };

            thread.setPriority(Thread.MIN_PRIORITY);
            thread.setDaemon(true);
            thread.setName(plugin.getName() + " Database Task #" + j1);
            thread.start();
        }
    }
    
    @Override
    public void commit() throws CoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAutoCommit(boolean auto) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void changeAutoCommit(boolean auto) throws CoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void verify(ICoreSQLThread thread, Exception ex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PreparedStatement prepare(String query) throws CoreException {

        PreparedStatement ps = null;
        
        if (cache.containsKey(query)) {
            return cache.get(query);
        }

        try {
            ps = connection.prepareStatement(query);
        } catch (SQLException ex) {
            plugin.getCommunicator().error(ex, "Failed to prepare the query: " + query);
        }

        if (ps != null) {
            cache.put(query, ps);
        }

        return ps;
    }

    @Override
    public void keepAliveTask() {

        keepAliveTask = new Runnable() {

            @Override
            public void run() {

                Communicator com = plugin.getCommunicator();

                if (!isAlive()) {
                    
                    try {
                        disconnect(true);
                    } catch (CoreException ex) {
                        com.error(ex);
                    }

                    com.log("[TAG] The {0} is down, reconnecting...", database_type);

                } else {
                    return;
                }

                if (database_fixes > 3) {
                    com.log("[TAG] Failed to fix the {0} connection after 3 attempts, shutting down...", database_type);
                    plugin.getPluginLoader().disablePlugin(plugin);
                    return;
                }

                try {

                    if (database_type == type.MySQL) {
                        connection = getMySQLConnection();
                    } else if (database_type == type.H2) {
                        connection = getH2Connection();
                    } else {
                        connection = getSQLiteConnection();
                    }

                } catch (CoreException ex) {
                    database_fixes++;
                    com.error(ex, "Failed to fix the {0} connection!, attempt {1} of 3.", database_type, database_fixes);
                }

                if (isAlive()) {
                    com.log("[TAG] The {0} connection is now up and running!", database_type);
                    database_fixes = 0;
                }
            }
        };
        
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, keepAliveTask, 300 * 20, 300 * 20);
    }
    
    public boolean isAlive() {

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            ps = connection.prepareStatement("SELECT version FROM `"+database_prefix+"internal`;");
            rs = ps.executeQuery();

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
        }

        return true;
    }

    @Override
    public void garbage() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {

                Iterator<PreparedStatement> it = cache.values().iterator();

                while (it.hasNext()) {
                    try {
                        it.next().close(); it.remove();
                    } catch (SQLException ex) { }
                }

                cache.clear();

            }
        }, 180 * 20, 180 * 20);
    }
    
    public static void closeLater(String query, Statement st) {
        if (st != null) {
            try {
                
            } catch (Throwable ex) { }
        }
    }

    public static void closeQuietly(Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (Throwable ex) { }
        }
    }

    public static void closeQuietly(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Throwable ex) { }
        }
    }
}