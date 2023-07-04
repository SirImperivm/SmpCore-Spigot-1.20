package me.sirimperivm.spigot.modules.commands.users.guilds;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Gui;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.utils.Colors;
import me.sirimperivm.spigot.assets.utils.Errors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("all")
public class GuildsCommand implements CommandExecutor {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Db data = Main.getData();
    private static Modules mods = Main.getMods();

    void getUsage(CommandSender s) {
        for (String usage : conf.getHelps().getStringList("helps.user-commands.guilds")) {
            s.sendMessage(Colors.text(usage));
        }
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {

        if (c.getName().equalsIgnoreCase("g")) {
            if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.guilds.main"))) {
                return true;
            } else {
                if (a.length == 0) {
                    s.sendMessage(Colors.text("<RAINBOW1>SMPCore: Plugin ideato e sviluppato da SirImperivm_</RAINBOW>"));
                } else if (a.length == 1) {
                    if (a[0].equalsIgnoreCase("help")) {
                        getUsage(s);
                    } else if (a[0].equalsIgnoreCase("shop")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.guilds.shop"))) {
                            return true;
                        } else {
                            if (Errors.noConsole(s)) {
                                return true;
                            } else {
                                Player p = (Player) s;
                                Gui gm = new Gui();
                                p.openInventory(gm.shopGui());
                            }
                        }
                    } else if (a.length == 2) {
                        if (a[0].equalsIgnoreCase("invite")) {
                            if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.guilds.invite"))) {
                                return true;
                            } else {
                                if (Errors.noConsole(s)) {
                                    return true;
                                } else {
                                    Player p = (Player) s;
                                    Player t = Bukkit.getPlayerExact(a[1]);
                                    if (t == null || !Bukkit.getOnlinePlayers().contains(t)) {
                                        p.sendMessage(Config.getTransl("settings", "messages.errors.player.not-found"));
                                    } else {
                                        String playerName = p.getName();
                                        String playerGuildId = data.getGuildMembers().getGuildIdFromMember(playerName);
                                        String playerGuildName = data.getGuilds().getGuildName(playerGuildId);

                                        String targetName = t.getName();
                                        String targetGuildId = data.getGuildMembers().getGuildIdFromMember(targetName);

                                        if (!targetGuildId.equalsIgnoreCase("null")) {

                                        } else {
                                            p.sendMessage(Config.getTransl("settings", "messages.errors.members.target.is-on-a-guild"));
                                        }
                                    }
                                }
                            }
                        } else {
                            getUsage(s);
                        }
                    } else {
                        getUsage(s);
                    }
                } else {
                    getUsage(s);
                }
            }
        }
        return false;
    }
}
