package me.sirimperivm.spigot.assets.managers;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.databases.Guilds;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SuppressWarnings("all")
public class Db {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Guilds guilds;

    public static Connection conn;
    public static String dbname = conf.getSettings().getString("settings.database.dbname");
    public static String tablePrefix = conf.getSettings().getString("settings.database.tablePrefix");
    boolean canConnect = true;

    public void setCanConnect(boolean value) {
        canConnect = value;
    }

    private void dataConnect() {
        String host = conf.getSettings().getString("settings.database.host");
        if (host.equalsIgnoreCase("localhost")) {
            host = "127.0.0.1";
        }
        int port = conf.getSettings().getInt("settings.database.port");
        String username = conf.getSettings().getString("settings.database.username");
        String password = conf.getSettings().getString("settings.database.password");
        String dbType = conf.getSettings().getString("settings.dbType");
        dbType = dbType.toLowerCase();

        if (!dbType.equals("mysql") && !dbType.equals("mariadb")) {
            setCanConnect(false);
        }

        if (canConnect) {
            String url = "jdbc:" + dbType + "://" + host + ":" + port + "/" + dbname;

            try {
                conn = DriverManager.getConnection(url, username, password);
                plugin.log("success", "Plugin connesso al database con successo!");
            } catch (SQLException e) {
                setCanConnect(false);
                plugin.log("severe", "Impossibile connettersi al database.");
                e.printStackTrace();
                plugin.disablePlugin();
            }
        } else {
            plugin.log("severe", "Impossibile connettersi al database.");
            plugin.disablePlugin();
        }
    }

    public void closeConnect() {
        if (canConnect) {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                    plugin.log("success", "Plugin disconnesso dal database.");
                }
            } catch (SQLException e) {
                plugin.log("severe", "Impossibile disconnettersi dal database.");
                e.printStackTrace();
            }
        }
    }

    public Db() {
        dataConnect();
        guilds = new Guilds();
        guilds.createTable();
    }

    public static Guilds getGuilds() {
        return guilds;
    }
}
