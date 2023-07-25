package me.sirimperivm.spigot.modules.listeners;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

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
        String playerName = p.getName();

        if (livesListener) {
            if (!p.hasPlayedBefore() || !data.getLives().existMemberData(p)) {
                data.getLives().insertMemberData(p, defaultLivesCount, 0, 1);
            }

            if (data.getLives().isDead(p)) {
                if (p.getGameMode() != GameMode.SPECTATOR) {
                    p.setGameMode(GameMode.SPECTATOR);
                }
                boolean canRespawn = data.getLives().canRespawn(p);

                if (canRespawn) {
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
                                }
                            }
                        }.runTaskLater(plugin, 20 * 5);
                    }
                }
            }
        }
    }
}
