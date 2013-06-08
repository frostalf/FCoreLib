package me.FurH.Core.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import me.FurH.Core.CorePlugin;
import me.FurH.Core.database.CoreSQLDatabase.type;
import me.FurH.Core.exceptions.CoreException;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public interface ICoreSQLThread {

    /**
     * Setup this sql connection
     *
     * @param plugin the plugin owner of this connection
     * @param prefix the connection prefix
     * @param type the connection type
     */
    public void setUpConnection(CorePlugin plugin, String prefix, type type);
    
    /**
     * Set if this database can be used from the main thread
     *
     * @param thread true if can, false otherwise
     */
    public void setAllowMainThread(boolean thread);

    /**
     * Set this interface connection
     *
     * @param connection the sql connection
     */
    public void setConnection(Connection connection);

    /**
     * Get this thread connection
     *
     * @return this thread connection object
     */
    public Connection getConnection();
    
    /**
     * Disconnect this thread
     *
     * @param supress if true, errors will be supressed
     * @return true if successfull, false otherwise.
     */
    public boolean disconnect(boolean supress);
    
    /**
     * Check if the current SQL connection is alive and running properly
     * 
     * @return true if the connection is ok, otherwise false.
     * @throws CoreException
     */
    public boolean isOk() throws CoreException;

    /**
     * Execute a new query
     *
     * @param query the query to be executed
     * @param objects the objects to be inserted in the query
     * @throws CoreException
     */
    public void execute(String query, Object...objects) throws CoreException;
    
    /**
     * Get informations out of the database
     *
     * @param query the query used to get the information
     * @param objects the objections to insert in the query
     * @return the PreparedStatement resulting the query
     * @throws CoreException
     */
    public PreparedStatement getQuery(String query, Object...objects) throws CoreException;
    
    /**
     * Create a new PreparedStatement using the this connection
     *
     * @param query the query to be prepared
     * @return the created PreparedStatement
     * @throws CoreException
     */
    public PreparedStatement prepare0(String query) throws CoreException;
    
    /**
     * Commits the current database connection
     *
     * @throws CoreException
     */
    public void commit() throws CoreException;

    /**
     * Set the AutoCommit status of the current connection
     *
     * @param auto the AuthCommit status
     * @throws CoreException
     */
    public void setAutoCommit(boolean auto) throws CoreException;
    
    /**
     * Check if this connection is alive
     *
     * @return true if alive, false otherwise
     */
    public boolean isAlive();
    
}