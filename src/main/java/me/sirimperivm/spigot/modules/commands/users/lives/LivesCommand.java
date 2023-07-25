package me.sirimperivm.spigot.modules.commands.users.lives;

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
public class LivesCommand implements CommandExecutor {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Db data = Main.getData();
    private static Modules mods = Main.getMods();

    private void getUsage(CommandSender s) {
        for (String usage : conf.getHelps().getStringList("helps.user-commands.lives")) {
            s.sendMessage(Colors.text(usage));
        }
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {

        if (c.getName().equalsIgnoreCase("l")) {
            if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.lives.main"))) {
                return true;
            } else {
                if (a.length == 0) {
                    getUsage(s);
                } else if (a.length == 1) {
                    if (a[0].equalsIgnoreCase("get")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.lives.get.user"))) {
                            return true;
                        } else {
                            if (Errors.noConsole(s)) {
                                return true;
                            } else {
                                Player p = (Player) s;
                                mods.sendLivesInfo(s, p);
                            }
                        }
                    } else {
                        getUsage(s);
                    }
                } else if (a.length == 2) {
                    if (a[0].equalsIgnoreCase("get")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.lives.get.others"))) {
                            return true;
                        } else {
                            Player p = (Player) s;
                            Player t = Bukkit.getPlayerExact(a[1]);
                            if (t == null) {
                                s.sendMessage(Config.getTransl("settings", "messages.errors.players.not-found"));
                            } else {
                                mods.sendLivesInfo(s, t);
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
