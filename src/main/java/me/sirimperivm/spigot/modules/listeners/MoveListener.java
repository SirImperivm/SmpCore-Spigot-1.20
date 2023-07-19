package me.sirimperivm.spigot.modules.listeners;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Location;
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
        Location loc = player.getLocation();
        BlockVector3 vector3 = loc.toVector().toBlockPoint();

        RegionContainer container = worldGuard.getWg().getPlatform().getRegionContainer();
        RegionManager regions = container.get(player.getWorld());

        for (ProtectedRegion region : regions.getApplicableRegions(vector3)) {
            String regionId = region.getId();
            if (region != null) {
                StateFlag smpcGuilds = worldGuard.smpcGuilds;
                if (region.getFlag(smpcGuilds) != null) {
                    if (region.getFlag(smpcGuilds) == StateFlag.State.ALLOW) {
                        StringFlag smpcGuildId = worldGuard.smpcGuildId;
                        if (!region.getFlag(smpcGuildId).equals(null)) {
                            HashMap<String, List<String>> guildsData = mods.getGuildsData();
                            String regionGuildId = region.getFlag(smpcGuildId);
                            if (guildsData.containsKey(playerName)) {
                                List<String> guildAndRole = guildsData.get(playerName);
                                String guildId = guildAndRole.get(0);
                                if (!regionGuildId.equals(guildId)) {
                                    if (!p.hasPermission(conf.getSettings().getString("permissions.admin-actions.guilds.entering.bypass"))) {
                                        if (e.getFrom().getBlock().equals(e.getTo().getBlock())) return;
                                        e.setCancelled(true);
                                        p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.region.entering"));
                                    }
                                }
                            } else {
                                if (!p.hasPermission(conf.getSettings().getString("permissions.admin-actions.guilds.entering.bypass"))) {
                                    if (e.getFrom().getBlock().equals(e.getTo().getBlock())) return;
                                    e.setCancelled(true);
                                    p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.region.entering"));
                                }
                            }
                        } else {
                            if (!p.hasPermission(conf.getSettings().getString("permissions.admin-actions.guilds.entering.bypass"))) {
                                if (e.getFrom().getBlock().equals(e.getTo().getBlock())) return;
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
