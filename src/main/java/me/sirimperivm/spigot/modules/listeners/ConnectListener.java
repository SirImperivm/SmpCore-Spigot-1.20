package me.sirimperivm.spigot.modules.listeners;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

@SuppressWarnings("all")
public class ConnectListener implements Listener {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Modules mods = Main.getMods();
    private static Db data = Main.getData();

    @EventHandler
    public void onConnect(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        String playerName = p.getName();

        if (conf.getSettings().getBoolean("settings.whitelist.enabled")) {
            if (!data.getWhitelist().existMemberData(playerName)) {
                e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, Config.getTransl("settings", "messages.info.whitelist.target.not-in"));
            } else {
                if (data.getWhitelist().getUuid(playerName) != p.getUniqueId().toString()) {
                    data.getWhitelist().updateUuid(playerName, p.getUniqueId().toString());
                }
            }
        }
    }
}
