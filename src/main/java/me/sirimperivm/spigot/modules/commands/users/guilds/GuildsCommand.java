package me.sirimperivm.spigot.modules.commands.users.guilds;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Gui;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.other.Strings;
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
                    s.sendMessage(Colors.text("<SOLID:00FFFF>Esegui /g help per informazioni sui comandi."));
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
                    } else if (a[0].equalsIgnoreCase("top-bank")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.guilds.top-bank"))) {
                            return true;
                        } else {
                            if (Errors.noConsole(s)) {
                                return true;
                            } else {
                                Player p = (Player) s;
                                mods.sendGuildTop(p, "bank");
                            }
                        }
                    } else if (a[0].equalsIgnoreCase("top-members")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.guilds.top-members"))) {
                            return true;
                        } else {
                            if (Errors.noConsole(s)) {
                                return true;
                            } else {
                                Player p = (Player) s;
                                mods.sendGuildTop(p, "members");
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
                                    String guildId = data.getGuilds().getGuildId(invites.get(playerName));
                                    if (data.getGuilds().getGuildBalance(guildId) >= mods.returnCost("per-invite")) {
                                        data.getGuilds().updateGuildBalance(guildId, String.valueOf(data.getGuilds().getGuildBalance(guildId) - mods.returnCost("per-invite")));
                                        mods.insertMember(p, invites.get(playerName), "member");
                                        mods.sendGuildersBroadcast(guildId, Config.getTransl("settings", "messages.info.guild.money.taken")
                                                        .replace("$cost", Strings.formatNumber(mods.returnCost("per-invite")))
                                                , "null");
                                        invites.remove(playerName);
                                    } else {
                                        mods.sendGuildersBroadcast(data.getGuilds().getGuildId(invites.get(playerName)), Config.getTransl("settings", "messages.errors.guilds.money.guild.invites.not-enough-after")
                                                        .replace("$cost", Strings.formatNumber(mods.returnCost("per-invite")))
                                                        .replace("$username", playerName)
                                                , "null");
                                        p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.money.guild.invites.not-enough-after")
                                                .replace("$cost", Strings.formatNumber(mods.returnCost("per-invite")))
                                                .replace("$username", playerName)
                                        );
                                        invites.remove(playerName);
                                    }
                                } else {
                                    p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.invites.not-received"));
                                }
                            }
                        }
                    } else if (a[0].equalsIgnoreCase("info")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.guilds.info"))) {
                            return true;
                        } else {
                            if (Errors.noConsole(s)) {
                                return true;
                            } else {
                                Player p = (Player) s;
                                String playerName = p.getName();

                                if (mods.getGuildsData().containsKey(playerName)) {
                                    String guildId = mods.getGuildsData().get(playerName).get(0);
                                    String guildName = data.getGuilds().getGuildName(guildId);
                                    mods.sendGuildInfo(p, "user", guildName);
                                } else {
                                    p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.dont-have"));
                                }
                            }
                        }
                    } else if (a[0].equalsIgnoreCase("list")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.guilds.list"))) {
                            return true;
                        } else {
                            if (Errors.noConsole(s)) {
                                return true;
                            } else {
                                Player p = (Player) s;
                                mods.sendGuildList(p, "user");
                            }
                        }
                    } else if (a[0].equalsIgnoreCase("chat")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.guilds.chat"))) {
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
                                    HashMap<String, String> guildsChat = mods.getGuildsChat();
                                    if (!guildsChat.containsKey(playerName)) {
                                        guildsChat.put(playerName, guildId);
                                        p.sendMessage(Config.getTransl("settings", "messages.info.guild.chat.changed")
                                                .replace("$chatType", "Gilda"));
                                    } else {
                                        guildsChat.remove(playerName);
                                        p.sendMessage(Config.getTransl("settings", "messages.info.guild.chat.changed")
                                                .replace("$chatType", "Pubblica"));
                                    }
                                } else {
                                    p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.dont-have"));
                                }
                            }
                        }
                    } else if (a[0].equalsIgnoreCase("bank")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.guilds.bank"))) {
                            return true;
                        } else {
                            if (Errors.noConsole(s)) {
                                return true;
                            } else {
                                Player p = (Player) s;
                                String playerName = p.getName();
                                if (mods.getGuildsData().containsKey(playerName)) {
                                    String guildId = mods.getGuildsData().get(playerName).get(0);
                                    Gui gm = new Gui();
                                    p.openInventory(gm.bankGui(p, guildId));
                                } else {
                                    p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.dont-have"));
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
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.guilds.leave"))) {
                            return true;
                        } else {
                            if (Errors.noConsole(s)) {
                                return true;
                            } else {
                                Player p = (Player) s;
                                String playerName = p.getName();
                                HashMap<String, List<String>> guildsData = mods.getGuildsData();
                                if (guildsData.containsKey(playerName)) {
                                    mods.leaveMember(p);
                                } else {
                                    p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.dont-have"));
                                }
                            }
                        }
                    } else if (a[0].equalsIgnoreCase("upgrades")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.guilds.upgrades"))) {
                            return true;
                        } else {
                            if (Errors.noConsole(s)) {
                                return true;
                            } else {
                                Player p = (Player) s;
                                String playerName = p.getName();
                                Gui g = new Gui();

                                if (mods.getGuildsData().containsKey(playerName)) {
                                    String guildId = mods.getGuildsData().get(playerName).get(0);
                                    p.openInventory(g.upgradesGui(guildId));
                                } else {
                                    p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.dont-have"));
                                }
                            }
                        }
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
                                Player t = Bukkit.getPlayer(a[1]);
                                if (t == null) {
                                    p.sendMessage(Config.getTransl("settings", "messages.errors.players.not-found"));
                                } else {
                                    String playerName = p.getName();
                                    HashMap<String, List<String>> guildsData = mods.getGuildsData();
                                    if (guildsData.containsKey(playerName)) {
                                        String targetName = t.getName();
                                        if (!guildsData.containsKey(a[1])) {
                                            List<String> guildSettings = guildsData.get(playerName);
                                            String guildId = guildSettings.get(0);
                                            int membersCount = data.getGuildMembers().getMembersCount(guildId);
                                            String guildName = data.getGuilds().getGuildName(guildId);
                                            int membersLimit = conf.getGuilds().getInt("guilds." + guildName + ".membersLimit");

                                            if (data.getGuilds().getGuildBalance(guildId) >= mods.returnCost("per-invite")) {
                                                if (membersCount < membersLimit || membersLimit == -1) {
                                                    mods.inviteMember(t, guildName);
                                                } else {
                                                    p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.members.limit-reached")
                                                            .replace("$membersLimit", String.valueOf(membersLimit)));
                                                }
                                            } else {
                                                p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.money.guild.invites.not-enough-money")
                                                        .replace("$cost", Strings.formatNumber(mods.returnCost("per-invite"))));
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
                    } else if (a[0].equalsIgnoreCase("newleadership")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.guilds.newleadership"))) {
                            return true;
                        } else {
                            if (Errors.noConsole(s)) {
                                return true;
                            } else {
                                Player p = (Player) s;
                                String playerName = p.getName();
                                HashMap<String, List<String>> guildsData = mods.getGuildsData();

                                if (guildsData.containsKey(playerName)) {
                                    List<String> playerGuildData = guildsData.get(playerName);
                                    String playerGuildId = playerGuildData.get(0);
                                    String playerGuildRole = playerGuildData.get(1);
                                    if (playerGuildRole.equalsIgnoreCase("leader")) {
                                        Player t = Bukkit.getPlayerExact(a[1]);
                                        if (t == null) {
                                            p.sendMessage(Config.getTransl("settings", "messages.errors.players.not-found"));
                                        } else {
                                            String targetName = t.getName();
                                            if (guildsData.containsKey(targetName)) {
                                                List<String> targetGuildData = guildsData.get(targetName);
                                                String targetGuildId = targetGuildData.get(0);
                                                String targetGuildRole = targetGuildData.get(1);
                                                if (targetGuildId.equalsIgnoreCase(playerGuildId)) {
                                                    mods.newLeadership(p, t, playerGuildId);
                                                } else {
                                                    p.sendMessage(Config.getTransl("settings", "messages.errors.members.target.isnt-a-guild-member"));
                                                }
                                            } else {
                                                p.sendMessage(Config.getTransl("settings", "messages.errors.members.target.isnt-on-a-guild"));
                                            }
                                        }
                                    } else {
                                        p.sendMessage(Config.getTransl("settings", "messages.errors.members.target.isnt-leader"));
                                    }
                                } else {
                                    p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.dont-have"));
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
                                            data.getTasks().insertTask("expelGuildMember", a[1]);
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
                    } else if (a[0].equalsIgnoreCase("info")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.guilds.info"))) {
                            return true;
                        } else {
                            if (Errors.noConsole(s)) {
                                return true;
                            } else {
                                Player p = (Player) s;
                                String guildName = a[1];
                                List<String> generatedGuilds = mods.getGeneratedGuilds();
                                boolean guildExist = false;
                                for (String generated : generatedGuilds) {
                                    String[] splitter = generated.split(";");
                                    if (splitter[1].equalsIgnoreCase(guildName)) {
                                        guildExist = true;
                                        break;
                                    }
                                }

                                if (guildExist) {
                                    mods.sendGuildInfo(p, "user", guildName);
                                } else {
                                    p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.not-exists"));
                                }
                            }
                        }
                    } else {
                        getUsage(s);
                    }
                } else if (a.length == 3) {
                    if (a[0].equalsIgnoreCase("officer")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.guilds.officer.main"))) {
                            return true;
                        } else {
                            if (Errors.noConsole(s)) {
                                return true;
                            } else {
                                Player p = (Player) s;
                                if (a[1].equalsIgnoreCase("set")) {
                                    if (Errors.noPermCommand(p, conf.getSettings().getString("permissions.user-commands.guilds.officer.set"))) {
                                        return true;
                                    } else {
                                        String playerName = p.getName();
                                        String targetName = a[2];
                                        HashMap<String, List<String>> guildsData = mods.getGuildsData();
                                        if (guildsData.containsKey(playerName)) {
                                            String playerGuildId = guildsData.get(playerName).get(0);
                                            String playerGuildName = data.getGuilds().getGuildName(playerGuildId);
                                            if (guildsData.containsKey(targetName)) {
                                                String targetGuildId = guildsData.get(targetName).get(0);
                                                String targetGuildName = data.getGuilds().getGuildName(targetGuildId);
                                                if (playerGuildName.equalsIgnoreCase(targetGuildName)) {
                                                    String targetGuildRole = guildsData.get(targetName).get(1);
                                                    if (!targetGuildRole.equalsIgnoreCase("officer")) {
                                                        data.getTasks().insertTask("setOfficer", targetName);
                                                        data.getTasks().insertTask("sendUserMessage", targetName + "£" + conf.getSettings().getString("messages.info.guild.members.officer.set")
                                                                .replace("%sp", plugin.getSuccessPrefix())
                                                                .replace("%ip", plugin.getInfoPrefix())
                                                                .replace("%fp", plugin.getFailPrefix())
                                                        );
                                                        mods.sendGuildersBroadcast(targetGuildId, conf.getSettings().getString("messages.info.guild.members.broadcast.officer.set")
                                                                .replace("%sp", plugin.getSuccessPrefix())
                                                                .replace("%ip", plugin.getInfoPrefix())
                                                                .replace("%fp", plugin.getFailPrefix())
                                                                .replace("$username", targetName)
                                                                , "null");
                                                    } else {
                                                        p.sendMessage(Config.getTransl("settings", "messages.errors.members.officer.already")
                                                                .replace("$username", targetName));
                                                    }
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
                                } else if (a[1].equalsIgnoreCase("remove")) {
                                    if (Errors.noPermCommand(p, conf.getSettings().getString("permissions.user-commands.guilds.officer.remove"))) {
                                        return true;
                                    } else {
                                        String playerName = p.getName();
                                        String targetName = a[2];
                                        HashMap<String, List<String>> guildsData = mods.getGuildsData();
                                        if (guildsData.containsKey(playerName)) {
                                            String playerGuildId = guildsData.get(playerName).get(0);
                                            String playerGuildName = data.getGuilds().getGuildName(playerGuildId);
                                            if (guildsData.containsKey(targetName)) {
                                                String targetGuildId = guildsData.get(targetName).get(0);
                                                String targetGuildName = data.getGuilds().getGuildName(targetGuildId);
                                                if (playerGuildName.equalsIgnoreCase(targetGuildName)) {
                                                    String targetGuildRole = guildsData.get(targetName).get(1);
                                                    if (targetGuildRole.equalsIgnoreCase("officer")) {
                                                        data.getTasks().insertTask("removeOfficer", targetName);
                                                        data.getTasks().insertTask("sendUserMessage", targetName + "£" + conf.getSettings().getString("messages.info.guild.members.officer.remove")
                                                                        .replace("%sp", plugin.getSuccessPrefix())
                                                                        .replace("%ip", plugin.getInfoPrefix())
                                                                        .replace("%fp", plugin.getFailPrefix())
                                                        );
                                                        mods.sendGuildersBroadcast(targetGuildId, conf.getSettings().getString("messages.info.guild.members.broadcast.officer.remove")
                                                                .replace("%sp", plugin.getSuccessPrefix())
                                                                .replace("%ip", plugin.getInfoPrefix())
                                                                .replace("%fp", plugin.getFailPrefix())
                                                                .replace("$username", targetName)
                                                                , "null"  );
                                                    } else {
                                                        p.sendMessage(Config.getTransl("settings", "messages.errors.members.officer.isnt")
                                                                .replace("$username", targetName));
                                                    }
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
                                } else {
                                    getUsage(p);
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
