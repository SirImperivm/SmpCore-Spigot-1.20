package me.sirimperivm.spigot.assets.managers.databases;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings("all")
public class Whitelist {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Logger log = Logger.getLogger("SMPCore");
    private static Db data = Main.getData();
    private static Modules mods = data.mods;

    static Connection conn = data.conn;
    String dbName = data.dbname;
    String tablePrefix = data.tablePrefix;
    String tableName = "whitelist";
    public String database = dbName + "." + tablePrefix + tableName;

    boolean tableExists() {
        boolean value = false;
        try {
            DatabaseMetaData dmd = conn.getMetaData();
            ResultSet rs = dmd.getTables(null, null, database, new String[] {"TABLE"});
            value = rs.next();
        } catch (SQLException e) {
            data.setCanConnect(false);
            log.severe("Impossibile capire se è presente la tabella della whitelist tra i database.");
            e.printStackTrace();
            plugin.disablePlugin();
        }
        return value;
    }

    public void createTable() {
        if (!tableExists()) {
            String query = "CREATE TABLE " + database + "(`id` INT AUTO_INCREMENT primary key NOT NULL, `playerName` TEXT NOT NULL, `playerUuid` TEXT NOT NULL);";

            try {
                PreparedStatement state = conn.prepareStatement(query);
                state.executeUpdate();
                log.info("Tabella whitelist creata con successo.");
            } catch (SQLException e) {
                log.severe("Impossibile creare la tabella della whitelist.");
                e.printStackTrace();
                plugin.disablePlugin();
            }
        }
    }

    public void insertMemberData(String username) {
        String query = "INSERT INTO " + database + "(playerName, playerUuid) VALUES (?, ?);";

        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, username);
            state.setString(2, "null");
            state.executeUpdate();
        } catch (SQLException e) {
            log.severe("Impossibile inserire un dato nella whitelist: " +
                    "\n Username: " + username +
                    "\n...!");
            e.printStackTrace();
        }
    }

    public void removeMemberData(String username) {
        String query = "DELETE FROM " + database + " WHERE playerName='" + username + "'";

        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.executeUpdate();
        } catch (SQLException e) {
            log.severe("Impossibile rimuovere un dato dalla whitelist: " +
                    "\n Username: " + username +
                    "\n...!");
            e.printStackTrace();
        }
    }

    public void updateUuid(String username, String uuid) {
        String query = "UPDATE " + database + " SET playerUuid='" + uuid + "' WHERE playerName='" + username + "'";

        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.executeUpdate();
        } catch (SQLException e) {
            log.severe("Impossibile aggiornare l'uuid dell'utente " + username + " in whitelist.");
            e.printStackTrace();
        }
    }

    public List<String> getWhitelistList() {
        List<String> whitelist = new ArrayList<String>();
        String query = "SELECT * FROM " + database;

        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                whitelist.add(rs.getString("playerName"));
            }
        } catch (SQLException e) {
            log.severe("Impossibile ottenere la lista dei whitelistati.");
            e.printStackTrace();
        }
        return whitelist;
    }

    public String getWhitelistedPlayer() {
        StringBuilder sb = new StringBuilder(conf.getSettings().getString("messages.others.whitelist.list-format.prefix"));
        String query = "SELECT * FROM " + database;

        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                String username = rs.getString("playerName");
                String commas = conf.getSettings().getString("messages.others.whitelist.list-format.commas");
                if (!rs.isLast()) {
                    String comma = commas.split("£")[0];
                    sb.append(conf.getSettings().getString("messages.others.whitelist.list-format.player")
                                    .replace("$username", username))
                            .append(comma);
                } else {
                    String dot = commas.split("£")[1];
                    sb.append(conf.getSettings().getString("messages.others.whitelist.list-format.player")
                                    .replace("$username", username))
                            .append(dot);
                }
            }
        } catch (SQLException e) {
            log.severe("Impossibile ottenere i player whitelistati.");
            e.printStackTrace();
        }
        return sb.toString();
    }

    public String getUuid(String username) {
        String uuid = "null";
        String query = "SELECT * FROM " + database;

        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                if (rs.getString("playerName").equalsIgnoreCase(username)) {
                    uuid = rs.getString("playerUuid");
                    break;
                }
            }
        } catch (SQLException e) {
            log.severe("Impossibile ottenere l'uuid dell'utente " + username + "!");
            e.printStackTrace();
        }
        return uuid;
    }

    public boolean existMemberData(String username) {
        boolean value = false;
        String query = "SELECT * FROM " + database;

        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                if (rs.getString("playerName").equalsIgnoreCase(username)) {
                    value = true;
                    break;
                }
            }
        } catch (SQLException e) {
            log.severe("Impossibile ottenere informazioni su una whitelist: " +
                    "\n Username: " + username +
                    "\n...!");
            e.printStackTrace();
        }
        return value;
    }
}
