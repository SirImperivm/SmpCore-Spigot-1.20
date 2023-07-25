package me.sirimperivm.spigot.modules.tabCompleters;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class GuildsTabCompleter implements TabCompleter {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Modules mods = Main.getMods();
    private static Db data = Main.getData();

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) {

        if (s instanceof Player) {
            Player p = (Player) s;
            if (p.hasPermission(conf.getSettings().getString("permissions.user-commands.guilds.main"))) {
                if (a.length == 1) {
                    List<String> args1 = new ArrayList<String>();
                    args1.add("accept");
                    args1.add("bank");
                    args1.add("chat");
                    args1.add("help");
                    args1.add("home");
                    args1.add("info");
                    args1.add("invite");
                    args1.add("kick");
                    args1.add("leave");
                    args1.add("list");
                    args1.add("newleadership");
                    args1.add("officer");
                    args1.add("shop");
                    args1.add("top-bank");
                    args1.add("top-members");
                    args1.add("upgrades");
                    return args1;
                } else if (a.length == 2) {
                    List<String> args2 = new ArrayList<String>();
                    if (a[0].equalsIgnoreCase("info")) {
                        for (String generatedGuilds : mods.getGeneratedGuilds()) {
                            String[] splitter = generatedGuilds.split(";");
                            String guildId = splitter[0];
                            String guildName = data.getGuilds().getGuildName(guildId);
                            args2.add(guildName);
                        }
                    } else if (a[0].equalsIgnoreCase("invite")) {
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            String allName = all.getName();
                            args2.add(allName);
                        }
                    } else if (a[0].equalsIgnoreCase("kick")) {
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            String allName = all.getName();
                            args2.add(allName);
                        }
                    } else if (a[0].equalsIgnoreCase("newleadership")) {
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            String allName = all.getName();
                            args2.add(allName);
                        }
                    } else if (a[0].equalsIgnoreCase("officer")) {
                        args2.add("set");
                        args2.add("remove");
                    }
                    return args2;
                } else if (a.length == 3) {
                    List<String> args3 = new ArrayList<String>();
                    if (a[0].equalsIgnoreCase("officer")) {
                        if (a[1].equalsIgnoreCase("set")) {
                            for (Player all : Bukkit.getOnlinePlayers()) {
                                String allName = all.getName();
                                args3.add(allName);
                            }
                        } else if (a[1].equalsIgnoreCase("remove")) {
                            for (Player all : Bukkit.getOnlinePlayers()) {
                                String allName = all.getName();
                                args3.add(allName);
                            }
                        }
                    }
                    return args3;
                }
            }
        }

        return null;
    }
}
