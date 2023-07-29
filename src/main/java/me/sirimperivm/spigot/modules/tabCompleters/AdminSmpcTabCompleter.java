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
public class AdminSmpcTabCompleter implements TabCompleter {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Modules mods = Main.getMods();
    private static Db data = Main.getData();

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) {

        if (s instanceof Player) {
            Player p = (Player) s;
            if (p.hasPermission(conf.getSettings().getString("permissions.admin-commands.guilds.main"))) {
                if (a.length == 1) {
                    List<String> args1 = new ArrayList<String>();
                    args1.add("reload");
                    args1.add("whitelist");
                    return args1;
                }
                if (a.length == 2) {
                    List<String> args2 = new ArrayList<String>();
                    if (a[0].equalsIgnoreCase("whitelist")) {
                        args2.add("add");
                        args2.add("list");
                        args2.add("remove");
                        args2.add("toggle");
                    }
                    return args2;
                }
                if (a.length == 3) {
                    List<String> args3 = new ArrayList<String>();
                    if (a[0].equalsIgnoreCase("whitelist")) {
                        if (a[1].equalsIgnoreCase("add")) {
                            args3.add("<insertName>");
                        }
                        if (a[1].equalsIgnoreCase("remove")) {
                            for (Player players : Bukkit.getOnlinePlayers()) {
                                args3.add(players.getName());
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
