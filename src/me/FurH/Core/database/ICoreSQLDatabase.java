package me.FurH.Core.database;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import me.FurH.Core.exceptions.CoreException;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public interface ICoreSQLDatabase {

    public void setSQLiteFile(File file);
    
    public void setDatabaseVersion(int version);
    
    public String getDatabasePrefix();
    
    public String getDatabaseHost();
    
    public type getDatabaseEngine();
    
    public int getDatabaseVersion();

    public void setupQueue(double queue_speed, int queue_threads);

    public void setAllowMainThread(boolean thread);

    public long ping() throws CoreException;

    public String getAutoVariable();

    public int getQueueSize();

    public int getReads();
    
    public int getWrites();
    
    public long getTableCount(String table) throws CoreException;

    public long getTableSize(String table) throws CoreException;

    public long getTableFree(String table) throws CoreException;

    public void connect() throws CoreException;

    public Connection getSQLiteConnection() throws CoreException;  //new File(plugin.getDataFolder(), "database.db")

    public Connection getSQLiteConnection(File sqlite) throws CoreException;

    public Connection getMySQLConnection() throws CoreException;

    public Connection getH2Connection() throws CoreException; //plugin.getDataFolder(), plugin.getName()

    public Connection getH2Connection(File dir, String name) throws CoreException;

    public void disconnect(boolean fix) throws CoreException;

    public void createTable(String query) throws CoreException;

    public void createIndex(String query) throws CoreException;

    public void incrementVersion(int version) throws CoreException;

    public boolean isUpdateAvailable() throws CoreException;

    public int getLatestVersion();

    public int getCurrentVersion() throws CoreException;

    public void queue(String query);

    public void execute(String query, Object...objects) throws CoreException;

    public PreparedStatement getQuery(String query, Object...objects) throws CoreException;

    public boolean hasTable(String table);

    public int getQueueSpeed();

    public void queue();

    public void commit() throws CoreException;

    public void setAutoCommit(boolean auto);

    public void changeAutoCommit(boolean auto) throws CoreException;

    public void verify(ICoreSQLThread thread, Exception ex);

    public PreparedStatement prepare(String query) throws CoreException;

    public void keepAliveTask();

    public void garbage();

    public enum type { MySQL, SQLite, H2; }
}
