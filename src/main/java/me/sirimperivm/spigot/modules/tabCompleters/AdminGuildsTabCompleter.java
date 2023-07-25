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
public class AdminGuildsTabCompleter implements TabCompleter {

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
                    args1.add("addmember");
                    args1.add("chatspy");
                    args1.add("changerole");
                    args1.add("changespawn");
                    args1.add("createguild");
                    args1.add("deleteguild");
                    args1.add("deletemember");
                    args1.add("info");
                    args1.add("list");
                    args1.add("money");
                    args1.add("setlobby");
                    return args1;
                } else if (a.length == 2) {
                    List<String> args2 = new ArrayList<String>();
                    if (a[0].equalsIgnoreCase("addmember")) {
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            args2.add(all.getName());
                        }
                    } else if (a[0].equalsIgnoreCase("changerole")) {
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            args2.add(all.getName());
                        }
                    } else if (a[0].equalsIgnoreCase("changespawn")) {
                        for (String generatedGuilds : mods.getGeneratedGuilds()) {
                            String[] splitter = generatedGuilds.split(";");
                            String guildId = splitter[0];
                            String guildName = data.getGuilds().getGuildName(guildId);
                            args2.add(guildName);
                        }
                    } else if (a[0].equalsIgnoreCase("createguild")) {
                        args2.add("<guildName>");
                    } else if (a[0].equalsIgnoreCase("deleteguild")) {
                        for (String generatedGuilds : mods.getGeneratedGuilds()) {
                            String[] splitter = generatedGuilds.split(";");
                            String guildId = splitter[0];
                            String guildName = data.getGuilds().getGuildName(guildId);
                            args2.add(guildName);
                        }
                    } else if (a[0].equalsIgnoreCase("info")) {
                        for (String generatedGuilds : mods.getGeneratedGuilds()) {
                            String[] splitter = generatedGuilds.split(";");
                            String guildId = splitter[0];
                            String guildName = data.getGuilds().getGuildName(guildId);
                            args2.add(guildName);
                        }
                    } else if (a[0].equalsIgnoreCase("money")) {
                        args2.add("take");
                        args2.add("give");
                        args2.add("set");
                    }
                    return args2;
                } else if (a.length == 3) {
                    List<String> args3 = new ArrayList<String>();
                    if (a[0].equalsIgnoreCase("addmember")) {
                        for (String generatedGuilds : mods.getGeneratedGuilds()) {
                            String[] splitter = generatedGuilds.split(";");
                            String guildId = splitter[0];
                            String guildName = data.getGuilds().getGuildName(guildId);
                            args3.add(guildName);
                        }
                    } else if (a[0].equalsIgnoreCase("changerole")) {
                        args3.add("officer");
                        args3.add("member");
                    } else if (a[0].equalsIgnoreCase("createguild")) {
                        args3.add("<guildTitle>");
                    } else if (a[0].equalsIgnoreCase("money")) {
                        for (String generatedGuilds : mods.getGeneratedGuilds()) {
                            String[] splitter = generatedGuilds.split(";");
                            String guildId = splitter[0];
                            String guildName = data.getGuilds().getGuildName(guildId);
                            args3.add(guildName);
                        }
                    }
                    return args3;
                } else if (a.length == 4) {
                    List<String> args4 = new ArrayList<String>();
                    if (a[0].equalsIgnoreCase("createguild")) {
                        args4.add("<insertNumber>");
                    } else if (a[0].equalsIgnoreCase("money")) {
                        args4.add("<insertNumber>");
                    }
                    return args4;
                }
            }
        }
        return null;
    }
}
