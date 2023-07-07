package me.sirimperivm.spigot.assets.managers.values;

import me.sirimperivm.spigot.Main;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.logging.Logger;

@SuppressWarnings("all")
public class Vault {

    private static Main plugin = Main.getPlugin();
    private static Economy econ = null;
    private static Permission perms = null;
    private static Logger log = Logger.getLogger("SMPCore");

    public Vault() {
        if (!setupEconomy()) {
            log.severe("Libreria vault non installata, disattivo il plugin.");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    private boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public static Economy getEcon() {
        return econ;
    }

    public static Permission getPerms() {
        return perms;
    }
}
