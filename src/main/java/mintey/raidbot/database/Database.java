package mintey.raidbot.database;

import mintey.raidbot.utility.EnvVariables;
import mintey.raidbot.utility.ServerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
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
            + ServerSettings.RaidLeaderRole + " text, \n"
            + ServerSettings.RaiderRole + " text, \n"
            + ServerSettings.SocialRole + " text,  \n"
            + ServerSettings.SignupChannel + " text, \n"
            + ServerSettings.ArchiveChannel + " text, \n"
            + ServerSettings.TankReaction + " text, \n"
            + ServerSettings.HealerReaction + " text, \n"
            + ServerSettings.MeleeReaction + " text, \n"
            + ServerSettings.RangedReaction + " text, \n"
            + ServerSettings.CantAttendReaction + " text, \n"
            + ServerSettings.AcceptedEmote + " text, \n"
            + ServerSettings.BenchedEmote + " text, \n"
            + ServerSettings.NoShowEmote + " text, \n"
            + ServerSettings.NotDecidedEmote + " text)";
    public Database(){}
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
    private void tableInits() throws SQLException {
        connection.createStatement().execute(raidTableInit);
        connection.createStatement().execute(raidUsersTableInit);
        connection.createStatement().execute(botServerSettingsInit);
    }
}
