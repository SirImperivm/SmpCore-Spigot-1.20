package me.sirimperivm.spigot.assets.managers.databases;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;

import java.sql.*;
import java.util.logging.Logger;

@SuppressWarnings("all")
public class Bounties {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Logger log = Logger.getLogger("SMPCore");
    private static Db data = Main.getData();
    private static Modules mods = data.mods;

    static Connection conn = data.conn;
    String dbName = data.dbname;
    String tablePrefix = data.tablePrefix;
    String tableName = "bounties";
    public String database = dbName + "." + tablePrefix + tableName;

    boolean tableExists() {
        boolean value = false;
        try {
            DatabaseMetaData dmd = conn.getMetaData();
            ResultSet rs = dmd.getTables(null, null, database, new String[] {"TABLE"});
            value = rs.next();
        } catch (SQLException e) {
            data.setCanConnect(false);
            log.severe("Impossibile capire se è presente la tabella delle taglie tra i database.");
            e.printStackTrace();
            plugin.disablePlugin();
        }
        return value;
    }

    public void createTable() {
        if (!tableExists()) {
            String query = "CREATE TABLE " + database + "(`id` INT AUTO_INCREMENT primary key NOT NULL, `target` TEXT NOT NULL, `bountyExecutor` TEXT NULL, `bounty` TEXT NOT NULL);";

            try {
                PreparedStatement state = conn.prepareStatement(query);
                state.executeUpdate();
                log.info("Tabella taglie creata con successo.");
            } catch (SQLException e) {
                log.severe("Impossibile creare la tabella delle taglie.");
                e.printStackTrace();
                plugin.disablePlugin();
            }
        }
    }

    public void insertMemberData(String playerName, String bountyExecutor, String bountyValue) {
        String query = "INSERT INTO " + database + "(target, bountyExecutor, bountyValue) VALUES (?, ?, ?);";

        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, playerName);
            state.setString(2, bountyExecutor);
            state.setString(3, bountyValue);
            state.executeUpdate();
        } catch (SQLException e) {
            log.severe("Impossibile inserire dei dati nel database delle taglie:" +
                    "\n Utente: " + playerName +
                    "\n Esecutore: " + bountyExecutor +
                    "\n Valore: " + bountyValue +
                    "\n...!");
            e.printStackTrace();
        }
    }

    public void deleteMemberData(String playerName) {
        String query = "DELETE FROM " + database + " WHERE target='" + playerName + "';";
        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.executeUpdate();
        } catch (SQLException e) {
            log.severe("Impossibile eliminare il dato relativo alla taglia di " + playerName + "!");
            e.printStackTrace();
        }
    }

    public String getBountyExecutor(String playerName) {
        String executor = "";
        String query = "SELECT * FROM " + database;
        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                if (rs.getString("target").equalsIgnoreCase(playerName)) {
                    executor = rs.getString("bountyExecutor");
                    break;
                }
            }
        } catch (SQLException e) {
            log.severe("Impossibile ottenere la taglia relativa all'utente " + playerName + "!");
            e.printStackTrace();
        }
        return executor;
    }

    public double getBounty(String playerName) {
        double bounty = 0.0;
        String query = "SELECT * FROM " + database;
        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                if (rs.getString("target").equalsIgnoreCase(playerName)) {
                    bounty = Double.parseDouble(rs.getString("bountyValue"));
                    break;
                }
            }
        } catch (SQLException e) {
            log.severe("Impossibile ottenere la taglia relativa all'utente " + playerName + "!");
            e.printStackTrace();
        }
        return bounty;
    }

    public boolean hasBounty(String playerName) {
        boolean value = false;
        String query = "SELECT * FROM " + database;
        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                if (rs.getString("target").equalsIgnoreCase(playerName)) {
                    value = true;
                    break;
                }
            }
        } catch (SQLException e) {
            log.severe("Impossibile capire se l'utente " + playerName + " ha una taglia.");
            e.printStackTrace();
        }
        return value;
    }
}
