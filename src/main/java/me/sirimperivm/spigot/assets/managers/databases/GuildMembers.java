package me.sirimperivm.spigot.assets.managers.databases;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings("all")
public class GuildMembers {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Logger log = Logger.getLogger("SMPCore");
    private static Db data = Main.getData();

    static Connection conn = data.conn;
    String dbName = data.dbname;
    String tablePrefix = data.tablePrefix;
    String tableName = "guild_members";
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
            String query = "CREATE TABLE " + database + "(`id` INT AUTO_INCREMENT primary key NOT NULL, `username` TEXT NOT NULL, `memberId` TEXT NOT NULL, `guildId` TEXT NOT NULL, `guildRole` TEXT NOT NULL);";

            try {
                PreparedStatement state = conn.prepareStatement(query);
                state.executeUpdate();
                log.info("Tabella dei membri delle guilds creata con successo.");
            } catch (SQLException e) {
                log.severe("Impossibile creare la tabella dei membri delle guilds.");
                e.printStackTrace();
                plugin.disablePlugin();
            }
        }
    }

    public void insertMemberData(String username, String memberId, String guildId, String guildRole) {
        String query = "INSERT INTO " + database + "(username, memberId, guildId, guildRole) VALUES (?, ?, ?, ?);";

        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, username);
            state.setString(2, memberId);
            state.setString(3, guildId);
            state.setString(4, guildRole);
            state.executeUpdate();
        } catch (SQLException e) {
            log.severe("Impossibile inserire un dato nella tabella guildMembers, valori: " +
                    "\n Username: " + username +
                    "\n UserId: " + memberId +
                    "\n GuildId: " + guildId +
                    "\n GuildRole " + guildRole +
                    "\n...!");
            e.printStackTrace();
        }
    }

    public List<String> getGeneratedMembersID() {
        List<String> generated = new ArrayList<>();
        String query = "SELECT * FROM " + database;
        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                generated.add(rs.getString("memberId") + ";" + rs.getString("username"));
            }
        } catch (SQLException e) {
            log.severe("Impossibile recuperare tutti gli id generati.");
            e.printStackTrace();
        }
        return generated;
    }

    public List<String> getGuildMembers() {
        List<String> guildMembers = new ArrayList<>();
        String query = "SELECT * FROM " + database;
        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                guildMembers.add(rs.getString("username") + ";" + rs.getString("guildId"));
            }
        } catch (SQLException e) {
            log.severe("Impossibile associare i partecipanti di una gilda.");
            e.printStackTrace();
        }
        return guildMembers;
    }

    public String getGuildIdFromMember(String username) {
        String guildId = "null";
        String query = "SELECT * FROM " + database;
        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                if (rs.getString("username").equalsIgnoreCase(username)) {
                    guildId = rs.getString("guildId");
                    break;
                }
            }
        } catch (SQLException e) {
            log.severe("Impossibile ottenere l'id della gilda relativa all'utente " + username + "!");
            e.printStackTrace();
        }
        return guildId;
    }
}
