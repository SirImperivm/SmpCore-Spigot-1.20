package me.sirimperivm.spigot.modules.listeners;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.managers.values.Vault;
import me.sirimperivm.spigot.assets.other.Strings;
import me.sirimperivm.spigot.assets.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
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

    boolean deathFromPlayer = false;

    @EventHandler
    public void onDeath(EntityDamageByEntityEvent e) {
        Entity en = e.getEntity();
        Entity damager = e.getDamager();

        if (en instanceof Player) {
            Player p = (Player) en;
            int playerDeaths = data.getStats().getPlayerData(p, "deaths");
            double damage = e.getFinalDamage();

            if (damager instanceof Player) {
                if (damage >= p.getHealth()) {
                    for (int i=0; i<p.getInventory().getSize(); i++) {
                        ItemStack is = p.getInventory().getItem(i);
                        if (is != null) {
                            p.getWorld().dropItem(p.getLocation(), is);
                            p.getInventory().remove(is);
                        }
                    }
                    Player killer = (Player) damager;

                    String killerName = killer.getName();
                    String playerName = p.getName();

                    int playerKills = data.getStats().getPlayerData(killer, "kills");
                    int addDeath = playerDeaths + 1;
                    data.getStats().updatePlayerData(p, "deaths", addDeath);
                    int addKills = playerKills + 1;
                    data.getStats().updatePlayerData(killer, "kills", addKills);

                    if (!data.getBounties().hasBounty(playerName)) {
                        double moneyToAdd = conf.getSettings().getDouble("settings.deathsManager.wins.money");
                        double moneyToRemove = conf.getSettings().getDouble("settings.deathsManager.lose.money");

                        Vault.getEcon().withdrawPlayer(p, moneyToRemove);
                        Vault.getEcon().depositPlayer(killer, moneyToAdd);

                        p.sendMessage(Config.getTransl("settings", "messages.info.money.withdrawn")
                                .replace("$value", Strings.formatNumber(moneyToRemove)));
                        killer.sendMessage(Config.getTransl("settings", "messages.info.money.deposit")
                                .replace("$value", Strings.formatNumber(moneyToAdd)));
                    }
                    deathFromPlayer = true;
                    for (Player players : Bukkit.getOnlinePlayers()) {
                        players.sendMessage(Config.getTransl("settings", "messages.others.deaths.message.by-player")
                                .replace("$killer", killerName)
                                .replace("$victim", playerName));
                    }

                    if (data.getBounties().hasBounty(p.getName())) {
                        for (Player players : Bukkit.getOnlinePlayers()) {
                            for (String line : conf.getSettings().getStringList("messages.info.bounties.obtained.broadcast")) {
                                players.sendMessage(Colors.text(line
                                        .replace("$targetName", p.getName())
                                ));
                            }
                        }
                        double bountyValue = 1;
                        if (data.getBounties().getBountyExecutor(p.getName()).equalsIgnoreCase(killerName)) {
                            bountyValue = 2 * data.getBounties().getBounty(playerName);
                        } else {
                            bountyValue = data.getBounties().getBounty(playerName);
                        }
                        Vault.getEcon().depositPlayer(killer, bountyValue);
                        p.sendMessage(Config.getTransl("settings", "messages.info.money.deposit")
                                .replace("$value", Strings.formatNumber(bountyValue)));
                        data.getBounties().deleteMemberData(p.getName());
                    }
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
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
                    for (int i=0; i<p.getInventory().getSize(); i++) {
                        ItemStack is = p.getInventory().getItem(i);
                        if (is != null) {
                            p.getWorld().dropItem(p.getLocation(), is);
                            p.getInventory().remove(is);
                        }
                    }
                    p.setHealth(20);
                    p.setGameMode(GameMode.SPECTATOR);
                    int playerDeaths = data.getStats().getPlayerData(p, "deaths");
                    int addDeath = playerDeaths + 1;
                    data.getStats().updatePlayerData(p, "deaths", addDeath);
                    data.getLives().updateIsDead(p, 1);

                    if (lives > 0) {
                        data.getLives().updateCanRespawn(p, 1);
                    } else {
                        data.getLives().updateCanRespawn(p, 0);
                    }

                    boolean canRespawn = data.getLives().canRespawn(p);
                    if (canRespawn) {
                        data.getLives().updatePlayerLives(p, data.getLives().getPlayerLives(p) - 1);
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
                    if (!deathFromPlayer) {
                        for (Player players : Bukkit.getOnlinePlayers()) {
                            players.sendMessage(Config.getTransl("settings", "messages.others.deaths.message.by-null")
                                    .replace("$victim", playerName));
                        }
                    }
                }
            } else {
                if (e.getFinalDamage() >= p.getHealth()) {
                    int playerDeaths = data.getStats().getPlayerData(p, "deaths");
                    int addDeath = playerDeaths + 1;
                    data.getStats().updatePlayerData(p, "deaths", addDeath);
                    if (!deathFromPlayer) {
                        for (Player players : Bukkit.getOnlinePlayers()) {
                            players.sendMessage(Config.getTransl("settings", "messages.others.deaths.message.by-null")
                                    .replace("$victim", p.getName()));
                        }
                    }
                }
            }

            if (deathFromPlayer) {
                deathFromPlayer = false;
            }
        }
    }
}
