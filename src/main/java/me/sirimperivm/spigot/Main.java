package me.sirimperivm.spigot;

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
import me.sirimperivm.spigot.modules.listeners.ChatListener;
import me.sirimperivm.spigot.modules.listeners.ClickListener;
import me.sirimperivm.spigot.modules.listeners.MoveListener;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getConsoleSender;
import static org.bukkit.Bukkit.getPluginManager;

@SuppressWarnings("all")
public final class Main extends JavaPlugin {
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

        getServer().getPluginManager().registerEvents(new ClickListener(), this);
        getServer().getPluginManager().registerEvents(new MoveListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
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
}
