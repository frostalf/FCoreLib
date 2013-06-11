package me.FurH.Core.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.FurH.Core.CorePlugin;
import me.FurH.Core.cache.CoreSafeCache;
import me.FurH.Core.exceptions.CoreException;
import me.FurH.Core.file.FileUtils;
import me.FurH.Core.util.Communicator;
import me.FurH.Core.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 *
 * @author FurmigaHumana
 */
public class CoreSQLDatabase {

    private CoreSafeCache<String, PreparedStatement> cache0 = new CoreSafeCache<String, PreparedStatement>();
    private ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();
    private List<ICoreSQLThread> connections = new ArrayList<ICoreSQLThread>();

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

    //private int current_connection      = -1;
    private int connection_threads      = 1;

    private Runnable keepAliveTask;

    public double queue_speed           = 0.1;
    public int queue_threads            = 1;

    private int database_version        = 1;
    private int database_writes         = 0;
    private int database_reads          = 0;
    private int database_fixes          = 0;

    private CorePlugin plugin;
    private type database_type;
    private File sqlite_file;

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
    public CoreSQLDatabase(CorePlugin plugin, String prefix, String engine, String database_host, String database_port, String database_table, String database_user, String database_pass) {
        this.database_host = database_host;
        this.database_port = database_port;
        this.database_table = database_table;
        
        this.database_pass = database_pass;
        this.database_user = database_user;
        
        this.plugin = plugin;
        this.database_prefix = prefix;
        this.database_engine = engine;
        
        plugin.coredatabase = this;
    }
    
    public void setSQLiteFile(File file) {
        this.sqlite_file = file;
    }
    
    public int getConnections() {
        return this.connection_threads;
    }
    
    public void setDatabaseVersion(int version) {
        this.database_version = version;
    }
    
    public String getDatabasePrefix() {
        return this.database_prefix;
    }
    
    public String getDatabaseHost() {
        return this.database_host;
    }
    
    public type getDatabaseEngine() {
        return this.database_type;
    }
    
    public int getDatabaseVersion() {
        return this.database_version;
    }
    
    public ICoreSQLThread getCoreThread() {
        /*this.current_connection++;
        
        if (current_connection >= connections.size()) {
            current_connection = 0;
        }*/
        
        return connections.get(0);
    }

    /**
     *
     * @param queue_speed
     * @param queue_threads
     */
    public void setupQueue(double queue_speed, int queue_threads) {
        this.queue_speed = queue_speed;
        this.queue_threads = queue_threads;
    }
    
    /*public void setConnections(int connections) {
        this.connection_threads = connections;
    }*/
    
    /**
     * Set if this database can be used from the main thread
     *
     * @param thread true if can, false otherwise
     */
    public void setAllowMainThread(boolean thread) {
        this.allow_mainthread = thread;
        this.connections.get(0).setAllowMainThread(false);
    }

    /**
     * Get the database server ping
     * 
     * @return the server ping in milliseconds
     * @throws CoreException
     */
    public long ping() throws CoreException {
        long ping = 0;

        if (database_type == type.MySQL) {
            ping = Utils.pingServer(database_host + ":" + database_port);
        }

        return ping;
    }

    /**
     * Get the AUTO_INCREMENT variable depending on the database type
     * 
     * @param type the database type
     * @return the id int auto_increment variable
     */
    public String getAutoVariable(type type) {
        if (type == type.MySQL || type == type.H2) {
            return "id INT AUTO_INCREMENT, PRIMARY KEY (id)";
        } else {
            return "id INTEGER PRIMARY KEY AUTOINCREMENT";
        }
    }
    
    /**
     * Get the primary key field by the given field name
     * 
     * @param type the database type
     * @param field the name of the field
     * @return the field string based on the database type
     */
    public String getPrimareKey(type type, String field) {
        if (type == type.MySQL || type == type.H2) {
            return field + ", PRIMARY KEY ("+field+")";
        } else {
            return field + " PRIMARY KEY";
        }
    }
    
    /**
     * Get the default primary key field
     * 
     * @param type the database type
     * @return the primary key field
     */
    public String getPrimareKey(type type) {
        if (type == type.MySQL || type == type.H2) {
            return "id INT, PRIMARY KEY (id)";
        } else {
            return "id INTEGER PRIMARY";
        }
    }

    /**
     * Get the queue total size
     * 
     * @return the queue size
     */
    public int getQueueSize() {
        return queue.size();
    }
    
    /**
     * Get the total database reads
     * 
     * @return the database reads
     */
    public int getReads() {
        return database_reads;
    }
    
    /**
     * Get the total database writes
     * 
     * @return the database writes
     */
    public int getWrites() {
        return database_writes;
    }
    
    /**
     * Get the total row count for the given table
     * 
     * @param table the table to count
     * @return the total amount of rows
     * @throws CoreException
     */
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
            FileUtils.closeQuietly(ps);
            FileUtils.closeQuietly(rs);
        }
        
        return count;
    }
    
    /**
     * Get the total table size
     *
     * @param table the table to check the size
     * @return the table size in bytes
     * @throws CoreException
     */
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
            FileUtils.closeQuietly(ps);
            FileUtils.closeQuietly(rs);
        }
        
        return size;
    }
    
    /**
     * Get the total table free space
     * 
     * @param table the table to check the free space
     * @return the table free space in bytes
     * @throws CoreException
     */
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
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) { }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) { }
            }
        }
        
        return size;
    }
    
    /**
     * Creates a new connection with the database
     * 
     * @throws CoreException
     */
    public void connect() throws CoreException {
        Communicator com = plugin.getCommunicator();
                
        if (database_engine.equalsIgnoreCase("MySQL")) {
            database_type = type.MySQL;
        } else
        if (database_engine.equalsIgnoreCase("H2")) {
            database_type = type.H2; connection_threads = 1;
        } else {
            database_type = type.SQLite; connection_threads = 1;
        }

        com.log("[TAG] Connecting to the "+database_type+" database ("+connection_threads+")...");

        for (int j = 0; j < connection_threads; j++) {
            getNewConnection();
        }

        if (!connections.isEmpty()) {

            ICoreSQLThread thread = getCoreThread();

            if (thread.getConnection() != null) {

                kill.set(false);

                queue();
                garbage();
                keepAliveTask();

                createTable("CREATE TABLE IF NOT EXISTS `"+database_prefix+"internal` (version INT);");
                com.log("[TAG] "+database_type+" database connected Successfuly!");

            }
        }
    }
    
    public ICoreSQLThread getNewConnection() throws CoreException {
        return getNewConnection(plugin, database_prefix, database_type, allow_mainthread, auto_commit);
    }
    
    public ICoreSQLThread getNewConnection(CorePlugin plugin, String prefix, type type, boolean allow_mainthread, boolean auto_commit) throws CoreException {
        ICoreSQLThread thread = new CoreSQLThread();

        thread.setUpConnection(plugin, prefix, type);
        thread.setAllowMainThread(allow_mainthread);

        if (database_type == type.MySQL) {
            thread.setConnection(getMySQLConnection());
        } else
        if (database_type == type.SQLite) {
            thread.setConnection(getSQLiteConnection());
        } else {
            thread.setConnection(getH2Connection());
        }
        
        thread.setAutoCommit(auto_commit);
        connections.add(thread);

        return thread;
    }
    
    /**
     * Creates a new SQLite connection using the default database.db file
     * 
     * @return the created connection
     * @throws CoreException
     */
    public Connection getSQLiteConnection() throws CoreException {
        return getSQLiteConnection(new File(plugin.getDataFolder(), "database.db"));
    }

    /**
     * Creates a new SQLite connection using the defined file
     *
     * @param sqlite the file used to connect
     * @return the created connection
     * @throws CoreException
     */
    public Connection getSQLiteConnection(File sqlite) throws CoreException {
        
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
    
    /**
     * Creates a new MySQL Connection
     *
     * @return the created connection
     * @throws CoreException
     */
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

    /**
     * Creates a new H2 connection using the default plugin folder
     * 
     * @return the created connection
     * @throws CoreException
     */
    public Connection getH2Connection() throws CoreException {
        return getH2Connection(plugin.getDataFolder(), plugin.getName());
    }

    /**
     * Creates a new H2 connection using the defined file and folder
     * 
     * @param dir the directory to create the connection
     * @param name the name of the file used to create the connection, might be null if the dir is a file
     * @return the created connection
     * @throws CoreException
     */
    public Connection getH2Connection(File dir, String name) throws CoreException {
        Communicator com = plugin.getCommunicator();
        
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
        
        this.sqlite_file = dir;

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
    
    /**
     * Disconnect the current connection
     * 
     * @param fix if true the remain queue wont be flushed, otherwise will.
     * @throws CoreException
     */
    public void disconnect(boolean fix) throws CoreException {
        Communicator com = plugin.getCommunicator();
        com.log("[TAG] Closing the "+database_type+" connection...");

        if (!fix) {
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
            if (!connections.isEmpty()) {
                
                commit();

                Iterator<ICoreSQLThread> it = connections.iterator();
                
                boolean ok = true;
                
                while (it.hasNext()) {
                    ICoreSQLThread thread = it.next();

                    thread.disconnect(false);

                    if (!thread.getConnection().isClosed()) {
                        ok = false;
                    }
                }
                
                if (ok) {
                    com.log("[TAG] "+database_type+" connection closed successfuly!");
                }
            }
        } catch (SQLException ex) {
            throw new CoreException(ex, "Can't close the "+database_type+" connection");
        }
    }
    
    /**
     * Creates a new table in the current database connection
     * 
     * @param query the query used to create the table
     * @throws CoreException
     */
    public void createTable(String query) throws CoreException {
        createTable(getCoreThread().getConnection(), query, database_type);
    }

    /**
     * Creates a new table using the given connection and database type
     *
     * @param connection the connection to be used
     * @param query the query used to create the table
     * @param type the database type
     * @throws CoreException
     */
    public void createTable(Connection connection, String query, type type) throws CoreException {
        Statement st = null;
        
        try {

            if (query.contains("{auto}")) {
                query = query.replaceAll("\\{auto}", getAutoVariable(type));
            }

            if (query.contains("{primary}")) {
                query = query.replace("{primary}", getPrimareKey(type));
            }

            st = connection.createStatement();
            st.executeUpdate(query);
        } catch (SQLException ex) {
            throw new CoreException(ex, "Failed to create table in the "+type+" database, query: " + query);
        } finally {
            FileUtils.closeQuietly(st);
        }
    }
    
    /**
     *  Creates a new index using current database connection
     * 
     * @param query the query used to create the index
     * @throws CoreException
     */
    public void createIndex(String query) throws CoreException {
        createIndex(getCoreThread().getConnection(), query);
    }

    /**
     * Creates a new index using the given connection
     * 
     * @param connection the connection to be used
     * @param query the query used to create the index
     * @throws CoreException
     */
    public void createIndex(Connection connection, String query) throws CoreException {
        Statement st = null;
        try {
            st = connection.createStatement();
            st.executeUpdate(query);
        } catch (SQLException ex) {
            if (ex.getMessage().contains("syntax") || ex.getMessage().contains("SYNTAX")) {
                throw new CoreException(ex, "Failed to create index in the "+database_type+" database, query: " + query);
            }
        } finally {
            FileUtils.closeQuietly(st);
        }
    }
    
    /**
     * Increment the database version
     * 
     * @param version the version to increment to
     * @throws CoreException
     */
    public void incrementVersion(int version) throws CoreException {
        execute("DELETE FROM `"+database_prefix+"internal`");
        execute("INSERT INTO `"+database_prefix+"internal` VALUES ('"+version+"');");
    }
    
    /**
     * Get if there is an database update available
     * 
     * @return true if an update is available, false otherwise.
     * @throws CoreException
     */
    public boolean isUpdateAvailable() throws CoreException {
        return getLatestVersion() > getCurrentVersion();
    }
    
    /**
     * Get the database lastest version
     *
     * @return the database lastest version
     */
    public int getLatestVersion() {
        return database_version;
    }
    
     /**
     *  Get the database current stored version
     * 
     * @return the database current version
     * @throws CoreException
     */
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
            }
            
            if (ret == -1) {
                execute("INSERT INTO `"+database_prefix+"internal` VALUES ('"+this.database_version+"');");

                return getCurrentVersion();
            }

        } catch (Exception ex) {
            throw new CoreException(ex, "Can't retrieve "+database_type+" database version");
        }
        
        return ret;
    }

    /**
     * Adds a new query to the queue
     * 
     * @param query the query to be added
     */
    public void queue(String query) {
        queue.add(query);
    }

    /**
     * Execute a new query
     *
     * @param query the query to be executed
     * @param objects the objects to be inserted in the query
     * @throws CoreException
     */
    public void execute(String query, Object...objects) throws CoreException {
        
        database_writes++;
        
        getCoreThread().execute(query, objects);
    }
    
    /**
     * Get informations out of the database
     *
     * @param query the query used to get the information
     * @param objects the objections to insert in the query
     * @return the PreparedStatement resulting the query
     * @throws CoreException
     */
    public PreparedStatement getQuery(String query, Object...objects) throws CoreException {
        
        database_reads++;
        
        return getCoreThread().getQuery(query, objects);
    }

    /**
     * Get if the database has an table
     * 
     * @param table the table to be checked
     * @return true if the database has the table, false otherwise
     */
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
            FileUtils.closeQuietly(ps);
            FileUtils.closeQuietly(rs);
        }
    }

    private int getQueueSpeed() {
        
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

    private void queue() {
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
    
    /**
     * Commits the current database connection
     *
     * @throws CoreException
     */
    public void commit() throws CoreException {
        Iterator<ICoreSQLThread> it = connections.iterator();
        while (it.hasNext()) {
            it.next().commit();
        }
    }
    
    /**
     * Set the AutoCommit status of future connections
     *
     * @param auto the AuthCommit status
     */
    public void setAutoCommit(boolean auto) {
        this.auto_commit = auto;
        try {
            this.connections.get(0).setAutoCommit(false);
        } catch (CoreException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Set the AutoCommit status of the current connection
     *
     * @param auto the AuthCommit status
     * @throws CoreException
     */
    public void changeAutoCommit(boolean auto) throws CoreException {
        Iterator<ICoreSQLThread> it = connections.iterator();
        while (it.hasNext()) {
            it.next().setAutoCommit(auto);
        }
    }

    /**
     * Check if the database connection is up and running, if its not, try to fix it.
     *
     * @param thread the connection thread to verify
     * @param ex the error that trigged this method
     */
    public void verify(ICoreSQLThread thread, Exception ex) {
        try {
            if (!thread.isOk()) { keepAliveTask.run(); }
        } catch (CoreException ex1) {
            plugin.getCommunicator().error(ex1);
        }
    }

    /**
     * Prepare and store a query method for further usage and cleanup
     *
     * @param query the query to be prepared
     * @return the PreparedStatement of the query
     * @throws CoreException
     */
    public PreparedStatement prepare(String query) throws CoreException {
        PreparedStatement ps = null;
        
        if (cache0.containsKey(query)) {
            ps = cache0.get(query);
            try {
                if (!ps.isClosed()) { return ps; }
            } catch (Throwable ex) { }
        }

        ps = getCoreThread().prepare0(query);
        cache0.put(query, ps);
        
        return ps;
    }

    private void keepAliveTask() {

        keepAliveTask = new Runnable() {
            @Override
            public void run() {

                Communicator com = plugin.getCommunicator();

                Iterator<ICoreSQLThread> it = connections.iterator();
                int killed = 0;

                while (it.hasNext()) {
                    ICoreSQLThread next = it.next();
                    if (!next.isAlive()) {

                        next.disconnect(true);
                        it.remove();
                        killed++;

                    }
                }

                if (killed > 0) {
                    com.log("[TAG] There are {0} {1} connections down, reconnecting...", killed, database_type);
                } else {
                    return;
                }

                int reconnected = 0;

                for (int j1 = 0; j1 < killed; j1++) {

                    if (database_fixes > 3) {
                        com.log("[TAG] Failed to fix the {0} connection after 3 attempts, shutting down...");
                        plugin.getPluginLoader().disablePlugin(plugin);
                        return;
                    }

                    try {
                        getNewConnection();
                        reconnected++;
                    } catch (CoreException ex) {
                        database_fixes++; j1--; com.error(ex, "Failed to fix the {0} connection!, attempt {1} of 3.", database_type, database_fixes);
                    }
                }

                if (reconnected == killed) {
                    com.log("[TAG] All {0} connections are now up and running!", database_type);
                    database_fixes = 0;
                }
            }
        };
        
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, keepAliveTask, 300 * 20, 300 * 20);
    }

    private void garbage() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Iterator<PreparedStatement> it = cache0.values().iterator();
                
                while (it.hasNext()) {
                    try {
                        it.next().close(); it.remove();
                    } catch (SQLException ex) { }
                }

                cache0.clear();
            }
        }, 180 * 20, 180 * 20);
    }

    public enum type { MySQL, SQLite, H2; }
}