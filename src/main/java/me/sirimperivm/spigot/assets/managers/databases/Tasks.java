package me.sirimperivm.spigot.assets.managers.databases;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings("all")
public class Tasks {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Logger log = Logger.getLogger("SMPCore");
    private static Db data = Main.getData();
    private static Modules mods = Main.getMods();
    static Connection conn = data.conn;
    String dbName = data.dbname;
    String tablePrefix = data.tablePrefix;
    String tableName = "tasks";
    String database = dbName + "." + tablePrefix + tableName;

    boolean tableExists() {
        boolean value = false;
        try {
            DatabaseMetaData dmd = conn.getMetaData();
            ResultSet rs = dmd.getTables(null, null, database, new String[]{"TABLE"});
            value = rs.next();
        } catch (SQLException e) {
            data.setCanConnect(false);
            log.severe("Impossibile capire se è presente la tabella delle tasks tra i database.");
            e.printStackTrace();
            plugin.disablePlugin();
        }
        return value;
    }

    public void createTable() {
        if (!tableExists()) {
            String query = "CREATE TABLE " + database + "(`taskId` INT AUTO_INCREMENT primary key NOT NULL, `taskType` TEXT NOT NULL, `taskValue` TEXT NULL, `persistent` INT NOT NULL);";

            try {
                PreparedStatement state = conn.prepareStatement(query);
                state.executeUpdate();
                log.info("Tabella delle tasks creata con successo.");
            } catch (SQLException e) {
                log.severe("Impossibile creare la tabella delle tasks.");
                e.printStackTrace();
                plugin.disablePlugin();
            }
        }
    }

    public void insertTask(String taskType, String taskValue, int persistent) {
        String query = "INSERT INTO " + database + "(taskType, taskValue, persistent) VALUES (?, ?, ?)";

        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, taskType);
            state.setString(2, taskValue);
            state.setInt(3, persistent);
            state.executeUpdate();
        } catch (SQLException e) {
            log.severe("Impossibile inserire una task: " +
                    "\n TaskType: " + taskType +
                    "\n TaskValue: " + taskValue +
                    "\n Persistent: " + persistent +
                    "...!");
            e.printStackTrace();
        }
    }

    public void deleteTask(int taskId) {
        String query = "DELETE FROM " + database + " WHERE taskId='" + taskId + "'";

        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.executeUpdate();
        } catch (SQLException e) {
            log.severe("Impossibile portare a termine una task. (" + taskId + ")");
            e.printStackTrace();
        }
    }

    public void executeTask(int taskId) {
        String query = "SELECT * FROM " + database;

        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                String taskType = rs.getString("taskType");
                int persistent = rs.getInt("persistent");
                switch (taskType) {
                    case "expelGuildMember":
                        String taskValue = rs.getString("taskValue");
                        Player target = Bukkit.getPlayerExact(taskValue);
                        if (target != null) {
                            mods.removeMember(target);
                            updatePersistent(taskId, 0);
                        }
                        break;
                    default:
                        break;
                }
                if (persistent == 0) {
                    deleteTask(taskId);
                }
            }
        } catch (SQLException e) {
            log.severe("Impossibile eseguire la task " + taskId + "!");
            e.printStackTrace();
        }
    }

    public void updatePersistent(int taskId, int persistent) {
        String query = "UPDATE " + database + " SET persistent='" + persistent + "' WHERE taskId='" + taskId + "'";

        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.executeUpdate();
        } catch (SQLException e) {
            log.severe("Impossibile aggiornare lo stato di persistenza di della task: " + taskId + "!");
            e.printStackTrace();
        }
    }

    public List<Integer> returnAllTasks() {
        List<Integer> taskList = new ArrayList<>();
        String query = "SELECT * FROM " + database;

        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                taskList.add(rs.getInt("taskId"));
            }
        } catch (SQLException e) {
            log.severe("Impossibile ottenere gli id per ogni tasks da eseguire.");
            e.printStackTrace();
        }
        return taskList;
    }
}
