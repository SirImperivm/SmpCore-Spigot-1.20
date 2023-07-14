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
public class Tasks {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Logger log = Logger.getLogger("SMPCore");
    private static Db data = Main.getData();
    private static Modules mods = data.mods;
    static Connection conn = data.conn;
    String dbName = data.dbname;
    String tablePrefix = data.tablePrefix;
    String tableName = "tasks";
    public String database = dbName + "." + tablePrefix + tableName;

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
            String query = "CREATE TABLE " + database + "(`taskId` INT AUTO_INCREMENT primary key NOT NULL, `taskType` TEXT NOT NULL, `taskValue` TEXT NULL);";

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

    public void insertTask(String taskType, String taskValue) {
        String query = "INSERT INTO " + database + "(taskType, taskValue) VALUES (?, ?)";

        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, taskType);
            state.setString(2, taskValue);
            state.executeUpdate();
        } catch (SQLException e) {
            log.severe("Impossibile inserire una task: " +
                    "\n TaskType: " + taskType +
                    "\n TaskValue: " + taskValue +
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
