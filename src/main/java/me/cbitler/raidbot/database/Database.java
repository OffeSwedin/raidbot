package me.cbitler.raidbot.database;

import me.cbitler.raidbot.utility.EnvVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;

/**
 * Class for managing the SQLite database for this bot
 * @author Christopher Bitler
 */
public class Database {
    private static final Logger log = LoggerFactory.getLogger(Database.class);

    private Connection connection;

    //Thee are the queries for creating the tables

    private final String raidTableInit = "CREATE TABLE IF NOT EXISTS raids (\n"
            + " raidId VARCHAR(255) PRIMARY KEY, \n"
            + " serverId text NOT NULL, \n"
            + " channelId text NOT NULL, \n"
            + " leader text NOT NULL, \n"
            + " `name` text NOT NULL, \n"
            + " `description` text, \n"
            + " `date` text NOT NULL, \n"
            + " `time` text NOT NULL, \n"
            + " roles text NOT NULL);";

    private final String raidUsersTableInit = "CREATE TABLE IF NOT EXISTS raidUsers (\n"
            + " userId text, \n"
            + " username text, \n"
            + " spec text, \n"
            + " role text, \n"
            + " raidId text, \n"
            + " signupStatus text, \n"
            + " signupTime text)";
    
    private final String botServerSettingsInit = "CREATE TABLE IF NOT EXISTS serverSettings (\n"
            + " serverId VARCHAR(255) PRIMARY KEY, \n"
            + " raid_leader_role text, \n"
            + " raider_role text, \n"
            + " signup_channel text, \n"
            + " archive_channel text)";
    
    
    /**
     * Create a new database
     */
    public Database(){}

    /**
     * Connect to the SQLite database and create the tables if they don't exist
     */
    public void connect() {
        try {
            EnvVariables envVariables = new EnvVariables();
            envVariables.loadFromEnvFile();

            Class.forName("com.mysql.cj.jdbc.Driver");
            String databaseUrl = envVariables.getValue("DATABASE_URL") + "?autoReconnect=true";
            String databaseUsername = envVariables.getValue("DATABASE_USERNAME");
            String databasePassword = envVariables.getValue("DATABASE_PASSWORD");
            connection = DriverManager.getConnection(databaseUrl, databaseUsername, databasePassword);
        } catch (SQLException e) {
            log.error("Database connection error. ", e);
            System.exit(1);
        } catch(IOException e){
            log.error("Error reading env-file for database connection string. ", e);
            System.exit(1);
        }catch (ClassNotFoundException e){
            log.error("Couldn't find or access the mysql jdbc driver. ", e);
            System.exit(1);
        }

        try {
            tableInits();
        } catch (SQLException e) {
            log.error("Couldn't create tables", e);
            System.exit(1);
        }
    }

    /**
     * Run a query and return the results using the specified query and parameters
     * @param query The query with ?s where the parameters need to be placed
     * @param data The parameters to put in the query
     * @return QueryResult representing the statement used and the ResultSet
     * @throws SQLException SQLException
     */
    public QueryResult query(String query, String[] data) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(query);
        int i = 1;
        for(String input : data) {
            stmt.setObject(i, input);
            i++;
        }

        ResultSet rs = stmt.executeQuery();

        return new QueryResult(stmt, rs);
    }

    /**
     * Run an update query with the specified parameters
     * @param query The query with ?s where the parameters need to be placed
     * @param data The parameters to put in the query
     * @throws SQLException SQLException
     */
    public void update(String query, String[] data) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(query);
        int i = 1;
        for(String input : data) {
            stmt.setObject(i, input);
            i++;
        }

        stmt.execute();
        stmt.close();
    }

    /**
     * Create the database tables. Also alters the raid table to add the leader column if it doesn't exist.
     * @throws SQLException SQLException
     */
    private void tableInits() throws SQLException {
        connection.createStatement().execute(raidTableInit);
        connection.createStatement().execute(raidUsersTableInit);
        connection.createStatement().execute(botServerSettingsInit);
    }
}
