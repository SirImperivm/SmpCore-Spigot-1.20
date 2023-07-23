package me.sirimperivm.spigot.assets.managers;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.databases.GuildMembers;
import me.sirimperivm.spigot.assets.managers.databases.Guilds;
import me.sirimperivm.spigot.assets.managers.databases.Lives;
import me.sirimperivm.spigot.assets.managers.databases.Tasks;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

@SuppressWarnings("all")
public class Db {

    private static Main plugin = Main.getPlugin();
    private static Logger log = Logger.getLogger("SMPCore");
    private static Config conf = Main.getConf();
    public static Modules mods = Main.getMods();
    private static Guilds guilds;
    private static GuildMembers guildMembers;
    private static Tasks tasks;
    private static Lives lives;
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
                log.info("Plugin connesso al database con successo!");
            } catch (SQLException e) {
                setCanConnect(false);
                log.severe("Impossibile connettersi al database.");
                e.printStackTrace();
                plugin.disablePlugin();
            }
        } else {
            log.severe("Impossibile connettersi al database.");
            plugin.disablePlugin();
        }
    }

    public void closeConnect() {
        if (canConnect) {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                    log.info("Plugin disconnesso dal database.");
                }
            } catch (SQLException e) {
                log.severe("Impossibile disconnettersi dal database.");
                e.printStackTrace();
            }
        }
    }

    public Db() {
        dataConnect();
        guilds = new Guilds();
        guildMembers = new GuildMembers();
        tasks = new Tasks();
        lives = new Lives();
        guilds.createTable();
        guildMembers.createTable();
        tasks.createTable();
        lives.createTable();
    }

    public static Guilds getGuilds() {
        return guilds;
    }

    public static GuildMembers getGuildMembers() {
        return guildMembers;
    }

    public static Tasks getTasks() {
        return tasks;
    }

    public static Lives getLives() {
        return lives;
    }
}
