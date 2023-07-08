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

import java.util.HashMap;
import java.util.List;

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
                    } else if (a[0].equalsIgnoreCase("accept")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.guilds.accept"))) {
                            return true;
                        } else {
                            if (Errors.noConsole(s)) {
                                return true;
                            } else {
                                Player p = (Player) s;
                                String playerName = p.getName();

                                HashMap<String, String> invites = mods.getInvites();
                                if (invites.containsKey(playerName)) {
                                    mods.insertMember(p, invites.get(playerName), "member");
                                    invites.remove(playerName);
                                } else {
                                    p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.invites.not-received"));
                                }
                            }
                        }
                    } else if (a[0].equalsIgnoreCase("home")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.guilds.home"))) {
                            return true;
                        } else {
                            if (Errors.noConsole(s)) {
                                return true;
                            } else {
                                Player p = (Player) s;
                                String playerName = p.getName();
                                HashMap<String, List<String>> guildsData = mods.getGuildsData();
                                if (guildsData.containsKey(playerName)) {
                                    String guildId = guildsData.get(playerName).get(0);
                                    String guildName = data.getGuilds().getGuildName(guildId);
                                    mods.sendPlayerToGhome(p, guildName);
                                } else {
                                    p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.dont-have"));
                                }
                            }
                        }
                    } else if (a[0].equalsIgnoreCase("leave")) {

                    } else {
                        getUsage(s);
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
                                    p.sendMessage(Config.getTransl("settings", "messages.errors.players.not-found"));
                                } else {
                                    String playerName = p.getName();
                                    HashMap<String, List<String>> guildsData = mods.getGuildsData();
                                    if (guildsData.containsKey(playerName)) {
                                        String targetName = t.getName();
                                        if (!guildsData.containsKey(targetName)) {
                                            List<String> guildSettings = guildsData.get(playerName);
                                            String guildId = guildSettings.get(0);
                                            int membersCount = data.getGuildMembers().getMembersCount(guildId);
                                            String guildName = data.getGuilds().getGuildName(guildId);
                                            int membersLimit = conf.getGuilds().getInt("guilds." + guildName + ".membersLimit");

                                            if (membersCount <= membersLimit) {
                                                mods.inviteMember(t, guildName);
                                            } else {
                                                p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.members.limit-reached")
                                                        .replace("$membersLimit", String.valueOf(membersLimit)));
                                            }
                                        } else {
                                            p.sendMessage(Config.getTransl("settings", "messages.errors.members.target.is-on-a-guild"));
                                        }
                                    } else {
                                        p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.dont-have"));
                                    }
                                }
                            }
                        }
                    } else if (a[0].equalsIgnoreCase("kick")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.guilds.kick"))) {
                            return true;
                        } else {
                            if (Errors.noConsole(s)) {
                                return true;
                            } else {
                                Player p = (Player) s;
                                String playerName = p.getName();
                                HashMap<String, List<String>> guildsData = mods.getGuildsData();
                                if (guildsData.containsKey(playerName)) {
                                    List<String> playerGuildAndRole = guildsData.get(playerName);
                                    String playerGuildId = playerGuildAndRole.get(0);
                                    String playerGuildName = data.getGuilds().getGuildName(playerGuildId);
                                    if (guildsData.containsKey(a[1])) {
                                        List<String> targetGuildAndRole = guildsData.get(a[1]);
                                        String targetGuildId = targetGuildAndRole.get(0);
                                        String targetGuildName = data.getGuilds().getGuildName(targetGuildId);
                                        if (targetGuildName.equalsIgnoreCase(playerGuildName)) {
                                            data.getTasks().insertTask("expelGuildMember", a[1], 1);
                                        } else {
                                            p.sendMessage(Config.getTransl("settings", "messages.errors.members.target.isnt-a-guild-member"));
                                        }
                                    } else {
                                        p.sendMessage(Config.getTransl("settings", "messages.errors.members.target.isnt-on-a-guild"));
                                    }
                                } else {
                                    p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.dont-have"));
                                }
                            }
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
