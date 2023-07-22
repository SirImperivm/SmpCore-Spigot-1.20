package me.sirimperivm.spigot.assets.managers.databases;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings("all")
public class GuildMembers {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Logger log = Logger.getLogger("SMPCore");
    private static Db data = Main.getData();
    private static Modules mods = data.mods;

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
            log.severe("Impossibile capire se è presente la tabella dei membri delle guilds tra i database.");
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

    private boolean existMemberData(String username) {
        boolean value = false;
        String query = "SELECT * FROM " + database;

        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                if (rs.getString("username").equalsIgnoreCase(username)) {
                    value = true;
                    break;
                }
            }
        } catch (SQLException e) {
            log.severe("Impossibile capire se esistono dati sul membro " + username + "!");
            e.printStackTrace();
        }
        return value;
    }

    public void removeMemberData(String username) {
        if (existMemberData(username)) {
            String query = "DELETE FROM " + database + " WHERE username='" + username + "';";

            try {
                PreparedStatement state = conn.prepareStatement(query);
                state.executeUpdate();
            } catch (SQLException e) {
                log.severe("Impossibile cancellare il dato del membro " + username + "!");
                e.printStackTrace();
            }
        }
    }

    public void updateMemberData(String username, String key, String value) {
        String query = "UPDATE " + database + " SET " + key + "='" + value + "' WHERE username='" + username + "'";

        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.executeUpdate();
        } catch (SQLException e) {
            log.severe("Impossibile modificare i dati del membro " + username + "!");
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

    public String guildLeader(String guildId) {
        String guildLeader = "null";
        String query = "SELECT * FROM " + database;
        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                if (rs.getString("guildId").equalsIgnoreCase(guildId) && rs.getString("guildRole").equalsIgnoreCase("leader")) {
                    guildLeader = rs.getString("username");
                    break;
                }
            }
        } catch (SQLException e) {
            log.severe("Impossibile ottenere il nome del proprietario di una gilda.");
            e.printStackTrace();
        }
        return guildLeader;
    }

    public String getOnlineOfficers(String guildId) {
        StringBuilder sb = new StringBuilder(conf.getSettings().getString("messages.others.guilds.guild-info.general.online-officers.format-prefix"));
        String query = "SELECT * FROM " + database;

        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                if (rs.getString("guildId").equalsIgnoreCase(guildId)) {
                    if (rs.getString("guildRole").equalsIgnoreCase("officer")) {
                        String username = rs.getString("username");
                        Player p = Bukkit.getPlayerExact(username);
                        if (p != null) {
                            sb.append(conf.getSettings().getString("messages.others.guilds.guild-info.general.online-officers.userformat")
                                    .replace("$username", username))
                                    .append(conf.getSettings().getString("messages.others.guilds.guild-info.general.online-officers.commas"));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            log.severe("Impossibile ottenere gli officers online.");
            e.printStackTrace();
        }
        return sb.toString();
    }

    public String getOnlineGuilders(String guildId) {
        StringBuilder sb = new StringBuilder(conf.getSettings().getString("messages.others.guilds.guild-info.general.online-guilders.format-prefix"));
        String query = "SELECT * FROM " + database;

        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                if (rs.getString("guildId").equalsIgnoreCase(guildId)) {
                    if (rs.getString("guildRole").equalsIgnoreCase("member")) {
                        String username = rs.getString("username");
                        Player p = Bukkit.getPlayerExact(username);
                        if (p != null) {
                            sb.append(conf.getSettings().getString("messages.others.guilds.guild-info.general.online-guilders.userformat")
                                            .replace("$username", username))
                                    .append(conf.getSettings().getString("messages.others.guilds.guild-info.general.online-guilders.commas"));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            log.severe("Impossibile ottenere i gildani online.");
            e.printStackTrace();
        }
        return sb.toString();
    }

    public int getOfficersCount(String guildId) {
        int count = 0;
        String query = "SELECT * FROM " + database;

        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                if (rs.getString("guildId").equalsIgnoreCase(guildId)) {
                    if (rs.getString("guildRole").equalsIgnoreCase("officer")) {
                        count++;
                    }
                }
            }
        } catch (SQLException e) {
            log.severe("Impossibile ottenere la quantità di officer presenti nella gilda " + data.getGuilds().getGuildName(guildId) + "!");
            e.printStackTrace();
        }
        return count;
    }

    public int getGuildersCount(String guildId) {
        int count = 0;
        String query = "SELECT * FROM " + database;

        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                if (rs.getString("guildId").equalsIgnoreCase(guildId)) {
                    if (rs.getString("guildRole").equalsIgnoreCase("member")) {
                        count++;
                    }
                }
            }
        } catch (SQLException e) {
            log.severe("Impossibile ottenere la quantità di gildani presenti nella gilda " + data.getGuilds().getGuildName(guildId) + "!");
            e.printStackTrace();
        }
        return count;
    }

    public HashMap<String, List<String>> guildsData() {
        HashMap<String, List<String>> guildsData = new HashMap<String, List<String>>();
        String query = "SELECT * FROM " + database;
        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                List<String> guildAndRole = new ArrayList<String>();
                guildAndRole.add(rs.getString("guildId"));
                guildAndRole.add(rs.getString("guildRole"));
                guildsData.put(rs.getString("username"), guildAndRole);
            }
        } catch (SQLException e) {
            log.severe("Impossibile ricevere i dati inerenti alle gilde dei membri.");
            e.printStackTrace();
        }
        return guildsData;
    }

    public int getMembersCount(String guildId) {
        int count = 0;
        String query = "SELECT * FROM " + database;
        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                if (rs.getString("guildId").equalsIgnoreCase(guildId)) {
                    count++;
                }
            }
        } catch (SQLException e) {
            log.severe("Impossibile ottenere il conto dei membri per la gilda " + data.getGuilds().getGuildName(guildId) + "!");
            e.printStackTrace();
        }
        return count;
    }
}
