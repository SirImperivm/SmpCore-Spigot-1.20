package me.sirimperivm.spigot.modules.commands.users.bounties;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.managers.values.Vault;
import me.sirimperivm.spigot.assets.other.Strings;
import me.sirimperivm.spigot.assets.utils.Colors;
import me.sirimperivm.spigot.assets.utils.Errors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("all")
public class BountiesCommand implements CommandExecutor {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Db data = Main.getData();
    private static Modules mods = Main.getMods();

    public void getUsage(CommandSender s) {
        for (String usage : conf.getHelps().getStringList("helps.user-commands.bounties")) {
            s.sendMessage(Colors.text(usage));
        }
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {

        if (c.getName().equalsIgnoreCase("b")) {
            if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.bounties.main"))) {
                return true;
            } else {
                if (a.length == 0) {
                    getUsage(s);
                } else if (a.length == 2) {
                    boolean isActualPlayer = false;
                    for (Player players : Bukkit.getOnlinePlayers()) {
                        if (players.getName().equalsIgnoreCase(a[1])) {
                            isActualPlayer = true;
                            break;
                        }
                    }
                    if (isActualPlayer) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.bounties.set"))) {
                            return true;
                        } else {
                            if (Errors.noConsole(s)) {
                                return true;
                            } else {
                                Player p = (Player) s;
                                Player t = Bukkit.getPlayerExact(a[0]);

                                if (t == null) {
                                    p.sendMessage(Config.getTransl("settings", "messages.errors.players.not-found"));
                                } else if (t == p) {
                                    p.sendMessage(Config.getTransl("settings", "messages.errors.bounties.target.cant-be-yourself"));
                                } else {
                                    boolean containsChars = false;
                                    for (char ch : a[2].toCharArray()) {
                                        if (!(ch >= '0' && ch <= '9')) {
                                            containsChars = true;
                                            break;
                                        }
                                    }
                                    if (!containsChars) {
                                        double bountyValue = Double.parseDouble(a[1]);
                                        double userBalance = Vault.getEcon().getBalance(p);
                                        if (userBalance >= bountyValue) {
                                            Vault.getEcon().withdrawPlayer(p, bountyValue);
                                            p.sendMessage(Config.getTransl("settings", "messages.info.money.withdrawn")
                                                    .replace("$value", Strings.formatNumber(bountyValue)));
                                            data.getBounties().insertMemberData(a[0], p.getName(), a[1]);
                                            for (Player players : Bukkit.getOnlinePlayers()) {
                                                for (String line : conf.getSettings().getStringList("messages.info.bounties.set.broadcast.fromPlayer")) {
                                                    players.sendMessage(Colors.text(line
                                                            .replace("$executorName", p.getName())
                                                            .replace("$targetName", a[0])
                                                            .replace("$bountyValue", Strings.formatNumber(bountyValue))
                                                    ));
                                                }
                                            }
                                        } else {
                                            p.sendMessage(Config.getTransl("settings", "messages.errors.bounties.money.not-enough"));
                                        }
                                    } else {
                                        p.sendMessage(Config.getTransl("settings", "messages.errors.bounties.chars-not-allowed"));
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
            }
        }
        return false;
    }
}
