package me.sirimperivm.spigot.modules.commands.admin.core;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.utils.Colors;
import me.sirimperivm.spigot.assets.utils.Errors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("all")
public class AdminSmpcCommand implements CommandExecutor {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Db data = Main.getData();
    private static Modules mods = Main.getMods();

    void getUsage(CommandSender s) {
        for (String usage : conf.getHelps().getStringList("helps.admin-commands.core")) {
            s.sendMessage(Colors.text(usage));
        }
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {

        if (c.getName().equalsIgnoreCase("smpc")) {
            if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.core.main"))) {
                return true;
            } else {
                if (a.length == 0) {
                    getUsage(s);
                } else if (a.length == 1) {
                    if (a[0].equalsIgnoreCase("reload")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.core.reload"))) {
                            return true;
                        } else {
                            conf.loadAll();
                            s.sendMessage(Config.getTransl("settings", "messages.success.plugin.reloaded"));
                        }
                    } else {
                        getUsage(s);
                    }
                } else if (a.length == 2) {
                    if (a[0].equalsIgnoreCase("whitelist")) {
                        if (a[1].equalsIgnoreCase("toggle")) {
                            if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.core.whitelist.toggle"))) {
                                return true;
                            } else {
                                boolean whitelistStatus = conf.getSettings().getBoolean("settings.whitelist.enabled");

                                if (whitelistStatus) {
                                    conf.getSettings().set("settings.whitelist.enabled", false);
                                    conf.save(conf.getSettings(), conf.getSettingsFile());
                                    s.sendMessage(Config.getTransl("settings", "messages.success.whitelist.status.changed")
                                            .replace("$status", "Disattivata"));
                                } else {
                                    conf.getSettings().set("settings.whitelist.enabled", true);
                                    conf.save(conf.getSettings(), conf.getSettingsFile());
                                    s.sendMessage(Config.getTransl("settings", "messages.success.whitelist.status.changed")
                                            .replace("$status", "Attivata"));
                                }
                            }
                        } else if (a[1].equalsIgnoreCase("list")) {
                            if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.core.whitelist.list"))) {
                                return true;
                            } else {
                                s.sendMessage(data.getWhitelist().getWhitelistedPlayer());
                            }
                        } else {
                            getUsage(s);
                        }
                    } else {
                        getUsage(s);
                    }
                } else if (a.length == 3) {
                    if (a[0].equalsIgnoreCase("whitelist")) {
                        if (conf.getSettings().getBoolean("settings.whitelist.enabled")) {
                            if (a[1].equalsIgnoreCase("add")) {
                                if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.core.whitelist.add"))) {
                                    return true;
                                } else {
                                    String username = a[2];
                                    if (!data.getWhitelist().existMemberData(username)) {
                                        data.getWhitelist().insertMemberData(username);
                                        s.sendMessage(Config.getTransl("settings", "messages.success.whitelist.member.added"));
                                    } else {
                                        s.sendMessage(Config.getTransl("settings", "messages.errors.whitelist.member.already-added"));
                                    }
                                }
                            } else if (a[1].equalsIgnoreCase("remove")) {
                                if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.core.whitelist.remove"))) {
                                    return true;
                                } else {
                                    String username = a[2];
                                    if (data.getWhitelist().existMemberData(username)) {
                                        data.getWhitelist().removeMemberData(username);
                                        s.sendMessage(Config.getTransl("settings", "messages.success.whitelist.member.removed"));
                                        Player target = Bukkit.getPlayerExact(username);
                                        if (target != null) {
                                            target.kickPlayer(Config.getTransl("settings", "messages.info.whitelist.target.unwhitelisted"));
                                        }
                                    } else {
                                        s.sendMessage(Config.getTransl("settings", "messages.errors.whitelist.member.not-added"));
                                    }
                                }
                            } else {
                                getUsage(s);
                            }
                        } else {
                            s.sendMessage(Config.getTransl("settings", "messages.errors.whitelist.not-enabled"));
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
