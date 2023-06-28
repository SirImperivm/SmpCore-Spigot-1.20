package me.sirimperivm.spigot;

import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.managers.placeholderapi.PapiExpansions;
import me.sirimperivm.spigot.assets.managers.values.Vault;
import me.sirimperivm.spigot.assets.utils.Colors;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getConsoleSender;
import static org.bukkit.Bukkit.getPluginManager;

@SuppressWarnings("all")
public final class Main extends JavaPlugin {

    private static Main plugin;
    private static Config conf;
    private static Db data;
    private static Vault vault;
    private static Modules mods;
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
        papi = new PapiExpansions(plugin);
        registerExpansions();
        mods = new Modules();
    }

    void closeup() {
        conf.saveAll();
    }

    @Override
    public void onEnable() {
        setup();
        getConsoleSender().sendMessage(Colors.text("<RAINBOW:acf>[SMPCore] Plugin attivato correttamente!</RAINBOW>"));
    }

    @Override
    public void onDisable() {
        closeup();
        getConsoleSender().sendMessage(Colors.text("<RAINBOW:acf>[SMPCore] Plugin disattivato correttamente!</RAINBOW>"));
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
