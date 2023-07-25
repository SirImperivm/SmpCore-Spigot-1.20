package me.sirimperivm.spigot.assets.managers.databases;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.logging.Logger;

@SuppressWarnings("all")
public class Statistics {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Logger log = Logger.getLogger("SMPCore");
    private static Db data = Main.getData();
    private static Modules mods = data.mods;

    static Connection conn = data.conn;
    String dbName = data.dbname;
    String tablePrefix = data.tablePrefix;
    String tableName = "statistics";
    public String database = dbName + "." + tablePrefix + tableName;

    boolean tableExists() {
        boolean value = false;
        try {
            DatabaseMetaData dmd = conn.getMetaData();
            ResultSet rs = dmd.getTables(null, null, database, new String[] {"TABLE"});
            value = rs.next();
        } catch (SQLException e) {
            data.setCanConnect(false);
            log.severe("Impossibile capire se è presente la tabella delle statistiche tra i database.");
            e.printStackTrace();
            plugin.disablePlugin();
        }
        return value;
    }

    public void createTable() {
        if (!tableExists()) {
            String query = "CREATE TABLE " + database + "(`id` INT AUTO_INCREMENT primary key NOT NULL, `playerName` TEXT NOT NULL, `deaths` INT NOT NULL, `kills` INT NOT NULL);";

            try {
                PreparedStatement state = conn.prepareStatement(query);
                state.executeUpdate();
                log.info("Tabella statistiche creata con successo.");
            } catch (SQLException e) {
                log.severe("Impossibile creare la tabella delle statistiche.");
                e.printStackTrace();
                plugin.disablePlugin();
            }
        }
    }

    public void insertMemberData(Player p, int deaths, int kills) {
        String playerName = p.getName();
        String query = "INSERT INTO " + database + "(playerName, deaths, kills) VALUES (?, ?, ?);";

        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, playerName);
            state.setInt(2, 0);
            state.setInt(3, 0);
            state.executeUpdate();
        } catch (SQLException e) {
            log.severe("Impossibile inserire dei dati nel database delle statistiche:" +
                    "\n Utente: " + playerName +
                    "\n Deaths: " + deaths +
                    "\n Kills: " + kills +
                    "\n...!");
            e.printStackTrace();
        }
    }

    public void updatePlayerData(Player p, String type, int value) {
        String playerName = p.getName();
        String query = "UPDATE " + database + " SET " + type + "=" + value + " WHERE playerName='" + playerName + "'";

        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.executeUpdate();
        } catch (SQLException e) {
            log.severe("Impossibile aggiornare un dato nelle statistiche dei players:" +
                    "\n Username: " + playerName +
                    "\n Type: " + type +
                    "\n Valore: "  + value +
                    "\n...!");
            e.printStackTrace();
        }
    }

    public int getPlayerData(Player p, String key) {
        int data = 0;
        String playerName = p.getName();
        String query = "SELECT * FROM " + database;

        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                if (rs.getString("playerName").equalsIgnoreCase(playerName)) {
                    data = rs.getInt(key);
                    break;
                }
            }
        } catch (SQLException e) {
            log.severe("Impossibile estrarre un dato dal database delle statistiche: " +
                    "\n Username: " + playerName +
                    "\n Key: " + key +
                    "\n...!");
            e.printStackTrace();
        }
        return data;
    }

    public boolean existMemberData(Player p) {
        boolean value = false;
        String playerName = p.getName();
        String query = "SELECT * FROM " + database;

        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                if (rs.getString("playerName").equalsIgnoreCase(playerName)) {
                    value = true;
                    break;
                }
            }
        } catch (SQLException e) {
            log.severe("Impossibile capire se sono presenti dei dati relativi all'utente " + playerName + "!");
            e.printStackTrace();
        }
        return value;
    }
}
