package me.sirimperivm.spigot.modules.listeners;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("all")
public class DamageListener implements Listener {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Modules mods = Main.getMods();
    private static Db data = Main.getData();

    private static Boolean livesListener = Main.getLivesListener();

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        Entity en = e.getEntity();

        if (en instanceof Player) {
            Player p = (Player) en;
            HashMap<String, List<String>> guildsData = data.getGuildMembers().guildsData();
            if (livesListener) {
                String playerName = p.getName();
                double damageValue = e.getFinalDamage();
                int lives = data.getLives().getPlayerLives(p);
                if (damageValue >= p.getHealth()) {
                    e.setCancelled(true);
                    p.setGameMode(GameMode.SPECTATOR);

                    boolean canRespawn = data.getLives().canRespawn(p);
                    if (canRespawn) {

                    } else {

                    }
                }
            }
        }
    }
}
