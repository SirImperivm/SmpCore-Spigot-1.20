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
public class AdminLivesTabCompleter implements TabCompleter {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Modules mods = Main.getMods();
    private static Db data = Main.getData();

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) {

        if (s instanceof Player) {
            Player p = (Player) s;
            if (p.hasPermission(conf.getSettings().getString("permissions.admin-commands.lives.main"))) {
                if (a.length == 1) {
                    List<String> args1 = new ArrayList<String>();
                    args1.add("give-life");
                    args1.add("info");
                    args1.add("life-top");
                    args1.add("setDeathZone");
                    args1.add("take-life");
                    if (args1.contains(p.getName())) {
                        args1.remove(p.getName());
                    }
                    return args1;
                } else if (a.length == 2) {
                    List<String> args2 = new ArrayList<String>();
                    if (a[0].equalsIgnoreCase("give-life")) {
                        for (Player players : Bukkit.getOnlinePlayers()) {
                            args2.add(players.getName());
                        }
                    } else if (a[0].equalsIgnoreCase("info")) {
                        for (Player players : Bukkit.getOnlinePlayers()) {
                            args2.add(players.getName());
                        }
                    } else if (a[0].equalsIgnoreCase("take-life")) {
                        for (Player players : Bukkit.getOnlinePlayers()) {
                            args2.add(players.getName());
                        }
                    }
                    if (args2.contains(p.getName())) {
                        args2.remove(p.getName());
                    }
                    return args2;
                } else if (a.length == 3) {
                    List<String> args3 = new ArrayList<String>();
                    if (a[0].equalsIgnoreCase("give-life")) {
                        args3.add("<insertNumber>");
                    } else if (a[0].equalsIgnoreCase("take-life")) {
                        args3.add("<insertNumber>");
                    }
                    if (args3.contains(p.getName())) {
                        args3.remove(p.getName());
                    }
                    return args3;
                }
            }
        }
        return null;
    }
}
