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
public class BountiesTabCompleter implements TabCompleter {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Modules mods = Main.getMods();
    private static Db data = Main.getData();

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) {

        if (s instanceof Player) {
            Player p = (Player) s;
            if (p.hasPermission(conf.getSettings().getString("permissions.user-commands.bounties.main"))) {
                if (a.length == 1) {
                    List<String> args1 = new ArrayList<String>();
                    for (Player players : Bukkit.getOnlinePlayers()) {
                        args1.add(players.getName());
                    }
                    return args1;
                } else if (a.length == 2) {
                    List<String> args2 = new ArrayList<String>();
                    args2.add("<insertNumber>");
                    return args2;
                }
            }
        }
        return null;
    }
}
