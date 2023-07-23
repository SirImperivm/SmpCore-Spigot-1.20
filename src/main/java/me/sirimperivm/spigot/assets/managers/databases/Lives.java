package me.sirimperivm.spigot.assets.managers.databases;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.logging.Logger;

@SuppressWarnings("all")
public class Lives {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Logger log = Logger.getLogger("SMPCore");
    private static Db data = Main.getData();
    private static Modules mods = Main.getMods();

    static Connection conn = data.conn;
    String dbName = data.dbname;
    String tablePrefix = data.tablePrefix;
    String tableName = "player_lives";
    public String database = dbName + "." + tablePrefix + tableName;

    boolean tableExists() {
        boolean value = false;
        try {
            DatabaseMetaData dmd = conn.getMetaData();
            ResultSet rs = dmd.getTables(null, null, database, new String[] {"TABLE"});
            value = rs.next();
        } catch (SQLException e) {
            data.setCanConnect(false);
            log.severe("Impossibile capire se è presente la tabella delle vite dei player tra i database.");
            e.printStackTrace();
            plugin.disablePlugin();
        }
        return value;
    }

    public void createTable() {
        if (!tableExists()) {
            String query = "CREATE TABLE " + database + "(`id` INT AUTO_INCREMENT primary key NOT NULL, `playerName` TEXT NOT NULL, `livesCount` INT NOT NULL, `isDead` INT NOT NULL, `canRespawn` INT NOT NULL);";

            try {
                PreparedStatement state = conn.prepareStatement(query);
                state.executeUpdate();
                log.info("Tabella delle vite dei player creata con successo.");
            } catch (SQLException e) {
                log.severe("Impossibile creare la tabella delle vite dei player.");
                e.printStackTrace();
                plugin.disablePlugin();
            }
        }
    }

    public void insertMemberData(Player p, int livesCount, int isDead, int canRespawn) {
        String playerName = p.getName();
        String query = "INSERT INTO " + database + "(playerName, livesCount, isDead, canRespawn) VALUES (?, ?, ?, ?);";

        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, playerName);
            state.setInt(2, livesCount);
            state.setInt(3, isDead);
            state.setInt(4, canRespawn);
            state.executeUpdate();
        } catch (SQLException e) {
            log.severe("Impossibile inserire i dati nella tabella delle vite dei membri, valori: " +
                    "\n Username: " + playerName +
                    "\n livesCount" + livesCount +
                    "\n isDead: " + isDead +
                    "\n canRespawn: " + canRespawn +
                    "\n...!");
            e.printStackTrace();
        }
    }

    public void updatePlayerLives(Player p, int newLives) {
        String username = p.getName();
        String query = "UPDATE " + database + " SET livesCount=" + newLives + " WHERE playerName='" + username + "'";

        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.executeUpdate();
        } catch (SQLException e) {
            log.severe("Impossibile aggiornare le vite per l'utente " + username + "!");
            e.printStackTrace();
        }
    }

    public void updateCanRespawn(Player p, int newRespawn) {
        String username = p.getName();
        String query = "UPDATE " + database + " SET canRespawn=" + newRespawn + " WHERE playerName='" + username + "'";

        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.executeUpdate();
        } catch (SQLException e) {
            log.severe("Impossibile aggiornare lo stato di respawn per l'utente " + username + "!");
            e.printStackTrace();
        }
    }

    public void updateIsDead(Player p, int newDead) {
        String username = p.getName();
        String query = "UPDATE " + database + " SET isDead=" + newDead + " WHERE playerName='" + username + "'";

        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.executeUpdate();
        } catch (SQLException e) {
            log.severe("Impossibile aggiornare lo stato di morte per l'utente " + username + "!");
            e.printStackTrace();
        }
    }

    public int getPlayerLives(Player p) {
        int lives = 0;
        String username = p.getName();
        String query = "SELECT * FROM " + database;

        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                if (rs.getString("playerName").equals(username)) {
                    lives = rs.getInt("livesCount");
                }
            }
        } catch (SQLException e) {
            log.severe("Impossibile ottenere la quantità di vite per l'utente " + username + "!");
            e.printStackTrace();
        }
        return lives;
    }

    public boolean isDead(Player p) {
        boolean value = false;
        String playerName = p.getName();
        String query = "SELECT * FROM " + database;

        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                if (rs.getString("playerName").equals(playerName)) {
                    if (rs.getInt("isDead") == 1) {
                        value = true;
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            log.severe("Impossibile ottenere lo stato di morte del player " + playerName + "!");
            e.printStackTrace();
        }
        return value;
    }

    public boolean canRespawn(Player p) {
        boolean value = false;
        String playerName = p.getName();
        String query = "SELECT * FROM " + database;

        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                if (rs.getString("playerName").equals(playerName)) {
                    if (rs.getInt("canRespawn") == 1) {
                        value = true;
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            log.severe("Impossibile ottenere lo stato di respawn del player " + playerName + "!");
            e.printStackTrace();
        }
        return value;
    }

    public boolean existMemberData(Player p) {
        boolean value = false;
        String playerName = p.getName();
        String query = "SELECT * FROM " + database;

        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                if (rs.getString("playerName").equals(playerName)) {
                    value = true;
                    break;
                }
            }
        } catch (SQLException e) {
            log.severe("Impossibile capire se è presente qualche dato inerente al player " + playerName + "!");
            e.printStackTrace();
        }
        return value;
    }
}
