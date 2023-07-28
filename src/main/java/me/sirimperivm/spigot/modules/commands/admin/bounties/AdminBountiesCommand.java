package me.sirimperivm.spigot.modules.commands.admin.bounties;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.other.Strings;
import me.sirimperivm.spigot.assets.utils.Colors;
import me.sirimperivm.spigot.assets.utils.Errors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("all")
public class AdminBountiesCommand implements CommandExecutor {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Db data = Main.getData();
    private static Modules mods = Main.getMods();

    public void getUsage(CommandSender s) {
        for (String usage : conf.getHelps().getStringList("helps.admin-commands.bounties")) {
            s.sendMessage(Colors.text(usage));
        }
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {

        if (c.getName().equalsIgnoreCase("ba")) {
            if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.bounties.main"))) {
                return true;
            } else {
                if (a.length == 0) {
                    getUsage(s);
                } else if (a.length == 2) {
                    if (a[0].equalsIgnoreCase("remove")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.bounties.remove"))) {
                            return true;
                        } else {
                            String targetName = a[1];
                            if (data.getBounties().hasBounty(targetName)) {
                                data.getBounties().deleteMemberData(a[1]);
                                for (Player players : Bukkit.getOnlinePlayers()) {
                                    for (String line : conf.getSettings().getStringList("messages.info.bounties.removed.broadcast")) {
                                        players.sendMessage(Colors.text(line
                                                .replace("$targetName", a[1])
                                        ));
                                    }
                                }
                            } else {
                                s.sendMessage(Config.getTransl("settings", "messages.errors.bounties.target.hasnt-bounty"));
                            }
                        }
                    } else {
                        getUsage(s);
                    }
                } else if (a.length == 3) {
                    if (a[0].equalsIgnoreCase("set")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.bounties.set"))) {
                            return true;
                        } else {
                            Player t = Bukkit.getPlayerExact(a[1]);
                            if (t == null) {
                                s.sendMessage(Config.getTransl("settings", "messages.errors.players.not-found"));
                            } else {
                                boolean containsChars = false;
                                for (char ch : a[2].toCharArray()) {
                                    if (!(ch >= '0' && ch <= '9')) {
                                        containsChars = true;
                                        break;
                                    }
                                }
                                if (!containsChars) {
                                    double bountyValue = Double.parseDouble(a[2]);
                                    data.getBounties().insertMemberData(a[1], "null", a[2]);
                                    for (Player players : Bukkit.getOnlinePlayers()) {
                                        for (String line : conf.getSettings().getStringList("messages.info.bounties.set.broadcast.fromAdmin")) {
                                            players.sendMessage(Colors.text(line
                                                    .replace("$executorName", (s instanceof Player) ? s.getName() : "Sistema")
                                                    .replace("$targetName", a[1])
                                                    .replace("$bountyValue", Strings.formatNumber(bountyValue))
                                            ));
                                        }
                                    }
                                } else {
                                    s.sendMessage(Config.getTransl("settings", "messages.errors.bounties.chars-not-allowed"));
                                }
                            }
                        }
                    }
                } else {
                    getUsage(s);
                }
            }
        }

        return false;
    }
}
