package me.sirimperivm.spigot.assets.managers.databases;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings("all")
public class Guilds {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Logger log = Logger.getLogger("SMPCore");
    private static Db data = Main.getData();

    static Connection conn = data.conn;
    String dbName = data.dbname;
    String tablePrefix = data.tablePrefix;
    String tableName = "guilds";
    String database = dbName + "." + tablePrefix + tableName;

    boolean tableExists() {
        boolean value = false;
        try {
            DatabaseMetaData dmd = conn.getMetaData();
            ResultSet rs = dmd.getTables(null, null, database, new String[] {"TABLE"});
            value = rs.next();
        } catch (SQLException e) {
            data.setCanConnect(false);
            log.severe("Impossibile capire se è presente la tabella delle guilds tra i database.");
            e.printStackTrace();
            plugin.disablePlugin();
        }
        return value;
    }

    public void createTable() {
        if (!tableExists()) {
            String query = "CREATE TABLE " + database + "(`id` INT AUTO_INCREMENT primary key NOT NULL, `guildName` TEXT NOT NULL, `guildId` TEXT NOT NULL, `bankBalance` TEXT NULL, `level` INT NOT NULL, `bought` INT NOT NULL);";

            try {
                PreparedStatement state = conn.prepareStatement(query);
                state.executeUpdate();
                log.info("Tabella delle guilds creata con successo.");
            } catch (SQLException e) {
                log.severe("Impossibile creare la tabella delle guilds.");
                e.printStackTrace();
                plugin.disablePlugin();
            }
        }
    }

    public String getGuildName(String guildId) {
        String guildName = "";
        String query = "SELECT * FROM " + database;

        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                if (rs.getString("guildId").equals(guildId)) {
                    guildName = rs.getString("guildName");
                    break;
                }
            }
        } catch (SQLException e) {
            log.severe("Impossibile ottenere il nome della gilda dall'id: " + guildId + "!");
            e.printStackTrace();
        }
        return guildName;
    }

    public void insertGuildData(String guildName, String guildId) {
        String query = "INSERT INTO " + database + "(guildName, guildId, level, bought) VALUES (?, ?, ?, ?);";

        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, guildName);
            state.setString(2, guildId);
            state.setInt(3, 1);
            state.setInt(4, 0);
            state.executeUpdate();
        } catch (SQLException e) {
            log.severe("Impossibile inserire il dato per la gilda corrente: " +
                    "\n Nome Gilda: " + guildName +
                    "\n Id Gilda: " + guildId + "" +
                    "\n...!");
            e.printStackTrace();
        }
    }

    public void updateGuildBalance(String guildId, String newBalance) {
        String query = "UPDATE " + database + " SET bankBalance='" + newBalance + "' WHERE guildId='" + guildId + "'";

        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.executeUpdate();
        } catch (SQLException e) {
            log.severe("Impossibile aggiornare il bilancio della gilda: " + getGuildName(guildId) + "!");
            e.printStackTrace();
        }
    }

    public void updateGuildLevel(String guildId, int newLevel) {
        String query = "UPDATE " + database + " SET level=" + newLevel + " WHERE guildId='" + guildId + "'";

        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.executeUpdate();
        } catch (SQLException e) {
            log.severe("Impossibile aggiornare il livello della gilda: " + getGuildName(guildId) + "!");
            e.printStackTrace();
        }
    }

    public void updateBoughtGuild(String guildId, int bought) {
        String query = "UPDATE " + database + " SET bought=" + bought + " WHERE guildId='" + guildId + "'";

        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.executeUpdate();
        } catch (SQLException e) {
            log.severe("Impossibile aggiornare lo stato di acquisto della gilda: " + getGuildName(guildId) + "!");
            e.printStackTrace();
        }
    }

    public boolean boughtStatus(String guildId) {
        boolean value = false;
        String query = "SELECT * FROM " + database;
        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                if (rs.getString("guildId").equals(guildId)) {
                    if (rs.getInt("bought") > 0) {
                        value = true;
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            log.severe("Impossibile capire se la gilda " + getGuildName(guildId) + " è stata mai acquistata.");
            e.printStackTrace();
        }
        return value;
    }

    public List<String> getGeneratedGuildsID() {
        List<String> generated = new ArrayList<>();
        String query = "SELECT * FROM " + database;
        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                generated.add(rs.getString("guildId") + ";" + rs.getString("guildName"));
            }
        } catch (SQLException e) {
            log.severe("Impossibile recuperare tutti gli id generati.");
            e.printStackTrace();
        }
        return generated;
    }
}
