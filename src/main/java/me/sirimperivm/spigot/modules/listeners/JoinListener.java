package me.sirimperivm.spigot.modules.listeners;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@SuppressWarnings("all")
public class JoinListener implements Listener {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Modules mods = Main.getMods();
    private static Db data = Main.getData();

    private static Boolean livesListener = Main.getLivesListener();
    private static Integer defaultLivesCount = Main.getDefaultLivesCount();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if (livesListener) {
            if (!p.hasPlayedBefore() || !data.getLives().existMemberData(p)) {
                data.getLives().insertMemberData(p, defaultLivesCount, 0, 1);
            }
        }
    }
}
