package me.sirimperivm.spigot;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.managers.placeholderapi.PapiExpansions;
import me.sirimperivm.spigot.assets.managers.values.Vault;
import me.sirimperivm.spigot.assets.managers.worldguard.Wg;
import me.sirimperivm.spigot.assets.utils.Colors;
import me.sirimperivm.spigot.modules.commands.admin.core.AdminSmpcCommand;
import me.sirimperivm.spigot.modules.commands.admin.guilds.AdminGuildsCommand;
import me.sirimperivm.spigot.modules.commands.users.guilds.GuildsCommand;
import me.sirimperivm.spigot.modules.listeners.*;
import me.sirimperivm.spigot.modules.tabCompleters.AdminGuildsTabCompleter;
import me.sirimperivm.spigot.modules.tabCompleters.AdminSmpcTabCompleter;
import me.sirimperivm.spigot.modules.tabCompleters.GuildsTabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

import static org.bukkit.Bukkit.getConsoleSender;
import static org.bukkit.Bukkit.getPluginManager;

@SuppressWarnings("all")
public final class Main extends JavaPlugin {
    private static Logger log = Logger.getLogger("SMPCore");

    private static StateFlag smpcGuilds;
    private static Flag<String> smpcGuildId;

    private static Main plugin;
    private static Config conf;
    private static Db data;
    private static Modules mods;
    private static Vault vault;
    private static Wg regionManager;
    private static PapiExpansions papi;
    private static String successPrefix;
    private static String infoPrefix;
    private static String failPrefix;
    private static Boolean livesListener;
    private static Integer defaultLivesCount;

    public void disablePlugin() {
        getPluginManager().disablePlugin(this);
    }

    void registerExpansions() {
        if (getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PapiExpansions(plugin).register();
        }
    }

    void setup() {
        plugin = this;
        conf = new Config();
        conf.loadAll();
        livesListener = conf.getSettings().getBoolean("settings.programs.livesListener");
        defaultLivesCount = conf.getSettings().getInt("settings.lives.defaultLivesCount");
        vault = new Vault();
        successPrefix = Colors.text(conf.getSettings().getString("messages.prefixes.success"));
        infoPrefix = Colors.text(conf.getSettings().getString("messages.prefixes.info"));
        failPrefix = Colors.text(conf.getSettings().getString("messages.prefixes.fail"));
        data = new Db();
        mods = new Modules();
        papi = new PapiExpansions(plugin);
        registerExpansions();
    }

    void closeup() {
        conf.saveAll();
    }

    @Override
    public void onLoad() {
        regionManager = new Wg();
    }

    @Override
    public void onEnable() {
        setup();
        getServer().getPluginCommand("ga").setExecutor(new AdminGuildsCommand());
        getServer().getPluginCommand("smpc").setExecutor(new AdminSmpcCommand());
        getServer().getPluginCommand("g").setExecutor(new GuildsCommand());

        getServer().getPluginCommand("ga").setTabCompleter(new AdminGuildsTabCompleter());
        getServer().getPluginCommand("smpc").setTabCompleter(new AdminSmpcTabCompleter());
        getServer().getPluginCommand("g").setTabCompleter(new GuildsTabCompleter());

        getServer().getPluginManager().registerEvents(new ClickListener(), this);
        getServer().getPluginManager().registerEvents(new MoveListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new DamageListener(), this);

        getConsoleSender().sendMessage(Colors.text("&a[SMPCore] Plugin attivato correttamente!"));
        getConsoleSender().sendMessage(Colors.text("<RAINBOW1>Plugin ideato e sviluppato da SirImperivm_</RAINBOW>"));
    }

    @Override
    public void onDisable() {
        closeup();
        getConsoleSender().sendMessage(Colors.text("&a[SMPCore] Plugin disattivato correttamente!"));
    }

    public static Db getData() {
        return data;
    }

    public static Modules getMods() {
        return mods;
    }

    public static Config getConf() {
        return conf;
    }

    public static Vault getVault() {
        return vault;
    }

    public static Wg getRegionManager() {
        return regionManager;
    }

    public static String getSuccessPrefix() {
        return successPrefix;
    }

    public static String getInfoPrefix() {
        return infoPrefix;
    }

    public static String getFailPrefix() {
        return failPrefix;
    }

    public static Main getPlugin() {
        return plugin;
    }

    public static StateFlag getSmpcGuilds() {
        return smpcGuilds;
    }

    public static Flag<String> getSmpcGuildId() {
        return smpcGuildId;
    }

    public static Boolean getLivesListener() {
        return livesListener;
    }

    public static Integer getDefaultLivesCount() {
        return defaultLivesCount;
    }
}
