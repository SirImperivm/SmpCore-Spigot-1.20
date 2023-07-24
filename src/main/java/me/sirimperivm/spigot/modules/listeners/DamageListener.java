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
import org.bukkit.scheduler.BukkitRunnable;

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
                    data.getLives().updateIsDead(p, 1);

                    if (lives > 0) {
                        data.getLives().updateCanRespawn(p, 1);
                    } else {
                        data.getLives().updateCanRespawn(p, 0);
                    }

                    boolean canRespawn = data.getLives().canRespawn(p);
                    if (canRespawn) {
                        data.getLives().updatePlayerLives(p, data.getLives().getPlayerLives(p) -1);
                        p.sendMessage(Config.getTransl("settings", "messages.info.lives.members.death.can-respawn")
                                .replace("$livesCount", String.valueOf(data.getLives().getPlayerLives(p))));
                        p.sendMessage(Config.getTransl("settings", "messages.info.lives.members.death.respawn-in"));

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (mods.getGuildsData().containsKey(playerName)) {
                                    String guildId = mods.getGuildsData().get(playerName).get(0);
                                    if (p != null) {
                                        p.setGameMode(GameMode.SURVIVAL);
                                        mods.sendPlayerToGhome(p, data.getGuilds().getGuildName(guildId));
                                        data.getLives().updateIsDead(p, 0);
                                    }
                                } else {
                                    if (p != null) {
                                        p.setGameMode(GameMode.SURVIVAL);
                                        mods.sendPlayerToLobby(p);
                                        data.getLives().updateIsDead(p, 0);
                                    }
                                }
                            }
                        }.runTaskLater(plugin, 20 * 5);
                    } else {
                        p.sendMessage(Config.getTransl("settings", "messages.info.lives.members.death.cant-respawn"));
                        if (!mods.isDeathZoneLocated()) {
                            p.sendMessage(Config.getTransl("settings", "messages.errors.lives.deathZone.not-located"));
                        } else {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (p != null) {
                                        p.setGameMode(GameMode.SURVIVAL);
                                        mods.sendPlayerToDeathZone(p);
                                        data.getLives().updateIsDead(p, 0);
                                    }
                                }
                            }.runTaskLater(plugin, 20 * 5);
                        }
                    }
                }
            }
        }
    }
}
