package me.sirimperivm.spigot.modules.listeners;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@SuppressWarnings("all")
public class QuitListener implements Listener {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Modules mods = Main.getMods();
    private static Db data = Main.getData();

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        if (conf.getSettings().getBoolean("settings.messages.leave")) {
            for (Player players : Bukkit.getOnlinePlayers()) {
                players.sendMessage(Config.getTransl("settings", "messages.others.quit-message")
                        .replace("$username", p.getName()));
            }
        }
    }
}
