package me.FurH.Core.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import me.FurH.Core.Core;
import me.FurH.Core.CorePlugin;
import me.FurH.Core.database.CoreSQLDatabase.type;
import me.FurH.Core.exceptions.CoreException;
import me.FurH.Core.file.FileUtils;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
class CoreSQLThread implements ICoreSQLThread {

    private boolean allow_mainthread = true;

    private Connection connection;
    private CorePlugin plugin;

    private String prefix = "core_";
    private type type;

    @Override
    public void setUpConnection(CorePlugin plugin, String prefix, type type) {
        this.plugin = plugin;
        this.prefix = prefix;
        this.type = type;
    }

    @Override
    public void setAllowMainThread(boolean thread) {
        this.allow_mainthread = thread;
    }

    @Override
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public Connection getConnection() {
        return this.connection;
    }
    
    @Override
    public boolean disconnect(boolean supress) {
        
        try {
            commit();
        } catch (CoreException ex) {
            if (!supress) {
                ex.printStackTrace();
            }
        }
        
        try {
            connection.close();
        } catch (SQLException ex) {
            if (!supress) {
                ex.printStackTrace();
            }
            return false;
        }
        
        return true;
    }
    
    @Override
    public boolean isOk() throws CoreException {
        
        if (connection == null) {
            return false;
        }
        
        try {

            if (connection.isClosed()) {
                return false;
            }

            if (!isAlive()) {
                return false;
            }

        } catch (SQLException ex) {
            throw new CoreException(ex, "Failed to check if the "+type+" connection is up");
        }

        return true;
    }

    @Override
    public void execute(String query, Object...objects) throws CoreException {

        if (objects != null && objects.length > 0) {
            query = MessageFormat.format(query, objects);
        }

        if (!allow_mainthread && Thread.currentThread() == Core.main_thread) {
            throw new IllegalStateException("This method cannot be cast from the main thread!");
        }

        PreparedStatement ps = null;

        try {
            ps = plugin.coredatabase.prepare(query);
            
            ps.execute();
        } catch (SQLException ex) {
            throw new CoreException(ex, "Can't write in the "+type+" database, query: " + query);
        } finally {
            FileUtils.closeQuietly(ps);
        }
    }

    @Override
    public PreparedStatement getQuery(String query, Object...objects) throws CoreException {        
        
        if (objects != null && objects.length > 0) {
            query = MessageFormat.format(query, objects);
        }

        if (!allow_mainthread && Thread.currentThread() == Core.main_thread) {
            throw new IllegalStateException("This method cannot be cast from the main thread!");
        }

        try {
                        
            PreparedStatement ps = plugin.coredatabase.prepare(query);

            try {
                ps.execute();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            return ps;
        } catch (Exception ex) {
            throw new CoreException(ex, "Can't read the "+type+" database, query: " + query);
        }
    }

    @Override
    public PreparedStatement prepare0(String query) throws CoreException {        
        try {
            return connection.prepareStatement(query);
        } catch (SQLException ex) {
            throw new CoreException(ex, "Can't prepare the statement, query: " + query);
        }
    }

    @Override
    public void commit() throws CoreException {
        try {
            if (!connection.getAutoCommit()) {
                connection.commit();
            }
        } catch (SQLException ex) {
            if (!ex.getMessage().contains("SQL logic error or missing database")) {
                throw new CoreException(ex, "Can't commit the "+type+" database");
            }
        }
    }

    @Override
    public void setAutoCommit(boolean auto) throws CoreException {
        try {
            connection.setAutoCommit(auto);
        } catch (SQLException ex) {
            throw new CoreException(ex, "Can't set auto commit status the the "+type+" database");
        }
    }

    @Override
    public boolean isAlive() {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = connection.prepareStatement("SELECT version FROM `"+prefix+"internal`;");
            rs = ps.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            FileUtils.closeQuietly(rs);
            FileUtils.closeQuietly(ps);
        }

        return true;
    }
}