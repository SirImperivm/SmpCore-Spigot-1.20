package me.sirimperivm.spigot.modules.listeners;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.managers.worldguard.Wg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("all")
public class MoveListener implements Listener {
    private static Main plugin = Main.getPlugin();
    private static Wg worldGuard = Main.getRegionManager();
    private static Config conf = Main.getConf();
    private static Db data = Main.getData();
    private static Modules mods = Main.getMods();

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        String playerName = p.getName();
        LocalPlayer player = WorldGuardPlugin.inst().wrapPlayer(p);

        RegionContainer container = worldGuard.getWg().getPlatform().getRegionContainer();
        RegionManager regions = container.get(player.getWorld());

        for (String regionId : regions.getRegions().keySet()) {
            ProtectedRegion region = regions.getRegion(regionId);
            if (region != null) {
                StateFlag smpcGuilds = worldGuard.smpcGuilds;
                if (region.getFlag(smpcGuilds) != null) {
                    if (region.getFlag(smpcGuilds).equals(StateFlag.State.ALLOW)) {
                        StringFlag smpcGuildId = worldGuard.smpcGuildId;
                        if (region.getFlag(smpcGuildId) != null) {
                            HashMap<String, List<String>> guildsData = mods.getGuildsData();
                            if (guildsData.containsKey(playerName)) {
                                List<String> guildAndRole = guildsData.get(playerName);
                                String guildId = guildAndRole.get(0);
                                if (!region.getFlag(smpcGuildId).equals(guildId)) {
                                    if (!p.hasPermission(conf.getSettings().getString("permissions.admin-actions.guilds.entering.bypass"))) {
                                        e.setCancelled(true);
                                        p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.region.entering"));
                                    }
                                }
                            } else {
                                if (!p.hasPermission(conf.getSettings().getString("permissions.admin-actions.guilds.entering.bypass"))) {
                                    e.setCancelled(true);
                                    p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.region.entering"));
                                }
                            }
                        } else {
                            if (!p.hasPermission(conf.getSettings().getString("permissions.admin-actions.guilds.entering.bypass"))) {
                                e.setCancelled(true);
                                p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.region.entering"));
                            }
                        }
                    }
                }
            }
        }
    }
}
