package me.sirimperivm.spigot.modules.listeners;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

@SuppressWarnings("all")
public class CommandListener implements Listener {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Modules mods = Main.getMods();
    private static Db data = Main.getData();

    private static Boolean livesListener = Main.getLivesListener();

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        String message = e.getMessage();
        if (livesListener) {
            boolean canRespawn = data.getLives().canRespawn(p);
            if (!canRespawn) {
                String bypassPermission = conf.getSettings().getString("permissions.admin-actions.lives.blockedCommands.bypass");
                for (String line : conf.getSettings().getStringList("settings.lives.cantRespawnBlockedCommands")) {
                    if (message.equalsIgnoreCase(line) && !p.hasPermission(bypassPermission)) {
                        e.setCancelled(true);
                        p.sendMessage(Config.getTransl("settings", "messages.errors.lives.commandBlocked"));
                    }
                }
            }
        }
    }
}
