package me.sirimperivm.spigot.modules.commands.admin.guilds;

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

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("all")
public class AdminGuildsCommand implements CommandExecutor {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Db data = Main.getData();
    private static Modules mods = Main.getMods();

    void getUsage(CommandSender s) {
        for (String usage : conf.getHelps().getStringList("helps.admin-commands.guilds")) {
            s.sendMessage(Colors.text(usage));
        }
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {

        if (c.getName().equalsIgnoreCase("ga")) {
            if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.guilds.main"))) {
                return true;
            } else {
                if (Errors.noConsole(s)) {
                    return true;
                } else {
                    Player p = (Player) s;
                    if (a.length == 0) {
                        getUsage(p);
                    } else if (a.length == 1) {
                        if (a[0].equalsIgnoreCase("setlobby")) {
                            if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.guilds.setlobby"))) {
                                return true;
                            } else {
                                mods.setLobby(p);
                            }
                        } else {
                            getUsage(p);
                        }
                    } else if (a.length == 2) {
                        if (a[0].equalsIgnoreCase("deleteguild")) {
                            if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.guilds.delete"))) {
                                return true;
                            } else {
                                String oldGuildName = a[1];
                                if (mods.isLobbyLocated()) {
                                    List<String> generatedGuilds = mods.getGeneratedGuilds();
                                    boolean guildExists = false;
                                    for (String generated : generatedGuilds) {
                                        String[] partGenerated = generated.split(";");
                                        String guildName = partGenerated[1];
                                        if (oldGuildName.equalsIgnoreCase(guildName)) {
                                            guildExists = true;
                                            break;
                                        }
                                    }

                                    if (guildExists) {
                                        mods.deleteGuild(p, oldGuildName);
                                    } else {
                                        p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.not-exists"));
                                    }
                                } else {
                                    p.sendMessage(Config.getTransl("settings", "messages.errors.lobby.not-located"));
                                }
                            }
                        } else {
                            getUsage(p);
                        }
                    } else if (a.length == 3) {
                        if (a[0].equalsIgnoreCase("addmember")) {
                            if (Errors.noPermCommand(p, conf.getSettings().getString("permissions.admin-commands.guilds.addmember"))) {
                                return true;
                            } else {
                                Player t = Bukkit.getPlayerExact(a[1]);
                                if (t == null || !Bukkit.getOnlinePlayers().contains(t)) {
                                    p.sendMessage(Config.getTransl("settings", "messages.errors.players.not-found"));
                                } else {
                                    String targetName = t.getName();
                                    HashMap<String, List<String>> guildsData = mods.getGuildsData();
                                    if (!guildsData.containsKey(targetName)) {
                                        List<String> guildsList = mods.getGeneratedGuilds();
                                        boolean guildExist = false;
                                        for (String guild : guildsList) {
                                            String[] splitter = guild.split(";");
                                            if (splitter[1].equalsIgnoreCase(a[2])) {
                                                guildExist = true;
                                                break;
                                            }
                                        }

                                        if (guildExist) {
                                            String guildId = data.getGuilds().getGuildId(a[2]);
                                            int membersCount = data.getGuildMembers().getMembersCount(guildId);
                                            int membersLimit = conf.getGuilds().getInt("guilds." + a[2] + ".membersLimit");
                                            if (membersCount <= membersLimit) {
                                                mods.inviteMember(t, a[2]);
                                            } else {
                                                p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.admin.limit-reached"));
                                            }
                                        } else {
                                            p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.not-exists"));
                                        }
                                    } else {
                                        p.sendMessage(Config.getTransl("settings", "messages.errors.members.target.is-on-a-guild"));
                                    }
                                }
                            }
                        } else if (a[0].equalsIgnoreCase("deletemember")) {
                            if (Errors.noPermCommand(p, conf.getSettings().getString("permissions.admin-commands.guilds.deletemember"))) {
                                return true;
                            } else {
                                String targetName = a[1];
                                HashMap<String, List<String>> guildsData = mods.getGuildsData();
                                if (guildsData.containsKey(targetName)) {
                                    String guildId = guildsData.get(targetName).get(0);
                                    String guildName = data.getGuilds().getGuildName(guildId);
                                    data.getTasks().insertTask("expelGuildMember", targetName, 1);
                                    p.sendMessage(Config.getTransl("settings", "messages.success.guilds.remove-member")
                                            .replace("$guildName", guildName));
                                } else {
                                    p.sendMessage(Config.getTransl("settings", "messages.errors.members.target.isnt-on-a-guild"));
                                }
                            }
                        } else if (a[0].equalsIgnoreCase("changerole")) {
                            if (Errors.noPermCommand(p, conf.getSettings().getString("permissions.admin-commands.guilds.changerole"))) {
                                return true;
                            } else {
                                String targetName = a[1];
                                String newRole = a[2];
                                HashMap<String, List<String>> guildsData = mods.getGuildsData();
                                if (guildsData.containsKey(targetName)) {
                                    List<String> guildAndRole = guildsData.get(targetName);
                                    String guildId = guildAndRole.get(0);
                                    String guildRole = guildAndRole.get(1);
                                    String guildName = data.getGuilds().getGuildName(guildId);
                                    switch (newRole) {
                                        case "officer":
                                            if (!guildRole.toLowerCase().equalsIgnoreCase(newRole)) {
                                                data.getTasks().insertTask("setOfficer", guildName, 1);
                                                data.getTasks().insertTask("sendUserMessage", targetName + "£" + conf.getSettings().getString("messages.info.guild.officer.set")
                                                                .replace("%sp", plugin.getSuccessPrefix())
                                                                .replace("%ip", plugin.getInfoPrefix())
                                                                .replace("%fp", plugin.getFailPrefix()),
                                                        1);
                                                mods.sendGuildersBroadcast(guildId, conf.getSettings().getString("messages.info.guild.members.broadcast.officer.set")
                                                        .replace("%sp", plugin.getSuccessPrefix())
                                                        .replace("%ip", plugin.getInfoPrefix())
                                                        .replace("%fp", plugin.getFailPrefix())
                                                        .replace("$username", targetName)
                                                );
                                            } else {
                                                p.sendMessage(Config.getTransl("settings", "messages.errors.admin.members.target.is-already-officer"));
                                            }
                                            break;
                                        case "member":
                                            if (!guildRole.toLowerCase().equalsIgnoreCase(newRole)) {
                                                data.getTasks().insertTask("removeOfficer", guildName, 1);
                                                data.getTasks().insertTask("sendUserMessage", targetName + "£" + conf.getSettings().getString("messages.info.guild.officer.remove")
                                                                .replace("%sp", plugin.getSuccessPrefix())
                                                                .replace("%ip", plugin.getInfoPrefix())
                                                                .replace("%fp", plugin.getFailPrefix()),
                                                        1);
                                                mods.sendGuildersBroadcast(guildId, conf.getSettings().getString("messages.info.guild.members.broadcast.officer.remove")
                                                        .replace("%sp", plugin.getSuccessPrefix())
                                                        .replace("%ip", plugin.getInfoPrefix())
                                                        .replace("%fp", plugin.getFailPrefix())
                                                        .replace("$username", targetName)
                                                );
                                            } else {
                                                p.sendMessage(Config.getTransl("settings", "messages.errors.admin.members.target.is-already-member"));
                                            }
                                            break;
                                        default:
                                            p.sendMessage(Config.getTransl("settings", "messages.errors.admin.members.target.role-not-found"));
                                            break;
                                    }
                                } else {
                                    p.sendMessage(Config.getTransl("settings", "messages.errors.members.target.isnt-on-a-guild"));
                                }
                            }
                        } else {
                            getUsage(p);
                        }
                    } else if (a.length == 4) {
                        if (a[0].equalsIgnoreCase("createguild")) {
                            if (Errors.noPermCommand(p, conf.getSettings().getString("permissions.admin-commands.guilds.create"))) {
                                return true;
                            } else {
                                String newGuildName = a[1];
                                if (mods.isLobbyLocated()) {
                                    List<String> generatedGuilds = mods.getGeneratedGuilds();
                                    boolean alreadyExists = false;
                                    for (String generated : generatedGuilds) {
                                        String[] partGenerated = generated.split(";");
                                        String guildName = partGenerated[1];
                                        if (newGuildName.equals(guildName)) {
                                            alreadyExists = true;
                                            break;
                                        }
                                    }
                                    if (!alreadyExists) {
                                        boolean containsChars = false;
                                        for (char mLimitChar : a[3].toCharArray()) {
                                            if (!((mLimitChar >= '0') && (mLimitChar <= '9'))) {
                                                containsChars = true;
                                                break;
                                            }
                                        }
                                        if (!containsChars) {
                                            int memberLimit = Integer.parseInt(a[3]);
                                            if (memberLimit > 0) {
                                                mods.createGuild(p, newGuildName, a[2], memberLimit);
                                            } else {
                                                p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.membersLimit.greaterThanZero"));
                                            }
                                        } else {
                                            p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.membersLimit.containsChars"));
                                        }
                                    } else {
                                        p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.alreadyExists"));
                                    }
                                } else {
                                    p.sendMessage(Config.getTransl("settings", "messages.errors.lobby.not-located"));
                                }
                            }
                        } else {
                            getUsage(p);
                        }
                    } else {
                        getUsage(p);
                    }
                }
            }
        }
        return false;
    }
}
